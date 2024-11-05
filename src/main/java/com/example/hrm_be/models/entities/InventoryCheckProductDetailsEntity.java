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
@Table(name = "inventory_check_product")
public class InventoryCheckProductDetailsEntity extends CommonEntity{
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "check_id",
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  InventoryCheckEntity inventoryCheck; // N-1 with InventoryCheckEntity

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_id",
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductEntity product; // N-1 with ProductEntity

  @Column(name = "system_quantity")
  Integer systemQuantity;

  @Column(name = "counted_quantity")
  Integer countedQuantity;

  @Column(name = "difference")
  Integer difference;

  @Column(name = "reason")
  String reason;
}
