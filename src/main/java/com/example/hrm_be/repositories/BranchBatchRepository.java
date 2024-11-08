package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchBatchRepository extends JpaRepository<BranchBatchEntity, Long> {
  @Query(
      "SELECT b.quantity FROM BranchBatchEntity b WHERE b.batch.id = :batchId AND b.branch.id ="
          + " :branchId")
  BigDecimal findQuantityByBatchIdAndBranchId(
      @Param("batchId") Long batchId, @Param("branchId") Long branchId);

  List<BranchBatchEntity> findByBatchId(Long batchId);

  Optional<BranchBatchEntity> findByBranch_IdAndBatch_Id(Long branchId, Long batchId);

  @Query(
          "SELECT b FROM BranchBatchEntity b " +
                  "JOIN b.batch batch " +
                  "WHERE batch.product.id = :productId " +
                  "AND b.branch.id = :branchId " +
                  "AND (batch.expireDate > CURRENT_TIMESTAMP OR batch.expireDate IS NULL) " +
                  "ORDER BY CASE WHEN batch.expireDate IS NULL THEN 1 ELSE 0 END, batch.expireDate ASC")
  List<BranchBatchEntity> findByProductIdAndBranchIdOrderByExpireDate(Long productId, Long branchId);
}
