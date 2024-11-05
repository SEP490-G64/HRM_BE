package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHBATCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHPRODUCT;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INVENTORY_CHECK;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.PRODUCT;
import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.InventoryCheckMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InventoryCheckEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateInventoryCheckRequest;
import com.example.hrm_be.repositories.InventoryCheckRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.BranchBatchService;
import com.example.hrm_be.services.BranchProductService;
import com.example.hrm_be.services.BranchService;
import com.example.hrm_be.services.InventoryCheckDetailsService;
import com.example.hrm_be.services.InventoryCheckProductDetailsService;
import com.example.hrm_be.services.InventoryCheckService;
import com.example.hrm_be.services.ProductService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryCheckServiceImpl implements InventoryCheckService {

  @Autowired private InventoryCheckRepository inventoryCheckRepository;

  @Autowired private InventoryCheckMapper inventoryCheckMapper;

  @Autowired private UserService userService;

  @Autowired private BranchService branchService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductService productService;
  @Autowired private BatchService batchService;
  @Autowired private InventoryCheckDetailsService inventoryCheckDetailsService;
  @Autowired private InventoryCheckProductDetailsService inventoryCheckProductDetailsService;

  @Autowired private UserMapper userMapper;
  @Autowired private BranchMapper branchMapper;

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
                  productDTO.setProductName(detail.getProduct().getProductName());
                  productDTO.setRegistrationCode(detail.getProduct().getRegistrationCode());
                  detailDTO.setProduct(productDTO);
                  detailDTO.setReason(detail.getReason());

                  // Set difference quantity
                  detailDTO.setDifference(detail.getDifference());

                  return detailDTO;
                })
            .collect(Collectors.toList());

    // Populate inventoryCheckDetails (for batches)
    List<InventoryCheckDetails> batchDetails =
        inventoryCheckEntity.getInventoryCheckDetails().stream()
            .map(
                batchDetail -> {
                  InventoryCheckDetails batchDetailDTO = new InventoryCheckDetails();

                  // Set batch and product details
                  Batch batchDTO = new Batch();
                  batchDTO.setId(batchDetail.getBatch().getId());
                  batchDTO.setBatchCode(batchDetail.getBatch().getBatchCode());
                  batchDTO.setExpireDate(batchDetail.getBatch().getExpireDate());

                  Product productDTO = new Product();
                  productDTO.setId(batchDetail.getBatch().getProduct().getId());
                  productDTO.setProductName(batchDetail.getBatch().getProduct().getProductName());
                  productDTO.setRegistrationCode(
                      batchDetail.getBatch().getProduct().getRegistrationCode());

                  batchDetailDTO.setBatch(batchDTO);
                  batchDetailDTO.setProduct(productDTO);
                  batchDetailDTO.setReason(batchDetail.getReason());
                  // Set difference quantity
                  batchDetailDTO.setDifference(batchDetail.getDifference());

                  return batchDetailDTO;
                })
            .collect(Collectors.toList());

    // Set details in the InventoryCheckDTO
    inventoryCheckDTO.setInventoryCheckProductDetails(productDetails);
    inventoryCheckDTO.setInventoryCheckDetails(batchDetails);

    return inventoryCheckDTO;
  }

  @Override
  public Page<InventoryCheck> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create a Pageable object for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated InventoryChecks and map them to DTOs
    return inventoryCheckRepository.findAll(pageable).map(dao -> inventoryCheckMapper.toDTO(dao));
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
                    .build())
        .map(inventoryCheckRepository::save) // Save updated entity
        .map(inventoryCheckMapper::toDTO) // Convert to DTO
        .orElse(null);
  }

  @Override
  public InventoryCheck createInitInventoryCheck() {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String checkCode = WplUtil.generateNoteCode(currentDateTime, "IC");
    if (inventoryCheckRepository.existsByCode(checkCode)) {
      throw new HrmCommonException(INVENTORY_CHECK.EXIST);
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

    // Delete existing details if any
    inventoryCheckDetailsService.deleteByInventoryCheckId(request.getInventoryCheckId());
    inventoryCheckProductDetailsService.deleteByInventoryCheckId(request.getInventoryCheckId());

    // Build the new InventoryCheck entity
    InventoryCheck updatedInventoryCheck =
        InventoryCheck.builder()
            .id(unsavedInventoryCheck.getId()) // Retain the existing ID
            .code(request.getInventoryCheckCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(InventoryCheckStatus.DANG_KIEM) // Example status
            .createdBy(unsavedInventoryCheck.getCreatedBy())
            .approvedBy(null) // Default to null for new checks
            .branch(unsavedInventoryCheck.getBranch())
            .isApproved(false)
            .note(request.getNote())
            .build();

    // Save the updated inventory check entity
    InventoryCheckEntity updatedInventoryCheckEntity =
        inventoryCheckRepository.save(inventoryCheckMapper.toEntity(updatedInventoryCheck));

    // Process each product or batch detail in the request
    for (InventoryCheckProductDetails productDetail : request.getInventoryCheckProductDetails()) {
      Product product = productDetail.getProduct();
      Batch batch = productDetail.getBatch();

      // Process as batch detail if batch information is provided
      if (batch != null) {
        Batch unsavedBatch = batchService.getById(batch.getId());

        InventoryCheckDetails existingBatchDetail =
            inventoryCheckDetailsService.findByCheckIdAndBatchId(
                updatedInventoryCheckEntity.getId(), batch.getId());

        InventoryCheckDetails inventoryCheckDetail;
        if (existingBatchDetail != null) {
          inventoryCheckDetail = existingBatchDetail;
          inventoryCheckDetail.setReason(productDetail.getReason());
          inventoryCheckDetail.setSystemQuantity(productDetail.getSystemQuantity());
          inventoryCheckDetail.setCountedQuantity(productDetail.getCountedQuantity());
          inventoryCheckDetail.setDifference(
              productDetail.getSystemQuantity() - productDetail.getCountedQuantity());
          // Update existing batch detail with new quantity
          inventoryCheckDetailsService.update(inventoryCheckDetail);
        } else {
          // Create a new batch detail
          inventoryCheckDetail =
              InventoryCheckDetails.builder()
                  .inventoryCheck(unsavedInventoryCheck)
                  .batch(unsavedBatch)
                  .countedQuantity(productDetail.getCountedQuantity())
                  .systemQuantity(productDetail.getSystemQuantity())
                  .reason(productDetail.getReason())
                  .difference(
                      productDetail.getSystemQuantity() - productDetail.getCountedQuantity())
                  .build();
          inventoryCheckDetailsService.create(inventoryCheckDetail);
        }

      } else {
        // Process as product detail if no batch is specified
        Product unsavedProduct = productService.getById(product.getId());

        if (unsavedProduct == null) {
          throw new HrmCommonException(PRODUCT.NOT_EXIST);
        }

        InventoryCheckProductDetails existingProductDetail =
            inventoryCheckProductDetailsService.findByCheckIdAndProductId(
                updatedInventoryCheck.getId(), product.getId());

        InventoryCheckProductDetails inventoryCheckProductDetail;
        if (existingProductDetail != null) {
          // Update existing product detail with new quantity
          inventoryCheckProductDetail = existingProductDetail;
          inventoryCheckProductDetail.setCountedQuantity(productDetail.getCountedQuantity());
          inventoryCheckProductDetail.setSystemQuantity(productDetail.getSystemQuantity());
          inventoryCheckProductDetail.setDifference(
              productDetail.getSystemQuantity() - productDetail.getCountedQuantity());
          inventoryCheckProductDetail.setReason(productDetail.getReason());
          inventoryCheckProductDetailsService.update(inventoryCheckProductDetail);
        } else {
          // Create a new product detail
          inventoryCheckProductDetail =
              InventoryCheckProductDetails.builder()
                  .inventoryCheck(unsavedInventoryCheck)
                  .product(product)
                  .countedQuantity(productDetail.getCountedQuantity())
                  .systemQuantity(productDetail.getSystemQuantity())
                  .difference(
                      productDetail.getSystemQuantity() - productDetail.getCountedQuantity())
                  .reason(productDetail.getReason())
                  .build();
        }
        inventoryCheckProductDetailsService.create(inventoryCheckProductDetail);
      }
    }

    return inventoryCheckMapper.toDTO(updatedInventoryCheckEntity);
  }

  @Override
  public InventoryCheck submitInventoryCheckToSystem(Long id) {
    InventoryCheck unsavedInventoryCheck = getById(id);

    if (unsavedInventoryCheck == null) {
      throw new HrmCommonException(INVENTORY_CHECK.NOT_EXIST);
    }
    Branch branch = unsavedInventoryCheck.getBranch();
    // Process each InventoryCheckProductDetail
    for (InventoryCheckProductDetails productDetail :
        unsavedInventoryCheck.getInventoryCheckProductDetails()) {
      Product product = productDetail.getProduct();

      // Find the BranchProduct entity for this product and branch
      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(branch.getId(), product.getId());

      if (branchProduct == null) {
        throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
      }
      // Subtract the converted quantity
      branchProduct.setQuantity(
          branchProduct.getQuantity().subtract(BigDecimal.valueOf(productDetail.getDifference())));
      branchProductService.update(branchProduct);
    }

    // Process each OutboundDetail (for batches)
    for (InventoryCheckDetails batchDetail : unsavedInventoryCheck.getInventoryCheckDetails()) {
      Batch batch = batchDetail.getBatch();

      // Find the BranchBatch entity for this batch and branch
      BranchBatch branchBatch =
          branchBatchService.getByBranchIdAndBatchId(branch.getId(), batch.getId());
      if (branchBatch == null) {
        throw new HrmCommonException(BRANCHBATCH.NOT_EXIST);
      }
      // Find the BranchProduct entity for this product and branch
      BranchProduct branchProduct =
          branchProductService.getByBranchIdAndProductId(
              branch.getId(), branchBatch.getBatch().getProduct().getId());
      // Subtract the quantity
      branchBatch.setQuantity(
          branchBatch.getQuantity().subtract(BigDecimal.valueOf(batchDetail.getDifference())));
      branchBatchService.update(branchBatch);
      branchProduct.setQuantity(
          branchProduct.getQuantity().subtract(BigDecimal.valueOf(batchDetail.getDifference())));
      branchProductService.update(branchProduct);
    }
    return null;
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
  public void updateInventoryCheckStatus(InventoryCheckStatus status, Long id) {
    InventoryCheck inventoryCheck = getById(id);
    if (inventoryCheck == null) {
      throw new HrmCommonException(INVENTORY_CHECK.NOT_EXIST);
    }
    inventoryCheckRepository.updateInboundStatus(status, id);
  }
}
