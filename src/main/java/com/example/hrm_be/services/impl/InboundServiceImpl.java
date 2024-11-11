package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.SUPPLIER;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.components.*;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.PDFUtil;
import com.example.hrm_be.utils.WplUtil;
import com.itextpdf.text.DocumentException;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
  @Autowired private ProductSuppliersRepository productSuppliersRepository;

  @Autowired private InboundMapper inboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private ProductMapper productMapper;
  @Autowired private SupplierMapper supplierMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private UserMapper userMapper;

  @Autowired private InboundDetailsService inboundDetailsService;
  @Autowired private InboundBatchDetailService inboundBatchDetailService;
  @Autowired private UserService userService;
  @Autowired private ProductService productService;
  @Autowired private BatchService batchService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private ProductSupplierService productSupplierService;

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
                                  batchDTO.setExpireDate(batch.getExpireDate());

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
    Inbound updatedInbound = inboundMapper.convertFromCreateRequest(request);
    updatedInbound.setId(inboundEntity.getId());
    updatedInbound.setToBranch(branchMapper.convertToDTOBasicInfo(inboundEntity.getToBranch()));

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
                .build();
      }
      inboundDetailsList.add(inboundDetails);

      // Process InboundBatchDetails for each batch in the product inbound
      if (productInbound.getBatches() != null && !productInbound.getBatches().isEmpty()) {
        for (Batch batch : productInbound.getBatches()) {
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
          inboundBatchDetailsList.add(inboundBatchDetail);
        }
      }
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
                    productSupplierService.findByProductAndSupplier(product, supplier);

                // If it exists, update necessary fields, otherwise create a new one
                if (productSupplier == null) {
                  ProductSuppliersEntity productSuppliersAdd = new ProductSuppliersEntity();
                  productSuppliersAdd.setProduct(product);
                  productSuppliersAdd.setSupplier(supplier);
                  productSuppliersEntities.add(productSuppliersAdd);
                }
              }
              productSupplierService.saveAll(productSuppliersEntities);
            });

    // Get the branch details
    BranchEntity toBranch = inboundEntity.getToBranch();

    // Iterate through InboundBatchDetails to create or update BranchBatchEntity
    inboundEntity
        .getInboundBatchDetails()
        .forEach(
            inboundBatchDetail -> {
              BatchEntity batch = inboundBatchDetail.getBatch();
              int quantity =
                  inboundBatchDetail.getQuantity() != null ? inboundBatchDetail.getQuantity() : 0;
              // Assume this represents the batch
              // quantity

              // Save the BranchBatchEntity
              branchBatchService.updateBranchBatchInInbound(
                  toBranch, batch, BigDecimal.valueOf(quantity));

              inboundBatchDetailService.updateAverageInboundPricesForBatches(batch);
            });

    // Iterate through InboundDetails to create or update BranchProductEntity
    inboundEntity
        .getInboundDetails()
        .forEach(
            inboundDetail -> {
              ProductEntity product = inboundDetail.getProduct();
              Integer totalQuantity =
                  inboundBatchDetailService.findTotalQuantityByInboundAndProduct(
                      inboundId, product);

              Integer quantity =
                  totalQuantity != 0
                      ? totalQuantity
                      : (inboundDetail.getReceiveQuantity() != null
                          ? inboundDetail.getReceiveQuantity()
                          : 0); // Assume this represents the
              // quantity to be stored

              inboundDetail.setReceiveQuantity(quantity);

              branchProductService.updateBranchProductInInbound(
                  toBranch, product, BigDecimal.valueOf(quantity));
            });

    inboundRepository.save(inboundEntity);
    InboundEntity inbound =
        inboundDetailsService.updateAverageInboundPricesForProductsAndInboundTotalPrice(
            inboundEntity);
    inboundRepository.save(inbound);

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
    Optional<InboundEntity> inbound = inboundRepository.findById(id);
    if (inbound.isEmpty()) {
      throw new HrmCommonException(INBOUND.NOT_EXIST);
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
    ByteArrayOutputStream out = PDFUtil.createReceiptPdf(inbound);

    return out;
  }
}
