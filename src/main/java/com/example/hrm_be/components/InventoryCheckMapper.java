package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.entities.InventoryCheckEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InventoryCheckMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private UserMapper userMapper;
  @Autowired @Lazy private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;

  // Convert InventoryCheckEntity to InventoryCheckDTO
  public InventoryCheck toDTO(InventoryCheckEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InventoryCheckDTO to InventoryCheckEntity
  public InventoryCheckEntity toEntity(InventoryCheck dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InventoryCheckEntity.builder()
                    .branch(d.getBranch() != null ? branchMapper.toEntity(d.getBranch()) : null)
                    .createdBy(
                        d.getCreatedBy() != null ? userMapper.toEntity(d.getCreatedBy()) : null)
                    .approvedBy(
                        d.getApprovedBy() != null ? userMapper.toEntity(d.getApprovedBy()) : null)
                    .createdDate(d.getCreatedDate())
                    .isApproved(d.getIsApproved())
                    .status(d.getStatus())
                    .note(d.getNote())
                    .inventoryCheckDetails(
                        d.getInventoryCheckDetails() != null
                            ? d.getInventoryCheckDetails().stream()
                                .map(inventoryCheckDetailsMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert InventoryCheckEntity to InventoryCheckDTO
  private InventoryCheck convertToDTO(InventoryCheckEntity entity) {
    return InventoryCheck.builder()
        .branch(entity.getBranch() != null ? branchMapper.toDTO(entity.getBranch()) : null)
        .createdBy(entity.getCreatedBy() != null ? userMapper.toDTO(entity.getCreatedBy()) : null)
        .approvedBy(
            entity.getApprovedBy() != null ? userMapper.toDTO(entity.getApprovedBy()) : null)
        .createdDate(entity.getCreatedDate())
        .isApproved(entity.getIsApproved())
        .status(entity.getStatus())
        .note(entity.getNote())
        .inventoryCheckDetails(
            entity.getInventoryCheckDetails() != null
                ? entity.getInventoryCheckDetails().stream()
                    .map(inventoryCheckDetailsMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }
}