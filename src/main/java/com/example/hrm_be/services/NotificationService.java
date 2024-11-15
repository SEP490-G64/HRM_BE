package com.example.hrm_be.services;

import com.example.hrm_be.components.NotificationUserMapper;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.dtos.NotificationUser;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.responses.NotificationAlertResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public interface NotificationService {
  Notification getById(Long id);

  Page<Notification> getByPaging(int pageNo, int pageSize, String sortBy);

  Notification create(Notification notification);

  Notification update(Notification notification);

  void delete(Long id);

  void sendNotification(Notification notification, List<User> recipients);

  List<NotificationUser> getAllNotificationsForUser(Long userId);

  List<NotificationUser> getUnreadNotificationsForUser(Long userId);

  void markNotificationAsRead(Long userId, Long notificationId);

  Integer getUnreadNotificationQuantity(Long userId);

  Flux<NotificationUser> streamNotificationsForUser(Long userId);

  void sendExpirationNotification(Branch branch, Product product);
  void sendQuantityNotification(Branch branch, Product product, int quantity, String type);

  NotificationAlertResponse createAlertProductNotification(Long branchId);
}
