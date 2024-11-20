package com.example.hrm_be.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InventoryCheckType {
  KIEM_KHO_DINH_KY("Kiểm kho định kỳ"),
  KIEM_KHO_DOT_XUAT("Kiểm kho đột xuất"),
  KIEM_KHO_TRONG_TAM("Kiểm kho theo trọng tâm"),
  KIEM_KHO_VAT_LY_TOAN_PHAN("Kiểm kho vật lý toàn phần");

  private final String value;

  // Static method to check if the string exists as a valid InboundType
  public static boolean exists(String name) {
    for (InventoryCheckType type : InventoryCheckType.values()) {
      if (type.name().equalsIgnoreCase(name) || type.getDisplayName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  public static InventoryCheckType parse(String branchTypeStr) {
    for (InventoryCheckType type : InventoryCheckType.values()) {
      if (type.name().equalsIgnoreCase(branchTypeStr)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid BranchType: " + branchTypeStr);
  }

  public String getDisplayName() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
