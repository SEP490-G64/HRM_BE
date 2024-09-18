package com.example.hrm_be.models.entities;

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
@Table(name = "suppliers")
public class SupplierEntity extends CommonEntity {
  @Column(name = "name")
  String name;

  @Column(name = "address")
  String address;

  @Column(name = "email")
  String email;

  @Column(name = "country")
  String country;

  @Column(name = "contact_person")
  String contactPerson;
}
