package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Notification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
  Notification getById(Long id);

  Page<Notification> getByPaging(int pageNo, int pageSize, String sortBy);

  Notification create(Notification notification);

  Notification update(Notification notification);

  void delete(Long id);
}
