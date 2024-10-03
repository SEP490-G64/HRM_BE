package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UnitConversionMapper {

  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired @Lazy private BatchMapper batchMapper;

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
                    .batch(d.getBatch() != null ? batchMapper.toEntity(d.getBatch()) : null)
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
        .batch(entity.getBatch() != null ? batchMapper.toDTO(entity.getBatch()) : null)
        .build();
  }
}
