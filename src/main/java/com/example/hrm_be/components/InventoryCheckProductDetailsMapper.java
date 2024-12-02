package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import com.example.hrm_be.models.entities.InventoryCheckProductDetailsEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class InventoryCheckProductDetailsMapper {
  @Autowired @Lazy private InventoryCheckMapper inventoryCheckMapper;
  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private BatchMapper batchMapper;

  // Convert InventoryCheckDetailsEntity to InventoryCheckDetailsDTO
  public InventoryCheckProductDetails toDTO(InventoryCheckProductDetailsEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InventoryCheckDetailsDTO to InventoryCheckDetailsEntity
  public InventoryCheckProductDetailsEntity toEntity(InventoryCheckProductDetails dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InventoryCheckProductDetailsEntity.builder()
                    .id(d.getId())
                    .inventoryCheck(
                        d.getInventoryCheck() != null
                            ? inventoryCheckMapper.toEntity(d.getInventoryCheck())
                            : null)
                    .product(
                        d.getInventoryCheck() != null
                            ? productMapper.toEntity(d.getProduct())
                            : null)
                    .systemQuantity(d.getSystemQuantity())
                    .countedQuantity(d.getCountedQuantity())
                    .difference(d.getDifference())
                    .reason(d.getReason())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InventoryCheckDetailsEntity to InventoryCheckDetailsDTO
  private InventoryCheckProductDetails convertToDTO(InventoryCheckProductDetailsEntity entity) {
    return InventoryCheckProductDetails.builder()
        .id(entity.getId())
        .product(entity.getProduct() != null ? productMapper.toDTO(entity.getProduct()) : null)
        .systemQuantity(entity.getSystemQuantity())
        .countedQuantity(entity.getCountedQuantity())
        .difference(entity.getDifference())
        .reason(entity.getReason())
        .build();
  }

  // Helper method to convert InventoryCheckDetailsEntity to InventoryCheckDetailsDTO
  public InventoryCheckProductDetails convertToDTOByBranchId(
      InventoryCheckProductDetailsEntity entity, Long branchId) {
    return InventoryCheckProductDetails.builder()
        .id(entity.getId())
        .product(
            entity.getProduct() != null
                ? productMapper.convertToDTOByBranchId(entity.getProduct(), branchId)
                : null)
        .systemQuantity(entity.getSystemQuantity())
        .countedQuantity(entity.getCountedQuantity())
        .difference(entity.getDifference())
        .reason(entity.getReason())
        .build();
  }
}
