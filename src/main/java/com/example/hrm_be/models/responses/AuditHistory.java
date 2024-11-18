package com.example.hrm_be.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class AuditHistory {
  String transactionType; // "INBOUND" or "OUTBOUND"
  Long transactionId;
  Long productId;
  String productName;
  BigDecimal quantity;
  String batch; // Batch details
  LocalDateTime createdAt; // Timestamp of the transaction
}
