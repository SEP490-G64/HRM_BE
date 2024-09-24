package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductIngredient;
import com.example.hrm_be.models.entities.ProductIngredientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductIngredientMapper {

  @Autowired private ProductIngredientMapMapper productIngredientMapMapper;

  // Convert ProductIngredientEntity to ProductIngredient DTO
  public ProductIngredient toDTO(ProductIngredientEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                ProductIngredient.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .description(e.getDescription())
                    .productIngredientMap(
                        e.getProductIngredientMapEntities() != null
                            ? e.getProductIngredientMapEntities().stream()
                            .map(productIngredientMapMapper::toDTO)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Convert ProductIngredient DTO to ProductIngredientEntity
  public ProductIngredientEntity toEntity(ProductIngredient dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductIngredientEntity.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .description(e.getDescription())
                    .productIngredientMapEntities(
                        e.getProductIngredientMap() != null
                            ? e.getProductIngredientMap().stream()
                            .map(productIngredientMapMapper::toEntity)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
