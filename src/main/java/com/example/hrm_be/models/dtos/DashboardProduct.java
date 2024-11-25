package com.example.hrm_be.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardProduct {
  Long Id;
  String ProductName;
  String Image;
  BigDecimal InboundQuantity;
  BigDecimal OutboundQuantity;
  BigDecimal TotalQuantity;
  BigDecimal InboundPrice;
  BigDecimal OutboundPrice;
  BigDecimal TotalPrice;
  String UnitName;
}
