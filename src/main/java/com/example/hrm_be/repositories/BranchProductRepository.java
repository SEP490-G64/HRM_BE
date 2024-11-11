package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.models.entities.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BranchProductRepository
    extends JpaRepository<BranchProductEntity, Long>,
        JpaSpecificationExecutor<BranchProductEntity> {

  @Query("SELECT SUM(bp.quantity) FROM BranchProductEntity bp WHERE bp.product.id = :productId")
  BigDecimal findTotalQuantityForProduct(Long productId);

  Optional<BranchProductEntity> findByBranch_IdAndProduct_Id(Long branchId, Long productId);

  @Modifying
  @Transactional
  @Query("UPDATE BranchProductEntity i SET i.productStatus = :status WHERE i.id = :id")
  void updateBranchProductStatus(@Param("status") ProductStatus status, @Param("id") Long id);
}
