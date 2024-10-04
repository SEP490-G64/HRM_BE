package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductCategoryService {
  ProductCategory getById(Long id);

  Page<ProductCategory> getByPaging(
      int pageNo, int pageSize, String sortBy, String keyWords);

  ProductCategory create(ProductCategory category);

  ProductCategory update(ProductCategory category);

  void delete(Long id);
}
