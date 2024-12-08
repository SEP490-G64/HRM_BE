package com.example.hrm_be.models.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.configs.SecurityConfig;
import com.example.hrm_be.repositories.UserRepository;
import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Testcontainers
@Disabled
class UserEntityTest {

  @Autowired UserRepository userRepository;

  @Autowired PasswordEncoder passwordEncoder;

  @Test
  @Transactional
  void shouldInitAdminUser() {
    UserEntity adminUserEntity = userRepository.findByEmail("dsdadmin@gmail.com").orElse(null);
    assertNotNull(adminUserEntity);
    assertNotNull(adminUserEntity.getId());
    assertNotNull(adminUserEntity.getUserRoleMap());
    // admin has ROLE_ADMIN and ROLE_USER
    assertEquals(3, adminUserEntity.getUserRoleMap().size());
    // admin should have admin role
    assertNotNull(
        adminUserEntity.getUserRoleMap().stream()
            .filter(Objects::nonNull)
            .filter(urm -> urm.getRole() != null)
            .filter(urm -> urm.getRole().getType().isAdmin())
            .findFirst());
  }

  @Test
  @Transactional
  void shouldBePersistedWhenCreatingNewRecord() {
    UserEntity userEntity = TestUtils.initTestUserEntity(passwordEncoder);

    UserEntity savedUserEntity = userRepository.save(userEntity);
    assertNotNull(savedUserEntity.getId());
    assertTrue(passwordEncoder.matches("Abcd1234", savedUserEntity.getPassword()));
    assertEquals("chuduong1811", savedUserEntity.getUserName());
    assertEquals("duongcdhe176312@gmail.com", savedUserEntity.getEmail());
    assertEquals("0915435790", savedUserEntity.getPhone());
    assertEquals("chu", savedUserEntity.getFirstName());
    assertEquals("duong", savedUserEntity.getLastName());
  }
}
