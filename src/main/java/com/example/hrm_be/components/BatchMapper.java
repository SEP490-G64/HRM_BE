package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.entities.BatchEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class BatchMapper {

  @Autowired @Lazy private UnitConversionMapper unitConversionMapper;
  @Autowired @Lazy private OutboundDetailMapper outboundDetailMapper;
  @Autowired @Lazy private BranchBatchMapper branchBatchMapper;
  @Autowired @Lazy private InboundBatchDetailMapper inboundBatchDetailMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert BatchEntity to BatchDTO
  public Batch toDTO(BatchEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert BatchDTO to BatchEntity
  public BatchEntity toEntity(Batch dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                BatchEntity.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .produceDate(e.getProduceDate())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .outboundDetails(
                        e.getOutboundDetails() != null
                            ? e.getOutboundDetails().stream()
                                .map(outboundDetailMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .branchBatches(
                        e.getBranchBatches() != null
                            ? e.getBranchBatches().stream()
                                .map(branchBatchMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .inboundBatchDetail(
                        e.getInboundBatchDetails() != null
                            ? e.getInboundBatchDetails().stream()
                                .map(inboundBatchDetailMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .product(e.getProduct() != null ? productMapper.toEntity(e.getProduct()) : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BatchEntity to BatchDTO
  private Batch convertToDto(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .produceDate(e.getProduceDate())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .outboundDetails(
                        e.getOutboundDetails() != null
                            ? e.getOutboundDetails().stream()
                                .map(outboundDetailMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .inboundBatchDetails(
                        e.getInboundBatchDetail() != null
                            ? e.getInboundBatchDetail().stream()
                                .map(inboundBatchDetailMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .product(e.getProduct() != null ? productMapper.toDTO(e.getProduct()) : null)
                    .build())
        .orElse(null);
  }
}
