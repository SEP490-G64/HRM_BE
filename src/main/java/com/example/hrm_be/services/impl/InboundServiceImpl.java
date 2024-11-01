package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.SUPPLIER;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InboundServiceImpl implements InboundService {
  @Autowired private InboundRepository inboundRepository;
  @Autowired private InboundDetailsRepository inboundDetailsRepository;
  @Autowired private ProductSuppliersRepository productSuppliersRepository;
  @Autowired private BranchProductRepository branchProductRepository;
  @Autowired private BranchBatchRepository branchBatchRepository;
  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;
  @Autowired private WplUtil wplUtil;
  @Autowired private BatchRepository batchRepository;
  @Autowired private InboundMapper inboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private SupplierMapper supplierMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private UserService userService;
  @Autowired private UserMapper userMapper;
  @Autowired private ProductRepository productRepository;

  @Override
  public InboundDetail getById(Long inboundId) {
    InboundEntity optionalInbound = inboundRepository.findById(inboundId).orElse(null);

    // Check if the inbound entity is null (i.e., not found)
    if (optionalInbound == null) {
      throw new HrmCommonException("Inbound not found with id: " + inboundId);
    }
    // Map InboundEntity to InboundDTO
    InboundDetail inboundDTO = new InboundDetail();
    inboundDTO.setId(optionalInbound.getId());
    inboundDTO.setInboundCode(optionalInbound.getInboundCode());
    inboundDTO.setInboundType(optionalInbound.getInboundType());
    inboundDTO.setCreatedDate(optionalInbound.getCreatedDate());
    inboundDTO.setInboundDate(optionalInbound.getInboundDate());
    inboundDTO.setTotalPrice(optionalInbound.getTotalPrice());
    inboundDTO.setIsApproved(optionalInbound.getIsApproved());
    inboundDTO.setStatus(optionalInbound.getStatus());
    inboundDTO.setApprovedBy(userMapper.convertToDtoBasicInfo(optionalInbound.getApprovedBy()));
    inboundDTO.setCreatedBy(userMapper.convertToDtoBasicInfo(optionalInbound.getCreatedBy()));
    inboundDTO.setFromBranch(branchMapper.convertToDTOBasicInfo(optionalInbound.getFromBranch()));
    inboundDTO.setSupplier(supplierMapper.toDTO(optionalInbound.getSupplier()));
    inboundDTO.setToBranch(branchMapper.convertToDTOBasicInfo(optionalInbound.getToBranch()));

    // Map inboundDetails to include product and batches
    List<InboundProductDetailDTO> productDetails =
        optionalInbound.getInboundDetails().stream()
            .filter(Objects::nonNull) // Filter out null inboundDetail objects
            .map(
                inboundDetail -> {
                  InboundProductDetailDTO productDetailDTO = new InboundProductDetailDTO();
                  productDetailDTO.setId(inboundDetail.getId());
                  productDetailDTO.setProductCode(inboundDetail.getProduct().getRegistrationCode());
                  productDetailDTO.setBaseUnit(
                      unitOfMeasurementMapper.toDTO(inboundDetail.getProduct().getBaseUnit()));
                  productDetailDTO.setDiscount(inboundDetail.getDiscount());
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
                                  batchDTO.setInboundPrice(batch.getInboundPrice());
                                  batchDTO.setBatchCode(batch.getBatchCode());

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
  public Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return inboundRepository.findAll(pageable).map(dao -> inboundMapper.toDTO(dao));
  }

  // Method to create a new Inbound record
  @Override
  public Inbound create(Inbound inbound) {
    if (inbound == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.EXIST); // Error handling for null inbound object
    }

    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity =
        userMapper.toEntity(userService.findLoggedInfoByEmail(email)); // Get user entity

    return Optional.ofNullable(inbound)
        .map(inboundMapper::toEntity)
        .map(
            e -> {
              e.setCreatedBy(userEntity);
              e.setCreatedDate(LocalDateTime.now());
              e.setStatus(InboundStatus.CHO_DUYET);
              e.setIsApproved(false);
              return inboundRepository.save(e);
            })
        .map(e -> inboundMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing Inbound record
  @Override
  public Inbound update(Inbound inbound) {
    InboundEntity oldinboundEntity = inboundRepository.findById(inbound.getId()).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    return Optional.ofNullable(oldinboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(inbound.getNote())
                    .inboundType(inbound.getInboundType())
                    .status(inbound.getStatus())
                    .taxable(inbound.getTaxable())
                    .totalPrice(inbound.getTotalPrice())
                    .inboundDate(inbound.getInboundDate())
                    .isApproved(inbound.getIsApproved())
                    .approvedBy(oldinboundEntity.getApprovedBy())
                    .build())
        .map(inboundRepository::save)
        .map(inboundMapper::toDTO)
        .orElse(null);
  }

  // Method to approve an inbound record
  @Override
  public Inbound approve(Long id, boolean accept) {
    InboundEntity oldinboundEntity = inboundRepository.findById(id).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldinboundEntity)
        .map(op -> op.toBuilder().isApproved(accept).approvedBy(userEntity).build())
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
    Inbound updatedInbound =
        Inbound.builder()
            .id(inboundEntity.getId())
            .inboundCode(request.getInboundCode())
            .createdDate(request.getCreatedDate() != null ? request.getCreatedDate() : null)
            .status(InboundStatus.BAN_NHAP)
            .inboundType(request.getInboundType())
            .createdBy(request.getCreatedBy())
            .toBranch(branchMapper.convertToDTOBasicInfo(inboundEntity.getToBranch()))
            .supplier(request.getSupplier() != null ? request.getSupplier() : null)
            .fromBranch(request.getFromBranch() != null ? request.getFromBranch() : null)
            .note(request.getNote())
            .build();

    // Save updated inbound entity
    InboundEntity updatedInboundEntity =
        inboundRepository.save(inboundMapper.toEntity(updatedInbound));

    // Fetch existing InboundDetails and InboundBatchDetails
    List<InboundDetailsEntity> existingInboundDetails =
        inboundDetailsRepository.findByInbound_Id(inboundEntity.getId());
    List<InboundBatchDetailEntity> existingInboundBatchDetails =
        inboundBatchDetailRepository.findByInbound_Id(inboundEntity.getId());

    // Lists for new/updated entities
    List<InboundDetailsEntity> inboundDetailsList = new ArrayList<>();
    List<InboundBatchDetailEntity> inboundBatchDetailsList = new ArrayList<>();

    // Process InboundDetails from request
    for (ProductInbound productInbound : request.getProductInbounds()) {
      ProductEntity product =
          productRepository
              .findByRegistrationCode(productInbound.getRegistrationCode())
              .orElseGet(
                  () -> {
                    ProductEntity newProduct = new ProductEntity();
                    newProduct.setRegistrationCode(productInbound.getRegistrationCode());
                    newProduct.setProductName(productInbound.getProductName());
                    newProduct.setBaseUnit(
                        unitOfMeasurementMapper.toEntity(productInbound.getBaseUnit()));
                    return productRepository.save(newProduct);
                  });

      // Update or create InboundDetails
      Optional<InboundDetailsEntity> optionalInboundDetails =
          existingInboundDetails.stream()
              .filter(detail -> detail.getProduct().getId().equals(product.getId()))
              .findFirst();

      InboundDetailsEntity inboundDetails;
      if (optionalInboundDetails.isPresent()) {
        inboundDetails = optionalInboundDetails.get();
        inboundDetails.setRequestQuantity(
            productInbound.getRequestQuantity() != null ? productInbound.getRequestQuantity() : 0);
        inboundDetails.setDiscount(
            productInbound.getDiscount() != null ? productInbound.getDiscount() : 0);
        inboundDetails.setReceiveQuantity(
            productInbound.getReceiveQuantity() != null ? productInbound.getReceiveQuantity() : 0);
        inboundDetails.setInboundPrice(
            BigDecimal.valueOf(productInbound.getPrice() != null ? productInbound.getPrice() : 0));
        existingInboundDetails.remove(
            inboundDetails); // Remove from existing list, mark as processed
      } else {
        inboundDetails =
            InboundDetailsEntity.builder()
                .inbound(updatedInboundEntity)
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
                .build();
      }
      inboundDetailsList.add(inboundDetails);

      // Process InboundBatchDetails for each batch in the product inbound
      if (productInbound.getBatchList() != null && !productInbound.getBatchList().isEmpty()) {
        for (Batch batch : productInbound.getBatchList()) {
          BatchEntity batchEntity =
              batchRepository
                  .findByBatchCodeAndProduct(batch.getBatchCode(), product)
                  .orElseGet(
                      () -> {
                        BatchEntity newBatch = new BatchEntity();
                        newBatch.setBatchCode(batch.getBatchCode());
                        newBatch.setInboundPrice(batch.getInboundPrice());
                        newBatch.setProduct(product);
                        newBatch.setExpireDate(batch.getExpireDate());
                        return batchRepository.save(newBatch);
                      });

          Optional<InboundBatchDetailEntity> optionalInboundBatchDetail =
              existingInboundBatchDetails.stream()
                  .filter(detail -> detail.getBatch().getId().equals(batchEntity.getId()))
                  .findFirst();

          InboundBatchDetailEntity inboundBatchDetail;
          if (optionalInboundBatchDetail.isPresent()) {
            inboundBatchDetail = optionalInboundBatchDetail.get();
            inboundBatchDetail.setQuantity(batch.getInboundBatchQuantity());
            inboundBatchDetail.setInboundPrice(batch.getInboundPrice());
            existingInboundBatchDetails.remove(
                inboundBatchDetail); // Remove from existing list, mark as processed
          } else {
            inboundBatchDetail =
                InboundBatchDetailEntity.builder()
                    .inbound(updatedInboundEntity)
                    .batch(batchEntity)
                    .quantity(batch.getInboundBatchQuantity())
                    .inboundPrice(batch.getInboundPrice())
                    .build();
          }
          inboundBatchDetailsList.add(inboundBatchDetail);
        }
      }
    }

    // Delete remaining unmatched entities in existing lists (entities that are not present in the
    // request anymore)
    inboundDetailsRepository.deleteAll(existingInboundDetails);
    inboundBatchDetailRepository.deleteAll(existingInboundBatchDetails);

    // Save updated entities
    inboundDetailsRepository.saveAll(inboundDetailsList);
    inboundBatchDetailRepository.saveAll(inboundBatchDetailsList);

    return Optional.ofNullable(inboundEntity).map(inboundMapper::toDTO).orElse(null);
  }

  @Override
  @Transactional
  public Inbound submitInboundToSystem(Long inboundId) {

    // Fetch the InboundEntity from the repository
    InboundEntity inboundEntity =
        inboundRepository
            .findById(inboundId)
            .orElseThrow(() -> new HrmCommonException(INBOUND.NOT_EXIST));

    // check status
    if (!inboundEntity.getStatus().isCheck()) {
      throw new HrmCommonException("Trạng thái của phiếu không hợp lệ");
    }
    List<ProductSuppliersEntity> productSuppliersEntities = new ArrayList<>();

    // Iterate through InboundDetails to manage Product-Supplier relations
    inboundEntity
        .getInboundDetails()
        .forEach(
            inboundDetail -> {
              ProductEntity product = inboundDetail.getProduct();
              SupplierEntity supplier = inboundEntity.getSupplier();

              if (supplier != null) {
                // Check if a ProductSupplierEntity exists for the product-supplier pair
                ProductSuppliersEntity productSupplier =
                    productSuppliersRepository
                        .findByProductAndSupplier(product, supplier)
                        .orElse(new ProductSuppliersEntity());

                // If it exists, update necessary fields, otherwise create a new one
                if (productSupplier.getId() == null) {
                  productSupplier.setProduct(product);
                  productSupplier.setSupplier(supplier);
                  productSuppliersEntities.add(productSupplier);
                }
              }
              productSuppliersRepository.saveAll(productSuppliersEntities);
            });

    // Get the branch details
    BranchEntity toBranch = inboundEntity.getToBranch();

    // Iterate through InboundBatchDetails to create or update BranchBatchEntity
    inboundEntity
        .getInboundBatchDetails()
        .forEach(
            inboundBatchDetail -> {
              BatchEntity batch = inboundBatchDetail.getBatch();
              Integer quantity =
                  inboundBatchDetail.getQuantity() != null
                      ? inboundBatchDetail.getQuantity()
                      : 0; //
              // Assume this represents the batch
              // quantity

              // Check if BranchBatchEntity already exists
              BranchBatchEntity branchBatch =
                  branchBatchRepository
                      .findByBranchAndBatch(toBranch, batch)
                      .orElse(new BranchBatchEntity());

              // If it exists, update the quantity, otherwise create a new one
              if (branchBatch.getId() != null) {
                branchBatch.setQuantity(
                    branchBatch.getQuantity() != null
                        ? branchBatch.getQuantity() + quantity
                        : quantity); // Update existing
                // quantity
              } else {
                branchBatch.setBatch(batch);
                branchBatch.setBranch(toBranch);
                branchBatch.setQuantity(quantity);
              }

              // Save the BranchBatchEntity
              branchBatchRepository.save(branchBatch);

              updateAverageInboundPricesForBatches(batch);
            });

    // Iterate through InboundDetails to create or update BranchProductEntity
    inboundEntity
        .getInboundDetails()
        .forEach(
            inboundDetail -> {
              ProductEntity product = inboundDetail.getProduct();
              Integer totalQuantity =
                  inboundBatchDetailRepository.findTotalQuantityByInboundAndProduct(
                      inboundId, product);

              Integer quantity =
                  totalQuantity != 0
                      ? totalQuantity
                      : (inboundDetail.getReceiveQuantity() != null
                          ? inboundDetail.getReceiveQuantity()
                          : 0); // Assume this represents the
              // quantity to be stored

              inboundDetail.setReceiveQuantity(quantity);

              // Check if BranchProductEntity already exists
              BranchProductEntity branchProduct =
                  branchProductRepository
                      .findByBranchAndProduct(toBranch, product)
                      .orElse(new BranchProductEntity());

              // If it exists, update the quantity, otherwise create a new one
              if (branchProduct.getId() != null) {
                branchProduct.setQuantity(
                    branchProduct.getQuantity() != null
                        ? branchProduct.getQuantity() + quantity
                        : quantity); // Update existing quantity
              } else {
                branchProduct.setProduct(product);
                branchProduct.setBranch(toBranch);
                branchProduct.setQuantity(quantity);
                branchProduct.setMinQuantity(null); // Set default min quantity, or use business
                // logic
                branchProduct.setMaxQuantity(null); // Set default max quantity, or use business
                // logic
              }

              // Save the BranchProductEntity
              branchProductRepository.save(branchProduct);
            });

    inboundRepository.save(inboundEntity);
    this.updateAverageInboundPricesForProductsAndInboundTotalPrice(inboundEntity);

    // Return the updated inbound entity (or any other response you need)
    return inboundMapper.convertToBasicInfo(
        inboundEntity); // You can return a DTO or any other object
  }

  private void updateAverageInboundPricesForBatches(BatchEntity batch) {
    // Get all inbound batch details of batch
    List<InboundBatchDetailEntity> allInboundBatchDetails =
        inboundBatchDetailRepository.findAllByBatchId(batch.getId());

    BigDecimal totalPrice = BigDecimal.ZERO;
    int totalQuantity = 0;

    // Check if batches in many inbounds
    if (!allInboundBatchDetails.isEmpty() && allInboundBatchDetails.size() != 1) {
      for (InboundBatchDetailEntity inboundBatchDetail : allInboundBatchDetails) {
        BigDecimal price = inboundBatchDetail.getInboundPrice();
        Integer quantity = inboundBatchDetail.getQuantity();

        totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(quantity)));
        totalQuantity += quantity;
      }
    }
    if (totalQuantity > 0) {
      BigDecimal averageProductPrice =
          totalPrice.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
      batch.setInboundPrice(averageProductPrice);
    }
    batchRepository.save(batch);
  }

  private void updateAverageInboundPricesForProductsAndInboundTotalPrice(InboundEntity inbound) {
    // Get all products with tax rate in inbounds
    List<InboundDetailsEntity> allDetails =
        inboundDetailsRepository.findInboundDetailsWithCategoryByInboundId(inbound.getId());
    // Variable to store value for get inbound total price
    BigDecimal inboundTotalPrice = BigDecimal.ZERO;

    for (InboundDetailsEntity inboundDetails : allDetails) {
      // Get all batches of product
      List<BatchEntity> allBatches =
          batchRepository.findAllByProductId(inboundDetails.getProduct().getId());

      // Variable to store value for update product average inbound price
      BigDecimal totalPrice = BigDecimal.ZERO;
      int totalQuantity = 0;

      // Get product tax rate
      BigDecimal taxRate = BigDecimal.ZERO;
      if (inbound.getTaxable() != null && inbound.getTaxable()) {
        if (inboundDetails.getProduct().getCategory() != null) {
          if (inboundDetails.getProduct().getCategory().getTaxRate() != null) {
            taxRate = inboundDetails.getProduct().getCategory().getTaxRate();
          }
        }
      }

      // Get product tax rate
      double discount = 0.0;
      if (inboundDetails.getDiscount() != null) {
        discount = inboundDetails.getDiscount() / 100;
      }

      // Check if product have batches
      if (!allBatches.isEmpty()) {
        for (BatchEntity batch : allBatches) {
          List<BranchBatchEntity> allBranchBatches =
              branchBatchRepository.findByBatchId(batch.getId());
          if (!allBranchBatches.isEmpty()) {
            // Get value of total batch price and total batch quantity
            for (BranchBatchEntity branchBatch : allBranchBatches) {
              BigDecimal batchPrice = batch.getInboundPrice();
              Integer quantity = branchBatch.getQuantity();

              totalPrice = totalPrice.add(batchPrice.multiply(BigDecimal.valueOf(quantity)));
              totalQuantity += quantity;
            }
          }

          InboundBatchDetailEntity inboundBatchDetails =
              inboundBatchDetailRepository
                  .findByBatch_IdAndAndInbound_Id(batch.getId(), inbound.getId())
                  .orElse(null);
          if (inboundBatchDetails != null) {
            BigDecimal originalPrice =
                inboundBatchDetails
                    .getInboundPrice()
                    .multiply(BigDecimal.valueOf(inboundBatchDetails.getQuantity()));
            inboundTotalPrice = inboundTotalPrice.add(originalPrice);

            if (taxRate.compareTo(BigDecimal.ZERO) != 0) {
              inboundTotalPrice = inboundTotalPrice.add(originalPrice.multiply(taxRate));
            }

            if (discount != 0.0) {
              inboundTotalPrice =
                  inboundTotalPrice.subtract(originalPrice.multiply(BigDecimal.valueOf(discount)));
            }
          }
        }
      } else {
        totalPrice =
            inboundDetailsRepository.findTotalPriceForProduct(inboundDetails.getProduct().getId());
        totalQuantity =
            branchProductRepository.findTotalQuantityForProduct(
                inboundDetails.getProduct().getId());

        BigDecimal originalPrice =
            inboundDetails
                .getInboundPrice()
                .multiply(BigDecimal.valueOf(inboundDetails.getReceiveQuantity()));
        inboundTotalPrice = inboundTotalPrice.add(originalPrice);

        if (taxRate.compareTo(BigDecimal.ZERO) != 0) {
          inboundTotalPrice = inboundTotalPrice.add(originalPrice.multiply(taxRate));
        }

        if (discount != 0.0) {
          inboundTotalPrice =
              inboundTotalPrice.subtract(originalPrice.multiply(BigDecimal.valueOf(discount)));
        }
      }
      if (totalQuantity > 0) {
        BigDecimal averageProductPrice =
            totalPrice.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
        inboundDetails.getProduct().setInboundPrice(averageProductPrice);
      } else {
        inboundDetails.getProduct().setInboundPrice(BigDecimal.ZERO);
      }
      productRepository.save(inboundDetails.getProduct());
    }

    inbound.setTotalPrice(inboundTotalPrice);
    inboundRepository.save(inbound);
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
    String inboundCode = wplUtil.generateInboundCode(currentDateTime);
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
  public Inbound updateInboundStatus(InboundStatus status, Long id) {
    Optional<InboundEntity> inbound = inboundRepository.findById(id);
    if (inbound.isEmpty()) {
      throw new HrmCommonException(INBOUND.NOT_EXIST);
    }
    inboundRepository.updateInboundStatus(status, id);
    return null;
  }
}
