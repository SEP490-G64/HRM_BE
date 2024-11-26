package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.SUPPLIER;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.commons.enums.NotificationType;
import com.example.hrm_be.components.*;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.PDFUtil;
import com.example.hrm_be.utils.WplUtil;
import com.itextpdf.text.DocumentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
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
public class InboundServiceImpl implements InboundService {
  @PersistenceContext private EntityManager entityManager;

  @Autowired private InboundRepository inboundRepository;

  @Autowired private InboundMapper inboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private UserMapper userMapper;
  @Autowired private BatchMapper batchMapper;
  @Autowired private ProductMapper productMapper;

  @Autowired private InboundDetailsService inboundDetailsService;
  @Autowired private InboundBatchDetailService inboundBatchDetailService;
  @Autowired private UserService userService;
  @Autowired private ProductService productService;
  @Autowired private BatchService batchService;
  @Autowired private NotificationService notificationService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductSupplierService productSupplierService;

  @Autowired private PDFUtil pdfUtil;

  @Override
  public InboundDetail getById(Long inboundId) {
    InboundEntity optionalInbound = inboundRepository.findById(inboundId).orElse(null);

    // Check if the inbound entity is null (i.e., not found)
    if (optionalInbound == null) {
      throw new HrmCommonException("Inbound not found with id: " + inboundId);
    }
    // Map InboundEntity to InboundDTO
    InboundDetail inboundDTO = inboundMapper.convertToInboundDetail(optionalInbound);

    // Map inboundDetails to include product and batches
    List<InboundProductDetailDTO> productDetails =
        optionalInbound.getInboundDetails().stream()
            .filter(Objects::nonNull) // Filter out null inboundDetail objects
            .map(
                inboundDetail -> {
                  InboundProductDetailDTO productDetailDTO = new InboundProductDetailDTO();
                  productDetailDTO.setId(inboundDetail.getId());
                  productDetailDTO.setRegistrationCode(
                      inboundDetail.getProduct().getRegistrationCode());
                  productDetailDTO.setBaseUnit(
                      unitOfMeasurementMapper.toDTO(inboundDetail.getProduct().getBaseUnit()));
                  productDetailDTO.setDiscount(inboundDetail.getDiscount());
                  productDetailDTO.setTaxRate(inboundDetail.getTaxRate());
                  productDetailDTO.setRequestQuantity(inboundDetail.getRequestQuantity());
                  productDetailDTO.setReceiveQuantity(inboundDetail.getReceiveQuantity());
                  productDetailDTO.setPrice(inboundDetail.getInboundPrice());

                  // Check if product is null
                  if (inboundDetail.getProduct() != null) {
                    productDetailDTO.setProductId(inboundDetail.getProduct().getId());
                    productDetailDTO.setProductName(inboundDetail.getProduct().getProductName());

                    // Map batches associated with this product in the context of this inbound
                    List<Batch> batches =
                        inboundDetail.getProduct().getBatches().stream()
                            .filter(Objects::nonNull) // Filter out null batch objects
                            .filter(
                                batch ->
                                    batch.getInboundBatchDetail() != null
                                        && batch.getInboundBatchDetail().stream()
                                            .anyMatch(
                                                inboundBatchDetail ->
                                                    inboundBatchDetail
                                                        .getInbound()
                                                        .getId()
                                                        .equals(
                                                            optionalInbound
                                                                .getId()))) // Only batches
                            // belonging to this
                            // inbound
                            .map(
                                batch -> {
                                  Batch batchDTO = new Batch();
                                  batchDTO.setId(batch.getId());
                                  batchDTO.setBatchCode(batch.getBatchCode());
                                  batchDTO.setExpireDate(batch.getExpireDate());

                                  BigDecimal price =
                                      (BigDecimal)
                                          batch.getInboundBatchDetail().stream()
                                              .filter(
                                                  Objects
                                                      ::nonNull) // Ensure inboundBatchDetail is not
                                              // null
                                              .filter(
                                                  inboundBatchDetail ->
                                                      inboundBatchDetail
                                                          .getInbound()
                                                          .getId()
                                                          .equals(optionalInbound.getId()))
                                              .map(
                                                  inboundBatchDetail ->
                                                      inboundBatchDetail.getInboundPrice() != null
                                                          ? inboundBatchDetail.getInboundPrice()
                                                          : 0)
                                              .findFirst()
                                              .orElse(BigDecimal.ZERO);
                                  batchDTO.setInboundPrice(price);

                                  // Find the quantity for this product-batch from the
                                  // inboundBatchDetails
                                  Integer quantity =
                                      batch.getInboundBatchDetail().stream()
                                          .filter(
                                              Objects::nonNull) // Ensure inboundBatchDetail is not
                                          // null
                                          .filter(
                                              inboundBatchDetail ->
                                                  inboundBatchDetail
                                                      .getInbound()
                                                      .getId()
                                                      .equals(optionalInbound.getId()))
                                          .map(
                                              inboundBatchDetail ->
                                                  inboundBatchDetail.getQuantity() != null
                                                      ? inboundBatchDetail.getQuantity()
                                                      : 0)
                                          .findFirst()
                                          .orElse(0); // Default quantity if not found

                                  batchDTO.setInboundBatchQuantity(quantity);
                                  return batchDTO;
                                })
                            .collect(Collectors.toList());

                    productDetailDTO.setBatches(batches);
                  }

                  return productDetailDTO;
                })
            .collect(Collectors.toList());

    inboundDTO.setProductBatchDetails(productDetails);

    return inboundDTO;
  }

  @Override
  public Page<Inbound> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String direction,
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      InboundStatus status,
      InboundType type) {
    // Check direction and set value for sort
    Sort sort =
        direction != null && direction.equalsIgnoreCase("ASC")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending(); // Default is descending

    Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

    Specification<InboundEntity> specification =
        getSpecification(branchId, keyword, startDate, endDate, status, type);
    return inboundRepository.findAll(specification, pageable).map(dao -> inboundMapper.toDTO(dao));
  }

  private Specification<InboundEntity> getSpecification(
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      InboundStatus status,
      InboundType type) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Get inbound have code containing keyword
      if (branchId != null) {
        // Get inbound in registered user's branch
        predicates.add(criteriaBuilder.equal(root.get("toBranch").get("id"), branchId));
      }

      // Get inbound have code containing keyword
      if (keyword != null && !keyword.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("inboundCode"), "%" + keyword + "%"));
      }

      // Get inbound in time range
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
        predicates.add(criteriaBuilder.equal(root.get("inboundType"), type));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  // Method to approve an inbound record
  @Override
  public Inbound approve(Long id, boolean accept) {
    InboundEntity oldinboundEntity = inboundRepository.findById(id).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    if (oldinboundEntity.getStatus() != InboundStatus.CHO_DUYET) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND.INVALID);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldinboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .isApproved(accept)
                    .approvedBy(userEntity)
                    .status(accept ? InboundStatus.CHO_HANG : InboundStatus.BAN_NHAP)
                    .build())
        .map(inboundRepository::save)
        .map(inboundMapper::toDTO)
        .orElse(null);
  }

  // Method to delete an inbound record
  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    InboundEntity oldinboundEntity = inboundRepository.findById(id).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    inboundDetailsService.deleteAllByInboundId(id);
    inboundBatchDetailService.deleteAllByInboundId(id);
    inboundRepository.deleteById(id); // Delete the inbound entity by ID
  }

  @Override
  @Transactional
  public Inbound saveInbound(CreateInboundRequest request) {
    Optional<InboundEntity> unsavedProduct = inboundRepository.findById(request.getInboundId());
    if (unsavedProduct.isEmpty()) {
      throw new HrmCommonException(INBOUND.NOT_EXIST);
    }
    InboundEntity inboundEntity = unsavedProduct.get();
    if (inboundEntity.getInboundType().isFromSupplier() && request.getSupplier() == null) {
      throw new HrmCommonException(SUPPLIER.NOT_EXIST);
    }
    if (inboundEntity.getInboundType().isFromBranch() && request.getFromBranch() == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }

    // Cập nhật inbound entity
    Inbound updatedInbound = inboundMapper.convertFromCreateRequest(request);
    updatedInbound.setId(inboundEntity.getId());
    updatedInbound.setToBranch(branchMapper.convertToDTOBasicInfo(inboundEntity.getToBranch()));
    updatedInbound.setApprovedBy(userMapper.convertToDtoBasicInfo(inboundEntity.getApprovedBy()));
    updatedInbound.setIsApproved(inboundEntity.getIsApproved());
    updatedInbound.setStatus(inboundEntity.getStatus());

    // Save updated inbound entity
    InboundEntity updatedInboundEntity =
        inboundRepository.save(inboundMapper.toEntity(updatedInbound));

    // Fetch existing InboundDetails and InboundBatchDetails
    List<InboundDetails> existingInboundDetails =
        inboundDetailsService.findByInboundId(inboundEntity.getId());
    List<InboundBatchDetail> existingInboundBatchDetails =
        inboundBatchDetailService.findByInboundId(inboundEntity.getId());

    // Lists for new/updated entities
    List<InboundDetails> inboundDetailsList = new ArrayList<>();
    List<InboundBatchDetail> inboundBatchDetailsList = new ArrayList<>();

    // Process InboundDetails from request
    for (ProductInbound productInbound : request.getProductInbounds()) {
      Product product = productService.addProductInInbound(productInbound);
      // Process InboundBatchDetails for each batch in the product inbound
      if (productInbound.getBatches() != null && !productInbound.getBatches().isEmpty()) {
        Batch firstBatch = productInbound.getBatches().get(0);

        // Kiểm tra firstBatch.getBatchCode() khác null và không rỗng
        if (firstBatch.getBatchCode() != null && !firstBatch.getBatchCode().trim().isEmpty()) {
          Double totalPrice = 0.0;
          Integer totalReceiveQuantity = 0;
          for (Batch batch : productInbound.getBatches()) {
            if (batch.getBatchCode() != null && !batch.getBatchCode().trim().isEmpty()) {
              Batch batchEntity = batchService.addBatchInInbound(batch, product);

              Optional<InboundBatchDetail> optionalInboundBatchDetail =
                  existingInboundBatchDetails.stream()
                      .filter(detail -> detail.getBatch().getId().equals(batchEntity.getId()))
                      .findFirst();

              InboundBatchDetail inboundBatchDetail;
              if (optionalInboundBatchDetail.isPresent()) {
                inboundBatchDetail = optionalInboundBatchDetail.get();
                inboundBatchDetail.setQuantity(
                    batch.getInboundBatchQuantity() != null ? batch.getInboundBatchQuantity() : 0);
                inboundBatchDetail.setInboundPrice(batch.getInboundPrice());
                existingInboundBatchDetails.remove(
                    inboundBatchDetail); // Remove from existing list, mark as processed
              } else {
                inboundBatchDetail =
                    InboundBatchDetail.builder()
                        .inbound(inboundMapper.toDTO(updatedInboundEntity))
                        .batch(batchEntity)
                        .quantity(
                            batch.getInboundBatchQuantity() != null
                                ? batch.getInboundBatchQuantity()
                                : 0)
                        .inboundPrice(batch.getInboundPrice())
                        .build();
              }
              totalReceiveQuantity = totalReceiveQuantity + inboundBatchDetail.getQuantity();
              totalPrice =
                  totalPrice
                      + (inboundBatchDetail.getInboundPrice().doubleValue()
                          * inboundBatchDetail.getQuantity());
              inboundBatchDetailsList.add(inboundBatchDetail);
            }
          }
          // set giá tiền và số lượng nhận được
          productInbound.setPrice(totalPrice / totalReceiveQuantity);
          productInbound.setReceiveQuantity(totalReceiveQuantity);
        } else {
          productInbound.setPrice(firstBatch.getInboundPrice().doubleValue());
          productInbound.setReceiveQuantity(firstBatch.getInboundBatchQuantity().intValue());
        }
      }
      // Update or create InboundDetails
      Optional<InboundDetails> optionalInboundDetails =
          existingInboundDetails.stream()
              .filter(detail -> detail.getProduct().getId().equals(product.getId()))
              .findFirst();

      InboundDetails inboundDetails;
      if (optionalInboundDetails.isPresent()) {
        inboundDetails = optionalInboundDetails.get();
        inboundDetails.setRequestQuantity(
            productInbound.getRequestQuantity() != null ? productInbound.getRequestQuantity() : 0);
        inboundDetails.setDiscount(
            productInbound.getDiscount() != null ? productInbound.getDiscount() : 0);
        inboundDetails.setTaxRate(
            productInbound.getTaxRate() != null ? productInbound.getTaxRate() : BigDecimal.ZERO);
        inboundDetails.setReceiveQuantity(
            productInbound.getReceiveQuantity() != null ? productInbound.getReceiveQuantity() : 0);
        inboundDetails.setInboundPrice(
            BigDecimal.valueOf(productInbound.getPrice() != null ? productInbound.getPrice() : 0));
        existingInboundDetails.remove(
            inboundDetails); // Remove from existing list, mark as processed
      } else {
        inboundDetails =
            InboundDetails.builder()
                .inbound(inboundMapper.toDTO(updatedInboundEntity))
                .product(product)
                .requestQuantity(
                    productInbound.getRequestQuantity() != null
                        ? productInbound.getRequestQuantity()
                        : 0)
                .discount(productInbound.getDiscount() != null ? productInbound.getDiscount() : 0)
                .receiveQuantity(
                    productInbound.getReceiveQuantity() != null
                        ? productInbound.getReceiveQuantity()
                        : 0)
                .receiveQuantity(
                    productInbound.getReceiveQuantity() != null
                        ? productInbound.getReceiveQuantity()
                        : 0)
                .inboundPrice(
                    BigDecimal.valueOf(
                        productInbound.getPrice() != null ? productInbound.getPrice() : 0))
                .taxRate(
                    productInbound.getTaxRate() != null
                        ? productInbound.getTaxRate()
                        : BigDecimal.ZERO)
                .build();
      }
      inboundDetailsList.add(inboundDetails);
    }

    // Delete remaining unmatched entities in existing lists (entities that are not present in the
    // request anymore)
    inboundDetailsService.deleteAll(existingInboundDetails);
    inboundBatchDetailService.deleteAll(existingInboundBatchDetails);

    // Save updated entities
    inboundDetailsService.saveAll(inboundDetailsList);
    inboundBatchDetailService.saveAll(inboundBatchDetailsList);
    return Optional.ofNullable(inboundEntity).map(inboundMapper::toDTO).orElse(null);
  }

  @Override
  @Transactional
  public Inbound submitInboundToSystem(CreateInboundRequest request) {
    // Fetch the InboundEntity from the repository
    InboundEntity inboundEntity =
        inboundRepository
            .findById(request.getInboundId())
            .orElseThrow(() -> new HrmCommonException(INBOUND.NOT_EXIST));

    // check status
    if (!inboundEntity.getStatus().isCheck()) {
      throw new HrmCommonException("Trạng thái của phiếu không hợp lệ");
    }

    // Map old and new quantities for comparison
    Map<Long, Integer> oldProductQuantities =
            Optional.ofNullable(inboundEntity.getInboundDetails())
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    detail -> detail.getProduct().getId(),
                                    detail -> detail.getReceiveQuantity() != null
                                            ? detail.getReceiveQuantity() : 0));

    Map<Long, Integer> oldBatchQuantities =
        Optional.ofNullable(inboundEntity.getInboundBatchDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getBatch().getId(),
                    batchDetail ->
                        batchDetail.getQuantity() != null ? batchDetail.getQuantity() : 0));

    saveInbound(request);
    inboundRepository.flush();
    entityManager.clear();
    InboundEntity updatedInboundEntity =
        inboundRepository
            .findById(request.getInboundId())
            .orElseThrow(() -> new HrmCommonException(INBOUND.NOT_EXIST));
    Map<Long, Integer> newProductQuantities =
            Optional.ofNullable(updatedInboundEntity.getInboundDetails())
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    detail -> detail.getProduct().getId(),
                                    detail -> detail.getReceiveQuantity() != null
                                            ? detail.getReceiveQuantity() : 0));

    Map<Long, Integer> newBatchQuantities =
        Optional.ofNullable(updatedInboundEntity.getInboundBatchDetails())
            .orElse(Collections.emptyList())
            .stream()
            .collect(
                Collectors.toMap(
                    batchDetail -> batchDetail.getBatch().getId(),
                    batchDetail ->
                        batchDetail.getQuantity() != null ? batchDetail.getQuantity() : 0));

    BranchEntity toBranch = updatedInboundEntity.getToBranch();
    SupplierEntity supplier = updatedInboundEntity.getSupplier();

    // Process Product changes
    for (Map.Entry<Long, Integer> entry : newProductQuantities.entrySet()) {
      Long productId = entry.getKey();
      int newQuantity = entry.getValue();
      int oldQuantity = oldProductQuantities.getOrDefault(productId, 0);
      int quantityDifference = newQuantity - oldQuantity;

      // Update BranchProduct with the difference
      ProductEntity productEntity = productMapper.toEntity(productService.getById(productId));
      branchProductService.updateBranchProductInInbound(
          toBranch, productEntity, BigDecimal.valueOf(quantityDifference));

      if (productSupplierService.findByProductAndSupplier(productEntity, supplier) == null) {
        ProductSuppliersEntity productSuppliersEntity = new ProductSuppliersEntity();
        productSuppliersEntity.setProduct(productEntity);
        productSuppliersEntity.setSupplier(supplier);
        productSupplierService.save(productSuppliersEntity);
      }
    }

    // Handle removed products
    for (Map.Entry<Long, Integer> entry : oldProductQuantities.entrySet()) {
      if (!newProductQuantities.containsKey(entry.getKey())) {
        Long productId = entry.getKey();
        int oldQuantity = entry.getValue();

        // Subtract the removed quantity from BranchProduct
        ProductEntity productEntity = productMapper.toEntity(productService.getById(productId));
        branchProductService.updateBranchProductInInbound(
            toBranch, productEntity, BigDecimal.valueOf(-oldQuantity));

        ProductSuppliers productSuppliers =
            productSupplierService.findByProductAndSupplier(productEntity, supplier);
        if (productSuppliers != null) {
          productSupplierService.delete(productSuppliers.getId());
        }
      }
    }

    // Process Batch changes
    for (Map.Entry<Long, Integer> entry : newBatchQuantities.entrySet()) {
      Long batchId = entry.getKey();
      int newQuantity = entry.getValue();
      int oldQuantity = oldBatchQuantities.getOrDefault(batchId, 0);
      int quantityDifference = newQuantity - oldQuantity;

      // Update BranchBatch with the difference
      BatchEntity batchEntity = batchMapper.toEntity(batchService.getById(batchId));
      branchBatchService.updateBranchBatchInInbound(
          toBranch, batchEntity, BigDecimal.valueOf(quantityDifference));

      if (productSupplierService.findByProductAndSupplier(batchEntity.getProduct(), supplier)
          == null) {
        ProductSuppliersEntity productSuppliersEntity = new ProductSuppliersEntity();
        productSuppliersEntity.setProduct(batchEntity.getProduct());
        productSuppliersEntity.setSupplier(supplier);
        productSupplierService.save(productSuppliersEntity);
      }
    }

    // Handle removed batches
    for (Map.Entry<Long, Integer> entry : oldBatchQuantities.entrySet()) {
      if (!newBatchQuantities.containsKey(entry.getKey())) {
        Long batchId = entry.getKey();
        int oldQuantity = entry.getValue();

        // Subtract the removed quantity from BranchBatch
        BatchEntity batchEntity = batchMapper.toEntity(batchService.getById(batchId));
        branchBatchService.updateBranchBatchInInbound(
            toBranch, batchEntity, BigDecimal.valueOf(-oldQuantity));

        ProductSuppliers productSuppliers =
            productSupplierService.findByProductAndSupplier(batchEntity.getProduct(), supplier);
        if (productSuppliers != null) {
          productSupplierService.delete(productSuppliers.getId());
        }
      }
    }

    // Save the updated inbound entity
    inboundRepository.save(updatedInboundEntity);

    // Tập hợp tất cả batch IDs duy nhất từ oldBatchQuantities và newBatchQuantities
    Set<Long> allBatchIds = new HashSet<>();
    allBatchIds.addAll(oldBatchQuantities.keySet());
    allBatchIds.addAll(newBatchQuantities.keySet());

    // Lặp qua tất cả batch IDs để cập nhật giá trung bình
    for (Long batchId : allBatchIds) {
      // Update average prices for this batch
      inboundBatchDetailService.updateAverageInboundPricesForBatches(batchId);
    }

    inboundDetailsService.updateAverageInboundPricesForProducts(inboundEntity);
    // Notification for Manager

    String message =
        "🔔 Thông báo: Phiếu nhập "
            + inboundEntity.getInboundCode()
            + " đã được thêm vào hệ"
            + " thống "
            + "bởi "
            + inboundEntity.getCreatedBy().getUserName();

    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setNotiName("Nhập phiếu vào kho");
    notification.setNotiType(NotificationType.NHAP_PHIEU_NHAP_VAO_HE_THONG);
    notification.setCreatedDate(LocalDateTime.now());

    notificationService.sendNotification(
        notification, userService.findAllManagerByBranchId(inboundEntity.getToBranch().getId()));
    // Return the updated inbound entity (or any other response you need)
    return inboundMapper.convertToBasicInfo(
        inboundEntity); // You can return a DTO or any other object
  }

  @Override
  public Inbound createInnitInbound(InboundType type) {
    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));
    BranchEntity branchEntity = userEntity.getBranch();
    if (!branchEntity.getBranchType().isMain() && type.isFromSupplier()) {
      throw new HrmCommonException("Chỉ có Kho chính mới được phép nhập hàng từ nhà cung cấp");
    }
    LocalDateTime currentDateTime = LocalDateTime.now();
    String inboundCode = WplUtil.generateNoteCode(currentDateTime, "IB");
    if (inboundRepository.existsByInboundCode(inboundCode)) {
      throw new HrmCommonException(INBOUND.EXIST);
    }

    if (branchEntity == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }
    InboundEntity inbound =
        InboundEntity.builder()
            .createdDate(currentDateTime)
            .inboundType(type)
            .status(InboundStatus.CHUA_LUU)
            .inboundCode(inboundCode)
            .createdBy(userEntity)
            .toBranch(branchEntity)
            .build();
    return Optional.ofNullable(inbound)
        .map(inboundRepository::save)
        .map(inboundMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void updateInboundStatus(InboundStatus status, Long id) {
    InboundEntity inbound = inboundRepository.findById(id).orElse(null);
    if (inbound == null) {
      throw new HrmCommonException(INBOUND.NOT_EXIST);
    }
    if (status.isWaitingForApprove()) {
      // Notification for Manager

      String message =
          "🔔 Thông báo: Phiếu nhập "
              + inbound.getInboundCode()
              + " đang chờ duyệt "
              + "bởi "
              + inbound.getCreatedBy().getUserName();

      Notification notification = new Notification();
      notification.setMessage(message);
      notification.setNotiName(NotificationType.YEU_CAU_DUYET_DON_NHAP.getDisplayName());
      notification.setNotiType(NotificationType.YEU_CAU_DUYET_DON_NHAP);
      notification.setCreatedDate(LocalDateTime.now());

      notificationService.sendNotification(
          notification, userService.findAllManagerByBranchId(inbound.getToBranch().getId()));
    }
    inboundRepository.updateInboundStatus(status, id);
  }

  @Override
  public ByteArrayOutputStream generateInboundPdf(Long inboundId)
      throws DocumentException, IOException {
    // Fetch Inbound and associated details
    InboundDetail inbound = getById(inboundId);
    if (inbound == null) {
      throw new EntityNotFoundException("Inbound record not found with ID: " + inboundId);
    }
    ByteArrayOutputStream out = pdfUtil.createReceiptPdf(inbound);

    return out;
  }
}
