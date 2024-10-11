package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                    .inbound(d.getInbound() != null ? inboundMapper.toEntity(d.getInbound()) : null)
                    .batch(d.getBatch() != null ? batchMapper.toEntity(d.getBatch()) : null)
                    .quantity(d.getQuantity())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InboundBatchDetailEntity to InboundBatchDetailDTO
  private InboundBatchDetail convertToDTO(InboundBatchDetailEntity entity) {
    return InboundBatchDetail.builder()
        .inbound(entity.getInbound() != null ? inboundMapper.toDTO(entity.getInbound()) : null)
        .batch(entity.getBatch() != null ? batchMapper.toDTO(entity.getBatch()) : null)
        .quantity(entity.getQuantity())
        .build();
  }
}
