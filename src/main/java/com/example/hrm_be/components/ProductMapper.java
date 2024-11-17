package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.ProductEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  @Autowired @Lazy private ManufacturerMapper manufacturerMapper;
  @Autowired @Lazy private ProductCategoryMapper productCategoryMapper;
  @Autowired @Lazy private ProductTypeMapper productTypeMapper;
  @Autowired @Lazy private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired @Lazy private UnitConversionMapper unitConversionMapper;
  @Autowired @Lazy private SpecialConditionMapper specialConditionMapper;
  @Autowired @Lazy private BatchMapper batchMapper;
  @Autowired @Lazy private BranchProductMapper branchProductMapper;

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

  // Convert ProductDTO to ProductEntity
  public ProductEntity toEntityWithUnitConversions(Product dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                ProductEntity.builder()
                    .id(d.getId())
                    .productName(d.getProductName())
                    .registrationCode(d.getRegistrationCode())
                    .urlImage(d.getUrlImage())
                    .unitConversions(
                        d.getUnitConversions() != null
                            ? d.getUnitConversions().stream()
                                .map(unitConversionMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
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
  public Product convertToDTOWithoutProductInBranchProduct(ProductEntity entity) {
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
                    .map(branchProductMapper::convertToDTOWithoutProduct)
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

  // Helper method to convert ProductEntity to ProductDTO
  public Product convertToDtoWithCategory(ProductEntity entity) {
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
        .category(
            entity.getCategory() != null ? productCategoryMapper.toDTO(entity.getCategory()) : null)
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
        .unitConversions(
            entity.getUnitConversions() != null
                ? entity.getUnitConversions().stream()
                    .map(unitConversionMapper::toDTO)
                    .collect(Collectors.toList())
                : null)
        .quantity(
            entity.getBranchProducs() != null
                ? entity.getBranchProducs().stream()
                    .map(branchProductMapper::toDTO)
                    .toList()
                    .stream()
                    .filter(product -> product.getQuantity() != null) // Lọc các giá trị null
                    .map(BranchProduct::getQuantity) // Lấy giá trị quantity kiểu BigDecimal
                    .reduce(BigDecimal.ZERO, BigDecimal::add) // Tính tổng
                : BigDecimal.ZERO // Nếu danh sách là null, trả về 0
            )
        .build();
  }

  public ProductSupplierDTO convertToProductSupplier(ProductEntity entity) {
    return ProductSupplierDTO.builder()
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .image(entity.getUrlImage())
        .baseUnit(
            entity.getBaseUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                : null)
        .build();
  }

  public ProductBaseDTO convertToProductForSearchInNotes(ProductEntity entity) {
    List<UnitOfMeasurement> unitOfMeasurementList = new ArrayList<>();
    if (entity.getUnitConversions() != null) {
      unitOfMeasurementList =
          entity.getUnitConversions().stream()
              .map(unitConversionMapper::toDTO)
              .map(UnitConversion::getSmallerUnit)
              .collect(Collectors.toList());
    }
    if (entity.getBaseUnit() != null) {
      unitOfMeasurementList.add(unitOfMeasurementMapper.toDTO(entity.getBaseUnit()));
    }

    return ProductBaseDTO.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .inboundPrice(entity.getInboundPrice())
        .sellPrice(entity.getSellPrice())
        .productBaseUnit(
            entity.getBaseUnit() != null
                ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                : null)
        .batches(
            entity.getBatches() != null
                ? entity.getBatches().stream()
                    .map(batchMapper::convertToDtoForGetProductInBranch)
                    .collect(Collectors.toList())
                : null)
        .productUnits(unitOfMeasurementList)
        .build();
  }

  // Helper method to convert ProductEntity to ProductDTO
  public Product convertToDtoForBatch(ProductEntity entity) {
    return Product.builder()
            .id(entity.getId())
            .productName(entity.getProductName())
            .baseUnit(
                    entity.getBaseUnit() != null
                            ? unitOfMeasurementMapper.toDTO(entity.getBaseUnit())
                            : null)
            .unitConversions(
                    entity.getUnitConversions() != null
                            ? entity.getUnitConversions().stream()
                            .map(unitConversionMapper::toDTO)
                            .collect(Collectors.toList())
                            : null)
            .build();
  }
}
