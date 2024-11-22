package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductCategoryEntity;

import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
  // Checks if a ProductCategory with the specified name already exists in the database
  boolean existsByCategoryName(String name);

  // Finds a paginated list of ProductCategory entities whose names contain the specified keyword
  // (case-insensitive)
  Page<ProductCategoryEntity> findByCategoryNameContainingIgnoreCase(
      String categoryName, Pageable pageable);

  Optional<ProductCategoryEntity> findByCategoryName(String name);

  @Query(value =
          "WITH ProductCount AS (\n" +
                  "    SELECT p.category_id, COUNT(DISTINCT p.id) AS product_count\n" +
                  "    FROM product p\n" +
                  "    JOIN branch_product bp ON p.id = bp.product_id \n" +
                  "    WHERE (:branchId IS NULL OR bp.branch_id = :branchId)\n" +
                  "    GROUP BY p.category_id\n" +
                  "),\n" +
                  "TotalProductCount AS (\n" +
                  "    SELECT COUNT(DISTINCT p.id) AS total_count\n" +
                  "    FROM product p \n" +
                  "    JOIN branch_product bp ON p.id = bp.product_id\n" +
                  "    WHERE (:branchId IS NULL OR bp.branch_id = :branchId)\n" +
                  ")\n" +
                  "SELECT c.id AS category_id,\n" +
                  "       c.category_name AS category_name,\n" +
                  "       COALESCE(pc.product_count, 0) AS product_count,\n" +
                  "       CASE\n" +
                  "           WHEN tp.total_count = 0 THEN 0\n" +
                  "           ELSE COALESCE(pc.product_count, 0) * 100.0 / tp.total_count\n" +
                  "       END AS percentage\n" +
                  "FROM category c\n" +
                  "LEFT JOIN ProductCount pc ON c.id = pc.category_id\n" +
                  "CROSS JOIN TotalProductCount tp\n" +
                  "GROUP BY c.id, c.category_name, pc.product_count, tp.total_count\n" +
                  "HAVING COALESCE(pc.product_count, 0) > 0\n" +
                  "ORDER BY product_count DESC\n" +
                  "LIMIT 5;",
          nativeQuery = true)
  List<Object[]> getCategoryWithProductPercentage(@Param("branchId") Long branchId);
}
