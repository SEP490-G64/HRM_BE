package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ManufacturerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Long> {
  boolean existsByManufacturerNameAndAddress(String name, String address);

  boolean existsByTaxCode(String code);

  Page<ManufacturerEntity> findByManufacturerNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
      String searchSupplier, String searchName, Pageable pageable);
}
