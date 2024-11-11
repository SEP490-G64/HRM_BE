package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundProductDetail;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface OutboundProductDetailService {
  void deleteByOutboundId(Long outboundId);

  OutboundProductDetail findByOutboundAndProduct(Long outboundId, Long productId);

  List<OutboundProductDetail> findByOutboundWithCategory(Long outboundId);

  List<OutboundProductDetail> saveAll(
      List<OutboundProductDetailEntity> outboundProductDetailEntities);
}
