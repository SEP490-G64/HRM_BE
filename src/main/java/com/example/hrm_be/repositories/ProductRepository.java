package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
  Page<ProductEntity> findProductEntitiesByProductNameContainingIgnoreCase(
      String name, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:cateId")
  Page<ProductEntity> findProductByPagingAndCategoryId(Long cateId, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:typeId")
  Page<ProductEntity> findProductByPagingAndTypeId(Long typeId, Pageable pageable);
}
