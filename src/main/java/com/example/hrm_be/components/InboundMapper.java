package com.example.hrm_be.components;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class InboundMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;
  @Autowired @Lazy private UserMapper userMapper;
  @Autowired @Lazy private InboundDetailsMapper inboundDetailsMapper;
  @Autowired @Lazy private InboundBatchDetailMapper inboundBatchDetailMapper;

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
                    .inboundCode(d.getInboundCode())
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
        .inboundCode(entity.getInboundCode())
        .inboundType(entity.getInboundType())
        .fromBranch(
            entity.getFromBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                : null)
        .toBranch(
            entity.getToBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .createdBy(
            entity.getCreatedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .approvedBy(
            entity.getApprovedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
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
        .inboundBatchDetails(
            entity.getInboundBatchDetails() != null
                ? entity.getInboundBatchDetails().stream()
                    .map(inboundBatchDetailMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }

  // Helper method to convert InboundEntity to InboundDTO
  public Inbound convertToBasicInfo(InboundEntity entity) {
    return Inbound.builder()
        .id(entity.getId())
        .inboundCode(entity.getInboundCode())
        .inboundType(entity.getInboundType())
        .fromBranch(
            entity.getFromBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                : null)
        .toBranch(
            entity.getToBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .createdBy(
            entity.getCreatedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .approvedBy(
            entity.getApprovedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .createdDate(entity.getCreatedDate())
        .inboundDate(entity.getInboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .note(entity.getNote())
        .inboundDate(entity.getInboundDate())
        .build();
  }

  // Helper method to convert InboundEntity to InboundDetail
  public InboundDetail convertToInboundDetail(InboundEntity entity) {
    return InboundDetail.builder()
        .id(entity.getId())
        .inboundCode(entity.getInboundCode())
        .note(entity.getNote())
        .inboundType(entity.getInboundType())
        .createdDate(entity.getCreatedDate())
        .inboundDate(entity.getInboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .approvedBy(
            entity.getApprovedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getApprovedBy())
                : null)
        .createdBy(
            entity.getCreatedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .fromBranch(
            entity.getFromBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                : null)
        .toBranch(
            entity.getToBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                : null)
        .build();
  }

  // Helper method to convert CreateInboundRequest to Inbound
  public Inbound convertFromCreateRequest(CreateInboundRequest entity) {
    return Inbound.builder()
        .inboundCode(entity.getInboundCode())
        .inboundType(entity.getInboundType())
        .createdDate(entity.getCreatedDate() != null ? entity.getCreatedDate() : null)
        .status(InboundStatus.BAN_NHAP)
        .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : null)
        .supplier(entity.getSupplier() != null ? entity.getSupplier() : null)
        .fromBranch(entity.getFromBranch() != null ? entity.getFromBranch() : null)
        .toBranch(entity.getFromBranch() != null ? entity.getToBranch() : null)
        .note(entity.getNote())
        .taxable(entity.getTaxable())
        .totalPrice(entity.getTotalPrice())
        .build();
  }
}
