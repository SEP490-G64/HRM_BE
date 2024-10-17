package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProductService {
  Product getById(Long id);

  Page<Product> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String searchType,
      String searchValue);

  Product create(Product product);

  Product update(Product product);

  void delete(Long id);

  Page<Product> getByPagingAndCateId(int pageNo, int pageSize, String sortBy, Long cateId);

  Page<Product> getByPagingAndTypeId(int pageNo, int pageSize, String sortBy, Long TypeId);

  List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList);

  List<Product> searchProducts(Optional<String> keyword, Optional<Long> manufacturerId, Optional<Long> categoryId,
      Optional<Long> typeId, Optional<String> status);
}
