package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired private ProductRepository productRepository;
  @Autowired private AllowedProductRepository allowedProductRepository;

  @Autowired private ProductMapper productMapper;

  @Override
  public Product getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> productRepository.findById(e).map(b -> productMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Product> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String searchType,
      String searchValue) {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

    if (searchValue != null && !searchValue.isEmpty()) {
      // Search by name if searchType is "name"
      if ("name".equalsIgnoreCase(searchType)) {
        return productRepository
            .findProductEntitiesByProductNameContainingIgnoreCase(searchValue, pageable)
            .map(dao -> productMapper.toDTO(dao));
      }
      // Search by code if searchType is "code"
      else if ("code".equalsIgnoreCase(searchType)) {
        return productRepository
            .findProductEntitiesByRegistrationCodeContainingIgnoreCase(searchValue, pageable)
            .map(dao -> productMapper.toDTO(dao));
      }
    }

    // Return all products if no search value is provided
    return productRepository.findAll(pageable).map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public Product create(Product product) {
    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }
    return Optional.ofNullable(product)
        .map(e -> productMapper.toEntity(e))
        .map(e -> productRepository.save(e))
        .map(e -> productMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Product update(Product product) {
    ProductEntity oldProductEntity = productRepository.findById(product.getId()).orElse(null);
    if (oldProductEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    return Optional.ofNullable(oldProductEntity)
        .map(
            op ->
                op.toBuilder()
                    .productName(product.getProductName())
                    .status(product.getStatus())
                    .build())
        .map(productRepository::save)
        .map(productMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    ProductEntity productEntity = productRepository.findById(id).orElse(null);
    if (productEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    productRepository.deleteById(id);
  }

  @Override
  public Page<Product> getByPagingAndCateId(int pageNo, int pageSize, String sortBy, Long cateId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndCategoryId(cateId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public Page<Product> getByPagingAndTypeId(int pageNo, int pageSize, String sortBy, Long TypeId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndTypeId(TypeId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList) {
    List<AllowedProductEntity> savedProducts = new ArrayList<>();

    for (Map<String, Object> productJson : productJsonList) {
      AllowedProductEntity product = new AllowedProductEntity();

      if (productJson.containsKey("tenThuoc")) {
        product.setProductName((String) productJson.get("tenThuoc"));
      }
      if (productJson.containsKey("id")) {
        product.setProductCode((String) productJson.get("id"));
      }
      if (productJson.containsKey("soDangKy")) {
        product.setRegistrationCode((String) productJson.get("soDangKy"));
      }
      if (productJson.containsKey("images")) {
        List<String> images = (List<String>) productJson.get("images");
        if (images != null && !images.isEmpty()) {
          product.setUrlImage(images.get(0)); // Assuming first image as URL
        }
      }
      if (productJson.containsKey("hoatChat")) {
        product.setActiveIngredient((String) productJson.get("hoatChat"));
      }
      if (productJson.containsKey("taDuoc")) {
        product.setExcipient((String) productJson.get("taDuoc"));
      }
      if (productJson.containsKey("baoChe")) {
        product.setFormulation((String) productJson.get("baoChe"));
      }

      // Save product to the database
      AllowedProductEntity savedProduct = allowedProductRepository.save(product);
      savedProducts.add(savedProduct);
    }

    return savedProducts;
  }
}
