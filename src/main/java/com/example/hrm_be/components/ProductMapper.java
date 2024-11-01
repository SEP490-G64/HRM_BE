package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
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
  @Autowired @Lazy private UnitConversionMapper unitConversionMapper;
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
                    .id(d.getId())
                    .productName(d.getProductName())
                    .registrationCode(d.getRegistrationCode())
                    .urlImage(d.getUrlImage())
                    //                    .unitConversions(
                    //                        d.getUnitConversions() != null
                    //                             ? d.getUnitConversions().stream()
                    //                                 .map(unitConversionMapper::toEntity)
                    //                                        .collect(Collectors.toList())
                    //                                        : null)
                    .specialConditions(
                        d.getSpecialConditions() != null
                            ? d.getSpecialConditions().stream()
                                .map(specialConditionMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
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
                    .branchProducs(
                        d.getBranchProducts() != null
                            ? d.getBranchProducts().stream()
                                .map(branchProductMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to convert ProductEntity to ProductDTO
  private Product convertToDTO(ProductEntity entity) {
    return Product.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .activeIngredient(entity.getActiveIngredient())
        .excipient(entity.getExcipient())
        .formulation(entity.getFormulation())
        .inboundPrice(entity.getInboundPrice())
        .sellPrice(entity.getSellPrice())
        .status(entity.getStatus())
        .unitConversions(
            entity.getUnitConversions() != null
                ? entity.getUnitConversions().stream()
                    .map(unitConversionMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .specialConditions(
            entity.getSpecialConditions() != null
                ? entity.getSpecialConditions().stream()
                    .map(specialConditionMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .baseUnit(
            entity.getBaseUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                : null)
        .branchProducts(
            entity.getBranchProducs() != null
                ? entity.getBranchProducs().stream()
                    .map(branchProductMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .category(
            entity.getCategory() != null ? productCategoryMapper.toDTO(entity.getCategory()) : null)
        .type(entity.getType() != null ? productTypeMapper.toDTO(entity.getType()) : null)
        .manufacturer(
            entity.getManufacturer() != null
                ? manufacturerMapper.toDTO(entity.getManufacturer())
                : null)
        .build();
  }

  // Helper method to convert ProductEntity to ProductDTO
  public Product convertToBaseInfo(ProductEntity entity) {
    return Product.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
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
        .build();
  }

  public Product convertToDTOWithBatch(ProductEntity entity) {
    return Product.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .activeIngredient(entity.getActiveIngredient())
        .excipient(entity.getExcipient())
        .formulation(entity.getFormulation())
        .status(entity.getStatus())
        .baseUnit(
            entity.getBaseUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                : null)
        .batches(
            entity.getBatches() != null
                ? entity.getBatches().stream().map(batchMapper::toDTO).collect(Collectors.toList())
                : null)
        .build();
  }

  public ProductBaseDTO convertToProductBaseDTO(ProductEntity entity) {
    return ProductBaseDTO.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .activeIngredient(entity.getActiveIngredient())
        .excipient(entity.getExcipient())
        .formulation(entity.getFormulation())
        .inboundPrice(entity.getInboundPrice())
        .sellPrice(entity.getSellPrice())
        .status(entity.getStatus())
        .baseUnit(entity.getBaseUnit() != null ? entity.getBaseUnit().getUnitName() : null)
        .categoryName(entity.getCategory() != null ? entity.getCategory().getCategoryName() : null)
        .typeName(entity.getType() != null ? entity.getType().getTypeName() : null)
        .manufacturerName(
            entity.getManufacturer() != null
                ? entity.getManufacturer().getManufacturerName()
                : null)
        .build();
  }
}
