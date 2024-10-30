package com.example.hrm_be.common.utils;

import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.models.entities.RoleEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

  // Enter email that have been register with its role
  public static void mockAuthenticatedUser(String email, RoleType roleType) {
    // Create a mock UserDetails instance with the specified email
    UserDetails mockUserDetails =
        new UserDetails() {
          @Serial private static final long serialVersionUID = 8253328499707309238L;

          @Override
          public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority(roleType.getValue()));
          }

          @Override
          public String getPassword() {
            return null; // Password is not required for this test
          }

          @Override
          public String getUsername() {
            return email; // The mock email to be returned by getAuthenticatedUserEmail
          }

          @Override
          public boolean isAccountNonExpired() {
            return true;
          }

          @Override
          public boolean isAccountNonLocked() {
            return true;
          }

          @Override
          public boolean isCredentialsNonExpired() {
            return true;
          }

          @Override
          public boolean isEnabled() {
            return true;
          }
        };

    // Create an authentication token with the specified user email and authority
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            mockUserDetails, null, mockUserDetails.getAuthorities());

    // Set the security context to use this authentication
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
