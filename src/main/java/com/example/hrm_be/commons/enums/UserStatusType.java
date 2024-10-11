package com.example.hrm_be.commons.enums;

public enum UserStatusType {
  PENDING("Chờ duyệt"),
  REJECTED("Từ chối"),
  ACTIVATE("Đang kích hoạt"),
  DEACTIVATE("Vô hiệu hoá");

  private final String displayName;

  UserStatusType(String displayName) {
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
