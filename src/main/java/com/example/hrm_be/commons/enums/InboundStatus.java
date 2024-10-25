package com.example.hrm_be.commons.enums;

import java.util.stream.Stream;

public enum InboundStatus {
  CHUA_LUU("Chưa lưu"),
  BAN_NHAP("Bản nháp"),
  CHO_DUYET("Chờ duyệt"),
  CHO_HANG("Chờ hàng"),
  KIEM_HANG("Kiểm hàng"),
  DANG_THANH_TOAN("Đang thanh toán"),
  HOAN_THANH("Hoàn thành"),
  UNDEFINED("Undefined");

  private final String displayName;

  InboundStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static InboundStatus parse(final String type) {
    return Stream.of(InboundStatus.values())
        .filter(e -> e.name().equalsIgnoreCase(type) || e.getDisplayName().equalsIgnoreCase(type))
        .findFirst()
        .orElse(InboundStatus.UNDEFINED);
  }

  @Override
  public String toString() {
    return displayName;
  }
}
