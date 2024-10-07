package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.NotificationType;
import com.example.hrm_be.components.NotificationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.notification.NotificationCreateRequest;
import com.example.hrm_be.models.requests.notification.NotificationUpdateRequest;
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
  public Notification create(NotificationCreateRequest notification) {
    if (notification == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    BranchBatchEntity branchBatch;
    if (notification.getBranchBatchId() != null) {
      branchBatch =
          entityManager.getReference(BranchBatchEntity.class, notification.getBranchBatchId());
      if (branchBatch == null) {
        throw new HrmCommonException(
            "Branch Batch not found with id: " + notification.getBranchBatchId());
      }
    } else {
      branchBatch = null;
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(notification)
        .map(e -> notificationMapper.toEntity(e, branchBatch))
        .map(e -> notificationRepository.save(e))
        .map(e -> notificationMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Notification update(NotificationUpdateRequest notification) {
    NotificationEntity oldNotificationDetailEntity =
        notificationRepository.findById(notification.getId()).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    return Optional.ofNullable(oldNotificationDetailEntity)
        .map(
            op ->
                op.toBuilder()
                    .notiName(notification.getNotiName())
                    .notiType(NotificationType.valueOf(notification.getNotiType()))
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

    notificationRepository.deleteById(id);
  }
}
