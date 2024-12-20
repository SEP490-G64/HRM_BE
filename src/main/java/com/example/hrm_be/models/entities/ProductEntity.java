package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.math.BigDecimal;
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
@Table(name = "product")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductEntity extends CommonEntity {

  @Column(name = "product_name", length = 50)
  String productName;

  @Column(name = "registration_code", length = 30)
  String registrationCode;

  @Column(name = "url_image", length = 255, nullable = true)
  String urlImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manufacturer_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ManufacturerEntity manufacturer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductCategoryEntity category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductTypeEntity type;

  @Column(name = "active_ingredient", length = 255, nullable = true)
  String activeIngredient;

  @Column(name = "excipient", length = 255, nullable = true)
  String excipient;

  @Column(name = "formulation", length = 255, nullable = true)
  String formulation;

  @Column(name = "inbound_price")
  BigDecimal inboundPrice;

  @Column(name = "sell_price")
  BigDecimal sellPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  ProductStatus status; // Enum for "Còn hàng", "Hết hàng", "Ngừng kinh doanh"

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "base_unit_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UnitOfMeasurementEntity baseUnit;

  // One-to-Many Relationships
  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<InboundDetailsEntity> inboundDetails; // 1-N with InboundDetails

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<SpecialConditionEntity> specialConditions; // 1-N with SpecialCondition

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<UnitConversionEntity> unitConversions; // 1-N with UnitConversion

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<BatchEntity> batches;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<BranchProductEntity> branchProducs; // 1-N with Batch

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<OutboundProductDetailEntity> outboundProductDetails; // 1-N with Batch

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductSuppliersEntity> productSuppliers; // 1-N with ProductSuppliers

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  List<InventoryCheckProductDetailsEntity> inventoryCheckProductDetails;
}
