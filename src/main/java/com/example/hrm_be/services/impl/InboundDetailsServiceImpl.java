package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundDetailsMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.*;
import io.micrometer.common.util.StringUtils;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InboundDetailsServiceImpl implements InboundDetailsService {

  @Autowired private InboundDetailsRepository inboundDetailsRepository;

  @Autowired private BatchService batchService;
  @Autowired private BranchBatchService branchBatchService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private InboundBatchDetailService inboundBatchDetailService;
  @Autowired private ProductService productService;

  @Autowired private InboundDetailsMapper inboundDetailsMapper;
  @Autowired private ProductMapper productMapper;

  @Override
  public InboundDetails getById(Long id) {
    // Retrieve inbound details by ID and convert to DTO
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                inboundDetailsRepository
                    .findById(e)
                    .map(b -> inboundDetailsMapper.toDTOWithInBoundDetails(b)))
        .orElse(null);
  }

  @Override
  public Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create pageable request for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated inbound details and map to DTOs
    return inboundDetailsRepository.findAll(pageable).map(dao -> inboundDetailsMapper.toDTO(dao));
  }

  @Override
  public InboundDetails create(InboundDetails inboundDetails) {
    // Validate that inboundDetails is not null
    if (inboundDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_DETAILS.EXIST); // Throw error if null
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inboundDetails)
        .map(inboundDetailsMapper::toEntity)
        .map(e -> inboundDetailsRepository.save(e))
        .map(e -> inboundDetailsMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InboundDetails update(InboundDetails inboundDetails) {
    // Find existing inbound details by ID
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(inboundDetails.getId()).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Update fields of the existing entity
    return Optional.ofNullable(oldInboundDetailsEntity)
        .map(
            op ->
                op.toBuilder()
                    .receiveQuantity(inboundDetails.getReceiveQuantity())
                    .requestQuantity(inboundDetails.getRequestQuantity())
                    .build())
        .map(inboundDetailsRepository::save)
        .map(inboundDetailsMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // Validate the ID
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if invalid
    }

    // Find existing inbound details by ID
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(id).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Delete the inbound details
    inboundDetailsRepository.deleteById(id);
  }

  @Override
  public List<InboundDetailsEntity> findByInboundId(Long inboundId) {
      return inboundDetailsRepository.findByInbound_Id(inboundId);
  }

  @Override
  public void deleteAll(List<InboundDetailsEntity> inboundDetailsEntities) {

    inboundDetailsRepository.deleteAll(inboundDetailsEntities);
  }

  @Override
  public void saveAll(List<InboundDetailsEntity> inboundDetailsEntities) {

    inboundDetailsRepository.saveAll(inboundDetailsEntities);
  }

  @Override
  public InboundEntity updateAverageInboundPricesForProductsAndInboundTotalPrice(InboundEntity inbound) {
    // Get all products with tax rate in inbounds
    List<InboundDetailsEntity> allDetails =
            inboundDetailsRepository.findInboundDetailsWithCategoryByInboundId(inbound.getId());
    // Variable to store value for get inbound total price
    BigDecimal inboundTotalPrice = BigDecimal.ZERO;

    for (InboundDetailsEntity inboundDetails : allDetails) {
      // Get all batches of product
      List<BatchEntity> allBatches =
              batchService.findAllByProductId(inboundDetails.getProduct().getId());

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
                  branchBatchService.findByBatchId(batch.getId());
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
                  inboundBatchDetailService
                          .findByBatchIdAndAndInboundId(batch.getId(), inbound.getId());

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
                branchProductService.findTotalQuantityForProduct(
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
      productService.update(productMapper.toDTO(inboundDetails.getProduct()));
    }

    inbound.setTotalPrice(inboundTotalPrice);

    return inbound;
  }
}
