package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductIngredientEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
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
public class ProductIngredientMap implements Serializable {

  @Serial private static final long serialVersionUID = -3328495976906911346L;
  Long id;
  ProductEntity product;

  ProductIngredientEntity ingredient;

  Double amount;

  String unitOfMeasurement;
}
