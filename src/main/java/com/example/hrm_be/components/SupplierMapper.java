package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SupplierMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert SupplierEntity to Supplier DTO
  public Supplier toDTO(SupplierEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Supplier.builder()
                    .id(e.getId())
                    .supplierName(e.getSupplierName())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .address(e.getAddress())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Convert Supplier DTO to SupplierEntity
  public SupplierEntity toEntity(Supplier dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                SupplierEntity.builder()
                    .id(e.getId())
                    .supplierName(e.getSupplierName())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .address(e.getAddress())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
