package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OutboundProductDetailService {
  void deleteByOutboundId(Long outboundId);

  OutboundProductDetail findByOutboundAndProduct(Long outboundId, Long productId);

  List<OutboundProductDetail> findByOutboundWithCategory(Long outboundId);

  List<OutboundProductDetail> saveAll(List<OutboundProductDetailEntity> outboundProductDetailEntities);
}
