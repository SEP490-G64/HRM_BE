package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.OutboundEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundRepository extends JpaRepository<OutboundEntity, Long> {

  boolean existsByOutboundCode(String outboundCode);

  @Query(
      "SELECT o FROM OutboundEntity o "
          + "LEFT JOIN FETCH o.outboundDetails od "
          + "WHERE o.id = :outboundId")
  Optional<OutboundEntity> findOutboundWithDetails(@Param("outboundId") Long outboundId);
}
