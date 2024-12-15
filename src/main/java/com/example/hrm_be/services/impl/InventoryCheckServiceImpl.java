package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHBATCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHPRODUCT;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INVENTORY_CHECK;
import com.example.hrm_be.commons.enums.*;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.CreateInventoryCheckRequest;
import com.example.hrm_be.models.responses.InventoryUpdate;
import com.example.hrm_be.repositories.InventoryCheckRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.BranchBatchService;
import com.example.hrm_be.services.BranchProductService;
import com.example.hrm_be.services.InventoryCheckDetailsService;
import com.example.hrm_be.services.InventoryCheckProductDetailsService;
import com.example.hrm_be.services.InventoryCheckService;
import com.example.hrm_be.services.NotificationService;
import com.example.hrm_be.services.ProductService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Sinks.Many;

@Service
@Transactional
@Slf4j
public class InventoryCheckServiceImpl implements InventoryCheckService {

  private final ConcurrentMap<Long, Many<InventoryUpdate>> inventoryUpdateSinks =
      new ConcurrentHashMap<>();
  private final Map<Long, Many<InventoryUpdate>> inventoryCheckSinks = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> inventoryCheckEmitters =
      new ConcurrentHashMap<>();
  private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
  private ExecutorService nonBlockingService = Executors.newCachedThreadPool();
  @Autowired private InventoryCheckRepository inventoryCheckRepository;
  @Autowired private InventoryCheckMapper inventoryCheckMapper;
  @Autowired private UserService userService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductService productService;
  @Autowired private NotificationService notificationService;
  @Autowired private BatchService batchService;
  @Autowired private InventoryCheckDetailsService inventoryCheckDetailsService;
  @Autowired private InventoryCheckProductDetailsService inventoryCheckProductDetailsService;
  @Autowired private UserMapper userMapper;
  @Autowired private BranchProductMapper branchProductMapper;

  private final SimpMessagingTemplate messagingTemplate;

  public InventoryCheckServiceImpl(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public InventoryCheck getById(Long id) {
    // Retrieve an InventoryCheck by ID and convert it to a DTO
    return Optional.ofNullable(id)
        .flatMap(e -> inventoryCheckRepository.findById(e).map(b -> inventoryCheckMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public InventoryCheck getInventoryCheckDetailById(Long id) {
    // Fetch the InventoryCheck entity by ID
    InventoryCheck inventoryCheckEntity = getById(id);

    // Map basic InventoryCheck details to InventoryCheckDTO
    InventoryCheck inventoryCheckDTO = new InventoryCheck();
    inventoryCheckDTO.setId(inventoryCheckEntity.getId());
    inventoryCheckDTO.setCode(inventoryCheckEntity.getCode());
    inventoryCheckDTO.setCreatedDate(inventoryCheckEntity.getCreatedDate());
    inventoryCheckDTO.setIsApproved(inventoryCheckEntity.getIsApproved());
    inventoryCheckDTO.setStatus(inventoryCheckEntity.getStatus());
    inventoryCheckDTO.setNote(inventoryCheckEntity.getNote());

    // Map the branch details
    inventoryCheckDTO.setBranch(inventoryCheckEntity.getBranch());

    // Map the user details for createdBy and approvedBy
    if (inventoryCheckEntity.getCreatedBy() != null) {
      inventoryCheckDTO.setCreatedBy(inventoryCheckEntity.getCreatedBy());
    }
    if (inventoryCheckEntity.getApprovedBy() != null) {
      inventoryCheckDTO.setApprovedBy(inventoryCheckEntity.getApprovedBy());
    }

    // Populate inventoryCheckProductDetails
    List<InventoryCheckProductDetails> productDetails =
        inventoryCheckEntity.getInventoryCheckProductDetails().stream()
            .map(
                detail -> {
                  InventoryCheckProductDetails detailDTO = new InventoryCheckProductDetails();
                  // Set product details
                  Product productDTO = new Product();
                  productDTO.setId(detail.getProduct().getId());
                  productDTO.setBaseUnit(detail.getProduct().getBaseUnit());
                  productDTO.setProductName(detail.getProduct().getProductName());
                  productDTO.setRegistrationCode(detail.getProduct().getRegistrationCode());
                  productDTO.setLastUpdated(detail.getProduct().getLastUpdated());
                  detailDTO.setProduct(productDTO);
                  detailDTO.setSystemQuantity(detail.getSystemQuantity());
                  detailDTO.setCountedQuantity(detail.getCountedQuantity());
                  detailDTO.setReason(detail.getReason());

                  // Set difference quantity
                  detailDTO.setDifference(detail.getDifference());

                  return detailDTO;
                })
            .collect(Collectors.toList());

    // Populate inventoryCheckDetails (for batches)
    List<InventoryCheckProductDetails> batchDetails =
        inventoryCheckEntity.getInventoryCheckDetails().stream()
            .map(
                batchDetail -> {
                  InventoryCheckProductDetails batchDetailDTO = new InventoryCheckProductDetails();

                  // Set batch and product details
                  Batch batchDTO = new Batch();
                  batchDTO.setId(batchDetail.getBatch().getId());
                  batchDTO.setBatchCode(batchDetail.getBatch().getBatchCode());
                  batchDTO.setExpireDate(batchDetail.getBatch().getExpireDate());

                  Product productDTO = new Product();
                  productDTO.setId(batchDetail.getBatch().getProductId());
                  productDTO.setBaseUnit(batchDetail.getBatch().getUnitOfMeasurement());
                  productDTO.setProductName(batchDetail.getBatch().getProductName());
                  productDTO.setLastUpdated(batchDetail.getBatch().getLastUpdated());
                  productDTO.setRegistrationCode(batchDetail.getBatch().getRegistrationCode());

                  batchDetailDTO.setBatch(batchDTO);
                  batchDetailDTO.setProduct(productDTO);
                  batchDetailDTO.setCountedQuantity(batchDetail.getCountedQuantity());
                  batchDetailDTO.setSystemQuantity(batchDetail.getSystemQuantity());
                  batchDetailDTO.setReason(batchDetail.getReason());
                  // Set difference quantity
                  batchDetailDTO.setDifference(batchDetail.getDifference());

                  return batchDetailDTO;
                })
            .collect(Collectors.toList());

    // Combine the two lists
    List<InventoryCheckProductDetails> combinedProducts = new ArrayList<>();
    combinedProducts.addAll(productDetails);
    combinedProducts.addAll(batchDetails);
    combinedProducts.sort(
        Comparator.comparing((InventoryCheckProductDetails detail) -> detail.getProduct().getId())
            .thenComparing(
                detail -> {
                  // Handle batch ID for tie-breaking
                  if (detail.getBatch() != null) {
                    return detail.getBatch().getId();
                  }
                  return Long.MIN_VALUE; // Non-batch items get the lowest priority
                }));
    // Set details in the InventoryCheckDTO
    inventoryCheckDTO.setInventoryCheckProductDetails(combinedProducts);

    return inventoryCheckDTO;
  }

  @Override
  public Page<InventoryCheck> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String direction,
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      InventoryCheckStatus status) {
    // Check direction and set value for sort
    Sort sort =
        direction != null && direction.equalsIgnoreCase("ASC")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending(); // Default is descending

    Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

    Specification<InventoryCheckEntity> specification =
        getSpecification(branchId, keyword, startDate, endDate, status);

    return inventoryCheckRepository
        .findAll(specification, pageable)
        .map(dao -> inventoryCheckMapper.toDTO(dao));
  }

  private Specification<InventoryCheckEntity> getSpecification(
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      InventoryCheckStatus status) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Get inventory check have code containing keyword
      if (branchId != null) {
        // Get inventory check in registered user's branch
        predicates.add(criteriaBuilder.equal(root.get("branch").get("id"), branchId));
      }

      // Get inventory check have code containing keyword
      if (keyword != null && !keyword.isEmpty()) {
        predicates.add(criteriaBuilder.like(root.get("code"), "%" + keyword + "%"));
      }

      // Get inventory check in time range
      if (startDate != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), startDate));
      }
      if (endDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), endDate));
      }

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  @Override
  public InventoryCheck create(InventoryCheck inventoryCheck) {
    // Check if the InventoryCheck is null
    if (inventoryCheck == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK.EXIST); // Throw an error if null
    }

    // Get the email of the authenticated user
    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Convert DTO to entity, set metadata, and save it
    return Optional.ofNullable(inventoryCheck)
        .map(inventoryCheckMapper::toEntity)
        .map(
            e -> {
              e.setCreatedBy(userEntity);
              e.setCreatedDate(LocalDateTime.now());
              e.setStatus(InventoryCheckStatus.CHO_DUYET);
              e.setIsApproved(false);
              return inventoryCheckRepository.save(e);
            })
        .map(e -> inventoryCheckMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InventoryCheck update(InventoryCheck inventoryCheck) {
    // Find the existing InventoryCheck by ID
    InventoryCheckEntity oldInventoryCheckEntity =
        inventoryCheckRepository.findById(inventoryCheck.getId()).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    // Update the existing entity with new data
    return Optional.ofNullable(oldInventoryCheckEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(inventoryCheck.getNote())
                    .startDate(inventoryCheck.getStartDate())
                    .endDate(inventoryCheck.getEndDate())
                    .status(inventoryCheck.getStatus())
                    .build())
        .map(inventoryCheckRepository::save)
        .map(inventoryCheckMapper::toDTO)
        .orElse(null);
  }

  @Override
  public InventoryCheck approve(Long id, boolean accept) {
    // Find the existing InventoryCheck by ID
    InventoryCheckEntity oldInventoryCheckEntity =
        inventoryCheckRepository.findById(id).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    if (oldInventoryCheckEntity.getStatus() != InventoryCheckStatus.CHO_DUYET) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.INVALID);
    }

    // Get the email of the authenticated user
    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Approve the inventory check and set the approver
    return Optional.ofNullable(oldInventoryCheckEntity)
        .map(
            op ->
                op.toBuilder()
                    .isApproved(accept) // Set approval flag
                    .approvedBy(userEntity) // Set approver
                    .status(
                        accept ? InventoryCheckStatus.DA_CAN_BANG : InventoryCheckStatus.DANG_KIEM)
                    .build())
        .map(inventoryCheckRepository::save) // Save updated entity
        .map(inventoryCheckMapper::toDTO) // Convert to DTO
        .orElse(null);
  }

  @Override
  public InventoryCheck createInitInventoryCheck(LocalDateTime startDate) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String checkCode = WplUtil.generateNoteCode(currentDateTime, "IC");
    if (inventoryCheckRepository.existsByCode(checkCode)) {
      throw new HrmCommonException(INVENTORY_CHECK.EXIST);
    }
    if (startDate == null) {
      startDate = LocalDateTime.now();
    }
    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));
    BranchEntity branchEntity = userEntity.getBranch();
    if (branchEntity == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }
    InventoryCheckEntity outbound =
        InventoryCheckEntity.builder()
            .createdDate(currentDateTime)
            .status(InventoryCheckStatus.CHUA_LUU)
            .code(checkCode)
            .startDate(startDate != null ? startDate : currentDateTime)
            .createdBy(userEntity)
            .branch(branchEntity)
            .build();
    return Optional.ofNullable(outbound)
        .map(inventoryCheckRepository::save)
        .map(inventoryCheckMapper::toDTO)
        .orElse(null);
  }

  @Override
  public InventoryCheck saveInventoryCheck(CreateInventoryCheckRequest request) {
    InventoryCheck unsavedInventoryCheck = getById(request.getInventoryCheckId());

    if (unsavedInventoryCheck == null) {
      throw new HrmCommonException(INVENTORY_CHECK.NOT_EXIST);
    }

    // Xóa dữ liệu liên quan với batch delete
    inventoryCheckDetailsService.deleteByInventoryCheckId(request.getInventoryCheckId());
    inventoryCheckProductDetailsService.deleteByInventoryCheckId(request.getInventoryCheckId());

    // Cập nhật InventoryCheck entity
    InventoryCheck updatedInventoryCheck =
        InventoryCheck.builder()
            .id(unsavedInventoryCheck.getId())
            .code(request.getCode())
            .inventoryCheckType(request.getInventoryCheckType())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(InventoryCheckStatus.DANG_KIEM)
            .createdBy(unsavedInventoryCheck.getCreatedBy())
            .approvedBy(unsavedInventoryCheck.getApprovedBy())
            .branch(unsavedInventoryCheck.getBranch())
            .isApproved(unsavedInventoryCheck.getIsApproved())
            .note(request.getNote())
            .build();

    InventoryCheckEntity updatedInventoryCheckEntity =
        inventoryCheckRepository.save(inventoryCheckMapper.toEntity(updatedInventoryCheck));

    // Tải sẵn dữ liệu đã tồn tại để giảm truy vấn lặp
    List<InventoryCheckDetails> existingBatchDetails =
        inventoryCheckDetailsService.findAllByCheckId(updatedInventoryCheckEntity.getId());
    Map<Long, InventoryCheckDetails> batchDetailsMap =
        existingBatchDetails.stream()
            .collect(Collectors.toMap(detail -> detail.getBatch().getId(), Function.identity()));

    List<InventoryCheckProductDetails> existingProductDetails =
        inventoryCheckProductDetailsService.findAllByCheckId(updatedInventoryCheckEntity.getId());
    Map<Long, InventoryCheckProductDetails> productDetailsMap =
        existingProductDetails.stream()
            .collect(Collectors.toMap(detail -> detail.getProduct().getId(), Function.identity()));

    // Chuẩn bị danh sách để batch insert/update
    List<InventoryCheckDetails> batchDetailsToSave = new ArrayList<>();
    List<InventoryCheckProductDetails> productDetailsToSave = new ArrayList<>();

    // Xử lý từng productDetail
    for (InventoryCheckProductDetails productDetail : request.getInventoryCheckProductDetails()) {
      if (productDetail.getBatch() != null) {
        // Batch xử lý
        Batch batch = batchService.getById(productDetail.getBatch().getId());
        InventoryCheckDetails existingDetail = batchDetailsMap.get(batch.getId());

        if (existingDetail != null) {
          existingDetail.setReason(productDetail.getReason());
          existingDetail.setSystemQuantity(productDetail.getSystemQuantity());
          existingDetail.setCountedQuantity(productDetail.getCountedQuantity());
          existingDetail.setDifference(
              productDetail.getCountedQuantity() != null
                  ? productDetail.getSystemQuantity() - productDetail.getCountedQuantity()
                  : null);
          batchDetailsToSave.add(existingDetail);
        } else {
          InventoryCheckDetails newDetail =
              InventoryCheckDetails.builder()
                  .inventoryCheck(unsavedInventoryCheck)
                  .batch(batch)
                  .reason(productDetail.getReason())
                  .systemQuantity(productDetail.getSystemQuantity())
                  .countedQuantity(productDetail.getCountedQuantity())
                  .difference(
                      productDetail.getCountedQuantity() != null
                          ? productDetail.getSystemQuantity() - productDetail.getCountedQuantity()
                          : null)
                  .build();
          batchDetailsToSave.add(newDetail);
        }
      } else {
        // Product xử lý
        Product product = productService.getById(productDetail.getProduct().getId());
        InventoryCheckProductDetails existingDetail = productDetailsMap.get(product.getId());

        if (existingDetail != null) {
          existingDetail.setReason(productDetail.getReason());
          existingDetail.setSystemQuantity(productDetail.getSystemQuantity());
          existingDetail.setCountedQuantity(productDetail.getCountedQuantity());
          existingDetail.setDifference(
              productDetail.getCountedQuantity() != null
                  ? productDetail.getSystemQuantity() - productDetail.getCountedQuantity()
                  : null);
          productDetailsToSave.add(existingDetail);
        } else {
          InventoryCheckProductDetails newDetail =
              InventoryCheckProductDetails.builder()
                  .inventoryCheck(unsavedInventoryCheck)
                  .product(product)
                  .reason(productDetail.getReason())
                  .systemQuantity(productDetail.getSystemQuantity())
                  .countedQuantity(productDetail.getCountedQuantity())
                  .difference(
                      productDetail.getCountedQuantity() != null
                          ? productDetail.getSystemQuantity() - productDetail.getCountedQuantity()
                          : null)
                  .build();
          productDetailsToSave.add(newDetail);
        }
      }
    }

    // Batch lưu các thay đổi
    inventoryCheckDetailsService.saveAll(batchDetailsToSave);
    inventoryCheckProductDetailsService.saveAll(productDetailsToSave);

    return inventoryCheckMapper.toDTO(updatedInventoryCheckEntity);
  }

  @Override
  public InventoryCheck submitInventoryCheckToSystem(Long id) {
    InventoryCheck unsavedInventoryCheck = getById(id);
    if (unsavedInventoryCheck == null) {
      throw new HrmCommonException(INVENTORY_CHECK.NOT_EXIST);
    }

    LocalDateTime endDate = LocalDateTime.now();
    unsavedInventoryCheck.setEndDate(endDate);
    update(unsavedInventoryCheck);

    Branch branch = unsavedInventoryCheck.getBranch();

    // Tải dữ liệu BranchProduct và BranchBatch trước
    Map<Long, BranchProduct> branchProductMap =
        branchProductService.getAllByBranchId(branch.getId()).stream()
            .collect(Collectors.toMap(bp -> bp.getProduct().getId(), Function.identity()));

    Map<Long, BranchBatch> branchBatchMap =
        branchBatchService.getAllByBranchId(branch.getId()).stream()
            .collect(Collectors.toMap(bb -> bb.getBatch().getId(), Function.identity()));

    // Danh sách cập nhật batch
    List<BranchProduct> branchProductsToUpdate = new ArrayList<>();
    List<BranchBatch> branchBatchesToUpdate = new ArrayList<>();

    // Xử lý InventoryCheckProductDetails
    for (InventoryCheckProductDetails productDetail : unsavedInventoryCheck.getInventoryCheckProductDetails()) {
      if (productDetail.getDifference() != 0) {
        Product product = productDetail.getProduct();
        BranchProduct branchProduct = branchProductMap.get(product.getId());

        if (branchProduct == null) {
          throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
        }

        branchProduct.setQuantity(
                (branchProduct.getQuantity() == null ? BigDecimal.ZERO : branchProduct.getQuantity())
                        .subtract(BigDecimal.valueOf(productDetail.getDifference()))
        );
        branchProductsToUpdate.add(branchProduct);
      }
    }

    // Xử lý InventoryCheckDetails
    for (InventoryCheckDetails batchDetail : unsavedInventoryCheck.getInventoryCheckDetails()) {
      if (batchDetail.getDifference() != 0) {
          Batch batch = batchDetail.getBatch();
          BranchBatch branchBatch = branchBatchMap.get(batch.getId());

          if (branchBatch == null) {
              throw new HrmCommonException(BRANCHBATCH.NOT_EXIST);
          }

          branchBatch.setQuantity(
                  (branchBatch.getQuantity() == null ? BigDecimal.ZERO : branchBatch.getQuantity())
                          .subtract(BigDecimal.valueOf(batchDetail.getDifference()))
          );
          branchBatchesToUpdate.add(branchBatch);
      }
    }

    // Batch cập nhật vào cơ sở dữ liệu
    branchProductService.saveAll(
        branchProductsToUpdate.stream().map(branchProductMapper::toEntity).toList());
    branchBatchService.saveAll(branchBatchesToUpdate);

    // Gửi thông báo cho quản lý
    String message =
        String.format(
            "🔔 Thông báo: Phiếu kiểm %s đã được thêm vào hệ thống bởi %s",
            unsavedInventoryCheck.getCode(), unsavedInventoryCheck.getCreatedBy().getUserName());

    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setNotiName(NotificationType.NHAP_PHIEU_KIEM_VAO_HE_THONG.getDisplayName());
    notification.setNotiType(NotificationType.NHAP_PHIEU_KIEM_VAO_HE_THONG);
    notification.setCreatedDate(LocalDateTime.now());

    notificationService.sendNotification(
        notification, userService.findAllManagerByBranchId(branch.getId()));

    return unsavedInventoryCheck;
  }

  @Override
  public void delete(Long id) {
    // Check if the ID is valid
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if invalid
    }

    // Find the existing InventoryCheck by ID
    InventoryCheckEntity inventoryCheckEntity = inventoryCheckRepository.findById(id).orElse(null);
    if (inventoryCheckEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    // Delete the inventory check
    inventoryCheckRepository.deleteById(id);
  }

  @Override
  public void broadcastInventoryCheckUpdates(
      Set<Long> productIds, Set<Long> batchIds, Long branchId) {
    // Fetch all InventoryChecks based on status and branch ID
    List<InventoryCheckEntity> inventoryChecks =
        inventoryCheckRepository.findInventoryCheckEntitiesByStatusAndBranchId(
            List.of(
                InventoryCheckStatus.DANG_KIEM,
                InventoryCheckStatus.BAN_NHAP,
                InventoryCheckStatus.CHUA_LUU),
            branchId);

    // Prepare the update payload
    InventoryUpdate updatePayload =
        InventoryUpdate.builder().batchIds(batchIds).productIds(productIds).build();

    // Broadcast the update via WebSocket to clients
    inventoryChecks.forEach(
        inventoryCheck -> {
          Long inventoryCheckId = inventoryCheck.getId();
          log.info("before sending payload");
          // Broadcast the message to a specific WebSocket destination
          messagingTemplate.convertAndSend(
              "/topic/inventory-check/" + inventoryCheckId, // Topic for this inventoryCheck
              updatePayload // Payload to send
              );
          log.info("after sending payload");
        });
  }

  @Override
  public void updateInventoryCheckStatus(InventoryCheckStatus status, Long id) {
    InventoryCheck inventoryCheck = getById(id);
    if (inventoryCheck == null) {
      throw new HrmCommonException(INVENTORY_CHECK.NOT_EXIST);
    }
    if (status.isWaitingForApprove()) {
      // Notification for Manager

      String message =
          "🔔 Thông báo: Phiều kiểm "
              + inventoryCheck.getCode()
              + " đang chờ duyệt"
              + " bởi "
              + inventoryCheck.getCreatedBy().getUserName();

      Notification notification = new Notification();
      notification.setMessage(message);
      notification.setNotiName(NotificationType.YEU_CAU_DUYET_DON_KIEM.getDisplayName());
      notification.setNotiType(NotificationType.YEU_CAU_DUYET_DON_KIEM);
      notification.setCreatedDate(LocalDateTime.now());

      notificationService.sendNotification(
          notification, userService.findAllManagerByBranchId(inventoryCheck.getBranch().getId()));
    }
    inventoryCheckRepository.updateInventoryCheckStatus(status, id);
  }
}
