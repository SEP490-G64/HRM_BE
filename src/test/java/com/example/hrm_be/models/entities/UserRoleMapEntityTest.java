package com.example.hrm_be.models.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.configs.SecurityConfig;
import com.example.hrm_be.repositories.RoleRepository;
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.repositories.UserRoleMapRepository;
import java.util.List;
import java.util.Objects;
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
class UserRoleMapEntityTest {

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired UserRoleMapRepository userRoleMapRepository;
  @Autowired UserRepository userRepository;
  @Autowired RoleRepository roleRepository;

  @Test
  @Transactional
  void shouldInitMappingUserRole() {
    UserEntity adminUserEntity = userRepository.findByEmail("dsdadmin@gmail.com").orElse(null);
    assertNotNull(adminUserEntity);
    List<UserRoleMapEntity> adminRoleMapEntities =
        userRoleMapRepository.findByUser(adminUserEntity);
    assertNotNull(adminRoleMapEntities);
    // admin should have staff role
    assertNotNull(
        adminRoleMapEntities.stream()
            .filter(Objects::nonNull)
            .filter(urm -> urm.getRole() != null)
            .filter(urm -> urm.getRole().getType().isStaff())
            .findFirst());
    // admin should have admin role
    assertNotNull(
        adminRoleMapEntities.stream()
            .filter(Objects::nonNull)
            .filter(urm -> urm.getRole() != null)
            .filter(urm -> urm.getRole().getType().isAdmin())
            .findFirst());
  }

  @Test
  @Transactional
  void shouldBePersistedWhenCreatingNewRecord() {
    RoleEntity roleEntity =
        RoleEntity.builder().name(RoleType.UNDEFINED.getValue()).type(RoleType.UNDEFINED).build();
    RoleEntity savedRoleEntity = roleRepository.save(roleEntity);
    assertNotNull(savedRoleEntity);
    assertNotNull(savedRoleEntity.getId());
    assertEquals(RoleType.UNDEFINED.getValue(), savedRoleEntity.getName());
    assertEquals(RoleType.UNDEFINED, savedRoleEntity.getType());

    UserEntity userEntity = TestUtils.initTestUserEntity(passwordEncoder);
    UserEntity savedUserEntity = userRepository.save(userEntity);
    assertNotNull(savedUserEntity.getId());
    assertTrue(passwordEncoder.matches("Abcd1234", savedUserEntity.getPassword()));
    assertEquals("chuduong1811", savedUserEntity.getUserName());
    assertEquals("duongcdhe176312@gmail.com", savedUserEntity.getEmail());
    assertEquals("0915435790", savedUserEntity.getPhone());
    assertEquals("chu", savedUserEntity.getFirstName());
    assertEquals("duong", savedUserEntity.getLastName());

    UserRoleMapEntity userRoleMapEntity =
        TestUtils.initTestUserRoleMapEntity(savedUserEntity, savedRoleEntity);
    UserRoleMapEntity createdUserRoleMapEntity = userRoleMapRepository.save(userRoleMapEntity);
    assertNotNull(createdUserRoleMapEntity.getId());
    assertEquals("duongcdhe176312@gmail.com", createdUserRoleMapEntity.getUser().getEmail());
    assertEquals(RoleType.UNDEFINED, createdUserRoleMapEntity.getRole().getType());
  }
}
