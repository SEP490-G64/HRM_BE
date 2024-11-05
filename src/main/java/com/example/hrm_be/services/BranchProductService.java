package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface BranchProductService {
  BranchProduct save(BranchProduct branchProduct);

  BranchProduct getByBranchIdAndProductId(Long branchId, Long productId);

  void delete(Long id);

  void updateBranchProductInInbound(BranchEntity toBranch, ProductEntity product, BigDecimal quantity);

  BigDecimal findTotalQuantityForProduct(Long productId);

  List<BranchProductEntity> saveAll(List<BranchProductEntity> branchProducts);
}
