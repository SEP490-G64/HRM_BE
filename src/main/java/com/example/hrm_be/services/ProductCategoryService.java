package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductCategoryService {
  Boolean existById(Long id);

  List<ProductCategory> getAll();

  // Retrieves a ProductCategory by its ID
  ProductCategory getById(Long id);

  // Retrieves a paginated list of ProductCategory objects, allowing sorting and searching by
  // keywords
  Page<ProductCategory> getByPaging(int pageNo, int pageSize, String sortBy, String keyword);

  // Creates a new ProductCategory entity and returns the created entity
  ProductCategory create(ProductCategory category);

  // Updates an existing ProductCategory entity and returns the updated entity
  ProductCategory update(ProductCategory category);

  // Deletes a ProductCategory by its ID
  void delete(Long id);

  ProductCategory findByCategoryName(String categoryName);
}
