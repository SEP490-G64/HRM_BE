package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.ProductUnitMap;
import com.example.hrm_be.models.entities.ProductUnitMapEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductUnitMapMapper {

  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;

  // Convert ProductUnitEntity to ProductUnit DTO
  public ProductUnitMap toDTO(ProductUnitMapEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                ProductUnitMap.builder()
                    .id(e.getId())
                    .product(productMapper.toDTO(e.getProduct()))
                    .unit(unitOfMeasurementMapper.toDTO(e.getUnit()))
                    .build())
        .orElse(null);
  }

  // Convert ProductUnit DTO to ProductUnitEntity
  public ProductUnitMapEntity toEntity(ProductUnitMap dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductUnitMapEntity.builder()
                    .id(e.getId())
                    .product(productMapper.toEntity(e.getProduct()))
                    .unit(unitOfMeasurementMapper.toEntity(e.getUnit()))
                    .build())
        .orElse(null);
  }
}
