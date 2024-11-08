package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
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

  OutboundDetail findByOutboundAndBatch(Long outboundId, Long batchId);

  void saveAll(List<OutboundDetailEntity> outboundDetailEntities);

  List<OutboundDetail> findByOutbound(Long outboundId);
}
