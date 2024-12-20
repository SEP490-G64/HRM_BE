package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.BatchStatus;
import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.models.entities.CommonEntity;
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
public class Batch extends CommonEntity {
  Long id;
  String batchCode;

  LocalDateTime produceDate;

  LocalDateTime expireDate;

  BatchStatus batchStatus;

  BigDecimal inboundPrice;
  LocalDateTime lastUpdated;

  List<OutboundDetail> outboundDetails; // 1-N with OutboundDetails

  List<BranchBatch> branchBatches; // 1-N with BranchBatch

  List<InboundBatchDetail> inboundBatchDetails;

  List<InventoryCheckDetails> inventoryCheckDetails;
  UnitOfMeasurement unitOfMeasurement;
  Product product;
  ProductBaseDTO productBaseDTO;

  Integer inboundBatchQuantity;
  Integer outBoundBatchQuantity;

  BigDecimal quantity;
  Long productId;
  String productName;
  String productCode;
  String registrationCode;
  String urlImage;
  String activeIngredient;
  String excipient;
  String formulation;
  BigDecimal sellPrice;
  BigDecimal productQuantity;
  ProductStatus status;
  String baseUnit;
  String categoryName;
  String typeName;
  String manufacturerName;
  BigDecimal preQuantity;
}
