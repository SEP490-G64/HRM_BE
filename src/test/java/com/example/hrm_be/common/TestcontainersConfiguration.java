package com.example.hrm_be.common;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class TestcontainersConfiguration extends PostgreSQLContainer<TestcontainersConfiguration> {
  private static final String IMAGE_VERSION = "postgres:16.4-alpine3.20";
  private static TestcontainersConfiguration container;

  private TestcontainersConfiguration() {
    super(IMAGE_VERSION);
  }

  //  @Container
  //  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
  //      TestcontainersConfiguration.getInstance();

  public static synchronized TestcontainersConfiguration getInstance() {
    if (container == null) {
      container = new TestcontainersConfiguration();
      List<String> portBindings = new ArrayList<>();
      portBindings.add("5555:5432"); // hostPort:containerPort
      container.setPortBindings(portBindings);
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
