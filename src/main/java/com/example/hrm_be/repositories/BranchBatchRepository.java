package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.BatchStatus;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
      "SELECT b FROM BranchBatchEntity b "
          + "JOIN b.batch batch "
          + "WHERE batch.product.id = :productId "
          + "AND b.branch.id = :branchId "
          + "AND (batch.expireDate > CURRENT_TIMESTAMP OR batch.expireDate IS NULL) "
          + "ORDER BY CASE WHEN batch.expireDate IS NULL THEN 1 ELSE 0 END, batch.expireDate ASC")
  List<BranchBatchEntity> findByProductIdAndBranchIdOrderByExpireDate(
      Long productId, Long branchId);

  @Modifying
  @Transactional
  @Query("UPDATE BranchBatchEntity i SET i.batchStatus = :status WHERE i.id = :id")
  void updateBranchBatchStatus(@Param("status") BatchStatus status, @Param("id") Long id);

  @Query(
      "SELECT b FROM BranchBatchEntity b WHERE b.branch.id = :branchId AND b.batch.product.id ="
          + " :productId")
  List<BranchBatchEntity> findByBranchIdAndProductId(Long branchId, Long productId);

  List<BranchBatchEntity> findByBranch_Id(Long branchId);
}
