package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundDetailsRepository extends JpaRepository<InboundDetailsEntity, Long> {

  boolean existsByInbound_IdAndProduct_Id(Long inboundId, Long productId);

  Optional<InboundDetailsEntity> findByInbound_IdAndProduct_Id(Long inboundId, Long productId);

  @Query("SELECT id FROM InboundDetailsEntity id WHERE id.inbound.id = :inboundId")
  List<InboundDetailsEntity> findInboundDetailsByInboundId(@Param("inboundId") Long inboundId);

  @Query(
      "SELECT SUM(id.receiveQuantity * id.inboundPrice) FROM InboundDetailsEntity id WHERE"
          + " id.product.id = :productId")
  BigDecimal findTotalPriceForProduct(Long productId);

  List<InboundDetailsEntity> findByInbound_Id(Long id);

  @Modifying
  @Query("DELETE FROM InboundDetailsEntity opd WHERE opd.id in :outboundId")
  void deleteByIds(@Param("outboundId") List<Long> outboundId);

  void deleteAllByInbound_Id(Long id);

  @Query(
      "SELECT i FROM InboundDetailsEntity i "
          + "WHERE i.product.id = :productId "
          + "AND i.inbound.createdDate BETWEEN :startDate AND :endDate "
          + "AND i.inbound.status in :status "
          + "AND i.receiveQuantity IS NOT NULL AND i.receiveQuantity > 0 " +
             "AND NOT EXISTS (" +
             "   SELECT 1 FROM InboundBatchDetailEntity ib " +
             "   WHERE ib.inbound.id = i.inbound.id AND ib.batch.product.id = i.product.id" +
             ")")
  List<InboundDetailsEntity> findInboundDetailsByProductIdAndPeriod(
      @Param("productId") Long productId,
      @Param("status") List<InboundStatus> status,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
