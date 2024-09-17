package com.example.hrm_be.common.utils;


import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.models.entities.RoleEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUtils {
  public static UserEntity initTestUserEntity(@NonNull PasswordEncoder passwordEncoder) {
    return UserEntity.builder()
        .userName("chuduong1811")
        .email("duongcdhe176312@gmail.com")
        .phone("0915435790")
        .firstName("chu")
        .lastName("duong")
        .password(passwordEncoder.encode("Abcd1234"))
        .build();
  }

  public static RoleEntity initTestRoleEntity(@NonNull RoleType type) {
    return RoleEntity.builder().name(type.getValue()).type(type).build();
  }

  public static UserRoleMapEntity initTestUserRoleMapEntity(
      @NonNull UserEntity user, @NonNull RoleEntity role) {
    return UserRoleMapEntity.builder().user(user).role(role).build();
  }
}
