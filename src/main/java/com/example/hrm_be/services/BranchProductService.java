package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import org.springframework.stereotype.Service;

@Service
public interface BranchProductService {
  BranchProduct create(BranchProduct branchProduct);

  BranchProduct update(BranchProduct branchProduct);

  void delete(Long id);

  BranchProduct getOrUpdateBranchProduct(Branch toBranch, Product product, Integer quantity);
}
