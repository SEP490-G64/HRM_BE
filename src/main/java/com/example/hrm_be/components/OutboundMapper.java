package com.example.hrm_be.components;

import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.outbound.OutboundCreateRequest;
import com.example.hrm_be.models.requests.outbound.OutboundUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OutboundMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;
  @Autowired @Lazy private UserMapper userMapper;
  @Autowired @Lazy private OutboundDetailMapper outboundDetailMapper;

  // Convert OutboundEntity to OutboundDTO
  public Outbound toDTO(OutboundEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert OutboundDTO to OutboundEntity
  public OutboundEntity toEntity(Outbound dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                OutboundEntity.builder()
                    .outboundType(d.getOutboundType())
                    .fromBranch(
                        d.getFromBranch() != null ? branchMapper.toEntity(d.getFromBranch()) : null)
                    .supplier(
                        d.getSupplier() != null ? supplierMapper.toEntity(d.getSupplier()) : null)
                    .toBranch(
                        d.getToBranch() != null ? branchMapper.toEntity(d.getToBranch()) : null)
                    .createdDate(d.getCreatedDate())
                    .totalPrice(d.getTotalPrice())
                    .isApproved(d.getIsApproved())
                    .approvedBy(
                        d.getApprovedBy() != null ? userMapper.toEntity(d.getApprovedBy()) : null)
                    .status(d.getStatus())
                    .taxable(d.getTaxable())
                    .note(d.getNote())
                    .createdBy(
                        d.getCreatedBy() != null ? userMapper.toEntity(d.getCreatedBy()) : null)
                    .outboundDate(d.getOutboundDate())
                    .outboundDetails(
                        d.getOutboundDetails() != null
                            ? d.getOutboundDetails().stream()
                                .map(outboundDetailMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert OutboundEntity to OutboundDTO
  private Outbound convertToDTO(OutboundEntity entity) {
    return Outbound.builder()
        .outboundType(entity.getOutboundType())
        .fromBranch(
            entity.getFromBranch() != null ? branchMapper.toDTO(entity.getFromBranch()) : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .toBranch(entity.getToBranch() != null ? branchMapper.toDTO(entity.getToBranch()) : null)
        .createdDate(entity.getCreatedDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .approvedBy(
            entity.getApprovedBy() != null ? userMapper.toDTO(entity.getApprovedBy()) : null)
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .note(entity.getNote())
        .createdBy(entity.getCreatedBy() != null ? userMapper.toDTO(entity.getCreatedBy()) : null)
        .outboundDate(entity.getOutboundDate())
        .outboundDetails(
            entity.getOutboundDetails() != null
                ? entity.getOutboundDetails().stream()
                    .map(outboundDetailMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }

  // Convert OutboundCreateRequest to OutboundEntity
  public OutboundEntity toEntity(OutboundCreateRequest dto, BranchEntity fromBranch,
                                 BranchEntity toBranch, SupplierEntity supplier) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create OutboundEntity from OutboundCreateRequest
                      return OutboundEntity.builder()
                              .outboundType(OutboundType.valueOf(dto.getOutboundType()))
                              .fromBranch(fromBranch)
                              .toBranch(toBranch)
                              .supplier(supplier)
                              .totalPrice(dto.getTotalPrice())
                              .status(OutboundStatus.valueOf(dto.getStatus()))
                              .taxable(dto.getTaxable())
                              .note(dto.getNote())
                              .outboundDate(dto.getOutboundDate())
                              .build();
                    })
            .orElse(null);
  }

  // Convert OutboundUpdateRequest to OutboundEntity
  public OutboundEntity toEntity(OutboundUpdateRequest dto,
                                 BranchEntity fromBranch, BranchEntity toBranch,
                                 SupplierEntity supplier, UserEntity approvedBy) {
    return Optional.ofNullable(dto)
            .map(
                    request -> {
                      // Create OutboundEntity from OutboundUpdateRequest
                      return OutboundEntity.builder()
                              .outboundType(OutboundType.valueOf(dto.getOutboundType()))
                              .fromBranch(fromBranch)
                              .toBranch(toBranch)
                              .supplier(supplier)
                              .approvedBy(approvedBy)
                              .totalPrice(dto.getTotalPrice())
                              .status(OutboundStatus.valueOf(dto.getStatus()))
                              .taxable(dto.getTaxable())
                              .note(dto.getNote())
                              .outboundDate(dto.getOutboundDate())
                              .build();
                    })
            .orElse(null);
  }
}
