package com.example.hrm_be.commons.enums;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleType {
  ADMIN("ROLE_ADMIN"),
  STAFF("ROLE_STAFF"),
  MANAGER("ROLE_MANAGER"),
  UNDEFINED("UNDEFINED");

  private final String value;

  public static RoleType parse(final String role) {
    return Stream.of(RoleType.values())
        .filter(e -> e.value.equals(role))
        .findFirst()
        .orElse(RoleType.UNDEFINED);
  }

  public boolean isAdmin() {
    return this == ADMIN;
  }

  public boolean isManager() {
    return this == MANAGER;
  }

  public boolean isStaff() {
    return this == STAFF;
  }

  public boolean isValid() {
    return this != UNDEFINED;
  }
}
