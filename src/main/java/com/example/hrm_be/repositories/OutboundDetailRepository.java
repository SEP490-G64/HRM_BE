package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundDetailRepository extends JpaRepository<OutboundDetailEntity, Long> {

  Optional<OutboundDetailEntity> findByOutboundIdAndBatchId(Long outboundId, Long batchId);

  List<OutboundDetailEntity> findByBatchAndOutbound(BatchEntity batch, OutboundEntity outbound);

  @Modifying
  @Query("DELETE FROM OutboundDetailEntity opd WHERE opd.outbound.id = :outboundId")
  void deleteByOutboundId(@Param("outboundId") Long outboundId);

  @Query(
      "SELECT od FROM OutboundDetailEntity od "
          + "JOIN FETCH od.batch b "
          + "JOIN FETCH b.product p "
          + "LEFT JOIN FETCH p.category c "
          + "WHERE od.outbound.id = :outboundId")
  List<OutboundDetailEntity> findAllWithBatchAndProductAndCategoryByOutboundId(Long outboundId);


  @Query("SELECT i FROM OutboundDetailEntity i " +
      "WHERE i.batch.product.id = :productId " +
      "AND i.outbound.createdDate BETWEEN :startDate AND :endDate")
  List<OutboundDetailEntity> findOutboundDetailsByProductIdAndPeriod(
      @Param("productId") Long productId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
