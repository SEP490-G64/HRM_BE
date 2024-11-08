package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.entities.NotificationEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
                    .id(d.getId())
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
        .id(entity.getId())
        .notiType(entity.getNotiType())
        .notiName(entity.getNotiName())
        .message(entity.getMessage())
        .branchBatch(
            entity.getBranchBatch() != null
                ? branchBatchMapper.toDTO(entity.getBranchBatch())
                : null)
        .build();
  }
}
