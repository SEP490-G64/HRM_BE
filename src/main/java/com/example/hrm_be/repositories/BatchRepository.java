package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository
    extends JpaRepository<BatchEntity, Long>, JpaSpecificationExecutor<BatchEntity> {
  // Check if a batch exists by its name.
  boolean existsByBatchCode(String batchCode);

  Optional<BatchEntity> findByBatchCode(String batchCode);

  Optional<BatchEntity> findByBatchCodeAndProduct(String batchCode, ProductEntity product);

  @Query(
      "SELECT b FROM BatchEntity b LEFT JOIN FETCH b.inboundBatchDetail bi JOIN bi.inbound i "
          + "WHERE bi.inbound.id=:productId")
  List<BatchEntity> findAllByProductIdThroughInbound(@Param("productId") Long productId);

  List<BatchEntity> findAllByProductId(Long productId);

  Optional<BatchEntity> findByBatchCodeAndProduct_Id(String code, Long id);
}
