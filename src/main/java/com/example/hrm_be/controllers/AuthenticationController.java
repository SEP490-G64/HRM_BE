package com.example.hrm_be.controllers;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.utils.DateUtil;
import com.example.hrm_be.utils.MailUtil;
import com.example.hrm_be.models.entities.PasswordResetTokenEntity;
import com.example.hrm_be.models.requests.ResetPasswordRequest;
import com.example.hrm_be.services.PasswordTokenService;
import com.example.hrm_be.utils.JwtUtil;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.configs.exceptions.JwtAuthenticationException;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.requests.AuthRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.models.responses.AccessToken;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final DateUtil dateUtil;
  private final MailUtil mailUtil;
  private final PasswordTokenService passwordTokenService;

  // POST: /api/v1/auth/login
  // Allow user to login into the system
  @PostMapping("/login")
  public ResponseEntity<BaseOutput<AccessToken>> login(@RequestBody AuthRequest request) {
    try {
      // Authenticate user credentials
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
      // Throw exception if credentials are invalid
      throw new JwtAuthenticationException("error.auth.invalid.credentials");
    }

    // Retrieve user information based on the email
    User userDetails = userService.getByEmail(request.getEmail());

    // Check if user not has admin role and account status not activate
    if (!userDetails.getRoles().stream()
            .anyMatch(role -> role.getType() != null && role.getType().isAdmin())
        && !Objects.equals(userDetails.getStatus(), UserStatusType.ACTIVATE.toString())) {

      // Return a Bad Request response with message about account not being activated
      return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Change the status to BAD_REQUEST
          .body(
              BaseOutput.<AccessToken>builder()
                  .message("Account not activated, unable to login.") // Descriptive message
                  .data(null) // Do not return AccessToken
                  .build());
    }

    // Generate JWT token for authenticated user
    String jwt = jwtUtil.generateToken(userDetails);
    AccessToken accessToken = AccessToken.builder().accessToken(jwt).build();

    // Return successful response with AccessToken
    BaseOutput<AccessToken> response =
        BaseOutput.<AccessToken>builder()
            .message(HttpStatus.OK.toString())
            .data(accessToken)
            .build();

    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/auth/register
  // Allow user to register new account
  @PostMapping("/register")
  public ResponseEntity<BaseOutput<User>> register(@RequestBody RegisterRequest request) {
    // Check if the request body is null
    if (request == null) {
      // Throw an exception for invalid request body
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    // Call the user service to register the new user with the provided request
    User newUser = userService.register(request);

    // Create a response object containing the newly created user
    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString()) // Set the response message to OK
            .data(newUser) // Attach the newly created user data
            .build();

    // Return a response entity with status OK and the user data
    return ResponseEntity.ok(response);
  }

  @PostMapping("/forget-password")
  public ResponseEntity<BaseOutput<String>> forgetPassword(
      HttpServletRequest request, @RequestBody String email) {
    User user = userService.getByEmail(email);
    if (user == null) {
      throw new HrmCommonException("Not found user regarding the email, please check again");
    }
    String token = jwtUtil.generateToken(email);
    JavaMailSender mailSender = mailUtil.getJavaMailSender();
    PasswordResetTokenEntity prt = new PasswordResetTokenEntity(token, email, dateUtil.addHours(1));
    passwordTokenService.create(prt);
    String scheme = request.getScheme(); // "http" or "https"
    String serverName = request.getServerName(); // example.com
    int serverPort = request.getServerPort(); // 80, 443, etc.
    String contextPath = request.getContextPath();
    String fullPath =
        scheme
            + "://"
            + serverName
            + ((serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort)
            + contextPath
            + "/api/v1/auth/change_password?token="
            + token;
    mailSender.send(mailUtil.constructResetTokenEmail(fullPath, token, user.getEmail()));
    BaseOutput<String> response =
        BaseOutput.<String>builder().message(HttpStatus.OK.toString()).build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/change_password")
  public String changePassword(@RequestParam("token") String token, Model model) {
    // Add the token to the model if needed
    model.addAttribute("token", token);
    return "index"; // This will render index.html from the templates folder
  }

  @PostMapping("/reset_password")
  public ResponseEntity<String> resetPassword(
      @RequestBody ResetPasswordRequest resetPasswordRequest) {
    String token = resetPasswordRequest.getToken();
    String newPassword = resetPasswordRequest.getPassword();

    // Validate the token
    String email = jwtUtil.extractEmail(token);

    // If the token is invalid or email is null
    if (email == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
    }

    // Load the user details using the email
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    if (userDetails == null || !jwtUtil.validateToken(token, userDetails)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
    }

    // Find the user in the database by email
    User user = userService.getByEmail(email);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // Hash and update the user's password
    userService.updatePassword(user, newPassword);

    // Respond with success message
    return ResponseEntity.ok("Password successfully reset");
  }
}
