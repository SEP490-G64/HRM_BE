package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.dtos.Product;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface InboundBatchDetailService {
  InboundBatchDetail create(InboundBatchDetail inboundBatchDetail);

  InboundBatchDetail update(InboundBatchDetail inboundBatchDetail);

  List<InboundBatchDetail> getByInboundId(Long id);

  void delete(Long id);

  Integer findTotalQuantityByInboundIdAndProduct(Long inboundId, Product product);
}
