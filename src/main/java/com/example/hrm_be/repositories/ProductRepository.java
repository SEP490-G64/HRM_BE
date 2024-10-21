package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
    extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
  Page<ProductEntity> findProductEntitiesByRegistrationCodeContainingIgnoreCase(
      String code, Pageable pageable);

  Page<ProductEntity> findProductEntitiesByProductNameContainingIgnoreCase(
      String name, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:cateId")
  Page<ProductEntity> findProductByPagingAndCategoryId(Long cateId, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:typeId")
  Page<ProductEntity> findProductByPagingAndTypeId(Long typeId, Pageable pageable);

  boolean existsByRegistrationCode(String code);

  @Query(
      "SELECT p FROM ProductEntity p JOIN FETCH p.branchProducs bp WHERE bp.branch.id = :branchId ")
  List<ProductEntity> findProductByBranchId(@Param("branchId") Long branchId);
}
