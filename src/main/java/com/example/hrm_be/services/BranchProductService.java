package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface BranchProductService {
  BranchProduct save(BranchProduct branchProduct);

  BranchProduct getByBranchIdAndProductId(Long branchId, Long productId);

  void delete(Long id);

  void updateBranchProductInInbound(
      BranchEntity toBranch, ProductEntity product, BigDecimal quantity);

  BigDecimal findTotalQuantityForProduct(Long productId);

  void saveAll(List<BranchProductEntity> branchProducts);

  List<BranchProduct> findBranchProductsWithQuantityAboveMax(Long branchId);

  List<BranchProduct> findBranchProductsWithQuantityBelowMin(Long branchId);

  List<BranchProduct> findBranchProductsWithQuantityIsZero(Long branchId);
}
