package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.ProductEntity;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private BatchMapper batchMapper;
  @Autowired @Lazy private TaxMapper taxMapper;
  @Autowired @Lazy private SupplierMapper supplierMapper;
  @Autowired @Lazy private ProductUnitMapMapper productUnitMapMapper;
  @Autowired @Lazy private ProductIngredientMapMapper productIngredientMapMapper;
  @Autowired @Lazy private ProductCategoryMapMapper productCategoryMapMapper;
  @Autowired @Lazy private InventoryMapper inventoryMapper;
  @Autowired @Lazy private ManufacturerMapper manufacturerMapper;
  @Autowired @Lazy private SpecialConditionMapper specialConditionMapper;

  // Convert ProductEntity to Product DTO
  public Product toDTO(ProductEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert Product DTO to ProductEntity
  public ProductEntity toEntity(Product dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                ProductEntity.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .price(e.getPrice())
                    .description(e.getDescription())
                    .image(e.getImage())
                    .barcodeImage(e.getBarcodeImage())
                    .branch(branchMapper.toEntity(e.getBranch()))
                    .batch(batchMapper.toEntity(e.getBatch()))
                    .supplier(supplierMapper.toEntity(e.getSupplier()))
                    .manufacturer(manufacturerMapper.toEntity(e.getManufacturer()))
                    .specialCondition(specialConditionMapper.toEntity(e.getSpecialCondition()))
                    .productUnitMap(
                        e.getProductUnitMaps() != null
                            ? e.getProductUnitMaps().stream()
                                .map(productUnitMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .productIngredientMap(
                        e.getProductIngredientMap() != null
                            ? e.getProductIngredientMap().stream()
                                .map(productIngredientMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .productCateMap(
                        e.getProductCateMap() != null
                            ? e.getProductCateMap().stream()
                                .map(productCategoryMapMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .inventoryEntities(
                        e.getInventory() != null
                            ? e.getInventory().stream()
                                .map(inventoryMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map ProductEntity to Product DTO
  private Product convertToDto(ProductEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Product.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .price(e.getPrice())
                    .description(e.getDescription())
                    .image(e.getImage())
                    .barcodeImage(e.getBarcodeImage())
                    .branch(branchMapper.toDTO(e.getBranch()))
                    .batch(batchMapper.toDTO(e.getBatch()))
                    .supplier(supplierMapper.toDTO(e.getSupplier()))
                    .manufacturer(manufacturerMapper.toDTO(e.getManufacturer()))
                    .specialCondition(specialConditionMapper.toDTO(e.getSpecialCondition()))
                    .productUnitMaps(
                        e.getProductUnitMap() != null
                            ? e.getProductUnitMap().stream()
                                .map(productUnitMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .productIngredientMap(
                        e.getProductIngredientMap() != null
                            ? e.getProductIngredientMap().stream()
                                .map(productIngredientMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .productCateMap(
                        e.getProductCateMap() != null
                            ? e.getProductCateMap().stream()
                                .map(productCategoryMapMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .inventory(
                        e.getInventoryEntities() != null
                            ? e.getInventoryEntities().stream()
                                .map(inventoryMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .build())
        .orElse(null);
  }
}
