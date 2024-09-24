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

  @Autowired @Lazy
  private ProductMapper productMapper;

  // Convert TaxEntity to Tax DTO
  public Tax toDTO(TaxEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Tax.builder()
                    .id(e.getId())
                    .taxName(e.getTaxName())
                    .taxRate(e.getTaxRate())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                            .map(productMapper::toDTO)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
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
