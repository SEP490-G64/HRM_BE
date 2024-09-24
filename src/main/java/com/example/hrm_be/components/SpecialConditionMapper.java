package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SpecialConditionMapper {

  @Autowired @Lazy
  private ProductMapper productMapper;

  // Convert SpecialConditionEntity to SpecialCondition DTO
  public SpecialCondition toDTO(SpecialConditionEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                SpecialCondition.builder()
                    .id(e.getId())
                    .conditionType(e.getConditionType())
                    .minTemperature(e.getMinTemperature())
                    .maxTemperature(e.getMaxTemperature())
                    .handlingInstruction(e.getHandlingInstruction())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                            .map(productMapper::toDTO)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Convert SpecialCondition DTO to SpecialConditionEntity
  public SpecialConditionEntity toEntity(SpecialCondition dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                SpecialConditionEntity.builder()
                    .id(e.getId())
                    .conditionType(e.getConditionType())
                    .minTemperature(e.getMinTemperature())
                    .maxTemperature(e.getMaxTemperature())
                    .handlingInstruction(e.getHandlingInstruction())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                            .map(productMapper::toEntity)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
