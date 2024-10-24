package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundBatchDetailRepository
    extends JpaRepository<InboundBatchDetailEntity, Long> {
  Optional<InboundBatchDetailEntity> findByBatch_IdAndAndInbound_Id(Long batchId, Long inboundId);

  @Query("SELECT id FROM InboundBatchDetailEntity id left JOIN FETCH id.batch b left join fetch b"
      + ".product"
      + " p "
      + "WHERE id"
      + ".inbound.id = "
      + ":inboundId")
  List<InboundBatchDetailEntity> findInboundBatchDetailByInboundId(@Param("inboundId") Long inboundId);
}
