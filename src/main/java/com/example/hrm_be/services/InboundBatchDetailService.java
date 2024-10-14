package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import org.springframework.stereotype.Service;

@Service
public interface InboundBatchDetailService {
  InboundBatchDetail create(InboundBatchDetail inboundBatchDetail);

  InboundBatchDetail update(InboundBatchDetail inboundBatchDetail);

  void delete(Long id);
}
