package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.models.entities.InventoryCheckEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InventoryCheckRepository
    extends JpaRepository<InventoryCheckEntity, Long>,
        JpaSpecificationExecutor<InventoryCheckEntity> {

  boolean existsByCode(String code);

  @Modifying
  @Transactional
  @Query("UPDATE InventoryCheckEntity i SET i.status = :status WHERE i.id = :id")
  void updateInventoryCheckStatus(
      @Param("status") InventoryCheckStatus status, @Param("id") Long id);

  @Query(
      "SELECT b FROM InventoryCheckEntity b LEFT JOIN FETCH b.branch brb WHERE brb.id = :branchId"
          + " AND b.status in :status")
  List<InventoryCheckEntity> findInventoryCheckEntitiesByStatusAndBranchId(
      @Param("status") List<InventoryCheckStatus> status, @Param("branchId") Long branchId);
}
