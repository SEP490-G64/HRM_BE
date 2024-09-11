package com.example.hrm_be;

import org.springframework.boot.SpringApplication;

public class TestHrmBeApplication {

  public static void main(String[] args) {
    SpringApplication.from(HrmBeApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
