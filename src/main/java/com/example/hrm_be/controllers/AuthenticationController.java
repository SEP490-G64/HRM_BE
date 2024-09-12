package com.example.hrm_be.controllers;


import com.example.hrm_be.components.JwtUtil;
import com.example.hrm_be.configs.exceptions.JwtAuthenticationException;
import com.example.hrm_be.models.requests.AuthRequest;
import com.example.hrm_be.models.responses.AccessToken;
import com.example.hrm_be.models.responses.BaseOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
  private final JwtUtil jwtUtil;

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

}