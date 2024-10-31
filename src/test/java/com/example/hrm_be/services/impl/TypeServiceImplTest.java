package com.example.hrm_be.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.repositories.ProductTypeRepository;
import com.example.hrm_be.services.ProductTypeService;
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
@Import(ProductTypeServiceImpl.class)
@Transactional
public class TypeServiceImplTest {

  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.postgreSQLContainer;

  @Autowired private ProductTypeService productTypeService;
  @Autowired private ProductTypeRepository productTypeRepository;

  // Helper to create a valid Type entity
  private ProductType createValidType() {
    return new ProductType()
        .setTypeName("Valid Type Name")
        .setTypeDescription("Valid Type Description");
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    ProductType Type = createValidType();
    ProductType Type1 = productTypeService.create(Type);
    ProductType Type2 = productTypeService.getById(Type1.getId());
    assertEquals(Type1, Type2);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> productTypeService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    productTypeRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, productTypeService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    ProductType Type = createValidType();
    productTypeRepository.deleteAll();
    ProductType savedType = productTypeService.create(Type);
    assertThat(savedType).isNotNull();

    Page<ProductType> result = productTypeService.getByPaging(0, 1, "typeName", "a");
    assertEquals(1, result.getTotalElements());
    assertEquals(Type.getTypeName(), result.getContent().get(0).getTypeName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productTypeService.getByPaging(-1, 1, "typeName", "a");
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
              productTypeService.getByPaging(0, 0, "typeName", "a");
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
              productTypeService.getByPaging(0, 1, "a", "a");
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    ProductType type = createValidType();
    ProductType savedType = productTypeService.create(type);

    assertThat(savedType).isNotNull();
    assertThat(savedType.getTypeName()).isEqualTo("Valid Type Name");
  }

  // UTCID02 - create: TypeName null
  @Test
  void testUTCID02_Create_TypeNameNull() {
    ProductType type = createValidType();
    type.setTypeName(null);

    assertThrows(HrmCommonException.class, () -> productTypeService.create(type));
  }

  // UTCID03 - create: TypeName empty
  @Test
  void testUTCID03_Create_TypeNameEmpty() {
    ProductType type = createValidType();
    type.setTypeName("");

    assertThrows(HrmCommonException.class, () -> productTypeService.create(type));
  }

  // UTCID04 - create: TypeName greater than 100 characters
  @Test
  void testUTCID04_Create_TypeNameLong() {
    ProductType type = createValidType();
    type.setTypeName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> productTypeService.create(type));
  }

  // UTCID05 - create: TypeName duplicate
  @Test
  void testUTCID05_Create_TypeNameDuplicate() {
    ProductType type = createValidType();
    productTypeService.create(type);
    ProductType duplicateType =
        new ProductType()
            .setTypeName("Valid Type Name")
            .setTypeDescription("Valid Type Description");

    assertThrows(HrmCommonException.class, () -> productTypeService.create(duplicateType));
  }

  // UTCID06 - create: TypeDescription greater than 500 characters
  @Test
  void testUTCID06_Create_TypeDescriptionLong() {
    ProductType type = createValidType();
    type.setTypeDescription("A".repeat(501));

    assertThrows(HrmCommonException.class, () -> productTypeService.create(type));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    ProductType savedType = productTypeService.create(type);
    ProductType updateType = productTypeService.update(savedType);

    assertThat(savedType).isNotNull();
    assertThat(updateType.getTypeName()).isEqualTo("Valid Type Name");
  }

  // UTCID02 - UPDATE: typeName null
  @Test
  void testUTCID02_Update_typeNameNull() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setTypeName(null);

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // UTCID03 - Update: typeName empty
  @Test
  void testUTCID03_Update_TypeNameEmpty() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setTypeName("");

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // UTCID04 - Update: typeName greater than 100 characters
  @Test
  void testUTCID04_Update_typeNameLong() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setTypeName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // UTCID05 - Update: typeName duplicate
  @Test
  void testUTCID05_Update_typeNameDuplicate() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    ProductType secondType =
        new ProductType()
            .setTypeName("Valid Type Name 123123")
            .setTypeDescription("Valid Type Description");
    ProductType returnValue = productTypeService.create(secondType);
    returnValue.setTypeName("Valid Type Name");

    assertThrows(HrmCommonException.class, () -> productTypeService.update(returnValue));
  }

  // UTCID06 - Update: TypeDescription greater than 500 characters
  @Test
  void testUTCID06_Update_typeDescriptionLong() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setTypeDescription("A".repeat(501));

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // UTCID07 - Update: id null
  @Test
  void testUTCID07_Update_idNull() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setId(null);

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // UTCID08 - Update: id not exist
  @Test
  void testUTCID08_Update_idNotExist() {
    productTypeRepository.deleteAll();
    ProductType type = createValidType();
    productTypeService.create(type);
    type.setId(1L);

    assertThrows(HrmCommonException.class, () -> productTypeService.update(type));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    ProductType type = createValidType();
    ProductType type1 = productTypeService.create(type);
    productTypeService.delete(type1.getId());
    assertEquals(productTypeService.getById(type1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> productTypeService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    productTypeRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> productTypeService.delete(nonExistingId));
  }
}
