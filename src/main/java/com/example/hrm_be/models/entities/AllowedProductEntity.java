package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "allowed-product")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AllowedProductEntity extends CommonEntity {
  @Column(name = "product_name", length = 50, nullable = false)
  String productName;

  @Column(name = "product_code", length = 30, nullable = false)
  String productCode;

  @Column(name = "registration_code", length = 30, nullable = false)
  String registrationCode;

  @Column(name = "url_image", length = 255, nullable = true)
  String urlImage;

  @Column(name = "active_ingredient", length = 255, nullable = true)
  String activeIngredient;

  @Column(name = "excipient", length = 255, nullable = true)
  String excipient;

  @Column(name = "formulation", length = 255, nullable = true)
  String formulation;
}
