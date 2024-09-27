package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Inventory;
import com.example.hrm_be.models.entities.InventoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert InventoryEntity to Inventory DTO
  public Inventory toDTO(InventoryEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Inventory DTO to InventoryEntity
  public InventoryEntity toEntity(Inventory dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                InventoryEntity.builder()
                    .id(e.getId())
                    .branch(branchMapper.toEntity(e.getBranch()))
                    .product(productMapper.toEntity(e.getProduct()))
                    .quantity(e.getQuantity())
                    .storageCondition(e.getStorageCondition())
                    .lastUpdated(e.getLastUpdated())
                    .status(e.getStatus())
                    .location(e.getLocation())
                    .build())
        .orElse(null);
  }

  // Helper method to map InventoryEntity to Inventory DTO
  private Inventory convertToDto(InventoryEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Inventory.builder()
                    .id(e.getId())
                    .branch(branchMapper.toDTO(e.getBranch()))
                    .product(productMapper.toDTO(e.getProduct()))
                    .quantity(e.getQuantity())
                    .storageCondition(e.getStorageCondition())
                    .lastUpdated(e.getLastUpdated())
                    .status(e.getStatus())
                    .location(e.getLocation())
                    .build())
        .orElse(null);
  }
}
