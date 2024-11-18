package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OutboundDetailService {
  OutboundDetail getById(Long id);

  Page<OutboundDetail> getByPaging(int pageNo, int pageSize, String sortBy);

  OutboundDetail create(OutboundDetail outboundDetail);

  OutboundDetail update(OutboundDetail outboundDetail);

  void delete(Long id);

  void deleteByOutboundId(Long outboundId);

  OutboundDetail findByOutboundAndBatch(Long outboundId, Long batchId);

  List<OutboundDetail> saveAll(List<OutboundDetailEntity> outboundDetailEntities);

  List<OutboundDetail> findByOutboundWithCategory(Long outboundId);

  List<OutboundDetail> getOutboundDetailsByProductIdAndPeriod(
      Long productId, LocalDateTime startDate, LocalDateTime endDate);
}
