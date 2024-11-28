package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.BatchDto;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class BatchMapper {

  @Autowired @Lazy private UnitConversionMapper unitConversionMapper;
  @Autowired @Lazy private OutboundDetailMapper outboundDetailMapper;
  @Autowired @Lazy private BranchBatchMapper branchBatchMapper;
  @Autowired @Lazy private InboundBatchDetailMapper inboundBatchDetailMapper;
  @Autowired @Lazy private ProductMapper productMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;

  // Convert BatchEntity to BatchDTO
  public Batch toDTO(BatchEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  // Convert BatchDTO to BatchEntity
  public BatchEntity toEntity(Batch dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                BatchEntity.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .produceDate(e.getProduceDate())
                    .batchStatus(e.getBatchStatus())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .outboundDetails(
                        e.getOutboundDetails() != null
                            ? e.getOutboundDetails().stream()
                                .map(outboundDetailMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .branchBatches(
                        e.getBranchBatches() != null
                            ? e.getBranchBatches().stream()
                                .map(branchBatchMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .inboundBatchDetail(
                        e.getInboundBatchDetails() != null
                            ? e.getInboundBatchDetails().stream()
                                .map(inboundBatchDetailMapper::toEntity)
                                .collect(Collectors.toList())
                            : null)
                    .product(e.getProduct() != null ? productMapper.toEntity(e.getProduct()) : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BatchEntity to BatchDTO
  private Batch convertToDto(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .produceDate(e.getProduceDate())
                    .batchStatus(e.getBatchStatus())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .outboundDetails(
                        e.getOutboundDetails() != null
                            ? e.getOutboundDetails().stream()
                                .map(outboundDetailMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .inboundBatchDetails(
                        e.getInboundBatchDetail() != null
                            ? e.getInboundBatchDetail().stream()
                                .map(inboundBatchDetailMapper::toDTO)
                                .collect(Collectors.toList())
                            : null)
                    .product(e.getProduct() != null ? productMapper.toDTO(e.getProduct()) : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BatchEntity to BatchDTO
  public Batch convertToDtoBasicInfo(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .batchStatus(e.getBatchStatus())
                    .produceDate(e.getProduceDate())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .product(
                        e.getProduct() != null
                            ? productMapper.convertToBaseInfo(e.getProduct())
                            : null)
                    .build())
        .orElse(null);
  }

  public Batch convertToDtoBasicInfoWithProductBaseDto(BatchEntity entity, Long branchId) {
    return Optional.ofNullable(entity)
        .map(
            e -> {
              // Filter and find the BranchBatchInfo by branchId
              BranchBatchEntity branchBatchInfo =
                  e.getBranchBatches().stream()
                      .filter(info -> info.getBranch().getId().equals(branchId))
                      .findFirst()
                      .orElse(null); // Default to null if not found

              // Retrieve quantity from the filtered branchBatchInfo, if available
              BigDecimal quantity =
                  branchBatchInfo != null ? branchBatchInfo.getQuantity() : BigDecimal.ZERO; //
              // Default to
              // 0 if not found
              // Build and return the BatchDTO with the required information
              return Batch.builder()
                  .id(e.getId())
                  .batchCode(e.getBatchCode())
                  .batchStatus(e.getBatchStatus())
                  .produceDate(e.getProduceDate())
                  .expireDate(e.getExpireDate())
                  .inboundPrice(e.getInboundPrice())
                  .productId(e.getProduct().getId())
                  .productName(e.getProduct().getProductName())
                  .registrationCode(e.getProduct().getRegistrationCode())
                  .urlImage(e.getProduct().getUrlImage())
                  .activeIngredient(e.getProduct().getActiveIngredient())
                  .excipient(e.getProduct().getExcipient())
                  .formulation(e.getProduct().getFormulation())
                  .inboundPrice(e.getProduct().getInboundPrice())
                  .sellPrice(e.getProduct().getSellPrice())
                  .status(e.getProduct().getStatus())
                  .baseUnit(
                      e.getProduct().getBaseUnit() != null
                          ? e.getProduct().getBaseUnit().getUnitName()
                          : null)
                  .categoryName(
                      e.getProduct().getCategory() != null
                          ? e.getProduct().getCategory().getCategoryName()
                          : null)
                  .typeName(
                      e.getProduct().getType() != null
                          ? e.getProduct().getType().getTypeName()
                          : null)
                  .manufacturerName(
                      e.getProduct().getManufacturer() != null
                          ? e.getProduct().getManufacturer().getManufacturerName()
                          : null)
                  .quantity(quantity) // Set the filtered quantity here
                  .build();
            })
        .orElse(null); // Return null if the entity is null
  }

  public Batch convertToDtoBasicInfoByBranchId(BatchEntity entity, Long branchId) {
    return Optional.ofNullable(entity)
        .map(
            e -> {
              // Filter and find the BranchBatchInfo by branchId
              BranchBatchEntity branchBatchInfo =
                  e.getBranchBatches().stream()
                      .filter(info -> info.getBranch().getId().equals(branchId))
                      .findFirst()
                      .orElse(null); // Default to null if not found

              // Retrieve quantity from the filtered branchBatchInfo, if available
              BigDecimal quantity =
                  branchBatchInfo != null ? branchBatchInfo.getQuantity() : BigDecimal.ZERO; //
              // Default to
              // 0 if not found
              // Build and return the BatchDTO with the required information
              return Batch.builder()
                  .id(e.getId())
                  .batchCode(e.getBatchCode())
                  .batchStatus(e.getBatchStatus())
                  .produceDate(e.getProduceDate())
                  .expireDate(e.getExpireDate())
                  .inboundPrice(e.getInboundPrice())
                  .productId(e.getProduct().getId())
                  .productName(e.getProduct().getProductName())
                  .registrationCode(e.getProduct().getRegistrationCode())
                  .urlImage(e.getProduct().getUrlImage())
                  .activeIngredient(e.getProduct().getActiveIngredient())
                  .excipient(e.getProduct().getExcipient())
                  .formulation(e.getProduct().getFormulation())
                  .inboundPrice(e.getProduct().getInboundPrice())
                  .sellPrice(e.getProduct().getSellPrice())
                  .status(e.getProduct().getStatus())
                  .lastUpdated(branchBatchInfo != null ? branchBatchInfo.getLastUpdated() : null)
                  .unitOfMeasurement(
                      e.getProduct().getBaseUnit() != null
                          ? unitOfMeasurementMapper.toDTO(e.getProduct().getBaseUnit())
                          : null)
                  .categoryName(
                      e.getProduct().getCategory() != null
                          ? e.getProduct().getCategory().getCategoryName()
                          : null)
                  .typeName(
                      e.getProduct().getType() != null
                          ? e.getProduct().getType().getTypeName()
                          : null)
                  .manufacturerName(
                      e.getProduct().getManufacturer() != null
                          ? e.getProduct().getManufacturer().getManufacturerName()
                          : null)
                  .quantity(quantity) // Set the filtered quantity here
                  .build();
            })
        .orElse(null); // Return null if the entity is null
  }

  // Helper method to map BatchEntity to BatchDTO
  public Batch convertToDtoWithCategory(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchStatus(e.getBatchStatus())
                    .batchCode(e.getBatchCode())
                    .produceDate(e.getProduceDate())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .product(
                        e.getProduct() != null
                            ? productMapper.convertToDtoWithCategory(e.getProduct())
                            : null)
                    .build())
        .orElse(null);
  }

  // Helper method to map BatchEntity to BatchDTO
  public Batch convertToDtoForGetProductInBranch(
      BatchEntity entity, BigDecimal branchBatchQuantity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                Batch.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .quantity(branchBatchQuantity)
                    .build())
        .orElse(null);
  }

  public BatchDto convertToDtoWithQuantity(BatchEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                BatchDto.builder()
                    .id(e.getId())
                    .batchCode(e.getBatchCode())
                    .batchStatus(e.getBatchStatus())
                    .produceDate(e.getProduceDate())
                    .expireDate(e.getExpireDate())
                    .inboundPrice(e.getInboundPrice())
                    .quantity(
                        e.getBranchBatches() != null
                            ? entity.getBranchBatches().stream()
                                .map(branchBatchMapper::toDTO)
                                .toList()
                                .stream()
                                .filter(
                                    batch -> batch.getQuantity() != null) // Lọc các giá trị null
                                .map(
                                    BranchBatch
                                        ::getQuantity) // Lấy giá trị quantity kiểu BigDecimal
                                .reduce(BigDecimal.ZERO, BigDecimal::add) // Tính tổng
                            : BigDecimal.ZERO // Nếu danh sách là null, trả về 0
                        )
                    .baseUnit(
                        e.getProduct() != null
                            ? (productMapper.convertToDTOWithBatch(e.getProduct()).getBaseUnit()
                                    != null
                                ? productMapper
                                    .convertToDTOWithBatch(e.getProduct())
                                    .getBaseUnit()
                                    .getUnitName()
                                : null)
                            : null)
                    .unitConversions(
                        e.getProduct() != null
                            ? (productMapper
                                        .convertToDTOWithoutProductInBranchProduct(e.getProduct())
                                        .getUnitConversions()
                                    != null
                                ? productMapper
                                    .convertToDTOWithoutProductInBranchProduct(e.getProduct())
                                    .getUnitConversions()
                                : null)
                            : null)
                    .build())
        .orElse(null);
  }
}
