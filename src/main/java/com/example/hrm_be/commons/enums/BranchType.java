package com.example.hrm_be.commons.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BranchType {
  MAIN("Trụ sở chính"),
  SUB("Chi nhánh");

  private final String displayName;

  BranchType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
