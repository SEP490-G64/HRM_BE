package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.InventoryStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
