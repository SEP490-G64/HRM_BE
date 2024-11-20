package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.models.dtos.ProductBatchDTO;
import com.example.hrm_be.models.entities.ProductEntity;
import java.math.BigDecimal;
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
public interface ProductRepository
    extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
  Page<ProductEntity> findProductEntitiesByRegistrationCodeContainingIgnoreCase(
      String code, Pageable pageable);

  @Query(
      "SELECT p FROM ProductEntity p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword,"
          + " '%'))")
  List<ProductEntity> findProductEntitiesByProductNameIgnoreCase(@Param("keyword") String keyword);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:cateId")
  Page<ProductEntity> findProductByPagingAndCategoryId(Long cateId, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p WHERE p.category.id=:typeId")
  Page<ProductEntity> findProductByPagingAndTypeId(Long typeId, Pageable pageable);

  @Query(
      "SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.registrationCode = :code AND p.status !="
          + " 'DA_XOA'")
  boolean existsByRegistrationCode(String code);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p JOIN p.branchProducs bp LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId LEFT JOIN"
          + " p.productSuppliers ps WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr,"
          + " '%')) AND bp.branch.id = :branchId AND bp.quantity > 0 AND (b IS NULL OR bb.quantity"
          + " > 0) AND (:checkValid IS NULL OR :checkValid = FALSE OR (b IS NOT NULL AND"
          + " b.expireDate >= CURRENT_TIMESTAMP)) AND (:supplierId IS NULL OR ps.supplier.id ="
          + " :supplierId)")
  List<ProductEntity> searchProductByBranchId(
      Long branchId, String searchStr, Boolean checkValid, Long supplierId);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p JOIN p.branchProducs bp LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId LEFT JOIN"
          + " p.productSuppliers ps WHERE (:searchStr IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr, '%')))"
          + " AND bp.branch.id = :branchId AND bp.quantity > 0 AND (b IS NULL OR bb.quantity > 0)"
          + " AND (:checkValid IS NULL OR :checkValid = FALSE OR (b IS NOT NULL AND b.expireDate >= CURRENT_TIMESTAMP))"
          + " AND (:supplierId IS NULL OR ps.supplier.id = :supplierId)")
  List<ProductEntity> searchAllProductByBranchId(
      Long branchId, String searchStr, Boolean checkValid, Long supplierId);

  Optional<ProductEntity> findByRegistrationCode(String registrationCode);

  @Query(
      "SELECT p FROM ProductEntity p JOIN p.productSuppliers ps WHERE ps.supplier.id = :supplierId"
          + " AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
  List<ProductEntity> findProductBySupplierAndName(
      @Param("supplierId") Long supplierId, @Param("productName") String productName);

  // Condition for quantity less than or equal
  @Query(
      "SELECT p FROM ProductEntity p left join fetch p.branchProducs bp WHERE :quantity >"
          + " bp.quantity AND bp.branch.id = :id")
  List<ProductEntity> findByQuantityLessThanEqualInBranch(
      @Param("quantity") Integer quantity, @Param("id") Long id);

  @Query(
      "SELECT p FROM ProductEntity p left join fetch p.branchProducs bp WHERE bp.quantity<"
          + " bp.minQuantity AND bp.branch.id = :id")
  List<ProductEntity> findByQuantityLessThanMinQuantityInBranch(@Param("id") Long id);

  @Query(
      "SELECT p FROM ProductEntity p left join fetch p.branchProducs bp WHERE bp.quantity= "
          + ":quantity AND bp.branch.id = :id")
  List<ProductEntity> findByQuantityInBranch(
      @Param("quantity") Integer quantity, @Param("id") Long id);

  @Modifying
  @Transactional
  @Query("UPDATE ProductEntity i SET i.status = :status WHERE i.id = :id")
  void updateProductStatus(@Param("status") ProductStatus status, @Param("id") Long id);

  @Query(
      "SELECT p FROM ProductEntity p "
          + "LEFT JOIN FETCH p.branchProducs bp "
          + "WHERE (p.sellPrice < p.inboundPrice OR p.sellPrice IS NULL OR p.sellPrice = 0) "
          + "AND bp.branch.id = :id")
  List<ProductEntity> findProductsWithLossOrNoSellPriceInBranch(@Param("id") Long id);

  @Query(
      "SELECT p FROM ProductEntity p "
          + "LEFT JOIN FETCH p.branchProducs bp "
          + "WHERE (p.sellPrice = :sellPrice) AND bp.branch.id = :id")
  List<ProductEntity> findProductsBySellPrice(
      @Param("sellPrice") BigDecimal sellPrice, @Param("id") Long id);
}
