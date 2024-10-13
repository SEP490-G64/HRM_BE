package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.InboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundRepository extends JpaRepository<InboundEntity, Long> {}
