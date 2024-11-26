package com.example.hrm_be.commons.enums;

public enum NotificationType {
  GAN_HET_HAN("Gần hết hạn"),
  HET_HAN("Hết hạn"),
  VUOT_DINH_MUC("Vượt định mức"),
  DUOI_DINH_MUC("Dưới định mức"),
  CANH_BAO_SAN_PHAM("Cảnh báo sản phẩm"),
  NHAP_PHIEU_NHAP_VAO_HE_THONG("nhập phiếu nhập vào hệ thống"),
  NHAP_PHIEU_XUAT_VAO_HE_THONG("nhập phiếu xuất vào hệ thống"),
  NHAP_PHIEU_KIEM_VAO_HE_THONG("nhập phiếu kiểm vào hệ thống"),
  YEU_CAU_DUYET_DON_NHAP("yêu cầu duyệt đơn nhập"),
  YEU_CAU_DUYET_DON_XUAT("yêu cầu duyệt đơn xuất"),
  YEU_CAU_DUYET_DON_KIEM("yêu cầu duyệt đơn kiểm"),
  YEU_CAU_DANG_KY_TAI_KHOAN("yêu cầu duyệt đăng ký"),
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
