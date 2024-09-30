package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.InventoryStatus;
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

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventory")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class InventoryEntity extends CommonEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  BranchEntity branch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  ProductEntity product;

  @Column(name = "quantity")
  Integer quantity;

  @Column(name = "storage_condition", length = 255)
  String storageCondition;

  @Column(name = "last_updated")
  LocalDateTime lastUpdated;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  InventoryStatus status;

  @Column(name = "location", length = 45)
  String location;
}
