package com.example.hrm_be.commons.enums;

public enum OutboundType {
  BAN_HANG("Bán hàng"),
  TRA_HANG("Trả hàng"),
  HUY_HANG("Hủy hàng"),
  CHUYEN_KHO_NOI_BO("Chuyển kho nội bộ");
  private final String displayName;

  OutboundType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
