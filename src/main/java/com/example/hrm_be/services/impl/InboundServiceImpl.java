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
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.InboundProductDetailDTO;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  @Autowired private BranchRepository branchRepository;
  @Autowired private InboundDetailsRepository inboundDetailsRepository;
  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;
  @Autowired private WplUtil wplUtil;
  @Autowired private BranchBatchRepository branchBatchRepository;
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
    inboundDTO.setInboundDate(optionalInbound.getInboundDate());
    inboundDTO.setTotalPrice(optionalInbound.getTotalPrice());
    inboundDTO.setIsApproved(optionalInbound.getIsApproved());
    inboundDTO.setStatus(optionalInbound.getStatus());
    inboundDTO.setFromBranch(branchMapper.convertToDTOBasicInfo(optionalInbound.getFromBranch()));
    inboundDTO.setSupplier(supplierMapper.toDTO(optionalInbound.getSupplier()));
    inboundDTO.setToBranch(branchMapper.convertToDTOBasicInfo(optionalInbound.getToBranch()));

    // Map inboundDetails to include product and batches
    List<InboundProductDetailDTO> productDetails =
        optionalInbound.getInboundDetails().stream()
            .filter(Objects::nonNull)  // Filter out null inboundDetail objects
            .map(inboundDetail -> {
              InboundProductDetailDTO productDetailDTO = new InboundProductDetailDTO();
              productDetailDTO.setId(inboundDetail.getId());
              productDetailDTO.setRequestQuantity(inboundDetail.getRequestQuantity());
              productDetailDTO.setReceiveQuantity(inboundDetail.getReceiveQuantity());

              // Check if product is null
              if (inboundDetail.getProduct() != null) {
                productDetailDTO.setProductId(inboundDetail.getProduct().getId());
                productDetailDTO.setProductName(inboundDetail.getProduct().getProductName());

                // Map batches associated with this product in the context of this inbound
                List<Batch> batches = inboundDetail.getProduct().getBatches().stream()
                    .filter(Objects::nonNull)  // Filter out null batch objects
                    .filter(batch -> batch.getInboundBatchDetail() != null && batch.getInboundBatchDetail().stream()
                        .anyMatch(inboundBatchDetail -> inboundBatchDetail.getInbound().getId().equals(optionalInbound.getId()))) // Only batches belonging to this inbound
                    .map(batch -> {
                      Batch batchDTO = new Batch();
                      batchDTO.setId(batch.getId());
                      batchDTO.setBatchCode(batch.getBatchCode());

                      // Find the quantity for this product-batch from the inboundBatchDetails
                      Integer quantity = batch.getInboundBatchDetail().stream()
                          .filter(Objects::nonNull)  // Ensure inboundBatchDetail is not null
                          .filter(inboundBatchDetail -> inboundBatchDetail.getInbound().getId().equals(optionalInbound.getId()))
                          .map(inboundBatchDetail -> inboundBatchDetail.getQuantity()!=null ?
                              inboundBatchDetail.getQuantity() :0)
                          .findFirst().orElse(0); // Default quantity if not found

                      batchDTO.setInboundBatchQuantity(quantity);
                      return batchDTO;
                    }).collect(Collectors.toList());

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
  public Inbound submitDraftInbound(CreateInboundRequest request) {
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
    Inbound updatedInbound =
        Inbound.builder()
            .id(
                inboundEntity
                    .getId()) // Retain the existing ID (and other immutable properties, if any)
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

    // Save the updated entity back to the repository
    InboundEntity updatedInboundEntity =
        inboundRepository.save(inboundMapper.toEntity(updatedInbound));

    List<InboundDetailsEntity> inboundDetailsList = new ArrayList<>();
    List<InboundBatchDetailEntity> inboundBatchDetailsList =
        new ArrayList<>(); // List for inbound-batch details

    for (ProductInbound productInbound : request.getProductInbounds()) {
      ProductEntity product =
          productRepository
              .findByRegistrationCode(productInbound.getRegistrationCode())
              .orElseGet(
                  () -> {
                    // If product doesn't exist, create a new one
                    ProductEntity newProduct = new ProductEntity();
                    newProduct.setRegistrationCode(productInbound.getRegistrationCode());
                    newProduct.setProductName(productInbound.getProductName());
                    newProduct.setBaseUnit(
                        unitOfMeasurementMapper.toEntity(productInbound.getBaseUnit()));
                    return productRepository.save(newProduct);
                  });
      if (productInbound.getBatchList() != null && !productInbound.getBatchList().isEmpty()) {
        for (Batch batch : productInbound.getBatchList()) {
          // Create or find the batch entity
          BatchEntity batchEntity =
              batchRepository
                  .findByBatchCodeAndProduct(batch.getBatchCode(),product)
                  .orElseGet(
                      () -> {
                        BatchEntity newBatch = new BatchEntity();
                        newBatch.setBatchCode(batch.getBatchCode());
                        newBatch.setProduct(product);
                        newBatch.setExpireDate(batch.getExpireDate());
                        return batchRepository.save(newBatch);
                      });

          Optional<InboundBatchDetailEntity> optionalInboundBatchDetailEntity =
              inboundBatchDetailRepository.findByBatch_IdAndAndInbound_Id(
                  batchEntity.getId(), updatedInbound.getId());
          // Create InboundBatchDetailsEntity to link InboundEntity and BatchEntity
          InboundBatchDetailEntity inboundBatchDetails;
          if (optionalInboundBatchDetailEntity.isPresent()) {
            inboundBatchDetails = optionalInboundBatchDetailEntity.get();
            inboundBatchDetails.setQuantity(batch.getInboundBatchQuantity());
          } else {
            inboundBatchDetails =
                InboundBatchDetailEntity.builder()
                    .inbound(updatedInboundEntity)
                    .batch(batchEntity)
                    .quantity(batch.getInboundBatchQuantity())
                    .build();
          }
          // Set the quantity for the batch

          inboundBatchDetailsList.add(inboundBatchDetails);
        }
      }

      // Create InboundDetails
      // Try to find the existing InboundDetailsEntity

      Optional<InboundDetailsEntity> optionalInboundDetails =
          inboundDetailsRepository.findByInbound_IdAndProduct_Id(
              updatedInboundEntity.getId(), product.getId());

      InboundDetailsEntity inboundDetails;

      if (optionalInboundDetails.isPresent()) {
        // If the entity exists, update it
        inboundDetails = optionalInboundDetails.get();
        inboundDetails.setRequestQuantity(productInbound.getRequestQuantity());
        inboundDetails.setReceiveQuantity(productInbound.getReceiveQuantity());

        // You can update other fields if necessary
      } else {
        // If the entity doesn't exist, create a new one
        inboundDetails =
            InboundDetailsEntity.builder()
                .inbound(updatedInboundEntity)
                .requestQuantity(productInbound.getRequestQuantity())
                .product(product)
                .receiveQuantity(productInbound.getReceiveQuantity())
                .build();
      }
      inboundDetailsList.add(inboundDetails);
    }
    inboundDetailsRepository.saveAll(inboundDetailsList);
    inboundBatchDetailRepository.saveAll(inboundBatchDetailsList);
    return Optional.ofNullable(inboundEntity).map(inboundMapper::toDTO).orElse(null);
  }

  @Override
  public Inbound createInnitInbound(InboundType type) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String inboundCode = wplUtil.generateInboundCode(currentDateTime);
    if (inboundRepository.existsByInboundCode(inboundCode)) {
      throw new HrmCommonException(INBOUND.EXIST);
    }
    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));
    BranchEntity branchEntity = userEntity.getBranch();
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
}
