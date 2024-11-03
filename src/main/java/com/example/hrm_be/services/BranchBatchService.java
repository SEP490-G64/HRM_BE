package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BranchBatchService {
  BranchBatch create(BranchBatch branchBatch);

  BranchBatch update(BranchBatch branchBatch);

  void delete(Long id);

  void updateBranchBatchInInbound(BranchEntity toBranch, BatchEntity batch, Integer quantity);

  List<BranchBatchEntity> findByBatchId(Long id);
}
