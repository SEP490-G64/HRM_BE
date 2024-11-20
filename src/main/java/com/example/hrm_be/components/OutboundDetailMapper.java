package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.responses.AuditHistory;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OutboundDetailMapper {

  @Autowired @Lazy private OutboundMapper outboundMapper;
  @Autowired @Lazy private BatchMapper batchMapper;
  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;

  // Convert OutboundDetailEntity to OutboundDetailDTO
  public OutboundDetail toDTO(OutboundDetailEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert OutboundDetailDTO to OutboundDetailEntity
  public OutboundDetailEntity toEntity(OutboundDetail dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                OutboundDetailEntity.builder()
                    .id(d.getId())
                    .outbound(
                        d.getOutbound() != null ? outboundMapper.toEntity(d.getOutbound()) : null)
                    .batch(d.getBatch() != null ? batchMapper.toEntity(d.getBatch()) : null)
                    .unitOfMeasurement(
                        d.getUnitOfMeasurement() != null
                            ? unitOfMeasurementMapper.toEntity(d.getUnitOfMeasurement())
                            : null)
                    .quantity(d.getQuantity())
                    .price(d.getPrice())
                    .build())
        .orElse(null);
  }

  // Helper method to convert OutboundDetailEntity to OutboundDetailDTO
  private OutboundDetail convertToDTO(OutboundDetailEntity entity) {
    return OutboundDetail.builder()
        .id(entity.getId())
        .quantity(entity.getQuantity())
        .price(entity.getPrice())
        .build();
  }

  // Helper method to convert OutboundDetailEntity to OutboundDetailDTO with Outbound Information
  public OutboundDetail toDTOWithOutBoundDetails(OutboundDetailEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .map(
            e ->
                e.toBuilder()
                    .outbound(
                        entity.getOutbound() != null
                            ? outboundMapper.toDTO(entity.getOutbound())
                            : null)
                    .build())
        .orElse(null);
  }

  public OutboundDetail toDTOWithBatch(OutboundDetailEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .map(
            e ->
                e.toBuilder()
                    .batch(
                        entity.getBatch() != null
                            ? batchMapper.convertToDtoBasicInfo(entity.getBatch())
                            : null)
                    .build())
        .orElse(null);
  }

  public OutboundDetail toDTOWithBatchAndOutBound(OutboundDetailEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .map(
            e ->
                e.toBuilder()
                    .outbound(
                        entity.getOutbound() != null
                            ? outboundMapper.toDTO(entity.getOutbound())
                            : null)
                    .batch(
                        entity.getBatch() != null
                            ? batchMapper.convertToDtoBasicInfo(entity.getBatch())
                            : null)
                    .build())
        .orElse(null);
  }

  public OutboundDetail toDTOWithProductAndCategory(OutboundDetailEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .map(
            e ->
                e.toBuilder()
                    .batch(
                        entity.getBatch() != null
                            ? batchMapper.convertToDtoWithCategory(entity.getBatch())
                            : null)
                    .outbound(
                        entity.getOutbound() != null
                            ? outboundMapper.toDTO(entity.getOutbound())
                            : null)
                    .unitOfMeasurement(
                        entity.getUnitOfMeasurement() != null
                            ? unitOfMeasurementMapper.toDTO(entity.getUnitOfMeasurement())
                            : null)
                    .build())
        .orElse(null);
  }

  public AuditHistory toAudit(OutboundDetail dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                AuditHistory.builder()
                    .transactionType("OUTBOUND")
                    .transactionId(dto.getOutbound().getId())
                    .productId(dto.getBatch().getProduct().getId())
                    .productName(dto.getBatch().getProduct().getProductName())
                    .quantity(dto.getQuantity())
                    .batch(dto.getBatch().getBatchCode()) // No batch details for InboundDetails
                    .createdAt(dto.getOutbound().getCreatedDate())
                    .build())
        .orElse(null);
  }
}
