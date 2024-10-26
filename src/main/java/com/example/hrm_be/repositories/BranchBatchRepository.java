package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchBatchRepository extends JpaRepository<BranchBatchEntity, Long> {

  Optional<BranchBatchEntity> findByBranchAndBatch(BranchEntity branch, BatchEntity batch);

  @Query(
      "SELECT b.quantity FROM BranchBatchEntity b WHERE b.batch.id = :batchId AND b.branch.id = :branchId")
  Integer findQuantityByBatchIdAndBranchId(
      @Param("batchId") Long batchId, @Param("branchId") Long branchId);
}
