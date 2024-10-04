package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryCheckDetailsMapper {

  @Autowired @Lazy private InventoryCheckMapper inventoryCheckMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert InventoryCheckDetailsEntity to InventoryCheckDetailsDTO
  public InventoryCheckDetails toDTO(InventoryCheckDetailsEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InventoryCheckDetailsDTO to InventoryCheckDetailsEntity
  public InventoryCheckDetailsEntity toEntity(InventoryCheckDetails dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InventoryCheckDetailsEntity.builder()
                    .inventoryCheck(
                        d.getInventoryCheck() != null
                            ? inventoryCheckMapper.toEntity(d.getInventoryCheck())
                            : null)
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .systemQuantity(d.getSystemQuantity())
                    .countedQuantity(d.getCountedQuantity())
                    .difference(d.getDifference())
                    .reason(d.getReason())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InventoryCheckDetailsEntity to InventoryCheckDetailsDTO
  private InventoryCheckDetails convertToDTO(InventoryCheckDetailsEntity entity) {
    return InventoryCheckDetails.builder()
        .inventoryCheck(
            entity.getInventoryCheck() != null
                ? inventoryCheckMapper.toDTO(entity.getInventoryCheck())
                : null)
        .product(entity.getProduct() != null ? productMapper.toDTO(entity.getProduct()) : null)
        .systemQuantity(entity.getSystemQuantity())
        .countedQuantity(entity.getCountedQuantity())
        .difference(entity.getDifference())
        .reason(entity.getReason())
        .build();
  }
}