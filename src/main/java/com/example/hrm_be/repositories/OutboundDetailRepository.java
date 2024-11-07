package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
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

  List<OutboundDetailEntity> findAllByOutboundId(Long outboundId);
}
