package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.components.InboundDetailsMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.services.*;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  public List<InboundDetails> findByInboundId(Long inboundId) {

    return inboundDetailsRepository.findByInbound_Id(inboundId).stream()
        .map(inboundDetailsMapper::toDTOWithInBoundDetails)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteAll(List<InboundDetails> inboundDetails) {
    List<Long> inboundDetailsIds =
        inboundDetails.stream()
            .map(InboundDetails::getId) // Assuming getId() returns the ID of the entity
            .collect(Collectors.toList());
    inboundDetailsRepository.deleteAllById(inboundDetailsIds);
  }

  @Override
  public void saveAll(List<InboundDetails> inboundDetails) {
    List<InboundDetailsEntity> save =
        inboundDetails.stream().map(inboundDetailsMapper::toEntity).collect(Collectors.toList());
    inboundDetailsRepository.saveAll(save);
  }

  @Override
  public void updateAverageInboundPricesForProducts(InboundEntity inbound) {
    // Get all products with tax rate in inbounds
    List<InboundDetailsEntity> allDetails =
        inboundDetailsRepository.findByInbound_Id(inbound.getId());

    for (InboundDetailsEntity inboundDetails : allDetails) {
      // Get all batches of product
      List<BatchEntity> allBatches =
          batchService.findAllByProductId(inboundDetails.getProduct().getId());

      // Variable to store value for update product average inbound price
      BigDecimal totalPrice = BigDecimal.ZERO;
      BigDecimal totalQuantity = BigDecimal.ZERO;

      // Check if product have batches
      if (!allBatches.isEmpty()) {
        for (BatchEntity batch : allBatches) {
          List<BranchBatchEntity> allBranchBatches =
              branchBatchService.findByBatchId(batch.getId());
          if (!allBranchBatches.isEmpty()) {
            // Get value of total batch price and total batch quantity
            for (BranchBatchEntity branchBatch : allBranchBatches) {
              BigDecimal batchPrice = batch.getInboundPrice();
              BigDecimal quantity = branchBatch.getQuantity();

              totalPrice = totalPrice.add(batchPrice.multiply(quantity));
              totalQuantity = totalQuantity.add(quantity);
            }
          }
        }
      } else {
        totalPrice =
            inboundDetailsRepository.findTotalPriceForProduct(inboundDetails.getProduct().getId());
        totalQuantity =
            branchProductService.findTotalQuantityForProduct(inboundDetails.getProduct().getId());
      }
      if (totalQuantity.compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal averageProductPrice = totalPrice.divide(totalQuantity, 2, RoundingMode.HALF_UP);
        inboundDetails.getProduct().setInboundPrice(averageProductPrice);
      } else {
        inboundDetails.getProduct().setInboundPrice(BigDecimal.ZERO);
      }
      productService.updateInboundPrice(productMapper.toDTO(inboundDetails.getProduct()));
    }
  }

  @Override
  public void deleteAllByInboundId(Long inboundId) {
    inboundDetailsRepository.deleteAllByInbound_Id(inboundId);
  }

  @Override
  public List<InboundDetails> getInboundDetailsByProductIdAndPeriod(
      Long productId, LocalDateTime startDate, LocalDateTime endDate) {
    return inboundDetailsRepository
        .findInboundDetailsByProductIdAndPeriod(
            productId,
            List.of(InboundStatus.KIEM_HANG, InboundStatus.HOAN_THANH),
            startDate,
            endDate)
        .stream()
        .map(inboundDetailsMapper::toDTOWithInBoundDetails)
        .collect(Collectors.toList());
  }
}
