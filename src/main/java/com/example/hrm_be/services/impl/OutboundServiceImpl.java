package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BATCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.OUTBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.PRODUCT;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.OutboundMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.dtos.Product;
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
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.OutboundService;
import com.example.hrm_be.services.ProductService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
  @Lazy @Autowired private OutboundServiceImpl outboundService;
   @Autowired private BatchService batchService;

  @Autowired private OutboundProductDetailRepository outboundProductDetailRepository;
  @Autowired private BranchProductRepository branchProductRepository;
  @Autowired private UnitConversionRepository unitConversionRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private BranchBatchRepository branchBatchRepository;
  @Autowired private BranchRepository branchRepository;
  @Autowired private OutboundDetailRepository outboundDetailRepository;
  @Autowired private BatchRepository batchRepository;
  @Autowired private OutboundMapper outboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private BatchMapper batchMapper;
  @Autowired private ProductMapper productMapper;
  @Autowired private SupplierMapper supplierMapper;
  @Autowired private WplUtil wplUtil;
  @Autowired private UserService userService;
  @Autowired private ProductService productService;
  @Autowired private UserMapper userMapper;

  @Override
  public Outbound getById(Long id) {
    // Fetch the OutboundEntity by ID
    OutboundEntity outboundEntity = outboundRepository.findById(id)
        .orElseThrow(() -> new HrmCommonException("Outbound not found with id: " + id));

    // Map basic OutboundEntity details to OutboundDetailDTO
    Outbound outboundDetailDTO = new Outbound();
    outboundDetailDTO.setId(outboundEntity.getId());
    outboundDetailDTO.setOutBoundCode(outboundEntity.getOutBoundCode());
    outboundDetailDTO.setOutboundDate(outboundEntity.getOutboundDate());
    outboundDetailDTO.setTotalPrice(outboundEntity.getTotalPrice());
    outboundDetailDTO.setIsApproved(outboundEntity.getIsApproved());
    outboundDetailDTO.setStatus(outboundEntity.getStatus());
    outboundDetailDTO.setFromBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getFromBranch()));
    outboundDetailDTO.setToBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getToBranch()));
    outboundDetailDTO.setSupplier(supplierMapper.toDTO(outboundEntity.getSupplier()));

    // Populate productsWithoutBatch from OutboundProductDetails
    List<OutboundProductDetail> productsWithoutBatch = outboundEntity.getOutboundProductDetails().stream()
        .map(outboundProductDetail -> {
          OutboundProductDetail productDetailDTO = new OutboundProductDetail();

          // Set Product details
          Product productDTO = new Product();
          productDTO.setId(outboundProductDetail.getProduct().getId());
          productDTO.setProductName(outboundProductDetail.getProduct().getProductName());
          productDTO.setProductCode(outboundProductDetail.getProduct().getRegistrationCode());
          productDetailDTO.setProduct(productDTO);

          // Set outbound quantity and price
          productDetailDTO.setOutboundQuantity(outboundProductDetail.getOutboundQuantity());

          return productDetailDTO;
        })
        .collect(Collectors.toList());

    // Populate productsWithBatch from OutboundDetails
    List<OutboundProductDetail> productsWithBatch = outboundEntity.getOutboundDetails().stream()
        .map(outboundDetail -> {
          OutboundProductDetail productWithBatchDetailDTO = new OutboundProductDetail();

          // Set Product details
          Product productDTO = new Product();
          productDTO.setId(outboundDetail.getBatch().getProduct().getId());
          productDTO.setProductName(outboundDetail.getBatch().getProduct().getProductName());
          productDTO.setProductCode(outboundDetail.getBatch().getProduct().getRegistrationCode());
          productWithBatchDetailDTO.setProduct(productDTO);

          // Set Batch details
          Batch batchDTO = new Batch();
          batchDTO.setId(outboundDetail.getBatch().getId());
          batchDTO.setBatchCode(outboundDetail.getBatch().getBatchCode());
          batchDTO.setExpireDate(outboundDetail.getBatch().getExpireDate());
          productWithBatchDetailDTO.setBatch(batchDTO);

          // Set outbound quantity and price
          productWithBatchDetailDTO.setOutboundQuantity(outboundDetail.getQuantity());

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

  // Method to create a new Outbound record
  @Override
  public Outbound create(Outbound outbound) {
    if (outbound == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.EXIST); // Error handling for null outbound object
    }

    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity =
        userMapper.toEntity(userService.findLoggedInfoByEmail(email)); // Get user entity

    return Optional.ofNullable(outbound)
        .map(outboundMapper::toEntity)
        .map(
            e -> {
              e.setCreatedBy(userEntity);
              e.setCreatedDate(LocalDateTime.now());
              e.setStatus(OutboundStatus.CHO_DUYET);
              e.setIsApproved(false);
              return outboundRepository.save(e);
            })
        .map(e -> outboundMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing Outbound record
  @Override
  public Outbound update(Outbound outbound) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(outbound.getId()).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    return Optional.ofNullable(oldoutboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(outbound.getNote())
                    .outboundType(outbound.getOutboundType())
                    .status(outbound.getStatus())
                    .taxable(outbound.getTaxable())
                    .totalPrice(outbound.getTotalPrice())
                    .build())
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  // Method to approve an outbound record
  @Override
  public Outbound approve(Long id, boolean accept) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldoutboundEntity)
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
        branchRepository.findById(request.getFromBranch().getId()).orElse(null);

    if (fromBranch == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }

    outboundProductDetailRepository.deleteByOutboundId(request.getOutboundId());
    outboundDetailRepository.deleteByOutboundId(request.getOutboundId());
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
        BatchEntity batchEntity = batchRepository.findById(batch.getId()).orElse(null);
        if (batchEntity == null) {
          throw new HrmCommonException(BATCH.NOT_EXIST);
        }

        if (branchBatchRepository.findQuantityByBatchIdAndBranchId(
                batchEntity.getId(), fromBranch.getId())
            < productDetail.getOutboundQuantity()) {
          throw new HrmCommonException(
              "Insufficient stock for batch: " + batchEntity.getBatchCode());
        }

        Optional<OutboundDetailEntity> existingBatchDetail =
            outboundDetailRepository.findByOutboundAndBatch(updatedOutboundEntity, batchEntity);

        OutboundDetailEntity outboundDetail;
        if (existingBatchDetail.isPresent()) {
          // Update existing batch detail with new quantity
          outboundDetail = existingBatchDetail.get();
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
        ProductEntity productEntity = productRepository.findById(product.getId()).orElse(null);
        if (productEntity == null) {
          throw new HrmCommonException(PRODUCT.NOT_EXIST);
        }

        // Fetch quantity from BranchProduct
        BranchProductEntity branchProduct =
            branchProductRepository
                .findByBranchAndProduct(fromBranch, productEntity)
                .orElseThrow(
                    () ->
                        new HrmCommonException(
                            "Product not available in branch: " + productEntity.getProductName()));

        if (branchProduct.getQuantity().intValue() < (productDetail.getOutboundQuantity())) {
          throw new HrmCommonException(
              "Insufficient stock for product: " + productEntity.getProductName());
        }

        Optional<OutboundProductDetailEntity> existingProductDetail =
            outboundProductDetailRepository.findByOutboundAndProduct(
                updatedOutboundEntity, productEntity);

        OutboundProductDetailEntity outboundProductDetail;
        if (existingProductDetail.isPresent()) {
          // Update existing product detail with new quantity
          outboundProductDetail = existingProductDetail.get();
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
    outboundDetailRepository.saveAll(outboundDetailEntities);
    outboundProductDetailRepository.saveAll(outboundProductDetailEntities);

    return outboundMapper.toDTO(updatedOutboundEntity);
  }

  @Override
  public Outbound saveOutboundForSell(CreateOutboundRequest request) {
    // Retrieve and validate the OutboundEntity
    OutboundEntity outboundEntity = outboundRepository.findById(request.getOutboundId())
        .orElseThrow(() -> new HrmCommonException(OUTBOUND.NOT_EXIST));

    BranchEntity fromBranch = branchRepository.findById(request.getFromBranch().getId())
        .orElseThrow(() -> new HrmCommonException(BRANCH.NOT_EXIST));

    // Delete existing outbound product and batch details
    outboundProductDetailRepository.deleteByOutboundId(request.getOutboundId());
    outboundDetailRepository.deleteByOutboundId(request.getOutboundId());

    // Update the OutboundEntity with the new request details
    Outbound updatedOutbound = Outbound.builder()
        .id(outboundEntity.getId())
        .outBoundCode(request.getOutboundCode())
        .createdDate(request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
        .status(OutboundStatus.BAN_NHAP)
        .outboundType(OutboundType.BAN_HANG)
        .createdBy(request.getCreatedBy())
        .toBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getToBranch()))
        .supplier(request.getSupplier())
        .fromBranch(request.getFromBranch())
        .note(request.getNote())
        .build();

    // Save the updated OutboundEntity
    OutboundEntity updatedOutboundEntity = outboundRepository.save(outboundMapper.toEntity(updatedOutbound));
    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();

    // Process each product or batch in the request
    for (OutboundProductDetail productDetail : request.getOutboundProductDetails()) {
      Product product = productDetail.getProduct();
      Batch batch = productDetail.getBatch();

      if (batch != null) {
        // Process batch-based outbound detail
       Batch batchDTO = batchService.getById(batch.getId());

        BigDecimal convertedQuantity = convertToBaseUnit(
            batchMapper.toEntity(batchDTO).getProduct(),
            BigDecimal.valueOf(productDetail.getOutboundQuantity()),
           unitOfMeasurementMapper.toEntity(productDetail.getTargetUnit()) );

        // Validate and subtract the quantity from BranchBatch
        BranchBatchEntity branchBatch = branchBatchRepository
            .findByBranchAndBatch(fromBranch,  batchMapper.toEntity(batchDTO))
            .orElseThrow(() -> new HrmCommonException("Batch not available in branch: " +  batchMapper.toEntity(batchDTO).getBatchCode()));

        if (branchBatch.getQuantity().compareTo(convertedQuantity) < 0) {
          throw new HrmCommonException("Insufficient stock for batch: " +  batchMapper.toEntity(batchDTO).getBatchCode());
        }
        branchBatch.setQuantity(branchBatch.getQuantity().subtract(convertedQuantity));
        branchBatchRepository.save(branchBatch);

        OutboundDetailEntity outboundDetail = OutboundDetailEntity.builder()
            .outbound(updatedOutboundEntity)
            .batch( batchMapper.toEntity(batchDTO))
            .quantity(productDetail.getOutboundQuantity())
            .unitOfMeasurement( batchMapper.toEntity(batchDTO).getProduct().getBaseUnit()) // Use product's base unit for batch
            .build();
        outboundDetailEntities.add(outboundDetail);
      } else {
        // Process product-based outbound detail
        ProductEntity productEntity = productRepository.findById(product.getId())
            .orElseThrow(() -> new HrmCommonException(PRODUCT.NOT_EXIST));

        BigDecimal convertedQuantity = convertToBaseUnit(
            productEntity, BigDecimal.valueOf(productDetail.getOutboundQuantity()),
           unitOfMeasurementMapper.toEntity(productDetail.getTargetUnit()));

        // Validate and subtract the quantity from BranchProduct
        BranchProductEntity branchProduct = branchProductRepository
            .findByBranchAndProduct(fromBranch, productEntity)
            .orElseThrow(() -> new HrmCommonException("Product not available in branch: " + productEntity.getProductName()));

        if (branchProduct.getQuantity().compareTo(convertedQuantity) < 0) {
          throw new HrmCommonException("Insufficient stock for product: " + productEntity.getProductName());
        }
        branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
        branchProductRepository.save(branchProduct);

        OutboundProductDetailEntity outboundProductDetail = OutboundProductDetailEntity.builder()
            .outbound(updatedOutboundEntity)
            .product(productEntity)
            .outboundQuantity(productDetail.getOutboundQuantity())
            .unitOfMeasurement(productDetail.getTargetUnit() != null
                ? unitOfMeasurementMapper.toEntity(productDetail.getTargetUnit())
                : productEntity.getBaseUnit())
            .build();
        outboundProductDetailEntities.add(outboundProductDetail);
      }
    }

    // Save all outbound details to the repositories
    outboundDetailRepository.saveAll(outboundDetailEntities);
    outboundProductDetailRepository.saveAll(outboundProductDetailEntities);

    // Update the Outbound status and save it
    updatedOutboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    OutboundEntity finalOutboundEntity = outboundRepository.save(updatedOutboundEntity);

    // Convert and return the Outbound DTO
    return outboundMapper.toDTO(finalOutboundEntity);
  }

  @Override
  @Transactional
  public Outbound submitOutboundToSystem(Long outboundId) {
    // Retrieve the OutboundEntity
    OutboundEntity outbound = outboundRepository.findById(outboundId).orElse(null);
 Outbound outboundEntity =outboundMapper.toDTO(outbound);
    Branch fromBranch = outboundEntity.getFromBranch();

    // Process each OutboundProductDetail
    for (OutboundProductDetail productDetail : outboundEntity.getOutboundProductDetails()) {
      Product product = productDetail.getProduct();

      // Find the BranchProduct entity for this product and branch
      BranchProductEntity branchProduct =
          branchProductRepository
              .findByBranchAndProduct(branchMapper.toEntity(fromBranch), productMapper.toEntity(product))
              .orElseThrow(
                  () ->
                      new HrmCommonException(
                          "Product not available in branch: " + product.getProductName()));

      // Convert the outbound quantity to the product's base unit if a different target unit is
      // specified
      BigDecimal convertedQuantity =
          convertToBaseUnit(
              productMapper.toEntity(product),
              BigDecimal.valueOf(productDetail.getOutboundQuantity()),
             unitOfMeasurementMapper.toEntity(productDetail.getTargetUnit()));

      // Check if sufficient quantity is available
      if (branchProduct.getQuantity().compareTo(convertedQuantity) < 0) {
        throw new HrmCommonException("Insufficient stock for product: " + product.getProductName());
      }

      // Subtract the converted quantity
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
      branchProductRepository.save(branchProduct);
    }

    // Process each OutboundDetail (for batches)
    for (OutboundDetail batchDetail : outboundEntity.getOutboundDetails()) {
      Batch batch = batchDetail.getBatch();

      // Find the BranchBatch entity for this batch and branch
      BranchBatchEntity branchBatch =
          branchBatchRepository
              .findByBranchAndBatch(branchMapper.toEntity(fromBranch), batchMapper.toEntity(batch))
              .orElseThrow(
                  () ->
                      new HrmCommonException(
                          "Batch not available in branch: " + batch.getBatchCode()));
// Find the BranchProduct entity for this product and branch
      BranchProductEntity branchProduct =
          branchProductRepository
              .findByBranchAndProduct(branchMapper.toEntity(fromBranch),
                  branchBatch.getBatch().getProduct())
              .orElseThrow(
                  () ->
                      new HrmCommonException(
                          "Product not available in branch: " + branchBatch.getBatch().getProduct().getProductName()));
      // Convert the outbound quantity to the product's base unit if a different target unit is
      // specified
      BigDecimal convertedQuantity =
          convertToBaseUnit(
             productMapper.toEntity(batch.getProduct()) ,
              BigDecimal.valueOf(batchDetail.getQuantity()),
              unitOfMeasurementMapper.toEntity(batchDetail.getUnitOfMeasurement()));

      // Check if sufficient quantity is available
      if (branchBatch.getQuantity().compareTo(convertedQuantity) < 0) {
        throw new HrmCommonException("Insufficient stock for batch: " + batch.getBatchCode());
      }

      // Subtract the converted quantity
      branchBatch.setQuantity(branchBatch.getQuantity().subtract(convertedQuantity));
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(convertedQuantity));
      branchBatchRepository.save(branchBatch);
    }

    // Update the Outbound status and save it
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    OutboundEntity updatedOutboundEntity = outboundRepository.save(outboundMapper.toEntity(outboundEntity));

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
            .toBranch(branchEntity)
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
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    outboundRepository.deleteById(id); // Delete the outbound entity by ID
  }

  private BigDecimal convertToBaseUnit(
      ProductEntity product, BigDecimal quantity, UnitOfMeasurementEntity targetUnit) {
    // If the target unit is the same as the base unit, no conversion is needed
    if (targetUnit==null||targetUnit.getId().equals(product.getBaseUnit().getId())) {
      return quantity;
    }

    // Check if conversion is from smaller to larger or larger to smaller
    Optional<UnitConversionEntity> conversionOpt =
        unitConversionRepository.findByProductAndLargerUnitAndSmallerUnit(
            product, product.getBaseUnit(), targetUnit);

    if (conversionOpt.isPresent()) {
      // Conversion from smaller unit to larger unit (e.g., kg to g), multiply
      return quantity.divide(BigDecimal.valueOf(conversionOpt.get().getFactorConversion()), RoundingMode.HALF_UP);
    } else {
      // Conversion from larger unit to smaller unit (e.g., g to kg), divide
      UnitConversionEntity reverseConversion =
          unitConversionRepository
              .findByProductAndLargerUnitAndSmallerUnit(product, targetUnit, product.getBaseUnit())
              .orElseThrow(
                  () ->
                      new HrmCommonException(
                          "Conversion not found for product: "
                              + product.getProductName()
                              + " from "
                              + targetUnit.getUnitName()
                              + " to "
                              + product.getBaseUnit().getUnitName()));
 
      return quantity.multiply(
          BigDecimal.valueOf(reverseConversion.getFactorConversion()));
    }
  }
}
