package com.example.hrm_be.commons.enums;

public enum OutboundStatus {
  CHUA_LUU("Chưa lưu"),
  BAN_NHAP("Bản nháp"),
  CHO_DUYET("Chờ duyệt"),
  DANG_XU_LI("Đang xử lí"),
  KIEM_HANG("Kiểm hàng"),
  DANG_THANH_TOAN("Đang thanh toán"),
  HOAN_THANH("Hoàn thành"),
  UNDEFINED("Undefined");

  private final String displayName;

  OutboundStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public boolean isCheck() {
    return this == KIEM_HANG;
  }
  public boolean isWaitingForApprove() {
    return this == CHO_DUYET;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
