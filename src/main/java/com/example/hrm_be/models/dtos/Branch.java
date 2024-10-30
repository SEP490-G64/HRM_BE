package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.fasterxml.jackson.annotation.JsonInclude;
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

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Branch {
  Long id;
  String branchName;

  BranchType branchType;

  String location;

  String contactPerson;

  String phoneNumber;

  Integer capacity;

  Boolean activeStatus;

  List<BranchBatch> branchBatches; // 1-N with BranchBatch

  List<Inbound> inbounds; // 1-N with Inbound

  List<BranchProduct> branchProducts; // 1-N with BranchProduct

  List<Outbound> outbounds; // 1-N with Outbound

  List<InventoryCheck> inventoryChecks; // 1-N with InventoryCheck

  List<User> users;
}
