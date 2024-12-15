package com.example.hrm_be.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InboundOutboundProductReport {
  Long productId;
  String image;
  String registrationCode;
  String productName;
  BigDecimal inboundPrice;
  BigDecimal sellPrice;
  BigDecimal inboundQuantity;
  BigDecimal inboundValue;
  BigDecimal outboundQuantity;
  BigDecimal outboundValue;
  BigDecimal differenceQuantity;
  BigDecimal differenceValue;
  String unit;
}
