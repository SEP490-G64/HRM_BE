package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Type;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class TypeMapper {

  @Autowired @Lazy private TypeTaxMapMapper typeTaxMapMapper;

  // Convert ProductTypeEntity to ProductType DTO
  public Type toDTO(ProductTypeEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert ProductType DTO to ProductTypeEntity
  public ProductTypeEntity toEntity(Type dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductTypeEntity.builder()
                    .id(e.getId())
                    .typeName(e.getTypeName())
                    .typeDescription(e.getTypeDescription())
                    .typeTaxMapEntities(
                        e.getTypeTaxMaps() != null
                            ? e.getTypeTaxMaps().stream()
                                .map(typeTaxMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map ProductTypeEntity to ProductType DTO
  private Type convertToDto(ProductTypeEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Type.builder()
                    .id(e.getId())
                    .typeName(e.getTypeName())
                    .typeDescription(e.getTypeDescription())
                    .typeTaxMaps(
                        e.getTypeDescription() != null
                            ? e.getTypeTaxMapEntities().stream()
                                .map(typeTaxMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
