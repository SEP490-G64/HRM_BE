package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductCategoryMapper {

  @Autowired @Lazy private ProductCategoryMapMapper productCategoryMapMapper;

  // Convert ProductCategoryEntity to ProductCategory DTO
  public ProductCategory toDTO(ProductCategoryEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                ProductCategory.builder()
                    .id(e.getId())
                    .categoryName(e.getCategoryName())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productCategoryMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Convert ProductCategory DTO to ProductCategoryEntity
  public ProductCategoryEntity toEntity(ProductCategory dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductCategoryEntity.builder()
                    .id(e.getId())
                    .categoryName(e.getCategoryName())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                                .map(productCategoryMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
