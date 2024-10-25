package com.example.hrm_be.models.responses;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.InboundProductDetailDTO;
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
public class InboundDetail {
  private Long id;
  private InboundType inboundType;
  private String inboundCode;
  private Branch fromBranch;
  private Branch toBranch;
  private Supplier supplier;
  private User createdBy;
  private User approvedBy;
  private LocalDateTime inboundDate;
  private LocalDateTime createdDate;
  private BigDecimal totalPrice;
  private Boolean isApproved;
  private InboundStatus status;
  private List<InboundProductDetailDTO> productBatchDetails;
}
