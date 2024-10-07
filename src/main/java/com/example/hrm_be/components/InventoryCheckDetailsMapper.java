package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsCreateRequest;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryCheckDetailsMapper {

  @Autowired @Lazy private InventoryCheckMapper inventoryCheckMapper;
  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private BatchMapper batchMapper;

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
                    .batch(
                        d.getInventoryCheck() != null ? batchMapper.toEntity(d.getBatch()) : null)
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
        .batch(entity.getBatch() != null ? batchMapper.toDTO(entity.getBatch()) : null)
        .systemQuantity(entity.getSystemQuantity())
        .countedQuantity(entity.getCountedQuantity())
        .difference(entity.getDifference())
        .reason(entity.getReason())
        .build();
  }

  // Convert InventoryCheckDetailsCreateRequest to InventoryCheckDetailsEntity
  public InventoryCheckDetailsEntity toEntity(InventoryCheckDetailsCreateRequest dto,
                                              InventoryCheckEntity inventoryCheck, BatchEntity batch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create InventoryCheckDetailsEntity from InventoryCheckDetailsCreateRequest
                      return InventoryCheckDetailsEntity.builder()
                              .inventoryCheck(inventoryCheck)
                              .batch(batch)
                              .systemQuantity(dto.getSystemQuantity())
                              .countedQuantity(dto.getCountedQuantity())
                              .difference(dto.getDifference())
                              .reason(dto.getReason())
                              .build();
                    })
            .orElse(null);
  }

  // Convert InventoryCheckDetailsUpdateRequest to InventoryCheckDetailsEntity
  public InventoryCheckDetailsEntity toEntity(InventoryCheckDetailsUpdateRequest dto,
                                              InventoryCheckEntity inventoryCheck, BatchEntity batch) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create InventoryCheckDetailsEntity from InventoryCheckDetailsUpdateRequest
                      return InventoryCheckDetailsEntity.builder()
                              .batch(batch)
                              .systemQuantity(dto.getSystemQuantity())
                              .countedQuantity(dto.getCountedQuantity())
                              .difference(dto.getDifference())
                              .reason(dto.getReason())
                              .build();
                    })
            .orElse(null);
  }
}
