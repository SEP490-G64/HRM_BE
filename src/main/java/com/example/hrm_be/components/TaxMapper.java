package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Tax;
import com.example.hrm_be.models.entities.TaxEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaxMapper {

  @Autowired @Lazy private TypeTaxMapMapper typeTaxMapMapper;

  // Convert TaxEntity to Tax DTO
  public Tax toDTO(TaxEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Tax DTO to TaxEntity
  public TaxEntity toEntity(Tax dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                TaxEntity.builder()
                    .id(e.getId())
                    .taxName(e.getTaxName())
                    .taxRate(e.getTaxRate())
                    .typeTaxMapEntities(
                        e.getTypeTaxMap() != null
                            ? e.getTypeTaxMap().stream()
                                .map(typeTaxMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map TaxEntity to Tax DTO
  private Tax convertToDto(TaxEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Tax.builder()
                    .id(e.getId())
                    .taxName(e.getTaxName())
                    .taxRate(e.getTaxRate())
                    .typeTaxMap(
                        e.getTypeTaxMapEntities() != null
                            ? e.getTypeTaxMapEntities().stream()
                                .map(typeTaxMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
