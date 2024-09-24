package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductCategoryMap;
import com.example.hrm_be.models.entities.ProductCategoryMapEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductCategoryMapMapper {

  @Autowired @Lazy  private ProductMapper productMapper;
  @Autowired @Lazy
  private ProductCategoryMapper productCategoryMapper;

  // Convert ProductCategoryMapEntity to ProductCategoryMap DTO
  public ProductCategoryMap toDTO(ProductCategoryMapEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert ProductCategoryMap DTO to ProductCategoryMapEntity
  public ProductCategoryMapEntity toEntity(ProductCategoryMap dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductCategoryMapEntity.builder()
                    .id(e.getId())
                    .product(productMapper.toEntity(e.getProduct()))
                    .category(productCategoryMapper.toEntity(e.getCategory()))
                    .build())
        .orElse(null);
  }

  // Helper method to map ProductCategoryMapEntity to ProductCategoryMap DTO
  private ProductCategoryMap convertToDto(ProductCategoryMapEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                ProductCategoryMap.builder()
                    .id(e.getId())
                    .product(productMapper.toDTO(e.getProduct()))
                    .category(productCategoryMapper.toDTO(e.getCategory()))
                    .build())
        .orElse(null);
  }
}
