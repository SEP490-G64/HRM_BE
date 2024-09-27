package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.entities.BatchEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BatchMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert BatchEntity to Batch DTO
  public Batch toDTO(BatchEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Batch DTO to BatchEntity
  public BatchEntity toEntity(Batch dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                BatchEntity.builder()
                    .id(e.getId())
                    .batchNumber(e.getBatchNumber())
                    .batchExpiredDate(e.getBatchExpiredDate())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BatchEntity to Batch DTO
  private Batch convertToDto(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchNumber(e.getBatchNumber())
                    .batchExpiredDate(e.getBatchExpiredDate())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
