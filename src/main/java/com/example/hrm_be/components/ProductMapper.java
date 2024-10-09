package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

  @Autowired @Lazy private ManufacturerMapper manufacturerMapper;
  @Autowired @Lazy private ProductCategoryMapper productCategoryMapper;
  @Autowired @Lazy private ProductTypeMapper productTypeMapper;
  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired @Lazy private InboundDetailsMapper inboundDetailsMapper;
  @Autowired @Lazy private SpecialConditionMapper specialConditionMapper;
  @Autowired @Lazy private BatchMapper batchMapper;
  @Autowired @Lazy private BranchProductMapper branchProductMapper;
  @Autowired @Lazy private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;
  @Autowired @Lazy private ProductSuppliersMapper productSuppliersMapper;

  // Convert ProductEntity to ProductDTO
  public Product toDTO(ProductEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert ProductDTO to ProductEntity
  public ProductEntity toEntity(Product dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                ProductEntity.builder()
                    .productName(d.getProductName())
                    .productCode(d.getProductCode())
                    .registrationCode(d.getRegistrationCode())
                    .urlImage(d.getUrlImage())
                    .manufacturer(
                        d.getManufacturer() != null
                            ? manufacturerMapper.toEntity(d.getManufacturer())
                            : null)
                    .category(
                        d.getCategory() != null
                            ? productCategoryMapper.toEntity(d.getCategory())
                            : null)
                    .type(d.getType() != null ? productTypeMapper.toEntity(d.getType()) : null)
                    .activeIngredient(d.getActiveIngredient())
                    .excipient(d.getExcipient())
                    .formulation(d.getFormulation())
                    .inboundPrice(d.getInboundPrice())
                    .sellPrice(d.getSellPrice())
                    .status(d.getStatus())
                    .baseUnit(
                        d.getBaseUnit() != null
                            ? unitOfMeasurementMapper.toEntity(d.getBaseUnit())
                            : null)
                    .inboundDetails(
                        d.getInboundDetails() != null
                            ? d.getInboundDetails().stream()
                                .map(inboundDetailsMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .specialConditions(
                        d.getSpecialConditions() != null
                            ? d.getSpecialConditions().stream()
                                .map(specialConditionMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .batches(
                        d.getBatches() != null
                            ? d.getBatches().stream()
                                .map(batchMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .branchProducs(
                        d.getBranchProducs() != null
                            ? d.getBranchProducs().stream()
                                .map(branchProductMapper::toEntity)
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

  // Helper method to convert ProductEntity to ProductDTO
  private Product convertToDTO(ProductEntity entity) {
    return Product.builder()
        .productName(entity.getProductName())
        .productCode(entity.getProductCode())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .manufacturer(
            entity.getManufacturer() != null
                ? manufacturerMapper.toDTO(entity.getManufacturer())
                : null)
        .category(
            entity.getCategory() != null ? productCategoryMapper.toDTO(entity.getCategory()) : null)
        .type(entity.getType() != null ? productTypeMapper.toDTO(entity.getType()) : null)
        .activeIngredient(entity.getActiveIngredient())
        .excipient(entity.getExcipient())
        .formulation(entity.getFormulation())
        .inboundPrice(entity.getInboundPrice())
        .sellPrice(entity.getSellPrice())
        .status(entity.getStatus())
        .baseUnit(
            entity.getBaseUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                : null)
        .inboundDetails(
            entity.getInboundDetails() != null
                ? entity.getInboundDetails().stream()
                    .map(inboundDetailsMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .specialConditions(
            entity.getSpecialConditions() != null
                ? entity.getSpecialConditions().stream()
                    .map(specialConditionMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .batches(
            entity.getBatches() != null
                ? entity.getBatches().stream().map(batchMapper::toDTO).collect(Collectors.toList())
                : null)
        .branchProducs(
            entity.getBranchProducs() != null
                ? entity.getBranchProducs().stream()
                    .map(branchProductMapper::toDTO)
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
