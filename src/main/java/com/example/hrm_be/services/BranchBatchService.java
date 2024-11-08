package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface BranchBatchService {
  BranchBatch save(BranchBatch branchBatch);

  void delete(Long id);

  void updateBranchBatchInInbound(BranchEntity toBranch, BatchEntity batch, BigDecimal quantity);

  List<BranchBatchEntity> findByBatchId(Long id);

  BranchBatch getByBranchIdAndBatchId(Long branchId, Long batchId);

  BigDecimal findQuantityByBatchIdAndBranchId(Long batchId, Long branchId);

  List<BranchBatch> findByProductAndBranchForSell(Long productId, Long branchId);
}
