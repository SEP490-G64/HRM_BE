package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.requests.outboundDetails.OutboundDetailsCreateRequest;
import com.example.hrm_be.models.requests.outboundDetails.OutboundDetailsUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    return OutboundDetail.builder()
        .id(entity.getId())
        .outbound(entity.getOutbound() != null ? outboundMapper.toDTO(entity.getOutbound()) : null)
        .batch(entity.getBatch() != null ? batchMapper.toDTO(entity.getBatch()) : null)
        .quantity(entity.getQuantity())
        .build();
  }

  // Convert OutboundDetailCreateRequest to OutboundDetailEntity
  public OutboundDetailEntity toEntity(OutboundDetailsCreateRequest dto,
                                       OutboundEntity outbound, BatchEntity batch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create OutboundDetailEntity from OutboundDetailCreateRequest
                      return OutboundDetailEntity.builder()
                              .outbound(outbound)
                              .batch(batch)
                              .quantity(dto.getQuantity())
                              .build();
                    })
            .orElse(null);
  }

  // Convert OutboundDetailUpdateRequest to OutboundDetailEntity
  public OutboundDetailEntity toEntity(OutboundDetailsUpdateRequest dto, BatchEntity batch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create OutboundDetailEntity from OutboundDetailUpdateRequest
                      return OutboundDetailEntity.builder()
                              .batch(batch)
                              .quantity(dto.getQuantity())
                              .build();
                    })
            .orElse(null);
  }
}
