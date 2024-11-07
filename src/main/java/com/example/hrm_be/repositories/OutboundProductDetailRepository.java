package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.models.entities.ProductEntity;
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

  List<OutboundProductDetailEntity> findAllByOutboundId(Long outboundId);
}
