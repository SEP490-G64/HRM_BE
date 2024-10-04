package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductCategoryService {
  // Retrieves a ProductCategory by its ID
  ProductCategory getById(Long id);

  // Retrieves a paginated list of ProductCategory objects, allowing sorting and searching by keywords
  Page<ProductCategory> getByPaging(int pageNo, int pageSize, String sortBy, String name);

  // Creates a new ProductCategory entity and returns the created entity
  ProductCategory create(ProductCategory category);

  // Updates an existing ProductCategory entity and returns the updated entity
  ProductCategory update(ProductCategory category);

  // Deletes a ProductCategory by its ID
  void delete(Long id);
}
