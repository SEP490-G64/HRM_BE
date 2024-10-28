package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.SupplierEntity;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, Long> {
  boolean existsBySupplierNameAndAddress(String name, String address);

  boolean existsByTaxCode(String code);

  @Query(
      "SELECT u FROM SupplierEntity u "
          + "WHERE (LOWER(u.supplierName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
          + "OR LOWER(u.address) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) "
          + "AND (:status IS NULL OR u.status = :status)")
  Page<SupplierEntity> searchSuppliers(
      String searchKeyword, @Nullable Boolean status, Pageable pageable);
}
