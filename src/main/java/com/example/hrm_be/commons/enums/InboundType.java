package com.example.hrm_be.commons.enums;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InboundType {
  NHAP_TU_NHA_CUNG_CAP("Nhập từ nhà cung cấp"),
  CHUYEN_KHO_NOI_BO("Chuyển kho nội bộ"),
  UNDEFINED("Undefined");

  private final String value;

  // Static method to check if the string exists as a valid InboundType
  public static boolean exists(String name) {
    for (InboundType type : InboundType.values()) {
      if (type.name().equalsIgnoreCase(name) || type.getDisplayName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public boolean isFromSupplier() {
    return this == NHAP_TU_NHA_CUNG_CAP;
  }

  public boolean isFromBranch() {
    return this == CHUYEN_KHO_NOI_BO;
  }

  public boolean isValid() {
    return this != UNDEFINED;
  }

  public static InboundType parse(final String type) {
    return Stream.of(InboundType.values())
        .filter(e -> e.name().equalsIgnoreCase(type) || e.getDisplayName().equalsIgnoreCase(type))
        .findFirst()
        .orElse(InboundType.UNDEFINED);
  }

  public String getDisplayName() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
