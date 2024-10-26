package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
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
public class Outbound {
  Long id;

  String outBoundCode;

  OutboundType outboundType; // Assume this is an Enum with values like "Bán hàng", "Trả hàng", etc.

  Branch fromBranch;

  Supplier supplier;

  Branch toBranch;

  LocalDateTime createdDate;

  LocalDateTime outboundDate;

  BigDecimal totalPrice;

  Boolean isApproved;

  User approvedBy;

  OutboundStatus status; // Enum for "Chờ duyệt", "Đang xử lý", etc.

  Boolean taxable;

  String note;

  User createdBy;

  List<OutboundDetail> outboundDetails;

}
