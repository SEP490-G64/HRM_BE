package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                    .id(d.getId())
                    .manufacturerName(d.getManufacturerName())
                    .address(d.getAddress())
                    .email(d.getEmail())
                    .phoneNumber(d.getPhoneNumber())
                    .taxCode(d.getTaxCode())
                    .origin(d.getOrigin())
                    .status(d.getStatus())
                    .build())
        .orElse(null);
  }

  // Helper method to convert ManufacturerEntity to ManufacturerDTO
  private Manufacturer convertToDTO(ManufacturerEntity entity) {
    return Manufacturer.builder()
        .id(entity.getId())
        .manufacturerName(entity.getManufacturerName())
        .address(entity.getAddress())
        .email(entity.getEmail())
        .phoneNumber(entity.getPhoneNumber())
        .taxCode(entity.getTaxCode())
        .origin(entity.getOrigin())
        .status(entity.getStatus())
        .build();
  }
}
