package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.InventoryStatus;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class Inventory implements Serializable {

  @Serial private static final long serialVersionUID = 3110771686280662988L;
  Long id;

  Branch branch;

  Product product;

  Integer quantity;

  String storageCondition;

  LocalDateTime lastUpdated;

  InventoryStatus status;

  String location;
}
