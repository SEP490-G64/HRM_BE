package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {
  // Checks if a ProductType with the specified name already exists in the database
  boolean existsByTypeName(String name);

  // Finds a paginated list of ProductType entities whose names contain the specified keyword
  // (case-insensitive)
  Page<ProductTypeEntity> findByTypeNameContainingIgnoreCase(String typeName, Pageable pageable);

  Optional<ProductTypeEntity> findByTypeName(String typeName);
}
