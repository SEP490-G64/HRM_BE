package com.example.hrm_be.components;

import com.example.hrm_be.commons.enums.NotificationType;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.NotificationEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.notification.NotificationCreateRequest;
import com.example.hrm_be.models.requests.notification.NotificationUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationMapper {

  @Autowired @Lazy private BranchBatchMapper branchBatchMapper;

  // Convert NotificationEntity to NotificationDTO
  public Notification toDTO(NotificationEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert NotificationDTO to NotificationEntity
  public NotificationEntity toEntity(Notification dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                NotificationEntity.builder()
                    .notiType(d.getNotiType())
                    .notiName(d.getNotiName())
                    .message(d.getMessage())
                    .branchBatch(
                        d.getBranchBatch() != null
                            ? branchBatchMapper.toEntity(d.getBranchBatch())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert NotificationEntity to NotificationDTO
  private Notification convertToDTO(NotificationEntity entity) {
    return Notification.builder()
        .notiType(entity.getNotiType())
        .notiName(entity.getNotiName())
        .message(entity.getMessage())
        .branchBatch(
            entity.getBranchBatch() != null
                ? branchBatchMapper.toDTO(entity.getBranchBatch())
                : null)
        .build();
  }

  // Convert NotificationCreateRequest to NotificationEntity
  public NotificationEntity toEntity(NotificationCreateRequest dto, BranchBatchEntity branchBatch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create NotificationEntity from NotificationCreateRequest
                      return NotificationEntity.builder()
                              .notiType(NotificationType.valueOf(dto.getNotiType()))
                              .notiName(dto.getNotiName())
                              .message(dto.getMessage())
                              .branchBatch(branchBatch)
                              .build();
                    })
            .orElse(null);
  }

  // Convert NotificationUpdateRequest to NotificationEntity
  public NotificationEntity toEntity(NotificationUpdateRequest dto, BranchBatchEntity branchBatch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create NotificationEntity from NotificationUpdateRequest
                      return NotificationEntity.builder()
                              .notiType(NotificationType.valueOf(dto.getNotiType()))
                              .notiName(dto.getNotiName())
                              .message(dto.getMessage())
                              .branchBatch(branchBatch)
                              .build();
                    })
            .orElse(null);
  }
}
