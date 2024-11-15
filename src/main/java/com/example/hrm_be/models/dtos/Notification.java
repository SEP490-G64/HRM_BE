package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {
  Long id;
  NotificationType notiType; // Enum for notification types like "Gần hết hạn", "Hết hạn", etc.

  String notiName;

  LocalDateTime createdDate;

  String message;

  BranchBatch branchBatch;

  Boolean isRead;

  List<NotificationUser> notificationUser;

  List<Long> userIds;
}
