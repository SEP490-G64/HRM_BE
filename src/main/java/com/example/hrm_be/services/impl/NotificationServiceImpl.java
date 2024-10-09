package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.NotificationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.NotificationRepository;
import com.example.hrm_be.services.NotificationService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
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
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private NotificationMapper notificationMapper;
  @Autowired private EntityManager entityManager;

  @Override
  public Notification getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> notificationRepository.findById(e).map(b -> notificationMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Notification> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return notificationRepository.findAll(pageable).map(dao -> notificationMapper.toDTO(dao));
  }

  @Override
  public Notification create(Notification notification) {
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
    NotificationEntity oldNotificationDetailEntity =
        notificationRepository.findById(notification.getId()).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.NOTIFICATION.NOT_EXIST);
    }

    return Optional.ofNullable(oldNotificationDetailEntity)
        .map(
            op ->
                op.toBuilder()
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
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    NotificationEntity oldNotificationDetailEntity =
            notificationRepository.findById(id).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.NOTIFICATION.NOT_EXIST);
    }

    notificationRepository.deleteById(id);
  }
}
