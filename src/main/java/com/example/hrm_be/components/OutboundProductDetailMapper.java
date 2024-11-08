package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductDetailMapper {
  @Autowired @Lazy private OutboundMapper outboundMapper;
  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;

  // Convert OutboundDetailEntity to OutboundDetailDTO
  public OutboundProductDetail toDTO(OutboundProductDetailEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert OutboundDetailDTO to OutboundDetailEntity
  public OutboundProductDetailEntity toEntity(OutboundProductDetail dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                OutboundProductDetailEntity.builder()
                    .id(d.getId())
                    .outbound(
                        d.getOutbound() != null ? outboundMapper.toEntity(d.getOutbound()) : null)
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .unitOfMeasurement(
                        d.getTargetUnit() != null
                            ? unitOfMeasurementMapper.toEntity(d.getTargetUnit())
                            : null)
                    .outboundQuantity(d.getOutboundQuantity())
                    .price(d.getPrice())
                    .build())
        .orElse(null);
  }

  // Helper method to convert OutboundDetailEntity to OutboundDetailDTO
  private OutboundProductDetail convertToDTO(OutboundProductDetailEntity entity) {
    return OutboundProductDetail.builder()
        .id(entity.getId())
        .product(productMapper.toDTO(entity.getProduct()))
        .outboundQuantity(entity.getOutboundQuantity())
        .outbound(
            entity.getOutbound() != null
                ? outboundMapper.convertToDtoBasicInfo(entity.getOutbound())
                : null)
        .targetUnit(
            entity.getUnitOfMeasurement() != null
                ? unitOfMeasurementMapper.toDTO(entity.getUnitOfMeasurement())
                : null)
        .price(entity.getPrice())
        .build();
  }
}
