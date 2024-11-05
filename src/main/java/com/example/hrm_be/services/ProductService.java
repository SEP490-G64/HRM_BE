package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.dtos.ProductSupplierDTO;
import com.example.hrm_be.models.entities.AllowedProductEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  Product getById(Long id);

  Product create(Product product);

  Product update(Product product);

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

  ProductEntity addProductInInbound(ProductInbound productInbound);

  List<ProductSupplierDTO> getAllProductsBySupplier(Long id, String ProductName);
}
