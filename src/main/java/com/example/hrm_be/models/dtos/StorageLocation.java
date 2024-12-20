package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.LocationType;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageLocation {
  Long id;
  String shelfName;
  Integer aisle;
  Integer rowNumber;
  Integer shelfLevel;
  String zone;
  LocationType locationType;
  String specialCondition;
  Boolean active;
  Branch branch;
  List<BranchProduct> branchProducts;
}
