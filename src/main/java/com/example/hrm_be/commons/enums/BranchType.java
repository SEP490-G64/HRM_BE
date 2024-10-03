package com.example.hrm_be.commons.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BranchType {
  MAIN("Trụ sở chính"),
  SUB("Chi nhánh");

  private final String value;

  BranchType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public static BranchType fromValue(String value) {
    for (BranchType type : BranchType.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
