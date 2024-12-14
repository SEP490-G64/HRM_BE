package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryCheckDetailsRepository
    extends JpaRepository<InventoryCheckDetailsEntity, Long> {

  Optional<InventoryCheckDetailsEntity> findByInventoryCheck_IdAndBatch_Id(
      Long checkId, Long batchId);

  List<InventoryCheckDetailsEntity> findByInventoryCheck_Id(Long checkId);

  void deleteByInventoryCheck_Id(Long checkId);
}
