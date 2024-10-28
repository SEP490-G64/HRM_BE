package com.example.hrm_be.models.dtos;

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
public class InboundProductDetailDTO {
  private Long id;
  private Long productId;
  private Double discount;
  private String productName;
  private String productCode;
  private UnitOfMeasurement baseUnit;
  private Integer requestQuantity;
  private Integer receiveQuantity;
  private List<Batch> batches; // Associated batches for this product
}
