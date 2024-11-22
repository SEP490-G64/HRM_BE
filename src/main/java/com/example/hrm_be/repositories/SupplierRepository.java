package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.SupplierEntity;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

  @Query(
      value =
          "WITH InboundTotal AS (\n"
              + "    SELECT i.supplier_id, \n"
              + "           SUM(i.total_price) AS inbound_total, \n"
              + "           COUNT(*) AS inbound_quantity\n"
              + "    FROM inbound i\n"
              + "    WHERE (:branchId IS NULL OR i.to_branch_id = :branchId)\n"
              + "      AND i.created_date > CURRENT_DATE - 30\n"
              + "    GROUP BY i.supplier_id\n"
              + "),\n"
              + "OutboundTotal AS (\n"
              + "    SELECT o.supplier_id, \n"
              + "           SUM(o.total_price) AS outbound_total, \n"
              + "           COUNT(*) AS outbound_quantity\n"
              + "    FROM outbound o\n"
              + "    WHERE (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + "      AND o.created_date > CURRENT_DATE - 30\n"
              + "    GROUP BY o.supplier_id\n"
              + ")\n"
              + "SELECT s.id AS supplier_id,\n"
              + "       s.supplier_name,\n"
              + "       s.address,\n"
              + "       COALESCE(it.inbound_quantity, 0) + COALESCE(ot.outbound_quantity, 0) AS"
              + " quantity,\n"
              + "       COALESCE(it.inbound_total, 0) + COALESCE(ot.outbound_total, 0) AS"
              + " total_transaction\n"
              + "FROM supplier s\n"
              + "LEFT JOIN InboundTotal it ON s.id = it.supplier_id\n"
              + "LEFT JOIN OutboundTotal ot ON s.id = ot.supplier_id\n"
              + "ORDER BY total_transaction DESC\n"
              + "LIMIT 5;",
      nativeQuery = true)
  List<Object[]> getTopSuppliers(Long branchId);
}
