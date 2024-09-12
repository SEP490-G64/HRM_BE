package com.example.hrm_be.configs.exceptions;

public class HrmCommonException extends RuntimeException {
  public HrmCommonException(String msg) {
    super(msg);
  }

  public HrmCommonException(Exception e) {
    super(e);
  }

  public HrmCommonException(String msg, Exception e) {
    super(msg, e);
  }
}
