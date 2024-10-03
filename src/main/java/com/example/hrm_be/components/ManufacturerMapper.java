package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

  @Autowired @Lazy private ProductMapper productMapper;

  // Convert ManufacturerEntity to ManufacturerDTO
  public Manufacturer toDTO(ManufacturerEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert ManufacturerDTO to ManufacturerEntity
  public ManufacturerEntity toEntity(Manufacturer dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                ManufacturerEntity.builder()
                    .manufacturerName(d.getManufacturerName())
                    .address(d.getAddress())
                    .email(d.getEmail())
                    .phoneNumber(d.getPhoneNumber())
                    .taxCode(d.getTaxCode())
                    .origin(d.getOrigin())
                    .status(d.getStatus())
                    .products(
                        d.getProducts() != null
                            ? d.getProducts().stream()
                                .map(productMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert ManufacturerEntity to ManufacturerDTO
  private Manufacturer convertToDTO(ManufacturerEntity entity) {
    return Manufacturer.builder()
        .manufacturerName(entity.getManufacturerName())
        .address(entity.getAddress())
        .email(entity.getEmail())
        .phoneNumber(entity.getPhoneNumber())
        .taxCode(entity.getTaxCode())
        .origin(entity.getOrigin())
        .status(entity.getStatus())
        .products(
            entity.getProducts() != null
                ? entity.getProducts().stream()
                    .map(productMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }
}
