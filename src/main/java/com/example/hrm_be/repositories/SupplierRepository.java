package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {
  boolean existsBySupplierNameAndAddress(String name, String address);

  boolean existsByTaxCode(String code);

  Page<SupplierEntity> findBySupplierNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
      String searchSupplier, String searchName, Pageable pageable);
}
