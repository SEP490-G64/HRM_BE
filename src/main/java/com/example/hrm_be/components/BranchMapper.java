package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.BranchEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BranchMapper {

  @Autowired @Lazy private InventoryMapper inventoryMapper;
  @Autowired @Lazy private UserMapper userMapper;

  // Convert BranchEntity to Branch DTO
  public Branch toDTO(BranchEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Branch DTO to BranchEntity
  public BranchEntity toEntity(Branch dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                BranchEntity.builder()
                    .id(e.getId())
                    .branchName(e.getBranchName())
                    .branchType(e.getBranchType())
                    .location(e.getLocation())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .capacity(e.getCapacity())
                    .activeStatus(e.getActiveStatus())
                    .users(
                        e.getUsers() != null
                            ? e.getUsers().stream()
                                .map(userMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .inventoryEntities(
                        e.getInventoryEntities() != null
                            ? e.getInventoryEntities().stream()
                                .map(inventoryMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BranchEntity to Branch DTO
  private Branch convertToDto(BranchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Branch.builder()
                    .id(e.getId())
                    .branchName(e.getBranchName())
                    .branchType(e.getBranchType())
                    .location(e.getLocation())
                    .contactPerson(e.getContactPerson())
                    .phoneNumber(e.getPhoneNumber())
                    .capacity(e.getCapacity())
                    .activeStatus(e.getActiveStatus())
                    .users(
                        e.getUsers() != null
                            ? e.getUsers().stream()
                                .map(userMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .inventoryEntities(
                        e.getInventoryEntities() != null
                            ? e.getInventoryEntities().stream()
                                .map(inventoryMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
