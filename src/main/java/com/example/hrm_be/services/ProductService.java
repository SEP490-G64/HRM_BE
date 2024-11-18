package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.dtos.ProductSupplierDTO;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.models.responses.AuditHistory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  Product getById(Long id);

  Product create(Product product);

  Product update(Product product);

  Product updateInboundPrice(Product product);

  void delete(Long id);

  List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList);

  Page<ProductBaseDTO> searchProducts(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      Optional<String> keyword,
      Optional<Long> manufacturerId,
      Optional<Long> categoryId,
      Optional<Long> typeId,
      Optional<String> status);

  List<String> importFile(MultipartFile file);

  ByteArrayInputStream exportFile() throws IOException;

  List<AllowedProductEntity> getAllowProducts(String searchStr);

  Product addProductInInbound(ProductInbound productInbound);

  List<ProductSupplierDTO> getAllProductsBySupplier(Long id, String ProductName);

  List<ProductBaseDTO> getProductInBranch(
      Long branchId, String keyword, Boolean checkValid, Long supplierId);

  List<ProductBaseDTO> filterProducts(
      Boolean lessThanOrEqual, Integer quantity, Boolean warning, Boolean outOfStock);

  List<ProductBaseDTO> getProductsWithLossOrNoSellPriceInBranch();

  List<ProductBaseDTO> getProductsBySellPrice(BigDecimal sellPrice);

  List<ProductBaseDTO> getByKeyword(String keyword);

  List<AuditHistory> getProductDetailsInPeriod(
      Long productId, LocalDateTime startDate, LocalDateTime endDate);
}
