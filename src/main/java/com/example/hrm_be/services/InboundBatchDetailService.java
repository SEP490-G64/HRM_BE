package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
