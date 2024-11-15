package com.example.hrm_be.models.requests;

import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import com.example.hrm_be.models.dtos.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
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
public class CreateInventoryCheckRequest {
  Long inventoryCheckId;
  String code;
  String note;
  LocalDateTime createdDate;
  User createdBy;
  List<InventoryCheckProductDetails> inventoryCheckProductDetails;
}
