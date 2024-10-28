package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InboundDetailsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundDetailsRepository extends JpaRepository<InboundDetailsEntity, Long> {

  boolean existsByInbound_IdAndProduct_Id(Long inboundId, Long productId);

  Optional<InboundDetailsEntity> findByInbound_IdAndProduct_Id(Long inboundId, Long productId);

  @Query("SELECT id FROM InboundDetailsEntity id WHERE id.inbound.id = :inboundId")
  List<InboundDetailsEntity> findInboundDetailsByInboundId(@Param("inboundId") Long inboundId);
}
