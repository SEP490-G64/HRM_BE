package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, Long> {
  // Check if a batch exists by its name.
  boolean existsByBatchCode(String batchCode);

  // User query to find batch based on a keyword for branch name
  Page<BatchEntity> findByBatchCodeContainingIgnoreCase(String batchCode, Pageable page);
}
