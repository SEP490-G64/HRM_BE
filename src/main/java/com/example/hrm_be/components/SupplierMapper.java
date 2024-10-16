package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.SupplierEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

  @Autowired @Lazy private PurchaseMapper purchaseMapper;
  @Autowired @Lazy private InboundMapper inboundMapper;
  @Autowired @Lazy private OutboundMapper outboundMapper;
  @Autowired @Lazy private ProductSuppliersMapper productSuppliersMapper;

  // Convert SupplierEntity to SupplierDTO
  public Supplier toDTO(SupplierEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert SupplierDTO to SupplierEntity
  public SupplierEntity toEntity(Supplier dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                SupplierEntity.builder()
                    .id(d.getId())
                    .supplierName(d.getSupplierName())
                    .address(d.getAddress())
                    .email(d.getEmail())
                    .phoneNumber(d.getPhoneNumber())
                    .taxCode(d.getTaxCode())
                    .faxNumber(d.getFaxNumber())
                    .status(d.getStatus())
                    .purchases(
                        d.getPurchases() != null
                            ? d.getPurchases().stream()
                                .map(purchaseMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .inbounds(
                        d.getInbounds() != null
                            ? d.getInbounds().stream()
                                .map(inboundMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .outbounds(
                        d.getOutbounds() != null
                            ? d.getOutbounds().stream()
                                .map(outboundMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .productSuppliers(
                        d.getProductSuppliers() != null
                            ? d.getProductSuppliers().stream()
                                .map(productSuppliersMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert SupplierEntity to SupplierDTO
  private Supplier convertToDTO(SupplierEntity entity) {
    return Supplier.builder()
        .id(entity.getId())
        .supplierName(entity.getSupplierName())
        .address(entity.getAddress())
        .email(entity.getEmail())
        .phoneNumber(entity.getPhoneNumber())
        .taxCode(entity.getTaxCode())
        .faxNumber(entity.getFaxNumber())
        .status(entity.getStatus())
        .purchases(
            entity.getPurchases() != null
                ? entity.getPurchases().stream()
                    .map(purchaseMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .inbounds(
            entity.getInbounds() != null
                ? entity.getInbounds().stream()
                    .map(inboundMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .outbounds(
            entity.getOutbounds() != null
                ? entity.getOutbounds().stream()
                    .map(outboundMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .productSuppliers(
            entity.getProductSuppliers() != null
                ? entity.getProductSuppliers().stream()
                    .map(productSuppliersMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .build();
  }
}
