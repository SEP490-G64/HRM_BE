package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InventoryEntity;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import com.example.hrm_be.models.entities.ProductCategoryMapEntity;
import com.example.hrm_be.models.entities.ProductIngredientMapEntity;
import com.example.hrm_be.models.entities.ProductUnitMapEntity;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.models.entities.TaxEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class Product {
  @Column(name = "name")
  String name;

  Double price;

  String description;

  String image;

  String barcodeImage;

  BranchEntity branch;

  BatchEntity batch;

  TaxEntity tax;

  Supplier supplier;

  List<ProductUnitMap> userUnitMap;

  List<ProductIngredientMap> productIngredientMap;

  List<ProductCategoryMap> userCateMap;

  List<Inventory> inventory;

  Manufacturer manufacturer;

  SpecialCondition specialCondition;
}
