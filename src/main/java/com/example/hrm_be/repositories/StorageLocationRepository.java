package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.StorageLocationEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocationEntity, Long> {

  Page<StorageLocationEntity> findByShelfNameContainingIgnoreCase(String name, Pageable pageable);
}
