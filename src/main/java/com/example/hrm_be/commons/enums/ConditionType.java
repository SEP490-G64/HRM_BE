package com.example.hrm_be.commons.enums;

public enum ConditionType {
  NHIET_DO("Nhiệt độ"),
  DO_AM("Độ ẩm"),
  ANH_SANG("Ánh sáng"),
  KHONG_KHI("Không khí"),
  KHAC("Khác");

  private final String displayName;

  ConditionType(String displayName) {
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
