package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.requests.notification.NotificationCreateRequest;
import com.example.hrm_be.models.requests.notification.NotificationUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    Notification getById(Long id);

    Page<Notification> getByPaging(int pageNo, int pageSize, String sortBy);

    Notification create(NotificationCreateRequest notification);

    Notification update(NotificationUpdateRequest notification);

    void delete(Long id);
}
