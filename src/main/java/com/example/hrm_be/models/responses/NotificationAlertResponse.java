package com.example.hrm_be.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationAlertResponse {
  private Integer nearlyExpiredProduct;
  private Integer expiredProduct;
  private Integer underThresholdProduct;
  private Integer upperThresholdProduct;
  private Integer outOfStockProduct;

  @Override
  public String toString() {
    return "Nearly Expired Product= "
        + nearlyExpiredProduct
        + ", Expired Product="
        + expiredProduct
        + ", Under Threshold Product= "
        + underThresholdProduct
        + ", Upper Threshold Product= "
        + upperThresholdProduct
        + ", Out Of Stock Product= "
        + outOfStockProduct;
  }
}
