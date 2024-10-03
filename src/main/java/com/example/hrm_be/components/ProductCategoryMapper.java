package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductCategoryMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert ProductCategoryEntity to ProductCategoryDTO
  public ProductCategory toDTO(ProductCategoryEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .orElse(null);
  }

  // Convert ProductCategoryDTO to ProductCategoryEntity
  public ProductCategoryEntity toEntity(ProductCategory dto) {
    return Optional.ofNullable(dto)
        .map(d -> ProductCategoryEntity.builder()
            .categoryName(d.getCategoryName())
            .categoryDescription(d.getCategoryDescription())
            .taxRate(d.getTaxRate())
            .products(
                d.getProducts() != null
                    ? d.getProducts().stream()
                    .map(productMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .build())
        .orElse(null);
  }

  // Helper method to convert ProductCategoryEntity to ProductCategoryDTO
  private ProductCategory convertToDTO(ProductCategoryEntity entity) {
    return ProductCategory.builder()
        .categoryName(entity.getCategoryName())
        .categoryDescription(entity.getCategoryDescription())
        .taxRate(entity.getTaxRate())
        .products(
            entity.getProducts() != null
                ? entity.getProducts().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList())
                : null)
        .build();
  }
}
