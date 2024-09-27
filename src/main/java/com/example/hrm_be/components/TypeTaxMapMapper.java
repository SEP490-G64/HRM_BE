package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.TypeTaxMap;
import com.example.hrm_be.models.entities.TypeTaxMapEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class TypeTaxMapMapper {
  @Lazy @Autowired private TaxMapper taxMapper;
  @Lazy @Autowired private TypeMapper typeMapper;

  public TypeTaxMap toDTO(TypeTaxMapEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                TypeTaxMap.builder()
                    .id(e.getId())
                    .tax(taxMapper.toDTO(e.getTax()))
                    .type(typeMapper.toDTO(e.getType()))
                    .build())
        .orElse(null);
  }

  public TypeTaxMapEntity toEntity(TypeTaxMap dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                TypeTaxMapEntity.builder()
                    .id(e.getId())
                    .tax(taxMapper.toEntity(e.getTax()))
                    .type(typeMapper.toEntity(e.getType()))
                    .build())
        .orElse(null);
  }
}
