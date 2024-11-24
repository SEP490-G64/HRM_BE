package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.responses.AuditHistory;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class InboundDetailsMapper {

  @Autowired @Lazy private InboundMapper inboundMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert InboundDetailsEntity to InboundDetailsDTO
  public InboundDetails toDTO(InboundDetailsEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InboundDetailsDTO to InboundDetailsEntity
  public InboundDetailsEntity toEntity(InboundDetails dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InboundDetailsEntity.builder()
                    .id(d.getId())
                    .inbound(d.getInbound() != null ? inboundMapper.toEntity(d.getInbound()) : null)
                    .discount(d.getDiscount())
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .requestQuantity(d.getRequestQuantity())
                    .receiveQuantity(d.getReceiveQuantity())
                    .inboundPrice(d.getInboundPrice())
                    .taxRate(d.getTaxRate())
                    .build())
        .orElse(null);
  }

  // Convert InboundDetailsDTO to InboundDetailsEntity
  public AuditHistory toAudit(InboundDetails dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                AuditHistory.builder()
                    .transactionType("INBOUND")
                    .transactionId(dto.getInbound().getId())
                    .productId(dto.getProduct().getId())
                    .productName(dto.getProduct().getProductName())
                    .quantity(BigDecimal.valueOf(dto.getReceiveQuantity()))
                    .batch(null) // No batch details for InboundDetails
                    .createdAt(dto.getInbound().getCreatedDate())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InboundDetailsEntity to InboundDetailsDTO
  private InboundDetails convertToDTO(InboundDetailsEntity entity) {
    return InboundDetails.builder()
        .id(entity.getId())
        .product(
            entity.getProduct() != null
                ? productMapper.convertToDTOWithBatch(entity.getProduct())
                : null)
        .requestQuantity(entity.getRequestQuantity())
        .receiveQuantity(entity.getReceiveQuantity())
        .build();
  }

  // Helper method to convert InboundDetailEntity to InboundDetailDTO with Inbound Information
  public InboundDetails toDTOWithInBoundDetails(InboundDetailsEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .map(
            e ->
                e.toBuilder()
                    .product(
                        entity.getProduct() != null
                            ? productMapper.convertToDTOWithBatch(entity.getProduct())
                            : null)
                    .inbound(
                        entity.getInbound() != null
                            ? inboundMapper.toDTO(entity.getInbound())
                            : null)
                    .build())
        .orElse(null);
  }
}
