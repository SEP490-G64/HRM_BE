package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
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
  @Column(name = "batch_code", length = 30, nullable = false)
  String batchCode;

  @Column(name = "produce_date", nullable = false)
  LocalDateTime produceDate;

  @Column(name = "expire_date", nullable = false)
  LocalDateTime expireDate;

  @Column(name = "inbound_price", nullable = false)
  BigDecimal inboundPrice;

  @ToString.Exclude
  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<UnitConversionEntity> unitConversions; // 1-N with UnitConversion

  @ToString.Exclude
  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<OutboundDetailEntity> outboundDetails; // 1-N with OutboundDetails

  @ToString.Exclude
  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<BranchBatchEntity> branchBatches; // 1-N with BranchBatch

  @ToString.Exclude
  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InboundBatchDetailEntity> inboundBatchDetail; // 1-N with InboundBatchDetail

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductEntity product;

  @ToString.Exclude
  @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InventoryCheckDetailsEntity> inventoryCheckDetails; // 1-N with InventoryCheckDetails
}
