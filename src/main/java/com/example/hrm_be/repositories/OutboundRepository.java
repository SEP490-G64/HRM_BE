package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.OutboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundRepository extends JpaRepository<OutboundEntity, Long> {}
