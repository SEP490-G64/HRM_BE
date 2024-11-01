package com.example.hrm_be.models.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.configs.SecurityConfig;
import com.example.hrm_be.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Testcontainers
class RoleEntityTest {

  @Autowired RoleRepository roleRepository;

  @Test
  @Transactional
  void shouldInitUserRole() {
    RoleEntity userRoleEntity = roleRepository.findByType(RoleType.MANAGER).orElse(null);
    assertNotNull(userRoleEntity);
    assertNotNull(userRoleEntity.getId());
    assertNotNull(userRoleEntity.getType());
    assertTrue(userRoleEntity.getType().isManager());
  }

  @Test
  @Transactional
  void shouldInitAdminRole() {
    RoleEntity adminRoleEntity = roleRepository.findByType(RoleType.ADMIN).orElse(null);
    assertNotNull(adminRoleEntity);
    assertNotNull(adminRoleEntity.getId());
    assertNotNull(adminRoleEntity.getType());
    assertTrue(adminRoleEntity.getType().isAdmin());
  }

  @Test
  @Transactional
  void shouldBePersistedWhenCreatingNewRecord() {
    RoleEntity newRoleEntity = TestUtils.initTestRoleEntity(RoleType.UNDEFINED);

    RoleEntity savedRoleEntity = roleRepository.save(newRoleEntity);
    assertNotNull(savedRoleEntity);
    assertNotNull(savedRoleEntity.getId());
    assertEquals(RoleType.UNDEFINED.getValue(), savedRoleEntity.getName());
    assertEquals(RoleType.UNDEFINED, savedRoleEntity.getType());
  }
}
