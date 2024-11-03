package com.example.hrm_be.common;

import com.example.hrm_be.HrmBeApplication;
import org.springframework.boot.SpringApplication;

public class TestHrmBeApplication {

  public static void main(String[] args) {
    SpringApplication.from(HrmBeApplication::main).run(args);
  }
}
