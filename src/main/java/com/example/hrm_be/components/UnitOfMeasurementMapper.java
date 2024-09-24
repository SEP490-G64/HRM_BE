package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnitOfMeasurementMapper {

  @Autowired private ProductMapper productMapper;

  // Convert UnitOfMeasurementEntity to UnitOfMeasurement DTO
  public UnitOfMeasurement toDTO(UnitOfMeasurementEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                UnitOfMeasurement.builder()
                    .id(e.getId())
                    .unitName(e.getUnitName())
                    .conversionFactor(e.getConversionFactor())
                    .pricePerUnit(e.getPricePerUnit())
                    .product(productMapper.toDTO(e.getProduct()))
                    .build())
        .orElse(null);
  }

  // Convert UnitOfMeasurement DTO to UnitOfMeasurementEntity
  public UnitOfMeasurementEntity toEntity(UnitOfMeasurement dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                UnitOfMeasurementEntity.builder()
                    .id(e.getId())
                    .unitName(e.getUnitName())
                    .conversionFactor(e.getConversionFactor())
                    .pricePerUnit(e.getPricePerUnit())
                    .product(productMapper.toEntity(e.getProduct()))
                    .build())
        .orElse(null);
  }
}
