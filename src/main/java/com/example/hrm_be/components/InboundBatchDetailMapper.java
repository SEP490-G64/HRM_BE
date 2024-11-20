package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.responses.AuditHistory;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class InboundBatchDetailMapper {

  @Autowired @Lazy private InboundMapper inboundMapper;
  @Autowired @Lazy private BatchMapper batchMapper;

  // Convert InboundBatchDetailEntity to InboundBatchDetailDTO
  public InboundBatchDetail toDTO(InboundBatchDetailEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InboundBatchDetailDTO to InboundBatchDetailEntity
  public InboundBatchDetailEntity toEntity(InboundBatchDetail dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InboundBatchDetailEntity.builder()
                    .id(d.getId())
                    .inbound(d.getInbound() != null ? inboundMapper.toEntity(d.getInbound()) : null)
                    .batch(d.getBatch() != null ? batchMapper.toEntity(d.getBatch()) : null)
                    .quantity(d.getQuantity())
                    .inboundPrice(d.getInboundPrice())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InboundBatchDetailEntity to InboundBatchDetailDTO
  private InboundBatchDetail convertToDTO(InboundBatchDetailEntity entity) {
    return InboundBatchDetail.builder()
        .id(entity.getId())
        .batch(
            entity.getBatch() != null ? batchMapper.convertToDtoBasicInfo(entity.getBatch()) : null)
        .quantity(entity.getQuantity())
        .build();
  }

  public InboundBatchDetail convertToDTOWithBatchAndInbound(InboundBatchDetailEntity entity) {
    return InboundBatchDetail.builder()
        .id(entity.getId())
        .quantity(entity.getQuantity())
        .batch(batchMapper.convertToDtoBasicInfo(entity.getBatch()))
        .inbound(inboundMapper.convertToBasicInfo(entity.getInbound()))
        .build();
  }

  public AuditHistory toAudit(InboundBatchDetail dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                AuditHistory.builder()
                    .transactionType("INBOUND")
                    .transactionId(dto.getInbound().getId())
                    .productId(dto.getBatch().getProduct().getId())
                    .productName(dto.getBatch().getProduct().getProductName())
                    .quantity(BigDecimal.valueOf(dto.getQuantity()))
                    .batch(dto.getBatch().getBatchCode()) // No batch details for InboundDetails
                    .createdAt(dto.getInbound().getCreatedDate())
                    .build())
        .orElse(null);
  }
}
