package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SpecialConditionMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert SpecialConditionEntity to SpecialConditionDTO
  public SpecialCondition toDTO(SpecialConditionEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert SpecialConditionDTO to SpecialConditionEntity
  public SpecialConditionEntity toEntity(SpecialCondition dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                SpecialConditionEntity.builder()
                    .id(d.getId())
                    .conditionType(d.getConditionType())
                    .handlingInstruction(d.getHandlingInstruction())
                    .build())
        .orElse(null);
  }

  // Helper method to convert SpecialConditionEntity to SpecialConditionDTO
  private SpecialCondition convertToDTO(SpecialConditionEntity entity) {
    return SpecialCondition.builder()
        .id(entity.getId())
        .conditionType(entity.getConditionType())
        .handlingInstruction(entity.getHandlingInstruction())
        .build();
  }
}
