package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundBatchDetailRepository
    extends JpaRepository<InboundBatchDetailEntity, Long> {
  Optional<InboundBatchDetailEntity> findByBatch_IdAndAndInbound_Id(Long batchId, Long inboundId);

  @Query(
      "SELECT id FROM InboundBatchDetailEntity id left JOIN FETCH id.batch b left join fetch b"
          + ".product"
          + " p "
          + "WHERE id"
          + ".inbound.id = "
          + ":inboundId")
  List<InboundBatchDetailEntity> findInboundBatchDetailByInboundId(
      @Param("inboundId") Long inboundId);

  @Query(
      "SELECT COALESCE(SUM(ib.quantity), 0) FROM InboundBatchDetailEntity ib "
          + "JOIN ib.batch b "
          + "JOIN b.product p "
          + "WHERE ib.inbound.id = :inboundId "
          + "AND p = :product")
  Integer findTotalQuantityByInboundAndProduct(Long inboundId, ProductEntity product);

  List<InboundBatchDetailEntity> findAllByBatchId(Long batchId);

  List<InboundBatchDetailEntity> findByInbound_Id(Long id);

  @Modifying
  @Query("DELETE FROM InboundBatchDetailEntity opd WHERE opd.id in :outboundId")
  void deleteByIds(@Param("outboundId") List<Long> outboundId);

  void deleteAllByInbound_Id(Long id);

  @Query(
      "SELECT i FROM InboundBatchDetailEntity i "
          + "WHERE i.batch.product.id = :productId "
          + "AND i.inbound.createdDate BETWEEN :startDate AND :endDate")
  List<InboundBatchDetailEntity> findInboundDetailsByProductIdAndPeriod(
      @Param("productId") Long productId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
