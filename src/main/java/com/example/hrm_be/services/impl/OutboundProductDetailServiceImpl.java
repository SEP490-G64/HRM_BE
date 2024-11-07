package com.example.hrm_be.services.impl;

import com.example.hrm_be.components.OutboundProductDetailMapper;
import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.repositories.OutboundProductDetailRepository;
import com.example.hrm_be.services.OutboundProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OutboundProductDetailServiceImpl implements OutboundProductDetailService {

  @Autowired private OutboundProductDetailRepository outboundProductDetailRepository;
  @Autowired private OutboundProductDetailMapper outboundProductDetailMapper;

  @Override
  public void deleteByOutboundId(Long outboundId) {
    outboundProductDetailRepository.deleteByOutboundId(outboundId);
  }

  @Override
  public OutboundProductDetailEntity findByOutboundAndProduct(Long outboundId, Long productId) {
    return outboundProductDetailRepository.findByOutboundIdAndProductId(outboundId, productId).orElse(null);
  }

  @Override
  public List<OutboundProductDetail> findByOutbound(Long outboundId) {
    return outboundProductDetailRepository
            .findAllWithProductAndCategoryByOutboundId(outboundId)
            .stream()
            .map(outboundProductDetailMapper::toDTO)
            .collect(Collectors.toList());
  }

  @Override
  public List<OutboundProductDetailEntity> saveAll(List<OutboundProductDetailEntity> outboundProductDetailEntities) {
    return outboundProductDetailRepository.saveAll(outboundProductDetailEntities);
  }
}
