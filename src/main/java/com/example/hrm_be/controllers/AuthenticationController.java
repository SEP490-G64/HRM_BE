package com.example.hrm_be.controllers;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.components.DateUtil;
import com.example.hrm_be.components.JwtUtil;
import com.example.hrm_be.components.MailUtil;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.configs.exceptions.JwtAuthenticationException;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.PasswordResetTokenEntity;
import com.example.hrm_be.models.requests.AuthRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.models.responses.AccessToken;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.PasswordTokenService;
import com.example.hrm_be.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final UserService userService;
  private final PasswordTokenService passwordTokenService;
  private final JwtUtil jwtUtil;
  private final MailUtil mailUtil;
  private final DateUtil dateUtil;

  @PostMapping("/login")
  public ResponseEntity<BaseOutput<AccessToken>> login(@RequestBody AuthRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
      throw new JwtAuthenticationException("error.auth.invalid.credentials");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
    String jwt = jwtUtil.generateToken(userDetails);
    AccessToken accessToken = AccessToken.builder().accessToken(jwt).build();
    BaseOutput<AccessToken> response =
        BaseOutput.<AccessToken>builder()
            .message(HttpStatus.OK.toString())
            .data(accessToken)
            .build();

    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<BaseOutput<User>> register(@RequestBody RegisterRequest request) {
    if (request == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    User newUser = userService.register(request);
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(newUser).build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/verify-user/{id}")
  public ResponseEntity<BaseOutput<User>> verifyUser(@PathVariable("id") Long id) {
    if (id == null) {
      throw new HrmCommonException(REQUEST.INVALID_PATH_VARIABLE);
    }
    User verifiedUser = userService.verifyUser(id);
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(verifiedUser).build();
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
    mailSender.send(
        mailUtil.constructResetTokenEmail(request.getContextPath(), token, user.getEmail()));
    BaseOutput<String> response =
        BaseOutput.<String>builder().message(HttpStatus.OK.toString()).build();
    return ResponseEntity.ok(response);
  }
}
