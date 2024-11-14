package com.example.hrm_be.commons.enums;

public enum BatchStatus {
  CON_HANG("Còn hàng"),
  HET_HANG("Hết hàng"),
  NGUNG_KINH_DOANH("Ngừng kinh doanh"),
  DA_XOA("Đã xóa");

  private final String displayName;

  BatchStatus(String displayName) {
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
