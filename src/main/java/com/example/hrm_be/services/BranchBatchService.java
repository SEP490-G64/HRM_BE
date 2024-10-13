package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchBatch;
import org.springframework.stereotype.Service;

@Service
public interface BranchBatchService {
  BranchBatch create(BranchBatch branchBatch);

  BranchBatch update(BranchBatch branchBatch);

  void delete(Long id);
}
