package com.example.hrm_be.commons.enums;

public enum OutboundStatus {
  CHO_DUYET("Chờ duyệt"),
  DANG_XU_LY("Đang xử lí"),
  DANG_THANH_TOAN("Đang thanh toán"),
  HOAN_THANH("Hoàn thành");

  private final String displayName;

  OutboundStatus(String displayName) {
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
