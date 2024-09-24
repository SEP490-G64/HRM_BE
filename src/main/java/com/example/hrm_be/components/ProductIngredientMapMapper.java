package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductIngredientMap;
import com.example.hrm_be.models.entities.ProductIngredientMapEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductIngredientMapMapper {

  @Autowired @Lazy
  private ProductMapper productMapper;
  @Autowired @Lazy private ProductIngredientMapper productIngredientMapper;

  // Convert ProductIngredientMapEntity to ProductIngredientMap DTO
  public ProductIngredientMap toDTO(ProductIngredientMapEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                ProductIngredientMap.builder()
                    .id(e.getId())
                    .product(productMapper.toDTO(e.getProduct()))
                    .ingredient(productIngredientMapper.toDTO(e.getIngredient()))
                    .amount(e.getAmount())
                    .unitOfMeasurement(e.getUnitOfMeasurement())
                    .build())
        .orElse(null);
  }

  // Convert ProductIngredientMap DTO to ProductIngredientMapEntity
  public ProductIngredientMapEntity toEntity(ProductIngredientMap dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductIngredientMapEntity.builder()
                    .id(e.getId())
                    .product(productMapper.toEntity(e.getProduct()))
                    .ingredient(productIngredientMapper.toEntity(e.getIngredient()))
                    .amount(e.getAmount())
                    .unitOfMeasurement(e.getUnitOfMeasurement())
                    .build())
        .orElse(null);
  }
}
