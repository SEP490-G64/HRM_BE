package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class BranchBatchMapper {
  @Autowired
  @Lazy
  private BatchMapper batchMapper;
  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private NotificationMapper notificationMapper;

  // Convert BranchBatchEntity to BranchBatchDTO
  public BranchBatch toDTO(BranchBatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDto)
        .orElse(null);
  }

  // Convert BranchBatchDTO to BranchBatchEntity
  public BranchBatchEntity toEntity(BranchBatch dto) {
    return Optional.ofNullable(dto)
        .map(e -> BranchBatchEntity.builder()
            .id(e.getId())
            .batch(
                e.getBatch() != null
                    ? batchMapper.toEntity(e.getBatch())
                    : null)
            .branch(
                e.getBranch() != null
                    ? branchMapper.toEntity(e.getBranch())
                    : null)
            .quantity(e.getQuantity())
            .notifications(
                e.getNotifications() != null
                    ? e.getNotifications().stream()
                    .map(notificationMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .build())
        .orElse(null);
  }

  // Helper method to map BranchBatchEntity to BranchBatchDTO
  private BranchBatch convertToDto(BranchBatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(e -> BranchBatch.builder()
            .id(e.getId())
            .batch(
                e.getBatch() != null
                    ? batchMapper.toDTO(e.getBatch())
                    : null)
            .branch(
                e.getBranch() != null
                    ? branchMapper.toDTO(e.getBranch())
                    : null)
            .quantity(e.getQuantity())
            .notifications(
                e.getNotifications() != null
                    ? e.getNotifications().stream()
                    .map(notificationMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .build())
        .orElse(null);
  }
}
