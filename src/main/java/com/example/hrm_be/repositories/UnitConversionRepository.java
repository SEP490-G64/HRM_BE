package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.StorageLocationEntity;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UnitConversionRepository extends JpaRepository<UnitConversionEntity, Long> {
  Page<UnitConversionEntity> findByContainingIgnoreCase(String name, Pageable pageable);


}
