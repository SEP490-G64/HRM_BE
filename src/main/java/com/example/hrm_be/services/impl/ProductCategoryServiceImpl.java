package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductCategoryMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import com.example.hrm_be.services.ProductCategoryService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

  // Injects the repository to interact with the database
  @Autowired private ProductCategoryRepository categoryRepository;
  // Injects the mapper to convert between DTO and Entity objects
  @Autowired private ProductCategoryMapper categoryMapper;

  @Override
  public List<ProductCategory> getAll() {
    List<ProductCategoryEntity> productCategoryEntities = categoryRepository.findAll();
    return productCategoryEntities.stream()
        .map(dao -> categoryMapper.toDTO(dao))
        .collect(Collectors.toList());
  }

  // Retrieves a ProductCategory by ID
  @Override
  public ProductCategory getById(Long id) {
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.INVALID);
    }

    return Optional.ofNullable(id)
        .flatMap(e -> categoryRepository.findById(e).map(b -> categoryMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of ProductCategory entities, allowing sorting and searching by name
  @Override
  public Page<ProductCategory> getByPaging(
      int pageNo, int pageSize, String sortBy, String keyword) {
    if (pageNo < 0 || pageSize < 1) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (sortBy == null) {
      sortBy = "id";
    }
    if (!Objects.equals(sortBy, "id")
        && !Objects.equals(sortBy, "categoryName")
        && !Objects.equals(sortBy, "categoryDescription")
        && !Objects.equals(sortBy, "taxRate")) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (keyword == null) {
      keyword = "";
    }

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return categoryRepository
        .findByCategoryNameContainingIgnoreCase(keyword, pageable)
        .map(dao -> categoryMapper.toDTO(dao));
  }

  // Creates a new ProductCategory
  @Override
  public ProductCategory create(ProductCategory category) {
    if (category == null || !commonValidate(category)) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.INVALID);
    }

    // Validation: Ensure the name does not already exist
    if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
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
    if (category == null || category.getId() == null || !commonValidate(category)) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.INVALID);
    }

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
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.INVALID);
    }

    // Retrieve the existing category entity by ID
    ProductCategoryEntity oldCategoryEntity = categoryRepository.findById(id).orElse(null);
    if (oldCategoryEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.NOT_EXIST);
    }

    // Delete the category by ID
    categoryRepository.deleteById(id);
  }

  @Override
  public ProductCategory findByCategoryName(String categoryName) {
    return Optional.of(categoryName)
            .flatMap(e -> categoryRepository.findByCategoryName(e).map(b -> categoryMapper.toDTO(b)))
            .orElse(null);
  }

  // This method will validate category field input values
  private boolean commonValidate(ProductCategory category) {
    if (category.getCategoryName() == null
        || category.getCategoryName().isEmpty()
        || category.getCategoryName().length() > 100) {
      return false;
    }
    if (category.getCategoryDescription() != null
        && category.getCategoryDescription().length() > 1000) {
      return false;
    }
    if (category.getTaxRate() != null
        && (category.getTaxRate().compareTo(BigDecimal.valueOf(100)) > 0
            || (category.getTaxRate().compareTo(BigDecimal.ZERO) < 0))) {
      return false;
    }
    return true;
  }
}
