package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                    .id(d.getId())
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
                    .build())
        .orElse(null);
  }

  // Helper method to convert ProductEntity to ProductDTO
  private Product convertToDTO(ProductEntity entity) {
    return Product.builder()
        .id(entity.getId())
        .productName(entity.getProductName())
        .productCode(entity.getProductCode())
        .registrationCode(entity.getRegistrationCode())
        .urlImage(entity.getUrlImage())
        .activeIngredient(entity.getActiveIngredient())
        .excipient(entity.getExcipient())
        .formulation(entity.getFormulation())
        .inboundPrice(entity.getInboundPrice())
        .sellPrice(entity.getSellPrice())
        .status(entity.getStatus())
        .build();
  }
}
