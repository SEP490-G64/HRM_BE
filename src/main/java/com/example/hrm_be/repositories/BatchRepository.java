package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.BatchStatus;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BatchRepository
    extends JpaRepository<BatchEntity, Long>, JpaSpecificationExecutor<BatchEntity> {

  Optional<BatchEntity> findByBatchCode(String batchCode);

  Optional<BatchEntity> findByBatchCodeAndProduct(String batchCode, ProductEntity product);

  @Query(
      "SELECT b FROM BatchEntity b LEFT JOIN FETCH b.inboundBatchDetail bi JOIN bi.inbound i "
          + "WHERE bi.inbound.id=:productId")
  List<BatchEntity> findAllByProductIdThroughInbound(@Param("productId") Long productId);

  List<BatchEntity> findAllByProductId(Long productId);

  Optional<BatchEntity> findByBatchCodeIgnoreCaseAndProduct_Id(String code, Long id);

  @Modifying
  @Transactional
  @Query("UPDATE BatchEntity i SET i.batchStatus = :status WHERE i.id = :id")
  void updateBatchStatus(@Param("status") BatchStatus status, @Param("id") Long id);

  @Query("SELECT b FROM BatchEntity b WHERE b.expireDate < :today")
  List<BatchEntity> findExpiredBatches(@Param("today") LocalDateTime today);

  @Query(
      "SELECT b FROM BatchEntity b LEFT JOIN FETCH b.branchBatches brb WHERE b.expireDate  "
          + "BETWEEN :today "
          + "AND :inDays AND brb.branch.id = :id")
  List<BatchEntity> findBatchesExpiringInDays(
      @Param("today") LocalDateTime today,
      @Param("inDays") LocalDateTime inDays,
      @Param("id") Long id);
}
