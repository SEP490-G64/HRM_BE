package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BATCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.OUTBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.PRODUCT;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.repositories.OutboundDetailRepository;
import com.example.hrm_be.repositories.OutboundProductDetailRepository;
import com.example.hrm_be.repositories.OutboundRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.repositories.UnitConversionRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.WplUtil;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.engines.EthereumIESEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OutboundServiceImpl implements OutboundService {

  @Autowired private OutboundRepository outboundRepository;

  @Autowired private OutboundMapper outboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private BatchMapper batchMapper;
  @Autowired private ProductMapper productMapper;
  @Autowired private UserMapper userMapper;
  @Autowired private BranchProductMapper branchProductMapper;
  @Autowired private BranchBatchMapper branchBatchMapper;
  @Autowired private OutboundProductDetailMapper outboundProductDetailMapper;

  @Autowired private UserService userService;
  @Autowired private BatchService batchService;
  @Autowired private BranchService branchService;
  @Autowired private OutboundProductDetailService outboundProductDetailService;
  @Autowired private OutboundDetailService outboundDetailService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductService productService;
  @Autowired private UnitConversionService unitConversionService;

  @Override
  public Outbound getById(Long id) {
    // Fetch the OutboundEntity by ID
    OutboundEntity outboundEntity =
        outboundRepository
            .findById(id)
            .orElseThrow(() -> new HrmCommonException("Outbound not found with id: " + id));

    // Map basic OutboundEntity details to OutboundDetailDTO
    Outbound outboundDetailDTO = outboundMapper.convertToDtoBasicInfo(outboundEntity);

    // Populate productsWithoutBatch from OutboundProductDetails
    List<OutboundProductDetail> productsWithoutBatch =
        outboundProductDetailService.findByOutbound(id).stream()
            .map(
                outboundProductDetail -> {
                  OutboundProductDetail productDetailDTO = new OutboundProductDetail();

                  // Set Product details
                  Product productDTO = new Product();
                  productDTO.setId(outboundProductDetail.getProduct().getId());
                  productDTO.setProductName(outboundProductDetail.getProduct().getProductName());
                  productDTO.setRegistrationCode(
                      outboundProductDetail.getProduct().getRegistrationCode());
                  productDetailDTO.setProduct(productDTO);

                  // Set outbound quantity and price
                  productDetailDTO.setOutboundQuantity(outboundProductDetail.getOutboundQuantity());
                  productDetailDTO.setPrice(outboundProductDetail.getPrice());

                  return productDetailDTO;
                })
            .collect(Collectors.toList());

    // Populate productsWithBatch from OutboundDetails
    List<OutboundProductDetail> productsWithBatch =
        outboundDetailService.findByOutbound(id).stream()
            .map(
                outboundDetail -> {
                  OutboundProductDetail productWithBatchDetailDTO = new OutboundProductDetail();

                  // Set Product details
                  Product productDTO = new Product();
                  productDTO.setId(outboundDetail.getBatch().getProduct().getId());
                  productDTO.setProductName(
                      outboundDetail.getBatch().getProduct().getProductName());
                  productDTO.setRegistrationCode(
                      outboundDetail.getBatch().getProduct().getRegistrationCode());
                  productWithBatchDetailDTO.setProduct(productDTO);
                  // Set Batch details
                  Batch batchDTO = new Batch();
                  batchDTO.setId(outboundDetail.getBatch().getId());
                  batchDTO.setBatchCode(outboundDetail.getBatch().getBatchCode());
                  batchDTO.setExpireDate(outboundDetail.getBatch().getExpireDate());
                  productWithBatchDetailDTO.setBatch(batchDTO);

                  // Set outbound quantity and price
                  productWithBatchDetailDTO.setOutboundQuantity(outboundDetail.getQuantity());
                  productWithBatchDetailDTO.setPrice(outboundDetail.getPrice());

                  return productWithBatchDetailDTO;
                })
            .collect(Collectors.toList());

    // Combine the two lists
    List<OutboundProductDetail> combinedProducts = new ArrayList<>();
    combinedProducts.addAll(productsWithoutBatch);
    combinedProducts.addAll(productsWithBatch);
    // Set the lists in OutboundDetailDTO
    outboundDetailDTO.setOutboundProductDetails(combinedProducts);

    return outboundDetailDTO;
  }

  @Override
  public Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return outboundRepository.findAll(pageable).map(dao -> outboundMapper.toDTO(dao));
  }

  // Method to approve an outbound record
  @Override
  public Outbound approve(Long id, boolean accept) {
    OutboundEntity oldOutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldOutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldOutboundEntity)
        .map(op -> op.toBuilder().isApproved(accept).approvedBy(userEntity).build())
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  @Override
  @Transactional
  public Outbound saveOutbound(CreateOutboundRequest request) {
    Optional<OutboundEntity> unsavedOutbound = outboundRepository.findById(request.getOutboundId());
    OutboundEntity outboundEntity =
        unsavedOutbound.orElseThrow(() -> new HrmCommonException(OUTBOUND.NOT_EXIST));
    BranchEntity fromBranch =
        branchMapper.toEntity(branchService.getById(request.getFromBranch().getId()));

    if (fromBranch == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }

    outboundProductDetailService.deleteByOutboundId(request.getOutboundId());
    outboundDetailService.deleteByOutboundId(request.getOutboundId());
    Outbound updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId()) // Retain the existing ID
            .outBoundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.BAN_NHAP)
            .outboundType(request.getOutboundType())
            .createdBy(request.getCreatedBy())
            .toBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getToBranch()))
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .taxable(request.getTaxable())
            .build();
    // Save the updated entity back to the repository
    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(updatedOutbound));

    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();

    // Process each product or batch in the request
    for (OutboundProductDetail productDetail : request.getOutboundProductDetails()) {
      Product product = productDetail.getProduct();
      Batch batch = productDetail.getBatch();

      // If batch information is provided, process as a batch detail
      if (batch != null) {
        BatchEntity batchEntity = batchMapper.toEntity(batchService.getById(batch.getId()));
        BigDecimal realityQuantity = branchBatchService.findQuantityByBatchIdAndBranchId(
                batch.getId(), fromBranch.getId());

        if (realityQuantity.compareTo(productDetail.getOutboundQuantity()) < 0) {
          throw new HrmCommonException(
              "Insufficient stock for batch: " + batch.getBatchCode());
        }

        OutboundDetailEntity existingBatchDetail =
            outboundDetailService.findByOutboundAndBatch(updatedOutboundEntity.getId(), batch.getId());

        OutboundDetailEntity outboundDetail;
        if (existingBatchDetail != null) {
          // Update existing batch detail with new quantity
          outboundDetail = existingBatchDetail;
          outboundDetail.setQuantity(productDetail.getOutboundQuantity());
        } else {
          // Create a new outbound batch detail
          outboundDetail =
              OutboundDetailEntity.builder()
                  .outbound(updatedOutboundEntity)
                  .quantity(productDetail.getOutboundQuantity())
                  .batch(batchEntity)
                  .build();
        }
        outboundDetailEntities.add(outboundDetail);
      } else {
        // Process as a product detail if no batch is specified
        ProductEntity productEntity = productMapper.toEntity(productService.getById(product.getId()));

        // Fetch quantity from BranchProduct
        BranchProductEntity branchProduct =
                branchProductMapper.toEntity(branchProductService
                .getByBranchIdAndProductId(fromBranch.getId(), productEntity.getId()));

        if (branchProduct.getQuantity().compareTo(productDetail.getOutboundQuantity()) < 0) {
          throw new HrmCommonException(
              "Insufficient stock for product: " + productEntity.getProductName());
        }

        OutboundProductDetailEntity existingProductDetail =
            outboundProductDetailService.findByOutboundAndProduct(
                updatedOutboundEntity.getId(), productEntity.getId());

        OutboundProductDetailEntity outboundProductDetail;
        if (existingProductDetail != null) {
          // Update existing product detail with new quantity
          outboundProductDetail = existingProductDetail;
          outboundProductDetail.setOutboundQuantity(productDetail.getOutboundQuantity());
        } else {
          // Create a new outbound product detail
          outboundProductDetail =
              OutboundProductDetailEntity.builder()
                  .outbound(updatedOutboundEntity)
                  .product(productEntity)
                  .outboundQuantity(productDetail.getOutboundQuantity())
                  .build();
        }
        outboundProductDetailEntities.add(outboundProductDetail);
      }
    }

    // Save all updated or new outbound details back to the repository
    outboundDetailService.saveAll(outboundDetailEntities);
    outboundProductDetailService.saveAll(outboundProductDetailEntities);

    return outboundMapper.toDTO(updatedOutboundEntity);
  }

  @Override
  public Outbound saveOutboundForSell(CreateOutboundRequest request) {
    // Retrieve and validate the OutboundEntity
    OutboundEntity outboundEntity =
        outboundRepository
            .findById(request.getOutboundId())
            .orElseThrow(() -> new HrmCommonException(OUTBOUND.NOT_EXIST));

    BranchEntity fromBranch =
        branchMapper.toEntity(branchService
            .getById(request.getFromBranch().getId()));

    // Delete existing outbound product and batch details
    outboundProductDetailService.deleteByOutboundId(request.getOutboundId());
    outboundDetailService.deleteByOutboundId(request.getOutboundId());

    // Update the OutboundEntity with the new request details
    Outbound updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outBoundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.BAN_NHAP)
            .outboundType(OutboundType.BAN_HANG)
            .createdBy(request.getCreatedBy())
            .toBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getToBranch()))
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .build();

    // Save the updated OutboundEntity
    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(updatedOutbound));
    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    // Process each product or batch in the request
    for (OutboundProductDetail productDetail : request.getOutboundProductDetails()) {
      Product product = productDetail.getProduct();
      ProductEntity productEntity =
              productMapper.toEntity(productService.getById(product.getId()));

      BranchProduct branchProduct = branchProductService
              .getByBranchIdAndProductId(fromBranch.getId(), product.getId());

      if (branchProduct != null) {
        BigDecimal pricePreUnit =
                convertToUnit(product.getId(), productEntity.getBaseUnit().getId(),
                        productEntity.getSellPrice(), productDetail.getTargetUnit(), true);

        BigDecimal outboundQuantity = productDetail.getOutboundQuantity();
        BigDecimal convertedQuantity =
                convertToUnit(
                        productEntity.getId(), productEntity.getBaseUnit().getId(),
                        outboundQuantity,
                        productDetail.getTargetUnit(), true);

        // Get all batches of product then check quantity to see which batch will be out
        List<BranchBatchEntity> productBranchBatches = branchBatchService.
                findByProductAndBranch(product.getId(), fromBranch.getId());

        // Compare outbound quantity to product quantity in branch
        if (branchProduct.getQuantity().compareTo(convertedQuantity) < 0) {
          throw new HrmCommonException(
                  "Insufficient stock for product: " + productEntity.getProductName());
        }

        if (!productBranchBatches.isEmpty()) {
          BigDecimal remainingQuantity = convertedQuantity;

          // For product have batch
          // Iterate through each batch to issue stock in order of earliest expiry date
          for (BranchBatchEntity branchBatch : productBranchBatches) {
            Batch batchDTO = batchService.getById(branchBatch.getBatch().getId());

            // Check if the current batch has enough quantity to meet the remaining requirement
            if (branchBatch.getQuantity().compareTo(remainingQuantity) < 0) {
              // If the current batch is insufficient, issue all of it and update the remaining quantity
              BigDecimal priceOutboundDetail = branchBatch.getQuantity().multiply(pricePreUnit);
              this.addOutboundDetailForSell(updatedOutboundEntity, batchDTO,
                      convertToUnit(product.getId(), productEntity.getBaseUnit().getId(),
                      branchBatch.getQuantity(), productDetail.getTargetUnit(), false),
                      priceOutboundDetail, outboundDetailEntities);

              totalPrice = totalPrice.add(priceOutboundDetail);
              remainingQuantity = remainingQuantity.subtract(branchBatch.getQuantity());
              branchBatch.setQuantity(BigDecimal.ZERO);
              branchBatchService.save(branchBatchMapper.toDTO(branchBatch));
            } else {
              BigDecimal priceOutboundDetail = remainingQuantity.multiply(pricePreUnit);
              // Issue only the required quantity and deduct it from the batch
              this.addOutboundDetailForSell(updatedOutboundEntity, batchDTO,
                      convertToUnit(product.getId(), productEntity.getBaseUnit().getId(),
                      remainingQuantity, productDetail.getTargetUnit(), false),
                      priceOutboundDetail, outboundDetailEntities);

              totalPrice = totalPrice.add(priceOutboundDetail);
              branchBatch.setQuantity(branchBatch.getQuantity().subtract(remainingQuantity));
              remainingQuantity = BigDecimal.ZERO;
              branchBatchService.save(branchBatchMapper.toDTO(branchBatch));
              break;
            }
          }
        }
        else {
          BigDecimal priceOutboundProductDetail = outboundQuantity.multiply(pricePreUnit);
          OutboundProductDetailEntity outboundProductDetail =
                  OutboundProductDetailEntity.builder()
                          .outbound(updatedOutboundEntity)
                          .product(productEntity)
                          .outboundQuantity(outboundQuantity)
                          .price(priceOutboundProductDetail)
                          .unitOfMeasurement(
                                  productDetail.getTargetUnit() != null
                                          ? unitOfMeasurementMapper.toEntity(productDetail.getTargetUnit())
                                          : productEntity.getBaseUnit())
                          .build();
          totalPrice = totalPrice.add(priceOutboundProductDetail);
          outboundProductDetailEntities.add(outboundProductDetail);
        }

        // save BranchProduct
        branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
        branchProduct.setProduct(product);
        branchProductService.save(branchProduct);
      }
      else {
        throw new HrmCommonException(HrmConstant.ERROR.BRANCHPRODUCT.NOT_EXIST);
      }
    }

    // Save all outbound details to the repositories
    outboundDetailService.saveAll(outboundDetailEntities);
    outboundProductDetailService.saveAll(outboundProductDetailEntities);

    // Update the Outbound status and save it
    updatedOutboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    updatedOutboundEntity.setTotalPrice(totalPrice);
    OutboundEntity finalOutboundEntity = outboundRepository.save(updatedOutboundEntity);

    // Convert and return the Outbound DTO
    return outboundMapper.toDTO(finalOutboundEntity);
  }

  @Override
  @Transactional
  public Outbound submitOutboundToSystem(Long outboundId) {
    // Retrieve the OutboundEntity
    OutboundEntity outbound = outboundRepository.findById(outboundId).orElse(null);
    Outbound outboundEntity = outboundMapper.toDTO(outbound);
    Branch fromBranch = outboundEntity.getFromBranch();

    boolean taxable = outbound.getTaxable();
    BigDecimal totalPrice = BigDecimal.ZERO;

    List<OutboundProductDetail> outboundProductDetails =
            outboundProductDetailService.findByOutbound(outboundId);
    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();

    // Process each OutboundProductDetail
    for (OutboundProductDetail productDetail : outboundProductDetails) {
      Product product = productDetail.getProduct();
      Product productEntity = productService.getById(product.getId());

      // Find the BranchProduct entity for this product and branch
      BranchProductEntity branchProduct =
          branchProductService.findByBranchAndProduct(fromBranch.getId(), product.getId());

      // Convert the outbound quantity to the product's base unit if a different target unit is
      // specified
      BigDecimal convertedQuantity = BigDecimal.ONE;
          convertToUnit(
                  product.getId(), productEntity.getBaseUnit().getId(),
                  productDetail.getOutboundQuantity(),
                  productDetail.getTargetUnit(), true);

      // Check if sufficient quantity is available
      if (branchProduct.getQuantity().compareTo(convertedQuantity) < 0) {
        throw new HrmCommonException("Insufficient stock for product: " + productEntity.getProductName());
      }

      // Subtract the converted quantity
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
      branchProductService.save(branchProductMapper.toDTO(branchProduct));

      BigDecimal outboundProductDetailPrice = productEntity.getSellPrice().multiply(convertedQuantity);
      BigDecimal updateOutboundProductDetailPrice = outboundProductDetailPrice;
      if (taxable) {
        if (productEntity.getCategory() != null) {
          BigDecimal taxRate = productEntity.getCategory().getTaxRate();
          if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            updateOutboundProductDetailPrice = updateOutboundProductDetailPrice.add(outboundProductDetailPrice.multiply(taxRate).divide(BigDecimal.valueOf(100)));
          }
        }
      }
      productDetail.setPrice(updateOutboundProductDetailPrice);
      totalPrice = totalPrice.add(updateOutboundProductDetailPrice);
      outboundProductDetailEntities.add(outboundProductDetailMapper.toEntity(productDetail));
    }

    List<OutboundDetailEntity> outboundDetails =
            outboundDetailService.findByOutbound(outboundId);

    // Process each OutboundDetail (for batches)
    for (OutboundDetailEntity batchDetail : outboundDetails) {
      BatchEntity batch = batchDetail.getBatch();

      // Find the BranchBatch entity for this batch and branch
      BranchBatchEntity branchBatch =
          branchBatchService
              .getByBranchIdAndBatchId(fromBranch.getId(), batch.getId());

      // Find the BranchProduct entity for this product and branch
      BranchProductEntity branchProduct =
          branchProductService
              .findByBranchAndProduct(
                  fromBranch.getId(), branchBatch.getBatch().getProduct().getId());
      // Convert the outbound quantity to the product's base unit if a different target unit is
      // specified
      BigDecimal convertedQuantity =
          convertToUnit(
              batch.getProduct().getId(), batch.getProduct().getBaseUnit().getId(),
              batchDetail.getQuantity(), unitOfMeasurementMapper.toDTO(batchDetail.getUnitOfMeasurement()), true);

      // Check if sufficient quantity is available
      if (branchBatch.getQuantity().compareTo(convertedQuantity) < 0) {
        throw new HrmCommonException("Insufficient stock for batch: " + batch.getBatchCode());
      }

      // Subtract the converted quantity
      branchBatch.setQuantity(branchBatch.getQuantity().subtract(convertedQuantity));
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
      branchBatchService.save(branchBatchMapper.toDTO(branchBatch));

      BigDecimal outboundDetailPrice = batch.getProduct().getSellPrice().multiply(convertedQuantity);
      BigDecimal updateOutboundDetailPrice = outboundDetailPrice;
      if (taxable) {
        if (batch.getProduct().getCategory() != null) {
          BigDecimal taxRate = batch.getProduct().getCategory().getTaxRate();
          if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            updateOutboundDetailPrice = updateOutboundDetailPrice.add(outboundDetailPrice.multiply(taxRate).divide(BigDecimal.valueOf(100)));
          }
        }
      }
      batchDetail.setPrice(updateOutboundDetailPrice);
      totalPrice = totalPrice.add(updateOutboundDetailPrice);
      outboundDetailEntities.add(batchDetail);
    }

    // Save all outbound details to the repositories
    outboundDetailService.saveAll(outboundDetailEntities);
    outboundProductDetailService.saveAll(outboundProductDetailEntities);

    // Update the Outbound status and save it
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    outboundEntity.setTotalPrice(totalPrice);
    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(outboundEntity));

    // Convert and return the Outbound object
    return outboundMapper.toDTO(updatedOutboundEntity);
  }

  @Override
  public Outbound createInnitOutbound(OutboundType type) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = WplUtil.generateNoteCode(currentDateTime, "OP");
    if (outboundRepository.existsByOutBoundCode(outboundCode)) {
      throw new HrmCommonException(INBOUND.EXIST);
    }
    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));
    BranchEntity branchEntity = userEntity.getBranch();
    if (branchEntity == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }
    OutboundEntity outbound =
        OutboundEntity.builder()
            .createdDate(currentDateTime)
            .outboundType(type)
            .status(OutboundStatus.CHUA_LUU)
            .outBoundCode(outboundCode)
            .createdBy(userEntity)
            .fromBranch(branchEntity)
            .build();
    return Optional.ofNullable(outbound)
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  // Method to delete an outbound record
  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.OUTBOUND.NOT_EXIST); // Error if outbound entity is not found
    }

    outboundDetailService.deleteByOutboundId(id);
    outboundProductDetailService.deleteByOutboundId(id);
    outboundRepository.deleteById(id); // Delete the outbound entity by ID
  }

  private BigDecimal convertToUnit(Long productId, Long baseUnitId, BigDecimal quantity,
                                   UnitOfMeasurement targetUnit, Boolean toBaseUnit) {
    // If the target unit is the same as the base unit, no conversion is needed
    if (targetUnit == null || targetUnit.getId().equals(baseUnitId)) {
      return quantity;
    }

    // Check if conversion is from smaller to larger or larger to smaller
    UnitConversionEntity conversion =
            unitConversionService.findByProductIdAndLargerUnitIdAndSmallerUnitId(
                productId, baseUnitId, targetUnit.getId());

    if (conversion != null) {
      if (toBaseUnit) {
        return quantity.divide(
                BigDecimal.valueOf(conversion.getFactorConversion()), RoundingMode.HALF_UP);
      }
      else {
        return quantity.multiply(BigDecimal.valueOf(conversion.getFactorConversion()));
      }
    } else {
      return BigDecimal.ZERO;
    }
  }

  private void addOutboundDetailForSell(OutboundEntity outboundEntity, Batch batchDTO,
                                 BigDecimal quantity, BigDecimal price, List<OutboundDetailEntity> outboundDetailEntities) {
    OutboundDetailEntity outboundDetail = OutboundDetailEntity.builder()
            .outbound(outboundEntity)
            .batch(batchMapper.toEntity(batchDTO))
            .quantity(quantity)
            .price(price)
            .unitOfMeasurement(
                    batchMapper
                            .toEntity(batchDTO)
                            .getProduct()
                            .getBaseUnit()) // Use product's base unit for batch
            .build();
    outboundDetailEntities.add(outboundDetail);
  }
}
