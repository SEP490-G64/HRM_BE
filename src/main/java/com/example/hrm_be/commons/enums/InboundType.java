package com.example.hrm_be.commons.enums;

public enum InboundType {
  NHAP_TU_NHA_CUNG_CAP("Nhập từ nhà cung cấp"),
  CHUYEN_KHO_NOI_BO("Chuyển kho nội bộ");

  private final String displayName;

  InboundType(String displayName) {
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
