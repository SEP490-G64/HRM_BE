package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notification")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class NotificationEntity extends CommonEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "noti_type", nullable = false)
  NotificationType notiType;  // Enum for notification types like "Gần hết hạn", "Hết hạn", etc.

  @Column(name = "noti_name", length = 200, nullable = false)
  String notiName;

  @Column(name = "message", columnDefinition = "TEXT", nullable = true)
  String message;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branchbatch_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchBatchEntity branchBatch;
}
