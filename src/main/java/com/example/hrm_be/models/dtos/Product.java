package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.TaxEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {

  @Serial private static final long serialVersionUID = -3105002269033600248L;
  Long id;
  String name;

  Double price;

  String description;

  String image;

  String barcodeImage;

  Branch branch;

  Batch batch;

  Tax tax;

  Supplier supplier;

  List<ProductUnitMap> productUnitMaps;

  List<ProductIngredientMap> productIngredientMap;

  List<ProductCategoryMap> productCateMap;

  List<Inventory> inventory;

  Manufacturer manufacturer;

  SpecialCondition specialCondition;
}
