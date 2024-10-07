package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inbound")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class InboundEntity extends CommonEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "inbound_type", nullable = false)
  InboundType inboundType; // Custom enum representing: Nhà cung cấp, Chuyển kho nội bộ

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "from_branch_id",
      nullable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity fromBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "to_branch_id",
      nullable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity toBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  SupplierEntity supplier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity createdBy;

  @Column(name = "created_date", nullable = false)
  LocalDateTime createdDate;

  @Column(name = "total_price", nullable = false)
  BigDecimal totalPrice;

  @Column(name = "is_approved", nullable = false)
  Boolean isApproved;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  InboundStatus status; // Custom enum: Chờ duyệt, Chờ hàng, Kiểm hàng, Đang thanh toán, Hoàn thành

  @Column(name = "taxable", nullable = false)
  Boolean taxable;

  @Column(name = "note")
  String note;

  @Column(name = "inbound_date")
  LocalDateTime inboundDate;

  @ToString.Exclude
  @OneToMany(mappedBy = "inbound", fetch = FetchType.LAZY)
  List<InboundDetailsEntity> inboundDetails;
}
