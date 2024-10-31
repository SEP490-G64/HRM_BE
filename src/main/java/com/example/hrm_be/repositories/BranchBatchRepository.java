package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchBatchRepository extends JpaRepository<BranchBatchEntity, Long> {

  Optional<BranchBatchEntity> findByBranchAndBatch(BranchEntity branch, BatchEntity batch);

  List<BranchBatchEntity> findByBatchId(Long batchId);
}
