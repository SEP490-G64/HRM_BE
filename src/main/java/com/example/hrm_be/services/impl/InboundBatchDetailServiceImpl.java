package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.InboundBatchDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import com.example.hrm_be.services.BatchService;
import com.example.hrm_be.services.InboundBatchDetailService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InboundBatchDetailServiceImpl implements InboundBatchDetailService {
  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;

  @Autowired private InboundBatchDetailMapper inboundBatchDetailMapper;
  @Autowired private BatchService batchService;
  @Autowired private BatchMapper batchMapper;

  @Override
  public InboundBatchDetail create(InboundBatchDetail inboundBatchDetail) {
    if (inboundBatchDetail == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inboundBatchDetail)
        .map(e -> inboundBatchDetailMapper.toEntity(e))
        .map(e -> inboundBatchDetailRepository.save(e))
        .map(e -> inboundBatchDetailMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InboundBatchDetail update(InboundBatchDetail inboundBatchDetail) {
    // Retrieve the existing branch entity by ID
    InboundBatchDetailEntity oldInboundBatchDetail =
        inboundBatchDetailRepository.findById(inboundBatchDetail.getId()).orElse(null);
    if (oldInboundBatchDetail == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST);
    }

    // Update the fields of the existing branch entity with new values
    return Optional.ofNullable(oldInboundBatchDetail)
        .map(op -> op.toBuilder().quantity(inboundBatchDetail.getQuantity()).build())
        .map(inboundBatchDetailRepository::save)
        .map(inboundBatchDetailMapper::toDTO)
        .orElse(null);
  }

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
  public List<InboundBatchDetailEntity> findByInboundId(Long inboundId) {
    if (inboundId == null || inboundBatchDetailRepository.findById(inboundId).isPresent()) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND.NOT_EXIST);
    }

    return inboundBatchDetailRepository.findByInbound_Id(inboundId);
  }

  @Override
  public void deleteAll(List<InboundBatchDetailEntity> inboundDetailsEntities) {

    inboundBatchDetailRepository.deleteAll(inboundDetailsEntities);
  }

  @Override
  public void saveAll(List<InboundBatchDetailEntity> inboundBatchDetailEntities) {

    inboundBatchDetailRepository.saveAll(inboundBatchDetailEntities);
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
}
