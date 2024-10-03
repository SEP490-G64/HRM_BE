package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {
  boolean existsBySupplierNameAndAddress(String name, String address);

  Page<SupplierEntity> findBySupplierNameContainsIgnoreCase(String name, Pageable pageable);
}
