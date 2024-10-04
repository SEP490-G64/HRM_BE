package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
  boolean existsByCategoryName(String name);

  Page<ProductCategoryEntity> findByCategoryNameContainingIgnoreCase(
      String categoryName, Pageable pageable);
}
