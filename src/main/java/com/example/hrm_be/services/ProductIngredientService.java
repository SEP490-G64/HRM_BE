package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductIngredient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductIngredientService {
    ProductIngredient getById(Long id);

    Page<ProductIngredient> getByPaging(int pageNo, int pageSize, String sortBy);

    ProductIngredient create(ProductIngredient ingredient);

    ProductIngredient update(ProductIngredient ingredient);

    void delete(Long id);
}
