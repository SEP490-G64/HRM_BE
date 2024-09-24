package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ProductTypeMap implements Serializable {

  @Serial
  private static final long serialVersionUID = -2042368325937200647L;
  Long id;
  Product product;

  ProductType type;
}
