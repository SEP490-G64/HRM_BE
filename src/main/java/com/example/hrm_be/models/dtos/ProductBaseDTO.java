package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.ProductStatus;
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
public class ProductBaseDTO {
  private Long id;
  private String productName;
  private String productCode;
  private String registrationCode;
  private String urlImage;
  private String activeIngredient;
  private String excipient;
  private String formulation;
  private BigDecimal inboundPrice;
  private BigDecimal sellPrice;
  private ProductStatus status;

  // New fields for category, type, and manufacturer names
  private String baseUnit;
  private String categoryName;
  private String typeName;
  private String manufacturerName;
  private BigDecimal quantity;

  private List<UnitConversion> unitConversions;
  private List<Batch> batches;
  private List<UnitOfMeasurement> productUnits;
  private UnitOfMeasurement productBaseUnit;

  private BranchProduct branchProduct;
}
