package com.example.hrm_be.commons.enums;

public enum ProductStatus {
  CON_HANG("Còn hàng"),
  HET_HANG("Hết hàng"),
  NGUNG_KINH_DOANH("Ngừng kinh doanh");

  private final String displayName;

  ProductStatus(String displayName) {
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
