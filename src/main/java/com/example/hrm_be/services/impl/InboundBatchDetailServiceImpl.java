package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundBatchDetailMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import com.example.hrm_be.services.InboundBatchDetailService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
  @Autowired private ProductMapper productMapper;

  @Override
  public List<InboundBatchDetail> getByInboundId(Long id) {
    // Retrieve inbound details by ID and convert to DTO
    return Optional.ofNullable(id)
        .map(
            e ->
                inboundBatchDetailRepository.findByInbound_Id(e).stream()
                    .map(b -> inboundBatchDetailMapper.toDTO(b))
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

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
  public Integer findTotalQuantityByInboundIdAndProduct(Long inboundId, Product product) {
    Integer totalQuantity =
        inboundBatchDetailRepository.findTotalQuantityByInboundAndProduct(
            inboundId, productMapper.toEntity(product));
    return totalQuantity;
  }
}
