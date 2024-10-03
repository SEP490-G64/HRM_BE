package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "manufacturer")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ManufacturerEntity extends CommonEntity {

  @Column(name = "manufacturer_name", nullable = false, length = 100)
  String manufacturerName;

  @Column(name = "address", nullable = false, length = 200)
  String address;

  @Column(name = "email", length = 100)
  String email;

  @Column(name = "phone_number", nullable = false, length = 11)
  String phoneNumber;

  @Column(name = "tax_code", length = 13)
  String taxCode;

  @Column(name = "origin", length = 20)
  String origin;

  @Column(name = "status", nullable = false)
  Boolean status;

  @ToString.Exclude
  @OneToMany(mappedBy = "manufacturer")
  List<ProductEntity> products;
}
