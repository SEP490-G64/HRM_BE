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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  @Column(name = "name")
  String name;

  @Column(name = "price")
  Double price;

  @Column(name = "description")
  String description;

  @Column(name = "image")
  String image;

  @Column(name = "barcode_image")
  String barcodeImage;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  BranchEntity branch;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "batch_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  BatchEntity batch;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tax_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  TaxEntity tax;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supplier_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  SupplierEntity supplier;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductUnitMapEntity> productUnitMap;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductIngredientMapEntity> productIngredientMap;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductCategoryMapEntity> productCateMap;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<InventoryEntity> inventoryEntities;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "manufacturer_id",
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  ManufacturerEntity manufacturer;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "special_condition_id",
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  SpecialConditionEntity specialCondition;
}
