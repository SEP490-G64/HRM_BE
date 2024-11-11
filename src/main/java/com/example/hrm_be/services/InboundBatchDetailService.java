package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface InboundBatchDetailService {
  void delete(Long id);

  List<InboundBatchDetail> findByInboundId(Long inboundId);

  void deleteAll(List<InboundBatchDetail> inboundBatchDetailEntities);

  void saveAll(List<InboundBatchDetail> inboundBatchDetailEntities);

  void updateAverageInboundPricesForBatches(BatchEntity batch);

  Integer findTotalQuantityByInboundAndProduct(Long inboundId, ProductEntity product);

  InboundBatchDetailEntity findByBatchIdAndAndInboundId(Long batchId, Long inboundId);
}
