package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OutboundDetailService {
  OutboundDetail getById(Long id);

  Page<OutboundDetail> getByPaging(int pageNo, int pageSize, String sortBy);

  OutboundDetail create(OutboundDetail outboundDetail);

  OutboundDetail update(OutboundDetail outboundDetail);

  void delete(Long id);

  void deleteByOutboundId(Long outboundId);

  OutboundDetailEntity findByOutboundAndBatch(Long outboundId, Long batchId);

  List<OutboundDetailEntity> saveAll(List<OutboundDetailEntity> outboundDetailEntities);

  List<OutboundDetailEntity> findByOutbound(Long outboundId);
}
