package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.NotificationUser;
import com.example.hrm_be.models.entities.NotificationUserEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class NotificationUserMapper {
  @Lazy @Autowired private UserMapper userMapper;
  @Lazy @Autowired private NotificationMapper notificationMapper;

  public NotificationUser toDTO(NotificationUserEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                NotificationUser.builder()
                    .id(e.getId())
                    .user(userMapper.convertToDtoBasicInfo(e.getUser()))
                    .notification(notificationMapper.toDTO(e.getNotification()))
                    .read(e.getIsRead())
                    .build())
        .orElse(null);
  }

  public NotificationUserEntity toEntity(NotificationUser dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                NotificationUserEntity.builder()
                    .id(e.getId())
                    .user(userMapper.toEntity(e.getUser()))
                    .notification(notificationMapper.toEntity(e.getNotification()))
                    .isRead(e.getRead())
                    .build())
        .orElse(null);
  }
}
