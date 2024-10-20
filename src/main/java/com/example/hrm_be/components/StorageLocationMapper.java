package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.StorageLocation;
import com.example.hrm_be.models.entities.StorageLocationEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class StorageLocationMapper {

  @Autowired @Lazy private BranchProductMapper branchProductMapper;

  // Convert StorageLocationEntity to StorageLocationDTO
  public StorageLocation toDTO(StorageLocationEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert StorageLocationDTO to StorageLocationEntity
  public StorageLocationEntity toEntity(StorageLocation dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                StorageLocationEntity.builder()
                    .id(d.getId())
                    .shelfName(d.getShelfName())
                    .branchProducts(
                        d.getBranchProducts() != null
                            ? d.getBranchProducts().stream()
                                .map(branchProductMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert StorageLocationEntity to StorageLocationDTO
  private StorageLocation convertToDTO(StorageLocationEntity entity) {
    return StorageLocation.builder().id(entity.getId()).shelfName(entity.getShelfName()).build();
  }
}
