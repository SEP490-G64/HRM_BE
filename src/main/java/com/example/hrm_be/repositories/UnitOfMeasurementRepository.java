package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitOfMeasurementRepository extends JpaRepository<UnitOfMeasurementEntity, Long> {
  boolean existsByUnitName(String name);

  Page<UnitOfMeasurementEntity> findByUnitNameContainingIgnoreCase(String name, Pageable pageable);

  Optional<UnitOfMeasurementEntity> findByUnitName(String name);
}
