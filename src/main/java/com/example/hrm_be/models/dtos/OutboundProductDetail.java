package com.example.hrm_be.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

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
public class OutboundProductDetail {
  Long id;

  Outbound outbound;

  Product product;

  Batch batch;

  List<Batch> batches;

  BigDecimal outboundQuantity;

  BigDecimal price;

  UnitOfMeasurement targetUnit;

  UnitOfMeasurement productBaseUnit;

  BigDecimal taxRate;

  BigDecimal productQuantity;

  BigDecimal preQuantity;

  BigDecimal inboundPrice;
}
