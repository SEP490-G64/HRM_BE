package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.entities.InboundEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InboundMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;
  @Autowired @Lazy private UserMapper userMapper;
  @Autowired @Lazy private InboundDetailsMapper inboundDetailsMapper;

  // Convert InboundEntity to InboundDTO
  public Inbound toDTO(InboundEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InboundDTO to InboundEntity
  public InboundEntity toEntity(Inbound dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InboundEntity.builder()
                    .id(d.getId())
                    .inboundType(d.getInboundType())
                    .fromBranch(
                        d.getFromBranch() != null ? branchMapper.toEntity(d.getFromBranch()) : null)
                    .toBranch(
                        d.getToBranch() != null ? branchMapper.toEntity(d.getToBranch()) : null)
                    .supplier(
                        d.getSupplier() != null ? supplierMapper.toEntity(d.getSupplier()) : null)
                    .createdBy(
                        d.getCreatedBy() != null ? userMapper.toEntity(d.getCreatedBy()) : null)
                    .approvedBy(
                        d.getApprovedBy() != null ? userMapper.toEntity(d.getApprovedBy()) : null)
                    .createdDate(d.getCreatedDate())
                    .inboundDate(d.getInboundDate())
                    .totalPrice(d.getTotalPrice())
                    .isApproved(d.getIsApproved())
                    .status(d.getStatus())
                    .taxable(d.getTaxable())
                    .note(d.getNote())
                    .inboundDate(d.getInboundDate())
                    .inboundDetails(
                        d.getInboundDetails() != null
                            ? d.getInboundDetails().stream()
                                .map(inboundDetailsMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert InboundEntity to InboundDTO
  private Inbound convertToDTO(InboundEntity entity) {
    return Inbound.builder()
        .id(entity.getId())
        .inboundType(entity.getInboundType())
        .fromBranch(
            entity.getFromBranch() != null ? branchMapper.toDTO(entity.getFromBranch()) : null)
        .toBranch(entity.getToBranch() != null ? branchMapper.toDTO(entity.getToBranch()) : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .createdBy(entity.getCreatedBy() != null ? userMapper.toDTO(entity.getCreatedBy()) : null)
        .approvedBy(entity.getApprovedBy() != null ? userMapper.toDTO(entity.getCreatedBy()) : null)
        .createdDate(entity.getCreatedDate())
        .inboundDate(entity.getInboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .note(entity.getNote())
        .inboundDate(entity.getInboundDate())
        .inboundDetails(
            entity.getInboundDetails() != null
                ? entity.getInboundDetails().stream()
                    .map(inboundDetailsMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }
}
