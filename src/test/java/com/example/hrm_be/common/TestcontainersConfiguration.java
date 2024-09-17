package com.example.hrm_be.common;

import org.testcontainers.containers.PostgreSQLContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
  public class TestcontainersConfiguration extends PostgreSQLContainer<TestcontainersConfiguration> {
  private static final String IMAGE_VERISON = "postgres:16.4-alpine3.20";
  private static TestcontainersConfiguration container;

  private TestcontainersConfiguration() {
    super(IMAGE_VERISON);
  }

  public static synchronized TestcontainersConfiguration getInstance() {
    if (container == null) {
      container = new TestcontainersConfiguration();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    log.info("===== Start test container =====");
    log.info("DB_URL: {}", container.getJdbcUrl());
    log.info("DB_USERNAME: {}", container.getUsername());
    log.info("DB_PASSWORD: {}", container.getPassword());
  }

  @Override
  public void stop() {
    log.info("===== Stop test container =====");
  }
}