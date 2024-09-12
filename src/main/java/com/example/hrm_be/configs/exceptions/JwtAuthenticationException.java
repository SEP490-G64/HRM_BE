package com.example.hrm_be.configs.exceptions;

public class JwtAuthenticationException extends RuntimeException {
  public JwtAuthenticationException(String msg) {
    super(msg);
  }
}
