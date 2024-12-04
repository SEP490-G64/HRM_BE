package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.StorageLocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocationEntity, Long> {

  Page<StorageLocationEntity> findByShelfNameContainingIgnoreCaseAndBranchId(
      String name, Long branchId, Pageable pageable);
}
