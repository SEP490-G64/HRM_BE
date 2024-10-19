package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasurementMapper {

  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private UnitConversionMapper unitConversionMapper;

  // Convert UnitOfMeasurementEntity to UnitOfMeasurementDTO
  public UnitOfMeasurement toDTO(UnitOfMeasurementEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert UnitOfMeasurementDTO to UnitOfMeasurementEntity
  public UnitOfMeasurementEntity toEntity(UnitOfMeasurement dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                UnitOfMeasurementEntity.builder()
                    .id(d.getId())
                    .unitName(d.getUnitName())
                    .largerUnitConversions(
                        d.getLargerUnitConversions() != null
                            ? d.getLargerUnitConversions().stream()
                                .map(unitConversionMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .smallerUnitConversions(
                        d.getSmallerUnitConversions() != null
                            ? d.getSmallerUnitConversions().stream()
                                .map(unitConversionMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert UnitOfMeasurementEntity to UnitOfMeasurementDTO
  private UnitOfMeasurement convertToDTO(UnitOfMeasurementEntity entity) {
    return UnitOfMeasurement.builder()
        .id(entity.getId())
        .unitName(entity.getUnitName())
        //            .largerUnitConversions(
        //                    entity.getLargerUnitConversions() != null
        //                            ? entity.getLargerUnitConversions().stream()
        //                            .map(unitConversionMapper::toDTO)
        //                            .collect(Collectors.toList())
        //                            : null)
        //            .smallerUnitConversions(
        //                    entity.getSmallerUnitConversions() != null
        //                            ? entity.getSmallerUnitConversions().stream()
        //                            .map(unitConversionMapper::toDTO)
        //                            .collect(Collectors.toList())
        //                            : null)
        .build();
  }
}
