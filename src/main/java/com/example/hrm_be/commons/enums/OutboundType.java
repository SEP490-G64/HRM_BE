package com.example.hrm_be.commons.enums;


import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OutboundType {
  BAN_HANG("Bán hàng"),
  TRA_HANG("Trả hàng"),
  HUY_HANG("Hủy hàng"),
  UNDEFINED("Undefined"),
  CHUYEN_KHO_NOI_BO("Chuyển kho nội bộ");


  private final String value;

  // Static method to check if the string exists as a valid InboundType
  public static boolean exists(String name) {
    for (OutboundType type : OutboundType.values()) {
      if (type.name().equalsIgnoreCase(name) || type.getDisplayName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public boolean isSell() {
    return this == BAN_HANG;
  }

  public boolean isReturn() {
    return this == TRA_HANG;
  }

  public boolean isCancel() {
    return this == HUY_HANG;
  }

  public boolean isFromBranch() {
    return this == CHUYEN_KHO_NOI_BO;
  }

  public boolean isValid() {
    return this != UNDEFINED;
  }

  public static OutboundType parse(final String type) {
    return Stream.of(OutboundType.values())
        .filter(e -> e.name().equalsIgnoreCase(type) || e.getDisplayName().equalsIgnoreCase(type))
        .findFirst()
        .orElse(OutboundType.UNDEFINED);
  }

  public String getDisplayName() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
