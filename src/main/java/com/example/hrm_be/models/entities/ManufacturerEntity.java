package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

  @Column(name = "manufacturer_name", length = 255)
  String manufacturerName;

  @Column(name = "contact_person", length = 255)
  String contactPerson;

  @Column(name = "phone_number", length = 50)
  String phoneNumber;

  @Column(name = "email", length = 255)
  String email;

  @Column(name = "address", length = 255)
  String address;

  @Column(name = "origin", length = 45)
  String origin;

  @ToString.Exclude
  @OneToMany(mappedBy = "manufacturer")
  List<ProductEntity> products;
}
