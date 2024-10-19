package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.BranchProductEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class BranchProductMapper {

  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private StorageLocationMapper storageLocationMapper;

  // Convert BranchProductEntity to BranchProductDTO
  public BranchProduct toDTO(BranchProductEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert BranchProductDTO to BranchProductEntity
  public BranchProductEntity toEntity(BranchProduct dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                BranchProductEntity.builder()
                    .id(d.getId())
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .branch(d.getBranch() != null ? branchMapper.toEntity(d.getBranch()) : null)
                    .storageLocation(
                        d.getStorageLocation() != null
                            ? storageLocationMapper.toEntity(d.getStorageLocation())
                            : null)
                    .minQuantity(d.getMinQuantity())
                    .maxQuantity(d.getMaxQuantity())
                    .quantity(d.getQuantity())
                    .build())
        .orElse(null);
  }

  // Helper method to convert BranchProductEntity to BranchProductDTO
  private BranchProduct convertToDTO(BranchProductEntity entity) {
    return BranchProduct.builder()
        .id(entity.getId())
        .minQuantity(entity.getMinQuantity())
        .maxQuantity(entity.getMaxQuantity())
        .quantity(entity.getQuantity())
        .storageLocation(
            entity.getStorageLocation() != null
                ? storageLocationMapper.toDTO(entity.getStorageLocation())
                : null)
        .branch(
            entity.getBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getBranch())
                : null)
        .build();
  }
}
