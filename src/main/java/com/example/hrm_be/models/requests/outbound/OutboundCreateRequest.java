package com.example.hrm_be.models.requests.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutboundCreateRequest {
  String outboundType;

  Long fromBranchId;

  Long supplierId;

  Long toBranchId;

  BigDecimal totalPrice;

  String status;

  Boolean taxable;

  String note;

  LocalDateTime outboundDate;
}
