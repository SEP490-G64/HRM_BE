package com.example.hrm_be.commons.enums;

public enum InventoryCheckStatus {
  CHUA_LUU("Chưa lưu"),
  BAN_NHAP("Bản nháp"),
  DANG_KIEM("Đang kiểm"),
  CHO_DUYET("Chờ duyệt"),
  DA_CAN_BANG("Đã cân bằng");

  private final String displayName;

  InventoryCheckStatus(String displayName) {
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
