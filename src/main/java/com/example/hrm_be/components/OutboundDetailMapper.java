package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OutboundDetailMapper {

  @Autowired @Lazy private OutboundMapper outboundMapper;
  @Autowired @Lazy private BatchMapper batchMapper;

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
                    .quantity(d.getQuantity())
                    .build())
        .orElse(null);
  }

  // Helper method to convert OutboundDetailEntity to OutboundDetailDTO
  private OutboundDetail convertToDTO(OutboundDetailEntity entity) {
    return OutboundDetail.builder().id(entity.getId()).quantity(entity.getQuantity()).build();
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
}
