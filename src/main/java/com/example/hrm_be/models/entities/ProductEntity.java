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
import org.hibernate.engine.jdbc.batch.spi.Batch;

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

  @Column(name = "description")
  String description;

  @Column(name = "image")
  String image;

  @Column(name = "barcode_image")
  String barcodeImage;

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
  @JoinColumn(name = "sup_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  SupplierEntity supplier;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "o_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  OriginEntity origin;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductUnitMapEntity> userUnitMap;
 @ToString.Exclude

  @OneToMany(mappedBy = "product")
  List<ProductIngredientMapEntity> productIngredientMap;

  @ToString.Exclude
  @OneToMany(mappedBy = "product")
  List<ProductCategoryMapEntity> userCateMap;
}
