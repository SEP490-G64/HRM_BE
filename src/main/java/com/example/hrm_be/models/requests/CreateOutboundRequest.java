package com.example.hrm_be.models.requests;

import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.dtos.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
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
public class CreateOutboundRequest {
  Long outboundId;
  String outboundCode;
  OutboundStatus outboundStatus;
  String note;
  LocalDateTime createdDate;
  User createdBy;
  OutboundType outboundType;
  Branch toBranch;
  Branch fromBranch;
  Supplier supplier;
  Boolean taxable;
  BigDecimal totalPrice;
  List<OutboundProductDetail> outboundProductDetails;
}
