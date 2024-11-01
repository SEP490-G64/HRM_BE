package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
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
public final class Product implements Serializable {

  @Serial private static final long serialVersionUID = -1092354895838817939L;
  Long id;
  String productName;

  String registrationCode;

  String urlImage;

  Manufacturer manufacturer;

  ProductCategory category;

  ProductType type;

  String activeIngredient;

  String excipient;

  String formulation;

  BigDecimal inboundPrice;

  BigDecimal sellPrice;

  ProductStatus status;

  UnitOfMeasurement baseUnit;

  List<InboundDetails> inboundDetails;

  List<SpecialCondition> specialConditions;

  Batch batch;

  List<Batch> batches;

  List<BranchProduct> branchProducts;

  List<ProductSuppliers> productSuppliers;

  List<UnitConversion> unitConversions; // 1-N with UnitConversion
}
