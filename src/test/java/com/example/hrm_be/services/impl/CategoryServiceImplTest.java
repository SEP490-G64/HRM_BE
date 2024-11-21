package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.hrm_be.components.ProductCategoryMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock
  private ProductCategoryMapper categoryMapper;
  @Mock private ProductCategoryRepository categoryRepository;
  @InjectMocks
  private ProductCategoryServiceImpl categoryService;

  private ProductCategory category;
  private ProductCategoryEntity categoryEntity;

  @BeforeEach
  public void setup() {
    category = ProductCategory.builder()
            .categoryName("Valid Category")
            .categoryDescription("Valid Description")
            .taxRate(BigDecimal.valueOf(10.0))
            .build();

    categoryEntity = ProductCategoryEntity.builder()
            .categoryName("Valid Category")
            .categoryDescription("Valid Description")
            .taxRate(BigDecimal.valueOf(10.0))
            .build();
  }

  // GET BY ID
  @Test
  void testUTCID01_Get_idValid() {
    Long id = 1L;
    when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);
    ProductCategory result = categoryService.getById(id);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("Valid Category", result.getCategoryName());
  }

  @Test
  void testUTCID02_Get_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> categoryService.getById(id));
  }

  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    Assertions.assertNull(categoryService.getById(id));
  }

  // GET BY PAGING
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryName"));
    List<ProductCategoryEntity> categories = Collections.singletonList(categoryEntity);
    Page<ProductCategoryEntity> page = new PageImpl<>(categories, pageable, categories.size());

    when(categoryRepository.findByCategoryNameContainingIgnoreCase(
            "a", pageable)).thenReturn(page);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    Page<ProductCategory> result = categoryService.getByPaging(0, 10, "categoryName", "a");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(HrmCommonException.class, () -> categoryService.getByPaging(-1, 10, "categoryName", "a"));
  }

  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () -> categoryService.getByPaging(0, 0, "categoryName", "a"));
  }

  // GET BY PAGING
  @Test
  void testUTCID04_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryDescription"));
    List<ProductCategoryEntity> categories = Collections.singletonList(categoryEntity);
    Page<ProductCategoryEntity> page = new PageImpl<>(categories, pageable, categories.size());

    when(categoryRepository.findByCategoryNameContainingIgnoreCase(
            "a", pageable)).thenReturn(page);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    Page<ProductCategory> result = categoryService.getByPaging(0, 10, "categoryDescription", "a");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // GET BY PAGING
  @Test
  void testUTCID05_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("taxRate"));
    List<ProductCategoryEntity> categories = Collections.singletonList(categoryEntity);
    Page<ProductCategoryEntity> page = new PageImpl<>(categories, pageable, categories.size());

    when(categoryRepository.findByCategoryNameContainingIgnoreCase(
            "a", pageable)).thenReturn(page);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    Page<ProductCategory> result = categoryService.getByPaging(0, 10, "taxRate", "a");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // GET BY PAGING
  @Test
  void testUTCID06_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
    List<ProductCategoryEntity> categories = Collections.singletonList(categoryEntity);
    Page<ProductCategoryEntity> page = new PageImpl<>(categories, pageable, categories.size());

    when(categoryRepository.findByCategoryNameContainingIgnoreCase(
            "a", pageable)).thenReturn(page);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    Page<ProductCategory> result = categoryService.getByPaging(0, 10, null, "a");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // GET BY PAGING
  @Test
  void testUTCID07_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
    List<ProductCategoryEntity> categories = Collections.singletonList(categoryEntity);
    Page<ProductCategoryEntity> page = new PageImpl<>(categories, pageable, categories.size());

    when(categoryRepository.findByCategoryNameContainingIgnoreCase(
            "", pageable)).thenReturn(page);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    Page<ProductCategory> result = categoryService.getByPaging(0, 10, null, null);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  @Test
  void testUTCID08_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () -> categoryService.getByPaging(0, 10, "abc", "a"));
  }

  // CREATE
  @Test
  void testUTCID01_Create_AllValid() {
    when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);
    when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    ProductCategory result = categoryService.create(category);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("Valid Category", result.getCategoryName());
  }

  @Test
  void testUTCID02_Create_categoryNameNull() {
    category.setCategoryName(null);
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  @Test
  void testUTCID03_Create_categoryNameEmpty() {
    category.setCategoryName("");
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  @Test
  void testUTCID04_Create_categoryNameLong() {
    category.setCategoryName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  @Test
  void testUTCID05_Create_categoryNameDuplicate() {
    category.setCategoryName("Name Duplicate");
    categoryEntity.setCategoryName("Name Duplicate");
    when(categoryRepository.existsByCategoryName(categoryEntity.getCategoryName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  // UTCID06 - create: categoryDescription greater than 256 characters
  @Test
  void testUTCID06_Create_categoryDescriptionLong() {
    category.setCategoryDescription("A".repeat(1001));
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  @Test
  void testUTCID07_Create_taxRateNegative() {
    category.setTaxRate(BigDecimal.valueOf(-1));
    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  // UTCID08 - create: taxRate greater than 100
  @Test
  void testUTCID08_Create_taxRateExcessive() {
    category.setTaxRate(BigDecimal.valueOf(101));

    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  // UTCID09 - create: category null
  @Test
  void testUTCID09_Create_categoryNull() {
    category= null;

    assertThrows(HrmCommonException.class, () -> categoryService.create(category));
  }

  // UPDATE
  @Test
  void testUTCID01_Update_AllValid() {
    category.setId(1L);
    categoryEntity.setId(1L);

    when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(categoryEntity));
    when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);

    ProductCategory result = categoryService.update(category);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("Valid Category", result.getCategoryName());
  }

  @Test
  void testUTCID02_Update_categoryNameNull() {
    category.setId(1L);
    category.setCategoryName(null);
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  @Test
  void testUTCID03_Update_categoryNameEmpty() {
    category.setId(1L);
    category.setCategoryName("");
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  @Test
  void testUTCID04_Update_categoryNameLong() {
    category.setId(1L);
    category.setCategoryName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID05 - Update: categoryName duplicate
  @Test
  void testUTCID05_Update_categoryNameDuplicate() {
    category.setId(1L);
    categoryEntity.setId(1L);
    category.setCategoryName("new Name");
    categoryEntity.setCategoryName("Old Name");

    when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(categoryEntity));

    when(categoryRepository.existsByCategoryName(category.getCategoryName())).thenReturn(true);

    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID06 - Update: categoryDescription greater than 1000 characters
  @Test
  void testUTCID06_Update_categoryDescriptionLong() {
    category.setCategoryDescription("A".repeat(1001));
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  @Test
  void testUTCID07_Update_taxRateNegative() {
    category.setId(1L);
    category.setTaxRate(BigDecimal.valueOf(-1));
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID08 - Update: taxRate greater than 100
  @Test
  void testUTCID08_Update_taxRateExcessive() {
    category.setTaxRate(BigDecimal.valueOf(101));
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID09 - Update: id null
  @Test
  void testUTCID09_Update_idNull() {
    category.setId(null);
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID010 - Update: id not exist
  @Test
  void testUTCID010_Update_idNotExist() {
    category.setId(1L);
    when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty() );
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }

  // UTCID11 - Update: category null
  @Test
  void testUTCID011_Update_categoryNull() {
    category = null;
    assertThrows(HrmCommonException.class, () -> categoryService.update(category));
  }


  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    category.setId(1L);
    when(categoryService.existById(category.getId())).thenReturn(true);
    categoryService.delete(category.getId());
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    category.setId(null);
    assertThrows(HrmCommonException.class, () -> categoryService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    category.setId(1L);
    when(categoryService.existById(category.getId())).thenReturn(false);
    assertThrows(HrmCommonException.class, () -> categoryService.delete(category.getId()));
  }

  // existById
  // UTCID01 - existById: valid
  @Test
  void testUTCID01_existById_AllValid() {
    Long id = 1L;
    boolean result = categoryService.existById(id);
    Assertions.assertNotNull(result);
  }

  // UTCID02 - existById: id null
  @Test
  void testUTCID02_existById_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> categoryService.existById(id));
  }

  // getAll
  // UTCID01 - getAll: valid
  @Test
  void testUTCID01_getAll_AllValid() {
    when(categoryRepository.findAll()).thenReturn(List.of(categoryEntity));
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);
    List<ProductCategory> result = categoryService.getAll();
    Assertions.assertNotNull(result);
  }

  // getByName
  // UTCID01 - getByName: valid
  @Test
  void testUTCID01_getByName_AllValid() {
    String name = "a";
    when(categoryRepository.findByCategoryName(name)).thenReturn(Optional.of(categoryEntity));
    when(categoryMapper.toDTO(categoryEntity)).thenReturn(category);
    ProductCategory result = categoryService.findByCategoryName(name);
    Assertions.assertNotNull(result);
  }
}

