package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.BatchStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
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
@Table(name = "batch")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BatchEntity extends CommonEntity {
  @Column(name = "batch_code")
  String batchCode;

  @Column(name = "produce_date")
  LocalDateTime produceDate;

  @Column(name = "expire_date")
  LocalDateTime expireDate;

  @Column(name = "inbound_price")
  BigDecimal inboundPrice;

  @Column(name = "status")
  BatchStatus batchStatus;

  @ToString.Exclude
  @OneToMany(mappedBy = "batch")
  List<OutboundDetailEntity> outboundDetails; // 1-N with OutboundDetails

  @ToString.Exclude
  @OneToMany(mappedBy = "batch")
  List<BranchBatchEntity> branchBatches; // 1-N with BranchBatch

  @ToString.Exclude
  @OneToMany(mappedBy = "batch")
  List<InboundBatchDetailEntity> inboundBatchDetail; // 1-N with InboundBatchDetail

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductEntity product;

  @ToString.Exclude
  @OneToMany(mappedBy = "batch")
  List<InventoryCheckDetailsEntity> inventoryCheckDetails; // 1-N with InventoryCheckDetails
}
