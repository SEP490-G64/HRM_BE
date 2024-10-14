package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "supplier")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SupplierEntity extends CommonEntity {

  @Column(name = "supplier_name", nullable = false, length = 100)
  String supplierName;

  @Column(name = "address", nullable = false, length = 200)
  String address;

  @Column(name = "email", length = 100)
  String email;

  @Column(name = "phone_number", nullable = false, length = 11)
  String phoneNumber;

  @Column(name = "tax_code", length = 14)
  String taxCode;

  @Column(name = "fax_number", length = 20)
  String faxNumber;

  @Column(name = "status", nullable = false)
  Boolean status;

  // One-to-Many relationships
  @ToString.Exclude
  @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<PurchaseEntity> purchases; // 1-N with PurchaseEntity

  @ToString.Exclude
  @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InboundEntity> inbounds; // 1-N with InboundEntity

  @ToString.Exclude
  @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<OutboundEntity> outbounds; // 1-N with OutboundEntity

  @ToString.Exclude
  @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<ProductSuppliersEntity> productSuppliers;
}
