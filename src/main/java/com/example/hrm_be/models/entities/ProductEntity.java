package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.ProductStatus;
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

  @Column(name = "product_name", length = 50, nullable = false)
  String productName;

  @Column(name = "product_code", length = 30, nullable = false)
  String productCode;

  @Column(name = "registration_code", length = 30, nullable = false)
  String registrationCode;

  @Column(name = "url_image", length = 255, nullable = true)
  String urlImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manufacturer_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ManufacturerEntity manufacturer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductCategoryEntity category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  ProductTypeEntity type;

  @Column(name = "active_ingredient", length = 255, nullable = true)
  String activeIngredient;

  @Column(name = "excipient", length = 255, nullable = true)
  String excipient;

  @Column(name = "formulation", length = 255, nullable = true)
  String formulation;

  @Column(name = "sell_price", precision = 5, scale = 2, nullable = false)
  BigDecimal sellPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  ProductStatus status; // Enum for "Còn hàng", "Hết hàng", "Ngừng kinh doanh"

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "base_unit_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  UnitOfMeasurementEntity baseUnit;

  // One-to-Many Relationships
  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InboundDetailsEntity> inboundDetails; // 1-N with InboundDetails

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<SpecialConditionEntity> specialConditions; // 1-N with SpecialCondition

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<BatchEntity> batches;

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<BranchProductEntity> branchProducs; // 1-N with Batch

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InventoryCheckDetailsEntity> inventoryCheckDetails; // 1-N with InventoryCheckDetails

  @ToString.Exclude
  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<ProductSuppliersEntity> productSuppliers; // 1-N with ProductSuppliers
}
