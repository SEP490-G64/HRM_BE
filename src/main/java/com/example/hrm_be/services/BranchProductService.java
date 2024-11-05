package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchProduct;
import org.springframework.stereotype.Service;

@Service
public interface BranchProductService {
  BranchProduct create(BranchProduct branchProduct);

  BranchProduct update(BranchProduct branchProduct);

  BranchProduct getByBranchIdAndProductId(Long branchId, Long productId);

  void delete(Long id);
}
