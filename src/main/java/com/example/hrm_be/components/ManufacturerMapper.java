package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ManufacturerMapper {

  @Autowired private ProductMapper productMapper;

  // Convert ManufacturerEntity to Manufacturer DTO
  public Manufacturer toDTO(ManufacturerEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Manufacturer DTO to ManufacturerEntity
  public ManufacturerEntity toEntity(Manufacturer dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ManufacturerEntity.builder()
                    .id(e.getId())
                    .manufacturerName(e.getManufacturerName())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .address(e.getAddress())
                    .origin(e.getOrigin())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                            .map(productMapper::toEntity)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map ManufacturerEntity to Manufacturer DTO
  private Manufacturer convertToDto(ManufacturerEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Manufacturer.builder()
                    .id(e.getId())
                    .manufacturerName(e.getManufacturerName())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .email(e.getEmail())
                    .address(e.getAddress())
                    .origin(e.getOrigin())
                    .products(
                        e.getProducts() != null
                            ? e.getProducts().stream()
                            .map(productMapper::toDTO)
                            .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
