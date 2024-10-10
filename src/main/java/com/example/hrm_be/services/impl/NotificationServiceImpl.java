package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.NotificationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.entities.NotificationEntity;
import com.example.hrm_be.repositories.NotificationRepository;
import com.example.hrm_be.services.NotificationService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private NotificationMapper notificationMapper;

  @Override
  public Notification getById(Long id) {
    // Fetch notification by ID and map it to DTO
    return Optional.ofNullable(id)
            .flatMap(e -> notificationRepository.findById(e).map(b -> notificationMapper.toDTO(b)))
            .orElse(null);
  }

  @Override
  public Page<Notification> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create a pageable object for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Fetch paginated notifications and convert them to DTOs
    return notificationRepository.findAll(pageable).map(dao -> notificationMapper.toDTO(dao));
  }

  @Override
  public Notification create(Notification notification) {
    // Check if the notification is null and throw an exception if it is
    if (notification == null) {
      throw new HrmCommonException(HrmConstant.ERROR.NOTIFICATION.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(notification)
            .map(notificationMapper::toEntity)
            .map(e -> notificationRepository.save(e))
            .map(e -> notificationMapper.toDTO(e))
            .orElse(null);
  }

  @Override
  public Notification update(Notification notification) {
    // Fetch the existing notification entity by ID
    NotificationEntity oldNotificationDetailEntity =
            notificationRepository.findById(notification.getId()).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.NOTIFICATION.NOT_EXIST);  // Throw exception if not found
    }

    // Update the notification properties and save the updated entity
    return Optional.ofNullable(oldNotificationDetailEntity)
            .map(
                    op -> op.toBuilder()
                            .notiName(notification.getNotiName())
                            .notiType(notification.getNotiType())
                            .message(notification.getMessage())
                            .build())
            .map(notificationRepository::save)
            .map(notificationMapper::toDTO)
            .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return;  // Exit if ID is invalid
    }

    // Fetch the existing notification entity by ID
    NotificationEntity oldNotificationDetailEntity =
        notificationRepository.findById(id).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.NOTIFICATION.NOT_EXIST);  // Throw exception if not found
    }

    // Delete the notification entity
    notificationRepository.deleteById(id);
  }
}
