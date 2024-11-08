package com.example.hrm_be.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Supplier {
  Long id;
  String supplierName;

  String address;

  String email;

  String phoneNumber;

  String taxCode;

  String faxNumber;

  Boolean status;

  List<Purchase> purchases; // 1-N with PurchaseEntity

  List<Inbound> inbounds; // 1-N with InboundEntity

  List<Outbound> outbounds; // 1-N with OutboundEntity

  List<ProductSuppliers> productSuppliers;
}
