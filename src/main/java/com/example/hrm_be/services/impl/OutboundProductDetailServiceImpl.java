package com.example.hrm_be.services.impl;

import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.repositories.OutboundProductDetailRepository;
import com.example.hrm_be.services.OutboundProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OutboundProductDetailServiceImpl implements OutboundProductDetailService {

  @Autowired private OutboundProductDetailRepository outboundProductDetailRepository;

  @Override
  public void deleteByOutboundId(Long outboundId) {
    outboundProductDetailRepository.deleteByOutboundId(outboundId);
  }

  @Override
  public OutboundProductDetailEntity findByOutboundAndProduct(Long outboundId, Long productId) {
    return outboundProductDetailRepository.findByOutboundIdAndProductId(outboundId, productId).orElse(null);
  }

  @Override
  public List<OutboundProductDetailEntity> findByOutbound(Long outboundId) {
    return outboundProductDetailRepository.findAllByOutboundId(outboundId);
  }

  @Override
  public List<OutboundProductDetailEntity> saveAll(List<OutboundProductDetailEntity> outboundProductDetailEntities) {
    return outboundProductDetailRepository.saveAll(outboundProductDetailEntities);
  }
}
