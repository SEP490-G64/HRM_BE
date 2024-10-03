package com.example.hrm_be.commons.enums;

public enum NotificationType {
  GAN_HET_HAN("Gần hết hạn"),
  HET_HAN("Hết hạn"),
  VUOT_DINH_MUC("Vượt định mức"),
  DUOI_DINH_MUC("Dưới định mức"),
  GIA_NHAP_SAN_PHAM_THAY_DOI("Giá nhập sản phẩm thay đổi");

  private final String displayName;

  NotificationType(String displayName) {
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
