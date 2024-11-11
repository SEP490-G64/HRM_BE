package com.example.hrm_be.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "category")
public class ProductCategoryEntity extends CommonEntity {
  @Column(name = "category_name")
  String categoryName;

  @Column(name = "category_description", length = 1000)
  String categoryDescription;

  @Column(name = "tax_rate")
  BigDecimal taxRate;

  @ToString.Exclude
  @OneToMany(mappedBy = "category")
  List<ProductEntity> products;
}
