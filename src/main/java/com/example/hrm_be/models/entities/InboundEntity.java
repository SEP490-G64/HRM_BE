package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
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
  @Column(name = "inbound_type")
  InboundType inboundType; // Custom enum representing: Nhà cung cấp, Chuyển kho nội bộ

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "from_branch_id",
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity fromBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "to_branch_id",
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BranchEntity toBranch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  SupplierEntity supplier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity createdBy;

  @Column(name = "created_date")
  LocalDateTime createdDate;

  @Column(name = "inbound_code")
  String inboundCode;

  @Column(name = "inbound_date")
  LocalDateTime inboundDate;

  @Column(name = "total_price" )
  BigDecimal totalPrice;

  @Column(name = "is_approved")
  Boolean isApproved;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  InboundStatus status; // Custom enum: Chờ duyệt, Chờ hàng, Kiểm hàng, Đang thanh toán, Hoàn thành

  @Column(name = "taxable")
  Boolean taxable;

  @Column(name = "note")
  String note;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UserEntity approvedBy;

  @ToString.Exclude
  @OneToMany(mappedBy = "inbound")
  List<InboundDetailsEntity> inboundDetails;

  @ToString.Exclude
  @OneToMany(mappedBy = "inbound")
  List<InboundBatchDetailEntity> inboundBatchDetails;
}
