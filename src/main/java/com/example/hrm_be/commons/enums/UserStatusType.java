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

  // Method to find enum by displayName
  public static UserStatusType fromDisplayName(String displayName) {
    if (displayName == null) {
      throw new IllegalArgumentException("Display name cannot be null");
    }

    for (UserStatusType status : UserStatusType.values()) {
      if (status.getDisplayName().equalsIgnoreCase(displayName)) {
        return status;
      }
    }
    throw new IllegalArgumentException("No enum constant with display name: " + displayName);
  }
}
