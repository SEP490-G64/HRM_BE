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

  @Autowired @Lazy private BranchBatchMapper branchBatchMapper;
  @Autowired @Lazy private InboundMapper inboundMapper;
  @Autowired @Lazy private BranchProductMapper branchProductMapper;
  @Autowired @Lazy private OutboundMapper outboundMapper;
  @Autowired @Lazy private InventoryCheckMapper inventoryCheckMapper;
  @Autowired @Lazy private UserMapper userMapper;

  // Convert BranchEntity to Branch
  public Branch toDTO(BranchEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .orElse(null);
  }

  // Convert Branch to BranchEntity
  public BranchEntity toEntity(Branch model) {
    return Optional.ofNullable(model)
        .map(e -> BranchEntity.builder()
            .id(e.getId())
            .branchName(e.getBranchName())
            .branchType(e.getBranchType())
            .location(e.getLocation())
            .contactPerson(e.getContactPerson())
            .phoneNumber(e.getPhoneNumber())
            .capacity(e.getCapacity())
            .activeStatus(e.getActiveStatus())
            .branchBatches(
                e.getBranchBatches() != null
                    ? e.getBranchBatches().stream()
                    .map(branchBatchMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .inbounds(
                e.getInbounds() != null
                    ? e.getInbounds().stream()
                    .map(inboundMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .branchProducts(
                e.getBranchProducts() != null
                    ? e.getBranchProducts().stream()
                    .map(branchProductMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .outbounds(
                e.getOutbounds() != null
                    ? e.getOutbounds().stream()
                    .map(outboundMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .inventoryChecks(
                e.getInventoryChecks() != null
                    ? e.getInventoryChecks().stream()
                    .map(inventoryCheckMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .users(
                e.getUsers() != null
                    ? e.getUsers().stream()
                    .map(userMapper::toEntity)
                    .collect(Collectors.toList())
                    : null)
            .build())
        .orElse(null);
  }

  // Helper method to map BranchEntity to Branch
  private Branch convertToDTO(BranchEntity entity) {
    return Optional.ofNullable(entity)
        .map(e -> Branch.builder()
            .id(e.getId())
            .branchName(e.getBranchName())
            .branchType(e.getBranchType())
            .location(e.getLocation())
            .contactPerson(e.getContactPerson())
            .phoneNumber(e.getPhoneNumber())
            .capacity(e.getCapacity())
            .activeStatus(e.getActiveStatus())
            .branchBatches(
                e.getBranchBatches() != null
                    ? e.getBranchBatches().stream()
                    .map(branchBatchMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .inbounds(
                e.getInbounds() != null
                    ? e.getInbounds().stream()
                    .map(inboundMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .branchProducts(
                e.getBranchProducts() != null
                    ? e.getBranchProducts().stream()
                    .map(branchProductMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .outbounds(
                e.getOutbounds() != null
                    ? e.getOutbounds().stream()
                    .map(outboundMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .inventoryChecks(
                e.getInventoryChecks() != null
                    ? e.getInventoryChecks().stream()
                    .map(inventoryCheckMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .users(
                e.getUsers() != null
                    ? e.getUsers().stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList())
                    : null)
            .build())
        .orElse(null);
  }
}
