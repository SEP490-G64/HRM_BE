package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.models.entities.InboundEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InboundRepository
    extends JpaRepository<InboundEntity, Long>, JpaSpecificationExecutor<InboundEntity> {

  boolean existsByInboundCode(String inboundCode);

  @Query("SELECT i FROM InboundEntity i WHERE i.id = :inboundId")
  Optional<InboundEntity> findInboundById(@Param("inboundId") Long inboundId);

  @Modifying
  @Transactional
  @Query("UPDATE InboundEntity i SET i.status = :status WHERE i.id = :id")
  void updateInboundStatus(@Param("status") InboundStatus status, @Param("id") Long id);
}
