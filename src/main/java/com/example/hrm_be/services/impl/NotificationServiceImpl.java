package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.NotificationType;
import com.example.hrm_be.components.NotificationMapper;
import com.example.hrm_be.components.NotificationUserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Notice;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.dtos.NotificationUser;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.NotificationEntity;
import com.example.hrm_be.models.entities.NotificationUserEntity;
import com.example.hrm_be.models.responses.NotificationAlertResponse;
import com.example.hrm_be.repositories.NotificationRepository;
import com.example.hrm_be.repositories.NotificationUserRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.BranchProductService;
import com.example.hrm_be.services.NotificationService;
import com.example.hrm_be.services.UserService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final Map<Long, Many<NotificationUser>> userNotificationSinks = new ConcurrentHashMap<>();
  @Autowired private final FirebaseMessaging firebaseMessaging;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private NotificationUserRepository notificationUserRepository;
  @Autowired private NotificationMapper notificationMapper;
  @Autowired private NotificationUserMapper notificationUserMapper;
  @Autowired private UserService userService;
  @Autowired private BatchService batchService;
  @Autowired private BranchProductService branchProductService;

  public NotificationServiceImpl(FirebaseMessaging firebaseMessaging) {
    this.firebaseMessaging = firebaseMessaging;
  }

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
      throw new HrmCommonException(
          HrmConstant.ERROR.NOTIFICATION.NOT_EXIST); // Throw exception if not found
    }

    // Update the notification properties and save the updated entity
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
    // Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if ID is invalid
    }

    // Fetch the existing notification entity by ID
    NotificationEntity oldNotificationDetailEntity =
        notificationRepository.findById(id).orElse(null);
    if (oldNotificationDetailEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.NOTIFICATION.NOT_EXIST); // Throw exception if not found
    }

    // Delete the notification entity
    notificationRepository.deleteById(id);
  }

  @Override
  public void sendNotification(Notification notification, List<User> recipients) {
    notification.setCreatedDate(LocalDateTime.now());
    Notification saved = create(notification);

    List<String> listDeviceToken = new ArrayList<>();
    recipients.forEach(
        user -> {
          NotificationUser notificationRecipient = new NotificationUser();
          notificationRecipient.setNotification(saved);
          notificationRecipient.setCreatedDate(saved.getCreatedDate());
          notificationRecipient.setUser(user);
          notificationRecipient.setRead(false);
          if (user.getFirebaseToken() != null) {
            listDeviceToken.add(user.getFirebaseToken().getDeviceToken());
          }
          NotificationUserEntity ne =
              notificationUserRepository.save(
                  notificationUserMapper.toEntity(notificationRecipient));
        });

    Map<String, String> dataAttributes = new HashMap<>();
    dataAttributes.put("notificationId", String.valueOf(saved.getId()));
    dataAttributes.put("notiType", String.valueOf(saved.getNotiType()));
    Notice notice =
        Notice.builder()
            .subject(notification.getNotiName())
            .registrationTokens(listDeviceToken)
            .data(dataAttributes)
            .content(notification.getMessage())
            .build();
    if (!listDeviceToken.isEmpty()) {
      sendNotification(notice);
    }
  }

  @Override
  public void sendExpirationNotification(Branch branch, Product product) {
    // Logic gửi thông báo về sản phẩm hết hạn
    String message =
        "🔔 Thông báo hết hạn: Sản phẩm "
            + product.getProductName()
            + " tại chi "
            + "nhánh "
            + branch.getBranchName();

    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setCreatedDate(LocalDateTime.now());

    List<User> users = userService.getUserByBranchId(branch.getId());
    sendNotification(notification, users);
  }

  @Override
  public void sendQuantityNotification(Branch branch, Product product, int quantity, String type) {
    String message;
    if ("UNDER_MIN".equals(type)) {
      message =
          "Sản phẩm "
              + product.getProductName()
              + " tại chi nhánh "
              + branch.getBranchName()
              + " dưới ngưỡng tối thiểu. Số lượng: "
              + quantity;

    } else if ("OVER_MAX".equals(type)) {
      message =
          "Sản phẩm "
              + product.getProductName()
              + " tại chi nhánh "
              + branch.getBranchName()
              + " vượt ngưỡng tối đa. Số lượng: "
              + quantity;
    } else {
      return;
    }
    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setCreatedDate(LocalDateTime.now());

    List<User> users = userService.getUserByBranchId(branch.getId());
    sendNotification(notification, users);
  }

  public List<NotificationUser> getAllNotificationsForUser(Long userId) {
    return notificationUserRepository.findByUser_Id(userId).stream()
        .sorted(
            (n1, n2) ->
                n2.getNotification()
                    .getCreatedDate()
                    .compareTo(n1.getNotification().getCreatedDate()))
        .map(notificationUserMapper::toDTO)
        .collect(Collectors.toList());
  }

  public List<NotificationUser> getUnreadNotificationsForUser(Long userId) {
    return notificationUserRepository.findByUser_IdAndIsReadIsFalse(userId).stream()
        .map(notificationUserMapper::toDTO)
        .collect(Collectors.toList());
  }

  public void markNotificationAsRead(Long userId, Long notificationId) {
    NotificationUserEntity recipient =
        notificationUserRepository
            .findByNotification_IdAndUser_Id(notificationId, userId)
            .orElseThrow(() -> new NoSuchElementException("NotificationRecipient not found"));

    recipient.setIsRead(Boolean.TRUE);
    notificationUserRepository.save(recipient);
  }

  @Override
  public Integer getUnreadNotificationQuantity(Long userId) {
    return notificationUserRepository.countByUser_IdAndIsReadFalse(userId);
  }

  public Flux<NotificationUser> streamNotificationsForUser(Long userId) {
    Sinks.Many<NotificationUser> sink =
        userNotificationSinks.computeIfAbsent(
            userId, id -> Sinks.many().multicast().onBackpressureBuffer());
    return sink.asFlux();
  }

  public NotificationAlertResponse createAlertProductNotification(Long branchId) {
    int nearlyExpiredCount = batchService.getExpiredBatches(LocalDateTime.now()).size();
    int expiredCount = batchService.getExpiredBatchesInDays(LocalDateTime.now(), 30l).size(); //
    // Assuming zero
    // quantity represents expired
    int underThresholdCount =
        branchProductService.findBranchProductsWithQuantityBelowMin(branchId).size();
    int upperThresholdCount =
        branchProductService.findBranchProductsWithQuantityAboveMax(branchId).size();
    int outOfStockCount =
        branchProductService.findBranchProductsWithQuantityIsZero(branchId).size();

    // Create the response object with count and URL for each alert type
    NotificationAlertResponse alertResponse =
        new NotificationAlertResponse(
            nearlyExpiredCount,
            expiredCount,
            underThresholdCount,
            upperThresholdCount,
            outOfStockCount);
    String message = alertResponse.toString();
    Notification notification = new Notification();
    notification.setNotiName(NotificationType.CANH_BAO_SAN_PHAM.getDisplayName());
    notification.setNotiType(NotificationType.CANH_BAO_SAN_PHAM);
    notification.setMessage(message);
    notification.setCreatedDate(LocalDateTime.now());

    List<User> users = userService.getUserByBranchId(branchId);
    sendNotification(notification, users);
    return alertResponse;
  }

  public BatchResponse sendNotification(Notice notice) {
    List<String> registrationTokens = notice.getRegistrationTokens();
    com.google.firebase.messaging.Notification notification =
        com.google.firebase.messaging.Notification.builder()
            .setTitle(notice.getSubject())
            .setBody(notice.getContent())
            .setImage(notice.getImage() != null ? notice.getImage() : null)
            .build();

    MulticastMessage message =
        MulticastMessage.builder()
            .addAllTokens(registrationTokens)
            .setNotification(notification)
            .putAllData(notice.getData() != null ? notice.getData() : null)
            .build();

    BatchResponse batchResponse = null;
    try {
      batchResponse = firebaseMessaging.sendEachForMulticast(message);
    } catch (FirebaseMessagingException e) {
      log.info("Firebase error {}", e.getMessage());
    }
    if (batchResponse.getFailureCount() > 0) {
      List<SendResponse> responses = batchResponse.getResponses();
      List<String> failedTokens = new ArrayList<>();
      for (int i = 0; i < responses.size(); i++) {
        if (!responses.get(i).isSuccessful()) {
          failedTokens.add(registrationTokens.get(i));
        }
      }
      log.info("List of tokens that caused failures: " + failedTokens);
    }
    return batchResponse;
  }
}
