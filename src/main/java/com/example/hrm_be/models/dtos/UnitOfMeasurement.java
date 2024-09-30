package com.example.hrm_be.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
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
public class UnitOfMeasurement implements Serializable {

  @Serial private static final long serialVersionUID = -3547890033004808244L;
  Long id;

  String unitName;

  Double conversionFactor;

  Double pricePerUnit;

  Product product;
}
