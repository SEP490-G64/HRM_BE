package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OutboundMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;
  @Autowired @Lazy private UserMapper userMapper;
  @Autowired @Lazy private OutboundDetailMapper outboundDetailMapper;
  @Autowired @Lazy private OutboundProductDetailMapper outboundProductDetailMapper;

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
                    .id(d.getId())
                    .outboundType(d.getOutboundType())
                    .fromBranch(
                        d.getFromBranch() != null ? branchMapper.toEntity(d.getFromBranch()) : null)
                    .supplier(
                        d.getSupplier() != null ? supplierMapper.toEntity(d.getSupplier()) : null)
                    .toBranch(
                        d.getToBranch() != null ? branchMapper.toEntity(d.getToBranch()) : null)
                    .createdDate(d.getCreatedDate())
                    .outboundDate(d.getOutboundDate())
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
        .id(entity.getId())
        .outboundType(entity.getOutboundType())
        .fromBranch(
            entity.getFromBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .toBranch(
            entity.getToBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                : null)
        .createdDate(entity.getCreatedDate())
        .outboundCode(entity.getOutboundCode())
        .outboundDate(entity.getOutboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .approvedBy(
            entity.getApprovedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getApprovedBy())
                : null)
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .note(entity.getNote())
        .createdBy(
            entity.getCreatedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .outboundDate(entity.getOutboundDate())
        .outboundDetails(
            entity.getOutboundDetails() != null
                ? entity.getOutboundDetails().stream()
                    .map(outboundDetailMapper::toDTOWithBatch)
                    .collect(Collectors.toList())
                : null)
        .outboundProductDetails(
            entity.getOutboundProductDetails() != null
                ? entity.getOutboundProductDetails().stream()
                    .map(outboundProductDetailMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  } // Helper method to convert OutboundEntity to OutboundDTO

  public Outbound convertToDTOWithProductDetail(
      OutboundEntity entity, List<OutboundProductDetailEntity> outboundProductDetails) {
    // Initialize the OutboundDTO with basic fields
    Outbound outboundDTO =
        Outbound.builder()
            .id(entity.getId())
            .outboundType(entity.getOutboundType())
            .fromBranch(
                entity.getFromBranch() != null
                    ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                    : null)
            .supplier(
                entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
            .toBranch(
                entity.getToBranch() != null
                    ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                    : null)
            .createdDate(entity.getCreatedDate())
            .outboundDate(entity.getOutboundDate())
            .totalPrice(entity.getTotalPrice())
            .isApproved(entity.getIsApproved())
            .approvedBy(
                entity.getApprovedBy() != null
                    ? userMapper.convertToDtoBasicInfo(entity.getApprovedBy())
                    : null)
            .status(entity.getStatus())
            .taxable(entity.getTaxable())
            .note(entity.getNote())
            .createdBy(
                entity.getCreatedBy() != null
                    ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                    : null)
            .outboundDetails(
                entity.getOutboundDetails() != null
                    ? entity.getOutboundDetails().stream()
                        .map(outboundDetailMapper::toDTOWithBatch)
                        .collect(Collectors.toList())
                    : null)
            .build();

    // Collect Batch IDs from OutboundDetails for easy lookup
    Set<Long> batchIdsInOutboundDetails =
        entity.getOutboundDetails().stream()
            .map(OutboundDetailEntity::getBatch)
            .filter(Objects::nonNull)
            .map(BatchEntity::getId)
            .collect(Collectors.toSet());

    // Use a Map to store unique ProductDTOs by product ID
    Map<Long, Product> productMap = new HashMap<>();

    // Process OutboundProductDetails to build ProductDTOs and include batches only if they exist in
    // outboundDetails
    for (OutboundProductDetailEntity opd : outboundProductDetails) {
      Product product = productMapper.toDTO(opd.getProduct());

      // Fetch or create ProductDTO
      Product productDTO =
          productMap.computeIfAbsent(
              product.getId(),
              id ->
                  Product.builder()
                      .id(product.getId())
                      .productName(
                          product.getProductName()) // Map other product-specific fields if needed
                      .batches(new ArrayList<>())
                      .build());

      // Add batches only if they exist in outboundDetails
      if (product.getBatches() != null) {
        for (Batch batch : product.getBatches()) {
          if (batchIdsInOutboundDetails.contains(batch.getId())) {
            productDTO
                .getBatches()
                .add(
                    Batch.builder()
                        .id(batch.getId())
                        .batchCode(
                            batch.getBatchCode()) // Map other batch-specific fields if needed
                        .build());
          }
        }
      }
    }

    // Set the compiled products in OutboundDTO
    outboundDTO.setProducts(new ArrayList<>(productMap.values()));

    return outboundDTO;
  }

  // Helper method to convert OutboundEntity to Outbound with just basic info
  public Outbound convertToDtoBasicInfo(OutboundEntity entity) {
    return Outbound.builder()
        .id(entity.getId())
        .outboundCode(entity.getOutboundCode())
        .outboundDate(entity.getOutboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .status(entity.getStatus())
        .fromBranch(
            entity.getFromBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getFromBranch())
                : null)
        .supplier(entity.getSupplier() != null ? supplierMapper.toDTO(entity.getSupplier()) : null)
        .toBranch(
            entity.getToBranch() != null
                ? branchMapper.convertToDTOBasicInfo(entity.getToBranch())
                : null)
        .createdDate(entity.getCreatedDate())
        .outboundCode(entity.getOutboundCode())
        .outboundDate(entity.getOutboundDate())
        .totalPrice(entity.getTotalPrice())
        .isApproved(entity.getIsApproved())
        .approvedBy(
            entity.getApprovedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getApprovedBy())
                : null)
        .status(entity.getStatus())
        .taxable(entity.getTaxable())
        .note(entity.getNote())
        .createdBy(
            entity.getCreatedBy() != null
                ? userMapper.convertToDtoBasicInfo(entity.getCreatedBy())
                : null)
        .outboundDate(entity.getOutboundDate())
        .build();
  }
}
