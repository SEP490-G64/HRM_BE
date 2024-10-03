package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.models.entities.CommonEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class InventoryCheck   {
  Long id;
  Branch branch; // N-1 with Branch


  User createdBy; // N-1 with User (for the user who created the check)


  User approvedBy; // N-1 with User (for the user who approved the check)

  LocalDateTime createdDate;

  Boolean isApproved;

  InventoryCheckStatus status; // Enum for 'Đang kiểm', 'Chờ duyệt', 'Đã cân bằng'

  String note;

  List<InventoryCheckDetails> inventoryCheckDetails;
}
