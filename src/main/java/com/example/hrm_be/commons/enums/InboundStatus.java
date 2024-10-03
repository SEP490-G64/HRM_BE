package com.example.hrm_be.commons.enums;

public enum InboundStatus {
  CHO_DUYET("Chờ duyệt"),
  CHO_HANG("Chờ hàng"),
  KIEM_HANG("Kiểm hàng"),
  DANG_THANH_TOAN("Đang thanh toán"),
  HOAN_THANH("Hoàn thành");

  private final String displayName;

  InboundStatus(String displayName) {
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
