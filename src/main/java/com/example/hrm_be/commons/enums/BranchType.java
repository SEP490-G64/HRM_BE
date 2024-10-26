package com.example.hrm_be.commons.enums;

import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BranchType {
  MAIN("Trụ sở chính"),
  SUB("Chi nhánh"),
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

  public boolean isMain() {
    return this == MAIN;
  }

  public boolean isSub() {
    return this == SUB;
  }


  public static BranchType parse(final String type) {
    return Stream.of(BranchType.values())
        .filter(e -> e.name().equalsIgnoreCase(type) || e.getDisplayName().equalsIgnoreCase(type))
        .findFirst()
        .orElse(BranchType.UNDEFINED);
  }

  public String getDisplayName() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
