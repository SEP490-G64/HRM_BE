package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.models.entities.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BranchProductRepository
    extends JpaRepository<BranchProductEntity, Long>,
        JpaSpecificationExecutor<BranchProductEntity> {

  @Query("SELECT SUM(bp.quantity) FROM BranchProductEntity bp WHERE bp.product.id = :productId")
  BigDecimal findTotalQuantityForProduct(Long productId);

  Optional<BranchProductEntity> findByBranch_IdAndProduct_Id(Long branchId, Long productId);

  @Query(
      "SELECT bp FROM BranchProductEntity bp WHERE bp.branch.id = :branchId AND bp.quantity <"
          + " bp.minQuantity")
  List<BranchProductEntity> findBranchProductsWithQuantityBelowMin(
      @Param("branchId") Long branchId);

  @Query(
      "SELECT bp FROM BranchProductEntity bp WHERE bp.branch.id = :branchId AND bp.quantity >"
          + " bp.maxQuantity")
  List<BranchProductEntity> findBranchProductsWithQuantityAboveMax(
      @Param("branchId") Long branchId);

  @Query("SELECT bp FROM BranchProductEntity bp WHERE bp.branch.id = :branchId AND bp.quantity = 0")
  List<BranchProductEntity> findBranchProductsWithQuantityIsZero(@Param("branchId") Long branchId);

  @Modifying
  @Transactional
  @Query("UPDATE BranchProductEntity i SET i.productStatus = :status WHERE i.id = :id")
  void updateBranchProductStatus(@Param("status") ProductStatus status, @Param("id") Long id);

  @Modifying
  @Transactional
  @Query("UPDATE BranchProductEntity i " +
      "SET i.lastUpdated = CURRENT_TIMESTAMP " +
      "WHERE i.branch.id = :branchId AND i.product.id = :productId")
  void updateBranchProductLastUpdated(@Param("branchId") Long branchId, @Param("productId") Long productId);


  @Query(
      "SELECT bp.lastUpdated FROM BranchProductEntity bp WHERE bp.branch.id = :branchId AND bp"
          + ".product.id ="
          + " :productId")
  LocalDateTime getLastUpdatedByBranchIdAndProductId(
      @Param("branchId") Long branchId, @Param("productId") Long productId);

  @Query(
      "SELECT bp FROM BranchProductEntity bp WHERE bp.branch.id = :branchId AND bp.quantity IS NOT"
          + " NULL AND (LOWER(bp.product.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR"
          + " LOWER(bp.product.registrationCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) order by"
          + " bp.quantity DESC, bp.product.registrationCode ASC")
  Page<BranchProductEntity> findByBranchIdAndProductNameOrProductRegistrationCode(
      Long branchId, String keyword, Pageable pageable);

  List<BranchProductEntity> findByBranch_Id(Long branchId);
}
