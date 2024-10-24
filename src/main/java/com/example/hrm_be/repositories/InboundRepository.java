package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboundRepository extends JpaRepository<InboundEntity, Long> {

  boolean existsByInboundCode(String inboundCode);
  @Query("SELECT i FROM InboundEntity i WHERE i.id = :inboundId")
  Optional<InboundEntity> findInboundById(@Param("inboundId") Long inboundId);


}
