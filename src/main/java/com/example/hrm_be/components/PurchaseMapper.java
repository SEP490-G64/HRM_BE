package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Purchase;
import com.example.hrm_be.models.entities.PurchaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PurchaseMapper {

  @Autowired @Lazy private SupplierMapper supplierMapper;

  // Convert PurchaseEntity to PurchaseDTO
  public Purchase toDTO(PurchaseEntity entity) {
    return Optional.ofNullable(entity)
        .map(this::convertToDTO)
        .orElse(null);
  }

  // Convert PurchaseDTO to PurchaseEntity
  public PurchaseEntity toEntity(Purchase dto) {
    return Optional.ofNullable(dto)
        .map(d -> PurchaseEntity.builder()
            .id(d.getId())
            .supplier(
                d.getSupplier() != null
                    ? supplierMapper.toEntity(d.getSupplier())
                    : null)
            .amount(d.getAmount())
            .purchaseDate(d.getPurchaseDate())
            .remainDebt(d.getRemainDebt())
            .build())
        .orElse(null);
  }

  // Helper method to convert PurchaseEntity to PurchaseDTO
  private Purchase convertToDTO(PurchaseEntity entity) {
    return Purchase.builder()
        .id(entity.getId())
        .supplier(
            entity.getSupplier() != null
                ? supplierMapper.toDTO(entity.getSupplier())
                : null)
        .amount(entity.getAmount())
        .purchaseDate(entity.getPurchaseDate())
        .remainDebt(entity.getRemainDebt())
        .build();
  }
}

