package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class BranchProduct {
  Long id;
  Product product;

  ProductBaseDTO productBaseDTO;

  Branch branch;

  StorageLocation storageLocation;

  Integer minQuantity;

  Integer maxQuantity;

  BigDecimal quantity;

  ProductStatus productStatus;

  LocalDateTime lastUpdated;
}
