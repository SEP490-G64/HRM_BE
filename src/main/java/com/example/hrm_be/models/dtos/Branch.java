package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.BranchType;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class Branch implements Serializable {

  @Serial private static final long serialVersionUID = 3226334222825750552L;
  Long id;
  String branchName;

  BranchType branchType;

  String location;

  String contactPerson;

  String phoneNumber;

  Integer capacity;

  Boolean activeStatus;

  List<User> users;

  List<Inventory> inventoryEntities;
}
