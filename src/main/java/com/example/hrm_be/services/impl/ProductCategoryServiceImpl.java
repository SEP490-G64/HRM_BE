package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductCategoryMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import com.example.hrm_be.services.ProductCategoryService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

  // Injects the repository to interact with the database
  @Autowired private ProductCategoryRepository categoryRepository;
  // Injects the mapper to convert between DTO and Entity objects
  @Autowired private ProductCategoryMapper categoryMapper;

  // Retrieves a ProductCategory by ID
  @Override
  public ProductCategory getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> categoryRepository.findById(e).map(b -> categoryMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of ProductCategory entities, allowing sorting and searching by name
  @Override
  public Page<ProductCategory> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return categoryRepository
        .findByCategoryNameContainingIgnoreCase(name, pageable)
        .map(dao -> categoryMapper.toDTO(dao));
  }

  // Creates a new ProductCategory
  @Override
  public ProductCategory create(ProductCategory category) {
    // Validation: Ensure the category is not null and the name does not already exist
    if (category == null || categoryRepository.existsByCategoryName(category.getCategoryName())) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.EXIST);
    }

    // Convert DTO to entity, save it, then convert the saved entity back to DTO
    return Optional.ofNullable(category)
        .map(e -> categoryMapper.toEntity(e))
        .map(e -> categoryRepository.save(e))
        .map(e -> categoryMapper.toDTO(e))
        .orElse(null);
  }

  // Updates an existing ProductCategory
  @Override
  public ProductCategory update(ProductCategory category) {
    // Retrieve the existing category entity by ID
    ProductCategoryEntity oldCategoryEntity =
        categoryRepository.findById(category.getId()).orElse(null);
    if (oldCategoryEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.NOT_EXIST);
    }

    // Check if category name exist except current category
    if (categoryRepository.existsByCategoryName(category.getCategoryName())
        && !Objects.equals(category.getCategoryName(), oldCategoryEntity.getCategoryName())) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.EXIST);
    }

    // Update the fields of the existing category entity, save it, and convert it back to DTO
    return Optional.ofNullable(oldCategoryEntity)
        .map(
            op ->
                op.toBuilder()
                    .categoryName(category.getCategoryName())
                    .categoryDescription(category.getCategoryDescription())
                    .taxRate(category.getTaxRate())
                    .build())
        .map(categoryRepository::save)
        .map(categoryMapper::toDTO)
        .orElse(null);
  }

  // Deletes a ProductCategory by ID
  @Override
  public void delete(Long id) {
    // Validation: Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Retrieve the existing category entity by ID
    ProductCategoryEntity oldCategoryEntity = categoryRepository.findById(id).orElse(null);
    if (oldCategoryEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.NOT_EXIST);
    }

    // Delete the category by ID
    categoryRepository.deleteById(id);
  }
}
