package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.InboundBatchDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.InboundBatchDetailService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InboundBatchDetailServiceImpl implements InboundBatchDetailService {
  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;

  @Autowired private InboundBatchDetailMapper inboundBatchDetailMapper;
  @Autowired private BatchService batchService;
  @Autowired private InboundRepository inboundRepository;
  @Autowired private BatchMapper batchMapper;

  @Override
  public void delete(Long id) {
    if (id == null || StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    InboundBatchDetailEntity oldInboundBatchDetail =
        inboundBatchDetailRepository.findById(id).orElse(null);
    if (oldInboundBatchDetail == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST); // Error if inbound entity is not found
    }

    inboundBatchDetailRepository.deleteById(id); // Delete the inbound entity by ID
  }

  @Override
  public List<InboundBatchDetail> findByInboundId(Long inboundId) {
    if (inboundId == null || inboundRepository.findById(inboundId).isEmpty()) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND.NOT_EXIST);
    }

    return inboundBatchDetailRepository.findByInbound_Id(inboundId).stream()
        .map(inboundBatchDetailMapper::convertToDTOWithBatchAndInbound)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteAll(List<InboundBatchDetail> inboundDetailsEntities) {
    List<Long> inboundDetailsIds =
        inboundDetailsEntities.stream()
            .map(InboundBatchDetail::getId) // Assuming getId() returns the ID of the entity
            .collect(Collectors.toList());
    inboundBatchDetailRepository.deleteByIds(inboundDetailsIds);
  }

  @Override
  public void saveAll(List<InboundBatchDetail> inboundBatchDetailEntities) {
    List<InboundBatchDetailEntity> save =
        inboundBatchDetailEntities.stream()
            .map(inboundBatchDetailMapper::toEntity)
            .collect(Collectors.toList());
    inboundBatchDetailRepository.saveAll(save);
  }

  @Override
  public void updateAverageInboundPricesForBatches(BatchEntity batch) {
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
    batchService.update(batchMapper.toDTO(batch));
  }

  @Override
  public Integer findTotalQuantityByInboundAndProduct(Long inboundId, ProductEntity product) {
    return inboundBatchDetailRepository.findTotalQuantityByInboundAndProduct(inboundId, product);
  }

  @Override
  public InboundBatchDetailEntity findByBatchIdAndAndInboundId(Long batchId, Long inboundId) {
    return inboundBatchDetailRepository
        .findByBatch_IdAndAndInbound_Id(batchId, inboundId)
        .orElse(null);
  }

  @Override
  public void deleteAllByInboundId(Long inboundId) {
    inboundBatchDetailRepository.deleteAllByInbound_Id(inboundId);
  }

  @Override
  public List<InboundBatchDetail> getInboundBatchDetailsByProductIdAndPeriod(
      Long productId, LocalDateTime startDate, LocalDateTime endDate) {
    return inboundBatchDetailRepository
        .findInboundDetailsByProductIdAndPeriod(
            productId, List.of(InboundStatus.HOAN_THANH), startDate, endDate)
        .stream()
        .map(inboundBatchDetailMapper::convertToDTOWithBatchAndInbound)
        .collect(Collectors.toList());
  }
}
