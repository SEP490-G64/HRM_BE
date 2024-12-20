package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "outbound_product_details")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OutboundProductDetailEntity extends CommonEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "outbound_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  OutboundEntity outbound;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductEntity product;

  @Column(name = "outbound_quantity")
  BigDecimal outboundQuantity;

  @Column(name = "price")
  BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "unit_of_measurement_id",
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UnitOfMeasurementEntity unitOfMeasurement;

  @Column(name = "tax_rate")
  BigDecimal taxRate;

  @Column(name = "pre_quantity")
  BigDecimal preQuantity;
}
