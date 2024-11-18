package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.OUTBOUND;
import com.example.hrm_be.commons.enums.NotificationType;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import com.example.hrm_be.repositories.OutboundRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.PDFUtil;
import com.example.hrm_be.utils.WplUtil;
import com.itextpdf.text.DocumentException;
import io.micrometer.common.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
  @Autowired private OutboundProductDetailMapper outboundProductDetailMapper;
  @Autowired private OutboundDetailMapper outboundDetailMapper;

  @Autowired private UserService userService;
  @Autowired private BatchService batchService;
  @Autowired private BranchService branchService;
  @Autowired private OutboundProductDetailService outboundProductDetailService;
  @Autowired private OutboundDetailService outboundDetailService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductService productService;
  @Autowired private NotificationService notificationService;
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
        outboundEntity.getOutboundProductDetails().stream()
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
                  productDetailDTO.setTargetUnit(
                      outboundProductDetail.getUnitOfMeasurement() != null
                          ? unitOfMeasurementMapper.toDTO(
                              outboundProductDetail.getUnitOfMeasurement())
                          : unitOfMeasurementMapper.toDTO(
                              outboundProductDetail.getProduct().getBaseUnit()));
                  productDetailDTO.setProductBaseUnit(
                      unitOfMeasurementMapper.toDTO(
                          outboundProductDetail.getProduct().getBaseUnit()));
                  return productDetailDTO;
                })
            .collect(Collectors.toList());

    // Populate productsWithBatch from OutboundDetails
    List<OutboundProductDetail> productsWithBatch =
        outboundEntity.getOutboundDetails().stream()
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
                  batchDTO.setInboundPrice(outboundDetail.getPrice());
                  productWithBatchDetailDTO.setBatch(batchDTO);

                  // Set outbound quantity and price
                  productWithBatchDetailDTO.setOutboundQuantity(outboundDetail.getQuantity());
                  productWithBatchDetailDTO.setPrice(outboundDetail.getPrice());
                  productWithBatchDetailDTO.setTargetUnit(
                      outboundDetail.getUnitOfMeasurement() != null
                          ? unitOfMeasurementMapper.toDTO(outboundDetail.getUnitOfMeasurement())
                          : unitOfMeasurementMapper.toDTO(
                              outboundDetail.getBatch().getProduct().getBaseUnit()));
                  productWithBatchDetailDTO.setProductBaseUnit(
                      unitOfMeasurementMapper.toDTO(
                          outboundDetail.getBatch().getProduct().getBaseUnit()));
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
  public Page<Outbound> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String direction,
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      OutboundStatus status,
      OutboundType type) {
    // Check direction and set value for sort
    Sort sort =
        direction != null && direction.equalsIgnoreCase("ASC")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending(); // Default is descending

    Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

    Specification<OutboundEntity> specification =
        getSpecification(branchId, keyword, startDate, endDate, status, type);
    return outboundRepository
        .findAll(specification, pageable)
        .map(dao -> outboundMapper.toDTO(dao));
  }

  private Specification<OutboundEntity> getSpecification(
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      OutboundStatus status,
      OutboundType type) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Get outbound in registered user's branch
      predicates.add(criteriaBuilder.equal(root.get("fromBranch").get("id"), branchId));

      // Get outbound have code containing keyword
      if (keyword != null && !keyword.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("outboundCode"), "%" + keyword + "%"));
      }

      // Get outbound in time range
      if (startDate != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), startDate));
      }
      if (endDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), endDate));
      }

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }

      if (type != null) {
        predicates.add(criteriaBuilder.equal(root.get("outboundType"), type));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
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
    Branch fromBranch = branchService.getById(request.getFromBranch().getId());

    if (fromBranch == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }

    outboundProductDetailService.deleteByOutboundId(request.getOutboundId());
    outboundDetailService.deleteByOutboundId(request.getOutboundId());
    Outbound updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId()) // Retain the existing ID
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.BAN_NHAP)
            .outboundType(request.getOutboundType())
            .createdBy(request.getCreatedBy())
            .toBranch(request.getToBranch())
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
      Product productEntity = productService.getById(product.getId());

      BigDecimal outboundQuantity = productDetail.getOutboundQuantity();

      // If batch information is provided, process as a batch detail
      if (batch != null) {
        BatchEntity batchEntity = batchMapper.toEntity(batchService.getById(batch.getId()));
        BigDecimal realityQuantity =
            branchBatchService.findQuantityByBatchIdAndBranchId(batch.getId(), fromBranch.getId());

        if (realityQuantity.compareTo(outboundQuantity) < 0) {
          throw new HrmCommonException(
              "Số lượng hiện tại trong kho của lô "
                  + batchEntity.getBatchCode()
                  + " chỉ còn "
                  + realityQuantity
                  + ", vui lòng nhập số lượng nhỏ hơn.");
        }

        OutboundDetailEntity existingBatchDetail =
            outboundDetailMapper.toEntity(
                outboundDetailService.findByOutboundAndBatch(
                    updatedOutboundEntity.getId(), batch.getId()));

        OutboundDetailEntity outboundDetail;
        if (existingBatchDetail != null) {
          // Update existing batch detail with new quantity
          outboundDetail = existingBatchDetail;
          outboundDetail.setQuantity(productDetail.getOutboundQuantity());
        } else {
          // Create a new outbound batch detail
          outboundDetail =
              OutboundDetailEntity.builder()
                  .id(null)
                  .price(productDetail.getPrice())
                  .outbound(updatedOutboundEntity)
                  .quantity(productDetail.getOutboundQuantity())
                  .batch(batchEntity)
                  .build();
        }
        outboundDetailEntities.add(outboundDetail);
      } else {
        // Process as a product detail if no batch is specified
        // Fetch quantity from BranchProduct
        BranchProductEntity branchProduct =
            branchProductMapper.toEntity(
                branchProductService.getByBranchIdAndProductId(
                    fromBranch.getId(), productEntity.getId()));

        if (branchProduct.getQuantity().compareTo(outboundQuantity) < 0) {
          throw new HrmCommonException(
              "Số lượng hiện tại trong kho của sản phẩm "
                  + productEntity.getProductName()
                  + " chỉ còn "
                  + branchProduct.getQuantity()
                  + ", vui lòng nhập số lượng nhỏ hơn.");
        }

        OutboundProductDetailEntity existingProductDetail =
            outboundProductDetailMapper.toEntity(
                outboundProductDetailService.findByOutboundAndProduct(
                    updatedOutboundEntity.getId(), productEntity.getId()));

        OutboundProductDetailEntity outboundProductDetail;
        if (existingProductDetail != null) {
          // Update existing product detail with new quantity
          outboundProductDetail = existingProductDetail;
          outboundProductDetail.setOutboundQuantity(productDetail.getOutboundQuantity());
        } else {
          // Create a new outbound product detail
          outboundProductDetail =
              OutboundProductDetailEntity.builder()
                  .id(null)
                  .outbound(updatedOutboundEntity)
                  .price(productDetail.getPrice())
                  .product(productMapper.toEntity(productEntity))
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
        branchMapper.toEntity(branchService.getById(request.getFromBranch().getId()));

    // Delete existing outbound product and batch details
    outboundProductDetailService.deleteByOutboundId(request.getOutboundId());
    outboundDetailService.deleteByOutboundId(request.getOutboundId());

    // Update the OutboundEntity with the new request details
    Outbound updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.HOAN_THANH)
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
      ProductEntity productEntity = productMapper.toEntity(productService.getById(product.getId()));

      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(fromBranch.getId(), product.getId());

      if (branchProduct != null) {
        BigDecimal productPrice = productEntity.getSellPrice();
        BigDecimal pricePerUnit =
            unitConversionService.convertToUnit(
                product.getId(),
                productEntity.getBaseUnit().getId(),
                productPrice != null ? productPrice : BigDecimal.ZERO,
                productDetail.getTargetUnit(),
                true);

        BigDecimal outboundQuantity = productDetail.getOutboundQuantity();
        BigDecimal convertedQuantity =
            unitConversionService.convertToUnit(
                productEntity.getId(),
                productEntity.getBaseUnit().getId(),
                outboundQuantity,
                productDetail.getTargetUnit(),
                true);

        // Get all batches of product then check quantity to see which batch will be out
        List<BranchBatch> productBranchBatches =
            branchBatchService.findByProductAndBranchForSell(product.getId(), fromBranch.getId());

        if (!productBranchBatches.isEmpty()) {
          BigDecimal availableForSell =
              productBranchBatches.stream()
                  .map(BranchBatch::getQuantity)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
          // Compare outbound quantity to product quantity in branch
          if (availableForSell.compareTo(convertedQuantity) < 0) {
            throw new HrmCommonException(
                "Số lượng hiện tại trong kho của sản phẩm "
                    + productEntity.getProductName()
                    + " chỉ còn "
                    + availableForSell
                    + ", vui lòng nhập số lượng nhỏ hơn.");
          }

          BigDecimal remainingQuantity = convertedQuantity;

          // For product have batch
          // Iterate through each batch to issue stock in order of earliest expiry date
          for (BranchBatch branchBatch : productBranchBatches) {
            Batch batchDTO = batchService.getById(branchBatch.getBatch().getId());

            // Check if the current batch has enough quantity to meet the remaining requirement
            if (branchBatch.getQuantity().compareTo(remainingQuantity) < 0) {
              // If the current batch is insufficient, issue all of it and update the remaining
              // quantity
              BigDecimal priceOutboundDetail = branchBatch.getQuantity().multiply(productPrice);
              this.addOutboundDetailForSell(
                  updatedOutboundEntity,
                  batchDTO,
                  unitConversionService.convertToUnit(
                      product.getId(),
                      productEntity.getBaseUnit().getId(),
                      branchBatch.getQuantity(),
                      productDetail.getTargetUnit(),
                      false),
                  pricePerUnit,
                  productDetail.getTargetUnit(),
                  outboundDetailEntities);

              totalPrice = totalPrice.add(priceOutboundDetail);
              remainingQuantity = remainingQuantity.subtract(branchBatch.getQuantity());
              branchBatch.setQuantity(BigDecimal.ZERO);
              branchBatchService.save(branchBatch);
            } else {
              BigDecimal priceOutboundDetail = remainingQuantity.multiply(productPrice);
              // Issue only the required quantity and deduct it from the batch
              this.addOutboundDetailForSell(
                  updatedOutboundEntity,
                  batchDTO,
                  unitConversionService.convertToUnit(
                      product.getId(),
                      productEntity.getBaseUnit().getId(),
                      remainingQuantity,
                      productDetail.getTargetUnit(),
                      false),
                  pricePerUnit,
                  productDetail.getTargetUnit(),
                  outboundDetailEntities);

              totalPrice = totalPrice.add(priceOutboundDetail);
              branchBatch.setQuantity(branchBatch.getQuantity().subtract(remainingQuantity));
              remainingQuantity = BigDecimal.ZERO;
              branchBatchService.save(branchBatch);
              break;
            }
          }
        } else {
          // Compare outbound quantity to product quantity in branch
          if (branchProduct.getQuantity().compareTo(convertedQuantity) < 0) {
            throw new HrmCommonException(
                "Số lượng hiện tại trong kho của sản phẩm "
                    + productEntity.getProductName()
                    + " chỉ còn "
                    + branchProduct.getQuantity()
                    + ", vui lòng nhập số lượng nhỏ hơn.");
          }

          BigDecimal priceOutboundProductDetail = convertedQuantity.multiply(productPrice);
          OutboundProductDetailEntity outboundProductDetail =
              OutboundProductDetailEntity.builder()
                  .outbound(updatedOutboundEntity)
                  .product(productEntity)
                  .outboundQuantity(outboundQuantity)
                  .price(pricePerUnit)
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
      } else {
        throw new HrmCommonException(HrmConstant.ERROR.BRANCHPRODUCT.NOT_EXIST);
      }
    }

    // Save all outbound details to the repositories
    List<OutboundProductDetailEntity> finalOutboundProductDetailEntities =
        outboundProductDetailService.saveAll(outboundProductDetailEntities).stream()
            .map(outboundProductDetailMapper::toEntity)
            .collect(Collectors.toList());
    List<OutboundDetailEntity> finalOutboundDetailEntities =
        outboundDetailService.saveAll(outboundDetailEntities).stream()
            .map(outboundDetailMapper::toEntity)
            .collect(Collectors.toList());
    outboundProductDetailService.saveAll(outboundProductDetailEntities);

    // Update the Outbound status and save it
    updatedOutboundEntity.setOutboundProductDetails(finalOutboundProductDetailEntities);
    updatedOutboundEntity.setOutboundDetails(finalOutboundDetailEntities);
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

    Boolean taxable = false;
    if (outbound.getTaxable() != null && outbound.getTaxable()) {
      taxable = true;
    }

    BigDecimal totalPrice = BigDecimal.ZERO;

    List<OutboundProductDetail> outboundProductDetails =
        outboundProductDetailService.findByOutboundWithCategory(outboundId);
    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();

    // Process each OutboundProductDetail
    for (OutboundProductDetail productDetail : outboundProductDetails) {
      Product productEntity = productDetail.getProduct();

      // Find the BranchProduct entity for this product and branch
      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(fromBranch.getId(), productEntity.getId());

      BigDecimal outboundQuantity = productDetail.getOutboundQuantity();

      // Check if sufficient quantity is available
      if (branchProduct.getQuantity().compareTo(outboundQuantity) < 0) {
        throw new HrmCommonException(
            "Số lượng hiện tại trong kho của sản phẩm "
                + productEntity.getProductName()
                + " chỉ còn "
                + branchProduct.getQuantity()
                + ", vui lòng nhập số lượng nhỏ hơn.");
      }

      // Subtract the converted quantity
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(outboundQuantity));
      branchProductService.save(branchProduct);

      BigDecimal outboundProductDetailPrice =
          productEntity.getInboundPrice().multiply(outboundQuantity);
      BigDecimal updateOutboundProductDetailPrice = outboundProductDetailPrice;
      if (taxable) {
        if (productEntity.getCategory() != null) {
          BigDecimal taxRate = productEntity.getCategory().getTaxRate();
          if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            updateOutboundProductDetailPrice =
                updateOutboundProductDetailPrice.add(
                    outboundProductDetailPrice.multiply(taxRate).divide(BigDecimal.valueOf(100)));
          }
        }
      }
      totalPrice = totalPrice.add(updateOutboundProductDetailPrice);
      outboundProductDetailEntities.add(outboundProductDetailMapper.toEntity(productDetail));
    }

    List<OutboundDetail> outboundDetails =
        outboundDetailService.findByOutboundWithCategory(outboundId);

    // Process each OutboundDetail (for batches)
    for (OutboundDetail batchDetail : outboundDetails) {
      Batch batch = batchDetail.getBatch();

      // Find the BranchBatch entity for this batch and branch
      BranchBatch branchBatch =
          branchBatchService.getByBranchIdAndBatchId(fromBranch.getId(), batch.getId());

      // Find the BranchProduct entity for this product and branch
      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(
              fromBranch.getId(), branchBatch.getBatch().getProduct().getId());
      // Convert the outbound quantity to the product's base unit if a different target unit is
      // specified

      BigDecimal batchQuantity = batchDetail.getQuantity();

      // Check if sufficient quantity is available
      if (branchBatch.getQuantity().compareTo(batchQuantity) < 0) {
        throw new HrmCommonException(
            "Số lượng hiện tại trong kho của lô "
                + batch.getBatchCode()
                + " chỉ còn "
                + branchBatch.getQuantity()
                + ", vui lòng nhập số lượng nhỏ hơn.");
      }

      // Subtract the converted quantity
      branchBatch.setQuantity(branchBatch.getQuantity().subtract(batchQuantity));
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(batchQuantity));
      branchBatchService.save(branchBatch);
      branchProductService.save(branchProduct);

      BigDecimal outboundDetailPrice = batch.getInboundPrice().multiply(batchQuantity);
      BigDecimal updateOutboundDetailPrice = outboundDetailPrice;
      if (taxable) {
        if (batch.getProduct().getCategory() != null) {
          BigDecimal taxRate = batch.getProduct().getCategory().getTaxRate();
          if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            updateOutboundDetailPrice =
                updateOutboundDetailPrice.add(
                    outboundDetailPrice.multiply(taxRate).divide(BigDecimal.valueOf(100)));
          }
        }
      }
      totalPrice = totalPrice.add(updateOutboundDetailPrice);
      outboundDetailEntities.add(outboundDetailMapper.toEntity(batchDetail));
    }

    // Update the Outbound status and save it
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    outboundEntity.setTotalPrice(totalPrice);
    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(outboundEntity));
    // Notification for Manager
    String message =
        "🔔 Thông báo: Phiều xuất "
            + outbound.getOutboundCode()
            + " đã được cập nhật vào hệ"
            + " "
            + "thống "
            + "bởi "
            + outbound.getCreatedBy().getUserName();

    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setNotiName("Nhập phiếu vào kho");
    notification.setNotiType(NotificationType.NHAP_PHIEU_VAO_HE_THONG);
    notification.setCreatedDate(LocalDateTime.now());

    notificationService.sendNotification(
        notification, userService.findAllManagerByBranchId(outbound.getFromBranch().getId()));
    // Convert and return the Outbound object
    return outboundMapper.toDTO(updatedOutboundEntity);
  }

  @Override
  public Outbound createInnitOutbound(OutboundType type) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = WplUtil.generateNoteCode(currentDateTime, "OP");
    if (outboundRepository.existsByOutboundCode(outboundCode)) {
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
            .outboundCode(outboundCode)
            .createdBy(userEntity)
            .fromBranch(branchEntity)
            .build();
    return Optional.ofNullable(outbound)
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void updateOutboundStatus(OutboundStatus status, Long id) {
    OutboundEntity outbound = outboundRepository.findById(id).orElse(null);
    if (outbound == null) {
      throw new HrmCommonException(INBOUND.NOT_EXIST);
    }
    if (status.isWaitingForApprove()) {
      // Notification for Manager

      String message =
          "🔔 Thông báo: Phiều nhập "
              + outbound.getOutboundCode()
              + "đang chờ duyệt "
              + "bởi "
              + outbound.getCreatedBy().getUserName();

      Notification notification = new Notification();
      notification.setMessage(message);
      notification.setNotiName(NotificationType.YEU_CAU_DUYET.getDisplayName());
      notification.setNotiType(NotificationType.YEU_CAU_DUYET);
      notification.setCreatedDate(LocalDateTime.now());

      notificationService.sendNotification(
          notification, userService.findAllManagerByBranchId(outbound.getFromBranch().getId()));
    }
    outboundRepository.updateOutboundStatus(status, id);
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

  private void addOutboundDetailForSell(
      OutboundEntity outboundEntity,
      Batch batchDTO,
      BigDecimal quantity,
      BigDecimal price,
      UnitOfMeasurement targetUnit,
      List<OutboundDetailEntity> outboundDetailEntities) {
    OutboundDetailEntity outboundDetail =
        OutboundDetailEntity.builder()
            .outbound(outboundEntity)
            .batch(batchMapper.toEntity(batchDTO))
            .quantity(quantity)
            .price(price)
            .unitOfMeasurement(
                unitOfMeasurementMapper.toEntity(targetUnit)) // Use product's base unit for batch
            .build();
    outboundDetailEntities.add(outboundDetail);
  }

  @Override
  public ByteArrayOutputStream generateOutboundPdf(Long outboundId)
      throws DocumentException, IOException {
    // Fetch Inbound and associated details
    Outbound outbound = getById(outboundId);
    if (outbound == null) {
      throw new EntityNotFoundException("Outbound record not found with ID: " + outboundId);
    }
    if (OutboundType.CHUYEN_KHO_NOI_BO.equals(outbound.getOutboundType())) {
      return PDFUtil.createOutboundInternalPdf(outbound);
    } else {
      return PDFUtil.createOutboundPdf(outbound);
    }
  }
}
