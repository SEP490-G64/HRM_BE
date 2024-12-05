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
public class StockProductReport {
  private Long productId;
  private String image;
  private String registrationCode;
  private String productName;
  private Integer minQuantity;
  private Integer maxQuantity;
  private BigDecimal totalQuantity;
  private BigDecimal sellableQuantity;
  private String storageLocation;
  private String unit;
  private List<StockBatchReport> batches;
}
