package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.commons.enums.InventoryCheckType;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventory_check")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class InventoryCheckEntity extends CommonEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity branch; // N-1 with Branch

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity createdBy; // N-1 with User (for the user who created the check)

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity approvedBy; // N-1 with User (for the user who approved the check)

  @Column(name = "created_date")
  LocalDateTime createdDate;

  @Column(name = "start_date")
  LocalDateTime startDate;

  @Column(name = "end_date")
  LocalDateTime endDate;

  @Column(name = "inventory_check_type")
  InventoryCheckType inventoryCheckType;

  @Column(name = "is_approved")
  Boolean isApproved;

  @Column(name = "inventory_check_code")
  String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  InventoryCheckStatus status; // Enum for 'Đang kiểm', 'Chờ duyệt', 'Đã cân bằng'

  @Column(name = "note")
  String note;

  @ToString.Exclude
  @OneToMany(mappedBy = "inventoryCheck")
  List<InventoryCheckDetailsEntity> inventoryCheckDetails;

  @ToString.Exclude
  @OneToMany(mappedBy = "inventoryCheck")
  List<InventoryCheckProductDetailsEntity> inventoryCheckProductDetails;
}
