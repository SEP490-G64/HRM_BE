package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ManufacturerEntity;
import io.micrometer.common.lang.Nullable;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Long> {
  boolean existsByManufacturerNameAndAddress(String name, String address);

  boolean existsByTaxCode(String code);

  Page<ManufacturerEntity> findByManufacturerNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
      String searchSupplier, String searchName, Pageable pageable);

  Optional<ManufacturerEntity> findByManufacturerName(String manufacturerName);

  @Query(
      "SELECT u FROM ManufacturerEntity u "
          + "WHERE (LOWER(u.manufacturerName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
          + "OR LOWER(u.address) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) "
          + "AND (:status IS NULL OR u.status = :status)")
  Page<ManufacturerEntity> searchManufacturers(
      String searchKeyword, @Nullable Boolean status, Pageable pageable);
}
