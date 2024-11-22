package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductTypeEntity;

import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {
  // Checks if a ProductType with the specified name already exists in the database
  boolean existsByTypeName(String name);

  // Finds a paginated list of ProductType entities whose names contain the specified keyword
  // (case-insensitive)
  Page<ProductTypeEntity> findByTypeNameContainingIgnoreCase(String typeName, Pageable pageable);

  Optional<ProductTypeEntity> findByTypeName(String typeName);

  @Query(value =
          "WITH ProductCount AS (\n" +
                  "    SELECT p.type_id, COUNT(DISTINCT p.id) AS product_count\n" +
                  "    FROM product p\n" +
                  "    JOIN branch_product bp ON p.id = bp.product_id \n" +
                  "    WHERE (:branchId IS NULL OR bp.branch_id = :branchId)\n" +
                  "    GROUP BY p.type_id\n" +
                  "),\n" +
                  "TotalProductCount AS (\n" +
                  "    SELECT COUNT(DISTINCT p.id) AS total_count\n" +
                  "    FROM product p \n" +
                  "    JOIN branch_product bp ON p.id = bp.product_id\n" +
                  "    WHERE (:branchId IS NULL OR bp.branch_id = :branchId)\n" +
                  ")\n" +
                  "SELECT c.id AS type_id,\n" +
                  "       c.type_name AS type_name,\n" +
                  "       COALESCE(pc.product_count, 0) AS product_count,\n" +
                  "       CASE\n" +
                  "           WHEN tp.total_count = 0 THEN 0\n" +
                  "           ELSE COALESCE(pc.product_count, 0) * 100.0 / tp.total_count\n" +
                  "       END AS percentage\n" +
                  "FROM type c\n" +
                  "LEFT JOIN ProductCount pc ON c.id = pc.type_id\n" +
                  "CROSS JOIN TotalProductCount tp\n" +
                  "GROUP BY c.id, c.type_name, pc.product_count, tp.total_count\n" +
                  "HAVING COALESCE(pc.product_count, 0) > 0\n" +
                  "ORDER BY product_count DESC\n" +
                  "LIMIT 5;",
          nativeQuery = true)
  List<Object[]> getTypeWithProductPercentage(@Param("branchId") Long branchId);
}
