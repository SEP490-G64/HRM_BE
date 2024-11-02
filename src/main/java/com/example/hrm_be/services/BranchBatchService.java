package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.BranchBatch;
import org.springframework.stereotype.Service;

@Service
public interface BranchBatchService {
  BranchBatch create(BranchBatch branchBatch);

  BranchBatch update(BranchBatch branchBatch);

  void delete(Long id);

  BranchBatch  updateQuantityOrCreateBranchBatch(Branch branch, Batch batch, Integer quantity);
}
