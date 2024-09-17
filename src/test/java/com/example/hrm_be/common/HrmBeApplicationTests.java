package com.example.hrm_be.common;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.configs.SecurityConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Import(SecurityConfig.class)
@ActiveProfiles("local")
class HrmBeApplicationTests {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Test
  void contextLoads() {
  }
}
