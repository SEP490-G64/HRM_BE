package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

  // Helper method to convert ProductEntity to ProductDTO by branchId
  public Product convertToDTOByBranchId(ProductEntity entity, Long branchId) {
    return Optional.ofNullable(entity).map(e -> {
      // Find the BranchProductEntity for the specified branchId
      BranchProductEntity branchBatchInfo = e.getBranchProducs().stream()
          .filter(info -> info.getBranch().getId().equals(branchId))
          .findFirst()
          .orElse(null);

      return Product.builder()
          .id(e.getId())
          .productName(e.getProductName())
          .registrationCode(e.getRegistrationCode())
          .urlImage(e.getUrlImage())
          .activeIngredient(e.getActiveIngredient())
          .excipient(e.getExcipient())
          .formulation(e.getFormulation())
          .inboundPrice(e.getInboundPrice())
          .sellPrice(e.getSellPrice())
          .lastUpdated(branchBatchInfo != null ? branchBatchInfo.getLastUpdated() : null)
          .status(e.getStatus())
          .unitConversions(
              Optional.ofNullable(e.getUnitConversions())
                  .map(conversions -> conversions.stream()
                      .map(unitConversionMapper::toDTO)
                      .collect(Collectors.toList()))
                  .orElse(null))
          .specialConditions(
              Optional.ofNullable(e.getSpecialConditions())
                  .map(conditions -> conditions.stream()
                      .map(specialConditionMapper::toDTO)
                      .collect(Collectors.toList()))
                  .orElse(null))
          .baseUnit(
              Optional.ofNullable(e.getBaseUnit())
                  .map(unitOfMeasurementMapper::toDTO)
                  .orElse(null))
          .branchProducts(
              Optional.ofNullable(e.getBranchProducs())
                  .map(products -> products.stream()
                      .map(branchProductMapper::toDTO)
                      .collect(Collectors.toList()))
                  .orElse(null))
          .category(
              Optional.ofNullable(e.getCategory())
                  .map(productCategoryMapper::toDTO)
                  .orElse(null))
          .type(
              Optional.ofNullable(e.getType())
                  .map(productTypeMapper::toDTO)
                  .orElse(null))
          .manufacturer(
              Optional.ofNullable(e.getManufacturer())
                  .map(manufacturerMapper::toDTO)
                  .orElse(null))
          .build();
    }).orElse(null);
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

  public ProductBaseDTO convertToProductForSearchInNotes(ProductEntity entity, Long branchId) {
    // Get branch product for the specific branch
    BranchProductEntity branchProduct =
        entity.getBranchProducs().stream()
            .filter(bp -> bp.getBranch().getId().equals(branchId))
            .findFirst()
            .orElse(null);

    // Get unit of measurements
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

    // Convert batches with BranchBatch quantities
    List<Batch> batchDTOs =
        entity.getBatches() != null
            ? entity.getBatches().stream()
                .map(
                    batch -> {
                      BranchBatchEntity branchBatch =
                          batch.getBranchBatches().stream()
                              .filter(bb -> bb.getBranch().getId().equals(branchId))
                              .findFirst()
                              .orElse(null);

                      return batchMapper.convertToDtoForGetProductInBranch(
                          batch, branchBatch != null ? branchBatch.getQuantity() : null);
                    })
                .collect(Collectors.toList())
            : null;

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
        .batches(batchDTOs)
        .lastUpdated(branchProduct.getLastUpdated()!=null? LocalDateTime.MIN:null)
        .productUnits(unitOfMeasurementList)
        .productQuantity(branchProduct != null ? branchProduct.getQuantity() : BigDecimal.ZERO) //
        // Add product
        // quantity
        .build();
  }

  public ProductBaseDTO convertToProductDto(ProductEntity entity) {
    // Get unit of measurements
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

    // Convert batches with BranchBatch quantities
    List<Batch> batchDTOs =
        entity.getBatches() != null
            ? entity.getBatches().stream()
                .map(
                    batch -> {
                      return batchMapper.convertToDtoForGetProductInBranch(
                          batch,
                          batch.getBranchBatches() != null
                              ? batch.getBranchBatches().stream().toList().stream()
                                  .filter(b -> b.getQuantity() != null) // Lọc các giá trị null
                                  .map(
                                      BranchBatchEntity
                                          ::getQuantity) // Lấy giá trị quantity kiểu BigDecimal
                                  .reduce(BigDecimal.ZERO, BigDecimal::add) // Tính tổng
                              : BigDecimal.ZERO // Nếu danh sách là null, trả về 0);
                          );
                    })
                .collect(Collectors.toList())
            : null;

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
        .batches(batchDTOs)
        .productUnits(unitOfMeasurementList)
        .productQuantity(
            entity.getBranchProducs() != null
                ? entity.getBranchProducs().stream()
                    .map(branchProductMapper::toDTO)
                    .toList()
                    .stream()
                    .filter(product -> product.getQuantity() != null) // Lọc các giá trị null
                    .map(BranchProduct::getQuantity) // Lấy giá trị quantity kiểu BigDecimal
                    .reduce(BigDecimal.ZERO, BigDecimal::add) // Tính tổng
                : BigDecimal.ZERO) // Nếu danh sách là null, trả về 0) //
        // Add product
        // quantity
        .taxRate(entity.getCategory().getTaxRate())
        .build();
  }

  public ProductBaseDTO convertToBranchProduct(ProductEntity entity, Long branchId) {
    // Get branch product for the specific branch
    BranchProductEntity branchProduct =
        entity.getBranchProducs().stream()
            .filter(bp -> bp.getBranch().getId().equals(branchId))
            .findFirst()
            .orElse(null);

    // Convert batches with BranchBatch quantities
    List<Batch> batchDTOs =
        entity.getBatches() != null
            ? entity.getBatches().stream()
                .map(
                    batch -> {
                      BranchBatchEntity branchBatch =
                          batch.getBranchBatches().stream()
                              .filter(bb -> bb.getBranch().getId().equals(branchId))
                              .findFirst()
                              .orElse(null);

                      return batchMapper.convertToDtoForGetProductInBranch(
                          batch, branchBatch != null ? branchBatch.getQuantity() : null);
                    })
                .collect(Collectors.toList())
            : null;

    BigDecimal totalQuantity =
        batchDTOs != null
            ? batchDTOs.stream()
                .map(Batch::getQuantity) // Lấy BigDecimal quantity từ mỗi BatchDto
                .filter(Objects::nonNull) // Loại bỏ giá trị null
                .reduce(BigDecimal.ZERO, BigDecimal::add) // Tính tổng, khởi tạo từ BigDecimal.ZERO
            : BigDecimal.ZERO;

    // Get unit of measurements
    BigDecimal productQuantity =
        branchProduct != null ? branchProduct.getQuantity() : BigDecimal.ZERO;

    // Get unit of measurements
    List<UnitOfMeasurement> unitOfMeasurementList = new ArrayList<>();
    if (entity.getUnitConversions() != null) {
      unitOfMeasurementList =
          entity.getUnitConversions().stream()
              .map(
                  unitConversionEntity -> {
                    UnitConversion unitConversion =
                        unitConversionMapper.toDTO(unitConversionEntity);
                    UnitOfMeasurement smallerUnit = unitConversion.getSmallerUnit();
                    // Tính toán quantity cho unit
                    Double conversionRate =
                        unitConversion.getFactorConversion(); // Hệ số chuyển đổi
                    if (batchDTOs != null) {
                      smallerUnit.setProductUnitQuantity(
                          totalQuantity.multiply(
                              BigDecimal.valueOf(conversionRate))); // Tính quantity
                    } else {
                      smallerUnit.setProductUnitQuantity(
                          productQuantity.multiply(
                              BigDecimal.valueOf(conversionRate))); // Tính quantity
                    }
                    return smallerUnit;
                  })
              .collect(Collectors.toList());
    }

    if (entity.getBaseUnit() != null) {
      UnitOfMeasurement unitOfMeasurement = unitOfMeasurementMapper.toDTO(entity.getBaseUnit());
      if (batchDTOs != null) {
        unitOfMeasurement.setProductUnitQuantity(totalQuantity);
      } else {
        unitOfMeasurement.setProductUnitQuantity(productQuantity);
      }
      unitOfMeasurementList.add(unitOfMeasurement);
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
        .batches(batchDTOs)
        .productUnits(unitOfMeasurementList)
        .productQuantity(branchProduct != null ? branchProduct.getQuantity() : BigDecimal.ZERO) //
        // Add product
        // quantity
        .taxRate(entity.getCategory().getTaxRate())
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
