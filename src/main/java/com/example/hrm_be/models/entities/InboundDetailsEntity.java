package com.example.hrm_be.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
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
@Table(name = "inbound_details")
public class InboundDetailsEntity extends CommonEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inbound_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  InboundEntity inbound;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductEntity product;

  @Column(name = "request_quantity")
  Integer requestQuantity;

  @Column(name = "receive_quantity")
  Integer receiveQuantity;

  @Column(name = "discount")
  Double discount;
}
