package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
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
public class Inbound {
  Long id;
  InboundType inboundType; // Custom enum representing: Nhà cung cấp, Chuyển kho nội bộ

  Branch fromBranch;

  Branch toBranch;

  Supplier supplier;

  User createdBy;

  LocalDateTime createdDate;

  BigDecimal totalPrice;

  Boolean isApproved;

  InboundStatus status; // Custom enum: Chờ duyệt, Chờ hàng, Kiểm hàng, Đang thanh toán, Hoàn thành

  Boolean taxable;

  String note;

  List<InboundDetails> inboundDetails;
}
