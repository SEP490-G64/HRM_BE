package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.ProductStatus;
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
      "SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.registrationCode = :code "
          + "AND (p.status != 'DA_XOA' OR p.status IS NULL)")
  boolean existsByRegistrationCode(String code);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p JOIN p.branchProducs bp LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId LEFT JOIN"
          + " p.productSuppliers ps WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr,"
          + " '%')) AND bp.branch.id = :branchId AND bp.quantity > 0 AND (b IS NULL OR bb.quantity"
          + " > 0) AND (:checkValid IS NULL OR :checkValid = FALSE OR (b IS NOT NULL AND"
          + " b.expireDate >= CURRENT_TIMESTAMP) OR :checkValid = TRUE) AND (:supplierId IS NULL OR"
          + " ps.supplier.id = :supplierId) AND p.status != 'DA_XOA'")
  List<ProductEntity> searchProductByBranchId(
      Long branchId, String searchStr, Boolean checkValid, Long supplierId);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p JOIN p.branchProducs bp LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId WHERE"
          + " LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr, '%')) AND bp.branch.id ="
          + " :branchId AND bp.quantity > 0 AND (b IS NULL OR (b IS NOT NULL AND (bb.quantity > 0"
          + " OR b.expireDate >= CURRENT_TIMESTAMP))) AND p.status != 'DA_XOA' AND p.sellPrice IS"
          + " NOT NULL")
  List<ProductEntity> searchProductByBranchIdWithSellPrice(Long branchId, String searchStr);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p JOIN p.branchProducs bp LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId LEFT JOIN"
          + " p.productSuppliers ps WHERE (:searchStr IS NULL OR LOWER(p.productName) LIKE"
          + " LOWER(CONCAT('%', :searchStr, '%'))) AND bp.branch.id = :branchId"
          + " AND (:checkValid IS NULL OR :checkValid = FALSE"
          + " OR"
          + " (b IS NOT NULL AND b.expireDate >= CURRENT_TIMESTAMP)) AND (:supplierId IS NULL OR"
          + " ps.supplier.id = :supplierId) AND p.status != 'DA_XOA'")
  List<ProductEntity> searchAllProductByBranchId(
      Long branchId, String searchStr, Boolean checkValid, Long supplierId);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p"
          + " JOIN p.branchProducs bp"
          + " LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId"
          + " LEFT JOIN p.productSuppliers ps"
          + " JOIN p.category c" // Join the Category entity
          + " WHERE (:searchStr IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr,"
          + " '%'))) AND (:categoryId IS NULL OR c.id = :categoryId)" // Add filter for categoryId
          + " AND bp.branch.id = :branchId"
          + " AND (:checkValid IS NULL OR :checkValid = FALSE OR (b IS NOT NULL AND b.expireDate >="
          + " CURRENT_TIMESTAMP)) AND (:supplierId IS NULL OR ps.supplier.id = :supplierId)")
  List<ProductEntity> searchAllProductByBranchIdAndCateId(
      Long branchId, Long categoryId, String searchStr, Boolean checkValid, Long supplierId);

  @Query(
      "SELECT DISTINCT p FROM ProductEntity p"
          + " JOIN p.branchProducs bp"
          + " LEFT JOIN FETCH p.batches b"
          + " LEFT JOIN BranchBatchEntity bb ON bb.batch = b AND bb.branch.id = :branchId"
          + " LEFT JOIN p.productSuppliers ps"
          + " JOIN p.type c" // Join the Category entity
          + " WHERE (:searchStr IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchStr,"
          + " '%'))) AND (:typeId IS NULL OR c.id = :typeId)" // Add filter for categoryId
          + " AND bp.branch.id = :branchId"
          + " AND (:checkValid IS NULL OR :checkValid = FALSE OR (b IS NOT NULL AND b.expireDate >="
          + " CURRENT_TIMESTAMP)) AND (:supplierId IS NULL OR ps.supplier.id = :supplierId)")
  List<ProductEntity> searchAllProductByBranchIdAndTypeId(
      Long branchId, Long typeId, String searchStr, Boolean checkValid, Long supplierId);

  Optional<ProductEntity> findByRegistrationCode(String registrationCode);

  @Query(
      "SELECT p FROM ProductEntity p JOIN p.productSuppliers ps WHERE ps.supplier.id = :supplierId"
          + " AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%')) AND p.status !="
          + " 'DA_XOA'")
  List<ProductEntity> findProductBySupplierAndName(
      @Param("supplierId") Long supplierId, @Param("productName") String productName);

  // Condition for quantity less than or equal
  @Query(
      "SELECT p FROM ProductEntity p left join fetch p.branchProducs bp WHERE :quantity >="
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
  Page<ProductEntity> findProductsWithLossOrNoSellPriceInBranch(
      @Param("id") Long id, Pageable pageable);

  @Query(
      "SELECT p FROM ProductEntity p "
          + "LEFT JOIN FETCH p.branchProducs bp "
          + "WHERE (p.sellPrice <= :sellPrice) AND bp.branch.id = :id")
  Page<ProductEntity> findProductsBySellPrice(
      @Param("sellPrice") BigDecimal sellPrice, @Param("id") Long id, Pageable pageable);

  @Modifying
  @Transactional
  @Query("UPDATE ProductEntity p SET p.category = null WHERE p.category.id = :categoryId")
  void removeCategoryFromProducts(Long categoryId);

  @Modifying
  @Transactional
  @Query("UPDATE ProductEntity p SET p.type = null WHERE p.type.id = :typeId")
  void removeTypeFromProducts(Long typeId);

  @Query(
      "SELECT COUNT(DISTINCT p) FROM ProductEntity p "
          + "LEFT JOIN p.branchProducs bp "
          + "WHERE (:branchId IS NULL OR bp.branch.id = :branchId) AND p.status != 'DA_XOA'")
  BigDecimal getTotalProductCountByBranchId(@Param("branchId") Long branchId);

  @Query(
      value =
          "WITH OutboundProduct AS (\n"
              + "    SELECT \n"
              + "        od.product_id,\n"
              + "        SUM(\n"
              + "            CASE \n"
              + "                -- Nếu đơn vị đo hiện tại là baseUnit, sử dụng trực tiếp\n"
              + "                WHEN od.unit_of_measurement_id = p.base_unit_id THEN"
              + " od.outbound_quantity \n"
              + "                -- Nếu không, chuyển đổi qua factor_conversion\n"
              + "                ELSE od.outbound_quantity * uc.factor_conversion \n"
              + "            END\n"
              + "        ) AS outbound_quantity_converted,\n"
              + "        SUM(od.outbound_quantity * od.price) AS outbound_total\n"
              + "    FROM outbound_product_details od\n"
              + "    JOIN outbound o ON od.outbound_id = o.id\n"
              + "    JOIN product p ON od.product_id = p.id\n"
              + "    LEFT JOIN unit_conversion uc \n"
              + "        ON od.unit_of_measurement_id = uc.smaller_unit \n"
              + "        AND p.base_unit_id = uc.larger_unit\n"
              + "        AND uc.product_id = p.id -- Đảm bảo chỉ lấy conversion đúng cho sản phẩm\n"
              + "    WHERE (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + "      AND o.created_date > CURRENT_DATE - INTERVAL '30' DAY\n"
              + "    GROUP BY od.product_id\n"
              + "),\n"
              + "OutboundBatchProduct AS (\n"
              + "    SELECT \n"
              + "        b.product_id, -- Sử dụng product_id từ bảng batch\n"
              + "        SUM(\n"
              + "            CASE \n"
              + "                -- Nếu đơn vị đo hiện tại là baseUnit, sử dụng trực tiếp\n"
              + "                WHEN od.unit_of_measurement_id = p.base_unit_id THEN od.quantity"
              + " \n"
              + "                -- Nếu không, chuyển đổi qua factor_conversion\n"
              + "                ELSE od.quantity * COALESCE(uc.factor_conversion, 1) -- Sử dụng 1"
              + " nếu không có conversion\n"
              + "            END\n"
              + "        ) AS outbound_quantity_converted,\n"
              + "        SUM(od.quantity * od.price) AS outbound_total\n"
              + "    FROM outbound_details od\n"
              + "    JOIN outbound o ON od.outbound_id = o.id\n"
              + "    JOIN batch b ON od.batch_id = b.id -- Lấy product_id từ batch\n"
              + "    JOIN product p ON b.product_id = p.id -- Xác định sản phẩm liên quan\n"
              + "    LEFT JOIN unit_conversion uc \n"
              + "        ON od.unit_of_measurement_id = uc.smaller_unit \n"
              + "        AND p.base_unit_id = uc.larger_unit\n"
              + "        AND uc.product_id = p.id -- Chuyển đổi đúng sản phẩm\n"
              + "    WHERE (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + "      AND o.created_date > CURRENT_DATE - INTERVAL '30' DAY\n"
              + "    GROUP BY b.product_id -- Gom theo product_id từ batch\n"
              + "),\n"
              + "InboundProduct AS (\n"
              + "    SELECT \n"
              + "        id.product_id, \n"
              + "        SUM(id.receive_quantity) AS inbound_quantity, \n"
              + "        SUM(id.receive_quantity * id.price) AS inbound_total\n"
              + "    FROM inbound_details id\n"
              + "    JOIN inbound i ON id.inbound_id = i.id\n"
              + "    WHERE (:branchId IS NULL OR i.to_branch_id = :branchId)\n"
              + "      AND i.created_date > CURRENT_DATE - INTERVAL '30' DAY\n"
              + "    GROUP BY id.product_id\n"
              + ")\n"
              + "SELECT \n"
              + "    p.id AS product_id,\n"
              + "    p.product_name,\n"
              + "    p.url_image,\n"
              + "    COALESCE(ip.inbound_quantity, 0) as inbound_quantity,\n"
              + "    COALESCE(op.outbound_quantity_converted, 0) +"
              + " COALESCE(obp.outbound_quantity_converted, 0) AS outbound_quantity,\n"
              + "    COALESCE(ip.inbound_quantity, 0) + COALESCE(op.outbound_quantity_converted, 0)"
              + " + COALESCE(obp.outbound_quantity_converted, 0) AS total_quantity,\n"
              + "    COALESCE(ip.inbound_total, 0) AS inbound_transaction,\n"
              + "    COALESCE(op.outbound_total, 0) + COALESCE(obp.outbound_total, 0) AS"
              + " outbound_transaction,\n"
              + "    COALESCE(ip.inbound_total, 0) + COALESCE(op.outbound_total, 0) +"
              + " COALESCE(obp.outbound_total, 0) AS total_transaction,\n"
              + "    uom.unit_name\n"
              + "FROM product p\n"
              + "LEFT JOIN InboundProduct ip ON p.id = ip.product_id\n"
              + "LEFT JOIN OutboundProduct op ON p.id = op.product_id\n"
              + "LEFT JOIN OutboundBatchProduct obp ON p.id = obp.product_id\n"
              + "JOIN unit_of_measurement uom ON uom.id = p.base_unit_id\n"
              + "WHERE COALESCE(ip.inbound_quantity, 0) + COALESCE(op.outbound_quantity_converted,"
              + " 0) > 0\n"
              + "ORDER BY total_transaction DESC\n"
              + "LIMIT 5;",
      nativeQuery = true)
  List<Object[]> getTopProduct(@Param("branchId") Long branchId);
}
