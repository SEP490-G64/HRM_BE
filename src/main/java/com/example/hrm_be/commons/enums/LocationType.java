package com.example.hrm_be.commons.enums;

public enum LocationType {
  RACK("Kệ"),
  PALLET("Pallet"),
  CABINET("Tủ"),
  FREEZER("Kho lạnh");

  private final String displayName;

  LocationType(String displayName) {
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
