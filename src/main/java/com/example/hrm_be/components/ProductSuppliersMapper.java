package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductSuppliers;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductSuppliersMapper {

  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;

  // Convert ProductSuppliersEntity to ProductSuppliersDTO
  public ProductSuppliers toDTO(ProductSuppliersEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .orElse(null);
  }

  // Convert ProductSuppliersDTO to ProductSuppliersEntity
  public ProductSuppliersEntity toEntity(ProductSuppliers dto) {
    return Optional.ofNullable(dto)
        .map(d -> ProductSuppliersEntity.builder()
            .id(d.getId())
            .product(
                d.getProduct() != null
                    ? productMapper.toEntity(d.getProduct())
                    : null)
            .supplier(
                d.getSupplier() != null
                    ? supplierMapper.toEntity(d.getSupplier())
                    : null)
            .build())
        .orElse(null);
  }

  // Helper method to convert ProductSuppliersEntity to ProductSuppliersDTO
  private ProductSuppliers convertToDTO(ProductSuppliersEntity entity) {
    return ProductSuppliers.builder()
        .id(entity.getId())
        .product(
            entity.getProduct() != null
                ? productMapper.toDTO(entity.getProduct())
                : null)
        .supplier(
            entity.getSupplier() != null
                ? supplierMapper.toDTO(entity.getSupplier())
                : null)
        .build();
  }
}

