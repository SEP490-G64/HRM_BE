package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert ProductTypeEntity to ProductTypeDTO
  public ProductType toDTO(ProductTypeEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert ProductTypeDTO to ProductTypeEntity
  public ProductTypeEntity toEntity(ProductType dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                ProductTypeEntity.builder()
                    .id(d.getId())
                    .typeName(d.getTypeName())
                    .typeDescription(d.getTypeDescription())
                    .build())
        .orElse(null);
  }

  // Helper method to convert ProductTypeEntity to ProductTypeDTO
  private ProductType convertToDTO(ProductTypeEntity entity) {
    return ProductType.builder()
        .id(entity.getId())
        .typeName(entity.getTypeName())
        .typeDescription(entity.getTypeDescription())
        .build();
  }
}
