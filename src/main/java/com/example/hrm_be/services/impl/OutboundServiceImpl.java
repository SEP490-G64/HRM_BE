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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import jakarta.persistence.PersistenceContext;
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
  @PersistenceContext private EntityManager entityManager;
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
  @Autowired private InventoryCheckService inventoryCheckService;

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
                  productDetailDTO.setPreQuantity(outboundProductDetail.getPreQuantity());
                  productDetailDTO.setOutboundQuantity(outboundProductDetail.getOutboundQuantity());
                  productDetailDTO.setPrice(outboundProductDetail.getPrice());
                  productDetailDTO.setTargetUnit(
                      unitOfMeasurementMapper.toDTO(
                          Optional.ofNullable(outboundProductDetail.getUnitOfMeasurement())
                              .orElse(outboundProductDetail.getProduct().getBaseUnit())));
                  productDetailDTO.setProductBaseUnit(
                      unitOfMeasurementMapper.toDTO(
                          outboundProductDetail.getProduct().getBaseUnit()));
                  productDetailDTO.setTaxRate(outboundProductDetail.getTaxRate());

                  ProductBaseDTO productBaseDTO =
                      productService.getBranchProducts(
                          outboundEntity.getFromBranch().getId(),
                          outboundProductDetail.getProduct().getId());

                  if (productBaseDTO != null) {
                    productDetailDTO.setProductQuantity(productBaseDTO.getProductQuantity());
                    productDetailDTO.setInboundPrice(productBaseDTO.getInboundPrice());

                    boolean includeAllBatches =
                        outboundEntity.getOutboundType() == OutboundType.TRA_HANG
                            || outboundEntity.getOutboundType() == OutboundType.HUY_HANG;
                    List<Batch> filteredBatches;

                    if (includeAllBatches) {
                      filteredBatches = productBaseDTO.getBatches();
                    } else {
                      filteredBatches =
                          productBaseDTO.getBatches().stream()
                              .filter(
                                  batch ->
                                      batch.getExpireDate() != null
                                          && batch.getExpireDate().isAfter(LocalDateTime.now())
                                          && batch.getQuantity() != null
                                          && batch.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                              .collect(Collectors.toList());
                    }

                    productDTO.setBatches(filteredBatches);
                  } else {
                    productDTO.setBatches(null);
                  }

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

                  ProductBaseDTO productBaseDTO =
                      productService.getBranchProducts(
                          outboundEntity.getFromBranch().getId(),
                          outboundDetail.getBatch().getProduct().getId());

                  if (productBaseDTO != null) {
                    boolean includeAllBatches =
                        outboundEntity.getOutboundType() == OutboundType.TRA_HANG
                            || outboundEntity.getOutboundType() == OutboundType.HUY_HANG
                            || outboundEntity.getStatus() == OutboundStatus.KIEM_HANG
                            || outboundEntity.getStatus() == OutboundStatus.HOAN_THANH;
                    List<Batch> filteredBatches;

                    if (includeAllBatches) {
                      // Nếu biến boolean là true, lấy toàn bộ lô sản phẩm
                      filteredBatches = productBaseDTO.getBatches();
                    } else {
                      // Nếu biến boolean là false, áp dụng bộ lọc
                      filteredBatches =
                          productBaseDTO.getBatches().stream()
                              .filter(
                                  batch ->
                                      (batch.getExpireDate() != null
                                              && batch.getExpireDate().isAfter(LocalDateTime.now()))
                                          && (batch.getQuantity() != null
                                              && batch.getQuantity().compareTo(BigDecimal.ZERO)
                                                  > 0))
                              .collect(Collectors.toList());
                    }

                    productDTO.setBatches(filteredBatches);
                  } else {
                    productDTO.setBatches(null);
                  }

                  productWithBatchDetailDTO.setTaxRate(
                      outboundDetail.getBatch().getProduct().getCategory().getTaxRate());
                  productWithBatchDetailDTO.setProduct(productDTO);

                  // Set Batch details
                  Batch batchDTO = new Batch();
                  batchDTO.setId(outboundDetail.getBatch().getId());
                  batchDTO.setBatchCode(outboundDetail.getBatch().getBatchCode());
                  batchDTO.setExpireDate(outboundDetail.getBatch().getExpireDate());
                  batchDTO.setInboundPrice(outboundDetail.getPrice());
                  productWithBatchDetailDTO.setBatch(batchDTO);

                  // Set outbound quantity and price
                  productWithBatchDetailDTO.setPreQuantity(outboundDetail.getPreQuantity());
                  productWithBatchDetailDTO.setOutboundQuantity(outboundDetail.getQuantity());
                  productWithBatchDetailDTO.setPrice(outboundDetail.getPrice());
                  productWithBatchDetailDTO.setTargetUnit(
                      unitOfMeasurementMapper.toDTO(
                          Optional.ofNullable(outboundDetail.getUnitOfMeasurement())
                              .orElse(outboundDetail.getBatch().getProduct().getBaseUnit())));
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

  Specification<OutboundEntity> getSpecification(
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
          HrmConstant.ERROR.OUTBOUND.NOT_EXIST); // Error if outbound entity is not found
    }

    if (oldOutboundEntity.getStatus() != OutboundStatus.CHO_DUYET) {
      throw new HrmCommonException(HrmConstant.ERROR.OUTBOUND.INVALID);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldOutboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .isApproved(accept)
                    .approvedBy(userEntity)
                    .status(accept ? OutboundStatus.KIEM_HANG : OutboundStatus.BAN_NHAP)
                    .build())
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
            .totalPrice(request.getTotalPrice())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(
                request.getOutboundStatus() == null
                    ? OutboundStatus.BAN_NHAP
                    : request.getOutboundStatus())
            .outboundType(request.getOutboundType())
            .createdBy(request.getCreatedBy())
            .toBranch(request.getToBranch())
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .taxable(
                outboundEntity.getStatus() == OutboundStatus.KIEM_HANG
                    ? outboundEntity.getTaxable()
                    : request.getTaxable())
            .isApproved(outboundEntity.getIsApproved())
            .approvedBy(
                outboundEntity.getApprovedBy() != null
                    ? userMapper.convertToDtoBasicInfo(outboundEntity.getApprovedBy())
                    : null)
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
      ProductEntity productEntity = productMapper.toEntity(productService.getById(product.getId()));

      BigDecimal preQuantity = productDetail.getPreQuantity();
      BigDecimal outboundQuantity = productDetail.getOutboundQuantity();

      // If batch information is provided, process as a batch detail
      if (batch != null && batch.getId() != null) {
        BatchEntity batchEntity = batchMapper.toEntity(batchService.getById(batch.getId()));
        BigDecimal realityQuantity =
            branchBatchService.findQuantityByBatchIdAndBranchId(batch.getId(), fromBranch.getId());

        if (!updatedOutbound.getStatus().equals(OutboundStatus.KIEM_HANG)
            && !updatedOutbound.getStatus().equals(OutboundStatus.DANG_THANH_TOAN)) {
          if (realityQuantity.compareTo(preQuantity) < 0) {
            throw new HrmCommonException(
                "Số lượng hiện tại trong kho của lô "
                    + batchEntity.getBatchCode()
                    + " chỉ còn "
                    + realityQuantity
                    + ", vui lòng nhập số lượng nhỏ hơn.");
          }
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
                  .preQuantity(productDetail.getPreQuantity())
                  .quantity(productDetail.getOutboundQuantity())
                  .batch(batchEntity)
                  .unitOfMeasurement(productEntity.getBaseUnit())
                  .taxRate(productDetail.getTaxRate())
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

        if (!outboundEntity.getStatus().equals(OutboundStatus.KIEM_HANG)
            && !outboundEntity.getStatus().equals(OutboundStatus.DANG_THANH_TOAN)) {
          if (branchProduct.getQuantity().compareTo(preQuantity) < 0) {
            throw new HrmCommonException(
                "Số lượng hiện tại trong kho của sản phẩm "
                    + productEntity.getProductName()
                    + " chỉ còn "
                    + branchProduct.getQuantity()
                    + ", vui lòng nhập số lượng nhỏ hơn.");
          }
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
                  .price(productEntity.getInboundPrice())
                  .product(productEntity)
                  .outboundQuantity(productDetail.getOutboundQuantity())
                  .unitOfMeasurement(productEntity.getBaseUnit())
                  .taxRate(productDetail.getTaxRate())
                  .preQuantity(productDetail.getPreQuantity())
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
    // Lấy OutboundEntity và validate
    OutboundEntity outboundEntity =
        outboundRepository
            .findById(request.getOutboundId())
            .orElseThrow(() -> new HrmCommonException(OUTBOUND.NOT_EXIST));

    BranchEntity fromBranch =
        branchMapper.toEntity(branchService.getById(request.getFromBranch().getId()));

    // Xóa dữ liệu cũ của Outbound
    CompletableFuture<Void> deleteOutboundProductDetails =
        CompletableFuture.runAsync(
            () -> outboundProductDetailService.deleteByOutboundId(request.getOutboundId()));
    CompletableFuture<Void> deleteOutboundDetails =
        CompletableFuture.runAsync(
            () -> outboundDetailService.deleteByOutboundId(request.getOutboundId()));

    CompletableFuture.allOf(deleteOutboundProductDetails, deleteOutboundDetails).join();

    // Cập nhật OutboundEntity với dữ liệu mới
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

    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(updatedOutbound));

    // Batch xử lý sản phẩm và batch chi tiết
    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    List<OutboundProductDetailEntity> outboundProductDetailEntities = new ArrayList<>();
    Map<Long, BigDecimal> batchQuantityUpdates = new HashMap<>();
    Map<Long, BigDecimal> productQuantityUpdates = new HashMap<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    Map<Long, List<BranchBatch>> branchBatchCache =
        branchBatchService.getAllByBranchId(fromBranch.getId()).stream()
            .collect(Collectors.groupingBy(batch -> batch.getBatch().getProduct().getId()));

    for (OutboundProductDetail productDetail : request.getOutboundProductDetails()) {
      ProductEntity productEntity =
          productMapper.toEntity(productService.getById(productDetail.getProduct().getId()));

      BigDecimal outboundQuantity = productDetail.getOutboundQuantity();
      BigDecimal convertedQuantity =
          unitConversionService.convertToUnit(
              productEntity.getId(),
              productEntity.getBaseUnit().getId(),
              outboundQuantity,
              productDetail.getTargetUnit(),
              true);

      // Kiểm tra tồn kho và xử lý xuất kho
      List<BranchBatch> productBatches =
          branchBatchCache.getOrDefault(productEntity.getId(), new ArrayList<>());
      BigDecimal remainingQuantity = convertedQuantity;

      for (BranchBatch branchBatch : productBatches) {
        if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) break;
        if (branchBatch.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
          BigDecimal batchDeduction = branchBatch.getQuantity().min(remainingQuantity);
          BigDecimal pricePerUnit = productEntity.getSellPrice();
          BigDecimal priceOutboundDetail = batchDeduction.multiply(pricePerUnit);

          outboundDetailEntities.add(
              createOutboundDetailEntity(
                  updatedOutboundEntity,
                  branchBatch.getBatch(),
                  batchDeduction,
                  pricePerUnit,
                  productDetail));

          totalPrice = totalPrice.add(priceOutboundDetail);
          batchQuantityUpdates.merge(branchBatch.getId(), batchDeduction.negate(), BigDecimal::add);
          remainingQuantity = remainingQuantity.subtract(batchDeduction);
        }
      }

      if (remainingQuantity.compareTo(BigDecimal.ZERO) > 0) {
        BranchProduct branchProduct =
            branchProductService.getByBranchIdAndProductId(
                fromBranch.getId(), productEntity.getId());
        if (branchProduct == null || branchProduct.getQuantity().compareTo(remainingQuantity) < 0) {
          throw new HrmCommonException(
              "Không đủ tồn kho cho sản phẩm: " + productEntity.getProductName());
        }
        BigDecimal priceOutboundProductDetail =
            remainingQuantity.multiply(productEntity.getSellPrice());
        totalPrice = totalPrice.add(priceOutboundProductDetail);

        outboundProductDetailEntities.add(
            createOutboundProductDetailEntity(
                updatedOutboundEntity, productEntity, remainingQuantity, productDetail));

        productQuantityUpdates.merge(
            productEntity.getId(), remainingQuantity.negate(), BigDecimal::add);
      }
    }

    // Cập nhật batch tồn kho
    branchBatchService.batchUpdateQuantities(batchQuantityUpdates);
    branchProductService.batchUpdateQuantities(productQuantityUpdates);

    // Lưu tất cả outbound details và product details
    outboundDetailService.saveAll(outboundDetailEntities);
    outboundProductDetailService.saveAll(outboundProductDetailEntities);

    // Gửi thông báo cập nhật tồn kho
    inventoryCheckService.broadcastInventoryCheckUpdates(
        productQuantityUpdates.keySet(), batchQuantityUpdates.keySet(), fromBranch.getId());

    // Cập nhật trạng thái outbound và trả về DTO
    updatedOutboundEntity.setTotalPrice(totalPrice);
    updatedOutboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    return outboundMapper.toDTO(outboundRepository.save(updatedOutboundEntity));
  }

  private OutboundDetailEntity createOutboundDetailEntity(
      OutboundEntity outbound,
      Batch batch,
      BigDecimal quantity,
      BigDecimal pricePerUnit,
      OutboundProductDetail detail) {
    return OutboundDetailEntity.builder()
        .outbound(outbound)
        .batch(batchMapper.toEntity(batch))
        .quantity(quantity)
        .price(pricePerUnit)
        .unitOfMeasurement(unitOfMeasurementMapper.toEntity(detail.getTargetUnit()))
        .build();
  }

  private OutboundProductDetailEntity createOutboundProductDetailEntity(
      OutboundEntity outbound,
      ProductEntity product,
      BigDecimal quantity,
      OutboundProductDetail detail) {
    return OutboundProductDetailEntity.builder()
        .outbound(outbound)
        .product(product)
        .outboundQuantity(quantity)
        .price(product.getSellPrice())
        .unitOfMeasurement(unitOfMeasurementMapper.toEntity(detail.getTargetUnit()))
        .taxRate(product.getCategory().getTaxRate())
        .build();
  }

  @Override
  @Transactional
  public Outbound submitOutboundToSystem(CreateOutboundRequest request) {
    request.setOutboundStatus(OutboundStatus.KIEM_HANG);

    // Fetch the OutboundEntity from the repository
    OutboundEntity outboundEntity =
        outboundRepository
            .findById(request.getOutboundId())
            .orElseThrow(() -> new HrmCommonException(OUTBOUND.NOT_EXIST));

    // check status
    if (!outboundEntity.getStatus().isCheck()) {
      throw new HrmCommonException("Trạng thái của phiếu không hợp lệ");
    }

    // Map old and new quantities for comparison
    Map<Long, BigDecimal> oldProductQuantities =
        Optional.ofNullable(outboundEntity.getOutboundProductDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getProduct().getId(),
                    batchDetail ->
                        batchDetail.getOutboundQuantity() != null
                            ? batchDetail.getOutboundQuantity()
                            : BigDecimal.ZERO));

    Map<Long, BigDecimal> oldBatchQuantities =
        Optional.ofNullable(outboundEntity.getOutboundDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getBatch().getId(),
                    batchDetail ->
                        batchDetail.getQuantity() != null
                            ? batchDetail.getQuantity()
                            : BigDecimal.ZERO));

    saveOutbound(request);
    outboundRepository.flush();
    entityManager.clear();
    OutboundEntity updatedOutboundEntity =
        outboundRepository
            .findById(request.getOutboundId())
            .orElseThrow(() -> new HrmCommonException(INBOUND.NOT_EXIST));
    Map<Long, BigDecimal> newProductQuantities =
        Optional.ofNullable(updatedOutboundEntity.getOutboundProductDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getProduct().getId(),
                    batchDetail ->
                        batchDetail.getOutboundQuantity() != null
                            ? batchDetail.getOutboundQuantity()
                            : BigDecimal.ZERO));

    Map<Long, BigDecimal> newBatchQuantities =
        Optional.ofNullable(updatedOutboundEntity.getOutboundDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getBatch().getId(),
                    batchDetail ->
                        batchDetail.getQuantity() != null
                            ? batchDetail.getQuantity()
                            : BigDecimal.ZERO));
    Outbound outbound = outboundMapper.toDTO(updatedOutboundEntity);
    Branch fromBranch = outbound.getFromBranch();

    Boolean taxable = false;
    if (updatedOutboundEntity.getTaxable() != null && updatedOutboundEntity.getTaxable()) {
      taxable = true;
    }

    BigDecimal totalPrice = BigDecimal.ZERO;

    List<OutboundProductDetail> outboundProductDetails =
        outboundProductDetailService.findByOutboundWithCategory(request.getOutboundId());

    // Process each OutboundProductDetail
    for (OutboundProductDetail productDetail : outboundProductDetails) {
      Product productEntity = productDetail.getProduct();

      // Find the BranchProduct entity for this product and branch
      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(fromBranch.getId(), productEntity.getId());

      BigDecimal oldQuantity =
          oldProductQuantities.getOrDefault(productEntity.getId(), BigDecimal.ZERO);
      BigDecimal newQuantity =
          newProductQuantities.getOrDefault(productEntity.getId(), BigDecimal.ZERO);
      BigDecimal difference = newQuantity.subtract(oldQuantity);

      // Check if sufficient quantity is available
      if (branchProduct.getQuantity().compareTo(difference) < 0) {
        throw new HrmCommonException(
            "Số lượng hiện tại trong kho của sản phẩm "
                + productEntity.getProductName()
                + " chỉ còn "
                + branchProduct.getQuantity()
                + ", vui lòng nhập số lượng nhỏ hơn.");
      }

      // Subtract the converted quantity
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(difference));
      branchProduct.setLastUpdated(LocalDateTime.now());
      branchProductService.save(branchProduct);

      BigDecimal outboundProductDetailPrice =
          productEntity.getInboundPrice().multiply(productDetail.getOutboundQuantity());
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
    }

    List<OutboundDetail> outboundDetails =
        outboundDetailService.findByOutboundWithCategory(request.getOutboundId());

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

      BigDecimal oldQuantity = oldBatchQuantities.getOrDefault(batch.getId(), BigDecimal.ZERO);
      BigDecimal newQuantity = newBatchQuantities.getOrDefault(batch.getId(), BigDecimal.ZERO);
      BigDecimal difference = newQuantity.subtract(oldQuantity);

      // Check if sufficient quantity is available
      if (branchBatch.getQuantity().compareTo(difference) < 0) {
        throw new HrmCommonException(
            "Số lượng hiện tại trong kho của lô "
                + batch.getBatchCode()
                + " chỉ còn "
                + branchBatch.getQuantity()
                + ", vui lòng nhập số lượng nhỏ hơn.");
      }

      // Subtract the converted quantity
      branchBatch.setQuantity(branchBatch.getQuantity().subtract(difference));
      branchBatch.setLastUpdated(LocalDateTime.now());
      branchProduct.setQuantity(branchProduct.getQuantity().subtract(difference));
      branchProduct.setLastUpdated(LocalDateTime.now());
      branchBatchService.save(branchBatch);
      branchProductService.save(branchProduct);

      BigDecimal outboundDetailPrice = batch.getInboundPrice().multiply(batchDetail.getQuantity());
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
    }

    // Update the Outbound status and save it
    outboundEntity.setTotalPrice(totalPrice);
    OutboundEntity updateOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(outbound));
    // Notification for Manager
    String message =
        "🔔 Thông báo: Phiếu xuất "
            + outbound.getOutboundCode()
            + " đã được cập nhật vào hệ"
            + " "
            + "thống "
            + "bởi "
            + outbound.getCreatedBy().getUserName();

    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setNotiName("Nhập phiếu vào kho");
    notification.setNotiType(NotificationType.NHAP_PHIEU_XUAT_VAO_HE_THONG);
    notification.setCreatedDate(LocalDateTime.now());

    // Collect all product IDs from outboundProductDetails
    Set<Long> allProductIds =
        outboundProductDetails.stream()
            .map(productDetail -> productDetail.getProduct().getId())
            .collect(Collectors.toSet());

    // Collect all batch IDs from outboundDetails
    Set<Long> allBatchIds =
        outboundDetails.stream()
            .map(batchDetail -> batchDetail.getBatch().getId())
            .collect(Collectors.toSet());
    // Collect all batch IDs from outboundDetails
    Set<Long> allProductIdsFromBatch =
        outboundDetails.stream()
            .map(batchDetail -> batchDetail.getBatch().getProduct().getId())
            .collect(Collectors.toSet());

    allProductIds.addAll(allProductIdsFromBatch);
    // Notify inventory checks
    inventoryCheckService.broadcastInventoryCheckUpdates(
        allProductIds, allBatchIds, fromBranch.getId());

    notificationService.sendNotification(
        notification, userService.findAllManagerByBranchId(outbound.getFromBranch().getId()));
    // Convert and return the Outbound object
    return outboundMapper.toDTO(updateOutboundEntity);
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
          "🔔 Thông báo: Phiếu xuất "
              + outbound.getOutboundCode()
              + "đang chờ duyệt "
              + "bởi "
              + outbound.getCreatedBy().getUserName();

      Notification notification = new Notification();
      notification.setMessage(message);
      notification.setNotiName(NotificationType.YEU_CAU_DUYET_DON_XUAT.getDisplayName());
      notification.setNotiType(NotificationType.YEU_CAU_DUYET_DON_XUAT);
      notification.setCreatedDate(LocalDateTime.now());

      notificationService.sendNotification(
          notification, userService.findAllManagerByBranchId(outbound.getFromBranch().getId()));
    }
    outboundRepository.updateOutboundStatus(status, id);
  }

  // Method to delete an outbound record
  @Override
  public void delete(Long id) {
    if (id == null) {
      throw new HrmCommonException("id not exist!");
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
    if (outboundId == null) {
      throw new HrmCommonException("id not exist!");
    }
    // Fetch Inbound and associated details
    Outbound outbound = this.getById(outboundId);
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
