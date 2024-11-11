package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.models.entities.OutboundEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OutboundRepository extends JpaRepository<OutboundEntity, Long> {

  boolean existsByOutboundCode(String outboundCode);

  @Query(
      "SELECT o FROM OutboundEntity o "
          + "LEFT JOIN FETCH o.outboundDetails od "
          + "WHERE o.id = :outboundId")
  Optional<OutboundEntity> findOutboundWithDetails(@Param("outboundId") Long outboundId);

  @Modifying
  @Transactional
  @Query("UPDATE OutboundEntity i SET i.status = :status WHERE i.id = :id")
  void updateOutboundStatus(@Param("status") OutboundStatus status, @Param("id") Long id);
}
