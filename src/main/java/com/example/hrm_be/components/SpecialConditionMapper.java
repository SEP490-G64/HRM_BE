package com.example.hrm_be.components;

import com.example.hrm_be.commons.enums.ConditionType;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionCreateRequest;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .conditionType(d.getConditionType())
                    .handlingInstruction(d.getHandlingInstruction())
                    .build())
        .orElse(null);
  }

  // Helper method to convert SpecialConditionEntity to SpecialConditionDTO
  private SpecialCondition convertToDTO(SpecialConditionEntity entity) {
    return SpecialCondition.builder()
        .id(entity.getId())
        .product(entity.getProduct() != null ? productMapper.toDTO(entity.getProduct()) : null)
        .conditionType(entity.getConditionType())
        .handlingInstruction(entity.getHandlingInstruction())
        .build();
  }

  // Convert SpecialConditionCreateRequest to SpecialConditionEntity
  public SpecialConditionEntity toEntity(SpecialConditionCreateRequest dto, ProductEntity product) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create SpecialConditionEntity from SpecialConditionCreateRequest
                      return SpecialConditionEntity.builder()
                              .product(product)
                              .conditionType(ConditionType.valueOf(dto.getConditionType()))
                              .handlingInstruction(dto.getHandlingInstruction())
                              .build();
                    })
            .orElse(null);
  }

  // Convert SpecialConditionUpdateRequest to SpecialConditionEntity
  public SpecialConditionEntity toEntity(SpecialConditionUpdateRequest dto, ProductEntity product) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create SpecialConditionEntity from SpecialConditionUpdateRequest
                      return SpecialConditionEntity.builder()
                              .product(product)
                              .conditionType(ConditionType.valueOf(dto.getConditionType()))
                              .handlingInstruction(dto.getHandlingInstruction())
                              .build();
                    })
            .orElse(null);
  }
}
