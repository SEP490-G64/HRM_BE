package com.example.hrm_be.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import com.example.hrm_be.services.ProductCategoryService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(ProductCategoryServiceImpl.class)
@Transactional
public class CategoryServiceImplTest {

  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private ProductCategoryService productCategoryService;
  @Autowired private ProductCategoryRepository productCategoryRepository;

  // Helper to create a valid category entity
  private ProductCategory createValidCategory() {
    return new ProductCategory()
        .setCategoryName("Valid Category Name")
        .setCategoryDescription("Valid Category Description")
        .setTaxRate(BigDecimal.TEN);
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    ProductCategory category = createValidCategory();
    ProductCategory category1 = productCategoryService.create(category);
    ProductCategory category2 = productCategoryService.getById(category1.getId());
    assertEquals(category1, category2);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> productCategoryService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    productCategoryRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, productCategoryService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    ProductCategory category = createValidCategory();
    productCategoryRepository.deleteAll();
    ProductCategory savedCategory = productCategoryService.create(category);
    assertThat(savedCategory).isNotNull();

    Page<ProductCategory> result = productCategoryService.getByPaging(0, 1, "categoryName", "a");
    assertEquals(1, result.getTotalElements());
    assertEquals(category.getCategoryName(), result.getContent().get(0).getCategoryName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productCategoryService.getByPaging(-1, 1, "categoryName", "a");
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productCategoryService.getByPaging(0, 0, "categoryName", "a");
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productCategoryService.getByPaging(0, 1, "a", "a");
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    ProductCategory category = createValidCategory();
    ProductCategory savedCategory = productCategoryService.create(category);

    assertThat(savedCategory).isNotNull();
    assertThat(savedCategory.getCategoryName()).isEqualTo("Valid Category Name");
  }

  // UTCID02 - create: categoryName null
  @Test
  void testUTCID02_Create_categoryNameNull() {
    ProductCategory category = createValidCategory();
    category.setCategoryName(null);

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UTCID03 - create: categoryName empty
  @Test
  void testUTCID03_Create_categoryNameEmpty() {
    ProductCategory category = createValidCategory();
    category.setCategoryName("");

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UTCID04 - create: categoryName greater than 100 characters
  @Test
  void testUTCID04_Create_categoryNameLong() {
    ProductCategory category = createValidCategory();
    category.setCategoryName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UTCID05 - create: categoryName duplicate
  @Test
  void testUTCID05_Create_categoryNameDuplicate() {
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    ProductCategory duplicateCategory =
        new ProductCategory()
            .setCategoryName("Valid Category Name")
            .setCategoryDescription("Valid Category Description")
            .setTaxRate(BigDecimal.ONE);

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(duplicateCategory));
  }

  // UTCID06 - create: categoryDescription greater than 256 characters
  @Test
  void testUTCID06_Create_categoryDescriptionLong() {
    ProductCategory category = createValidCategory();
    category.setCategoryDescription("A".repeat(1001));

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UTCID07 - create: taxRate negative number
  @Test
  void testUTCID07_Create_taxRateNegative() {
    ProductCategory category = createValidCategory();
    category.setTaxRate(BigDecimal.valueOf(-1));

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UTCID08 - create: taxRate greater than 100
  @Test
  void testUTCID08_Create_taxRateExcessive() {
    ProductCategory category = createValidCategory();
    category.setTaxRate(BigDecimal.valueOf(101));

    assertThrows(HrmCommonException.class, () -> productCategoryService.create(category));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    ProductCategory savedCategory = productCategoryService.create(category);
    ProductCategory updateCategory = productCategoryService.update(savedCategory);

    assertThat(savedCategory).isNotNull();
    assertThat(updateCategory.getCategoryName()).isEqualTo("Valid Category Name");
  }

  // UTCID02 - UPDATE: categoryName null
  @Test
  void testUTCID02_Update_categoryNameNull() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setCategoryName(null);

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID03 - Update: categoryName empty
  @Test
  void testUTCID03_Update_categoryNameEmpty() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setCategoryName("");

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID04 - Update: categoryName greater than 100 characters
  @Test
  void testUTCID04_Update_categoryNameLong() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setCategoryName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID05 - Update: categoryName duplicate
  @Test
  void testUTCID05_Update_categoryNameDuplicate() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    ProductCategory secondcategory =
        new ProductCategory()
            .setCategoryName("Valid Category Name 123123")
            .setCategoryDescription("Valid Category Description")
            .setTaxRate(BigDecimal.ZERO);
    ProductCategory returnValue = productCategoryService.create(secondcategory);
    returnValue.setCategoryName("Valid Category Name");

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(returnValue));
  }

  // UTCID06 - Update: categoryDescription greater than 1000 characters
  @Test
  void testUTCID06_Update_categoryDescriptionLong() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setCategoryDescription("A".repeat(1001));

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID07 - Update: taxRate negative number
  @Test
  void testUTCID07_Update_taxRateNegative() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setTaxRate(BigDecimal.valueOf(-1));

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID08 - Update: taxRate greater than 100
  @Test
  void testUTCID08_Update_taxRateExcessive() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setTaxRate(BigDecimal.valueOf(101));

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID09 - Update: id null
  @Test
  void testUTCID09_Update_idNull() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setId(null);

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // UTCID010 - Update: id not exist
  @Test
  void testUTCID010_Update_idNotExist() {
    productCategoryRepository.deleteAll();
    ProductCategory category = createValidCategory();
    productCategoryService.create(category);
    category.setId(1L);

    assertThrows(HrmCommonException.class, () -> productCategoryService.update(category));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    ProductCategory category = createValidCategory();
    ProductCategory category1 = productCategoryService.create(category);
    productCategoryService.delete(category1.getId());
    assertEquals(productCategoryService.getById(category1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> productCategoryService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    productCategoryRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> productCategoryService.delete(nonExistingId));
  }
}
