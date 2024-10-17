package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.dtos.UnitConversion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductTypeService {
  List<ProductType> getAll();
  // Retrieves a ProductType by its ID
  ProductType getById(Long id);

  // Retrieves a paginated list of ProductType objects, allowing sorting and searching name
  Page<ProductType> getByPaging(int pageNo, int pageSize, String sortBy, String name);

  // Creates a new ProductType entity and returns the created entity
  ProductType create(ProductType productType);

  // Updates an existing ProductType entity and returns the updated entity
  ProductType update(ProductType productType);

  // Deletes a ProductType by its ID
  void delete(Long id);
}
