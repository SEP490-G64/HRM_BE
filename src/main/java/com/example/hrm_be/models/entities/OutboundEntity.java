package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
import java.math.BigDecimal;
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
@Table(name = "outbound")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OutboundEntity extends CommonEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "outbound_type", nullable = false)
  OutboundType outboundType; // Assume this is an Enum with values like "Bán hàng", "Trả hàng", etc.

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "from_branch_id",
      nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity fromBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "supplier_id",
      nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  SupplierEntity supplier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "to_branch_id",
      nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity toBranch;

  @Column(name = "created_date", nullable = false)
  LocalDateTime createdDate;

  @Column(name = "total_price", nullable = false)
  BigDecimal totalPrice;

  @Column(name = "is_approved", nullable = false)
  Boolean isApproved;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "approved_by",
      nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity approvedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  OutboundStatus status; // Assume this is an Enum for "Chờ duyệt", "Đang xử lý", etc.

  @Column(name = "taxable", nullable = true)
  Boolean taxable;

  @Column(name = "note", columnDefinition = "TEXT", nullable = true)
  String note;

  @Column(name = "outbound_date")
  LocalDateTime outboundDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "created_by",
      nullable = true,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity createdBy;

  @ToString.Exclude
  @OneToMany(mappedBy = "outbound", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<OutboundDetailEntity> outboundDetails;
}
