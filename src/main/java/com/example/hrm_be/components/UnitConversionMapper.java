package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UnitConversionMapper {

  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert UnitConversionEntity to UnitConversionDTO
  public UnitConversion toDTO(UnitConversionEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert UnitConversionDTO to UnitConversionEntity
  public UnitConversionEntity toEntity(UnitConversion dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                UnitConversionEntity.builder()
                    .id(d.getId())
                    .largerUnit(
                        d.getLargerUnit() != null
                            ? unitOfMeasurementMapper.toEntity(d.getLargerUnit())
                            : null)
                    .smallerUnit(
                        d.getSmallerUnit() != null
                            ? unitOfMeasurementMapper.toEntity(d.getSmallerUnit())
                            : null)
                    .factorConversion(d.getFactorConversion())
//                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert UnitConversionEntity to UnitConversionDTO
  private UnitConversion convertToDTO(UnitConversionEntity entity) {
    return UnitConversion.builder()
        .id(entity.getId())
        .largerUnit(
            entity.getLargerUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getLargerUnit())
                : null)
        .smallerUnit(
            entity.getSmallerUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getSmallerUnit())
                : null)
        .factorConversion(entity.getFactorConversion())
//        .product(entity.getProduct() != null ? productMapper.toDTO(entity.getProduct()) : null)
        .build();
  }
}
