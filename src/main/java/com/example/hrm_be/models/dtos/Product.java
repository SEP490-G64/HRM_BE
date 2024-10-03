package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.models.entities.CommonEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product  {
  Long id;
  String productName;

  String productCode;

  String registrationCode;

  String urlImage;

  Manufacturer manufacturer;

  ProductCategory category;

  ProductType type;

  String activeIngredient;

  String excipient;

  String formulation;

  BigDecimal sellPrice;

  ProductStatus status;

  UnitOfMeasurement baseUnit;

  List<InboundDetails> inboundDetails;


  List<SpecialCondition> specialConditions;


  List<Batch> batches;


  List<BranchProduct> branchProducs;


  List<InventoryCheckDetails> inventoryCheckDetails;


  List<ProductSuppliers> productSuppliers;
}
