package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductRepository
    extends JpaRepository<BranchProductEntity, Long>,
        JpaSpecificationExecutor<BranchProductEntity> {

  Optional<BranchProductEntity> findByBranchAndProduct(BranchEntity branch, ProductEntity product);

  @Query("SELECT SUM(bp.quantity) FROM BranchProductEntity bp WHERE bp.product.id = :productId")
  Integer findTotalQuantityForProduct(Long productId);
}
