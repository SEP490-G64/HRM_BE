package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundProductDetailRepository
    extends JpaRepository<OutboundProductDetailEntity, Long> {

  Optional<OutboundProductDetailEntity> findByOutboundIdAndProductId(
      Long outboundId, Long productId);

  @Modifying
  @Query("DELETE FROM OutboundProductDetailEntity opd WHERE opd.outbound.id = :outboundId")
  void deleteByOutboundId(@Param("outboundId") Long outboundId);

  @Query(
      "SELECT opd FROM OutboundProductDetailEntity opd "
          + "JOIN FETCH opd.product p "
          + "LEFT JOIN FETCH p.category c "
          + "WHERE opd.outbound.id = :outboundId")
  List<OutboundProductDetailEntity> findAllWithProductAndCategoryByOutboundId(Long outboundId);

  @Query(
      "SELECT i FROM OutboundProductDetailEntity i "
          + "WHERE i.product.id = :productId "
          + "AND i.outbound.createdDate BETWEEN :startDate AND :endDate")
  List<OutboundProductDetailEntity> findInboundDetailsByProductIdAndPeriod(
      @Param("productId") Long productId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
