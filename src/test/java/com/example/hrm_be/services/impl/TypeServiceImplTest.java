package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hrm_be.components.ProductTypeMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import com.example.hrm_be.repositories.ProductTypeRepository;
import com.example.hrm_be.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TypeServiceImplTest {
  @Mock
  private ProductService productService;
  @Mock private ProductTypeMapper typeMapper;
  @Mock private ProductTypeRepository typeRepository;
  @InjectMocks private ProductTypeServiceImpl typeService;

  private ProductType type;
  private ProductTypeEntity typeEntity;

  @BeforeEach
  public void setup() {
    type =
        ProductType.builder()
            .typeName("Valid Type Name")
            .typeDescription("Valid Type Description")
            .build();

    typeEntity =
        ProductTypeEntity.builder()
            .typeName("Valid Type Name")
            .typeDescription("Valid Type Description")
            .build();
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_idValid() {
    type.setId(1L);
    typeEntity.setId(1L);
    when(typeRepository.findById(type.getId())).thenReturn(Optional.of(typeEntity));
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);
    ProductType result = typeService.getById(typeEntity.getId());
    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> typeService.getById(id));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(typeRepository.findById(id)).thenReturn(Optional.empty());
    Assertions.assertNull(typeService.getById(id));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("typeName").ascending());
    List<ProductTypeEntity> types = Collections.singletonList(typeEntity);
    Page<ProductTypeEntity> page = new PageImpl<>(types, pageable, types.size());

    when(typeRepository.findByTypeNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    Page<ProductType> result = typeService.getByPaging(0, 10, "typeName", "");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(HrmCommonException.class, () -> typeService.getByPaging(-1, 10, "typeName", ""));
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () -> typeService.getByPaging(0, 0, "typeName", ""));
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    assertThrows(HrmCommonException.class, () -> typeService.getByPaging(0, 0, "a", ""));
  }

  // UTCID05 - getByPaging: All valid
  @Test
  void testUTCID05_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    List<ProductTypeEntity> types = Collections.singletonList(typeEntity);
    Page<ProductTypeEntity> page = new PageImpl<>(types, pageable, types.size());

    when(typeRepository.findByTypeNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    Page<ProductType> result = typeService.getByPaging(0, 10, null, "");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID06 - getByPaging: All valid
  @Test
  void testUTCID06_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
    List<ProductTypeEntity> types = Collections.singletonList(typeEntity);
    Page<ProductTypeEntity> page = new PageImpl<>(types, pageable, types.size());

    when(typeRepository.findByTypeNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    Page<ProductType> result = typeService.getByPaging(0, 10, null, null);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID06 - getByPaging: All valid
  @Test
  void testUTCID07_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("typeDescription").ascending());
    List<ProductTypeEntity> types = Collections.singletonList(typeEntity);
    Page<ProductTypeEntity> page = new PageImpl<>(types, pageable, types.size());

    when(typeRepository.findByTypeNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    Page<ProductType> result = typeService.getByPaging(0, 10, "typeDescription", null);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    when(typeMapper.toEntity(type)).thenReturn(typeEntity);
    when(typeRepository.save(typeEntity)).thenReturn(typeEntity);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    ProductType result = typeService.create(type);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - create: TypeName null
  @Test
  void testUTCID02_Create_nameNull() {
    type.setTypeName(null);
    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UTCID03 - create: TypeName empty
  @Test
  void testUTCID03_Create_nameEmpty() {
    type.setTypeName("");
    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UTCID04 - create: TypeName greater than 100 characters
  @Test
  void testUTCID04_Create_nameLong() {
    type.setTypeName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UTCID05 - create: TypeName duplicate
  @Test
  void testUTCID05_Create_TypeNameDuplicate() {
    type.setTypeName("Duplicate");
    when(typeRepository.existsByTypeName(type.getTypeName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UTCID06 - create: TypeDescription greater than 500 characters
  @Test
  void testUTCID06_Create_TypeDescriptionLong() {
    type.setTypeDescription("A".repeat(501));

    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UTCID07 - create: Type Null
  @Test
  void testUTCID07_Create_TypeNull() {
    type = null;

    assertThrows(HrmCommonException.class, () -> typeService.create(type));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    type.setId(1L);
    typeEntity.setId(1L);

    when(typeRepository.findById(type.getId())).thenReturn(Optional.of(typeEntity));
    when(typeRepository.save(typeEntity)).thenReturn(typeEntity);
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);

    ProductType result = typeService.update(type);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - Update: TypeName null
  @Test
  void testUTCID02_Update_nameNull() {
    type.setTypeName(null);
    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // UTCID03 - Update: TypeName empty
  @Test
  void testUTCID03_Update_nameEmpty() {
    type.setTypeName("");
    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // UTCID04 - Update: TypeName greater than 100 characters
  @Test
  void testUTCID04_Update_nameLong() {
    type.setTypeName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // UTCID05 - Update: TypeName duplicate
  @Test
  void testUTCID05_Update_nameDuplicate() {
    type.setTypeName("new Name");
    typeEntity.setTypeName("Old Name");

    lenient().when(typeRepository.existsByTypeName(type.getTypeName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // UTCID06 - Update: TypeDescription greater than 500 characters
  @Test
  void testUTCID06_Update_typeDescriptionLong() {
    type.setTypeDescription("A".repeat(501));

    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // UTCID07 - Update: id null
  @Test
  void testUTCID07_Update_idNull() {
    type.setId(null);
    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  @Test
  void testUTCID08_Update_idNotExist() {
    type.setId(1L);

    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  @Test
  void testUTCID09_Update_typeNull() {
    type = null;

    assertThrows(HrmCommonException.class, () -> typeService.update(type));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    type.setId(1L);
    when(typeService.existById(type.getId())).thenReturn(true);
    typeService.delete(type.getId());
    verify(productService, times(1)).removeTypeFromProducts(type.getId()); // Ensure product removal was called
    verify(typeRepository, times(1)).deleteById(type.getId()); // Ensure the repository delete method was called
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    type.setId(null);
    assertThrows(HrmCommonException.class, () -> typeService.delete(type.getId()));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    type.setId(1L);
    when(typeService.existById(type.getId())).thenReturn(false);
    assertThrows(HrmCommonException.class, () -> typeService.delete(type.getId()));
  }

  // existById
  // UTCID01 - existById: valid
  @Test
  void testUTCID01_ExistById_AllValid() {
    type.setId(1L);
    boolean result = typeService.existById(type.getId());
    Assertions.assertNotNull(result);
  }

  // existById
  // UTCID01 - existById: invalid
  @Test
  void testUTCID02_ExistById_InValid() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> typeService.existById(id));
  }

  // getAll
  // UTCID01 - getAll: valid
  @Test
  void testUTCID01_getAll_AllValid() {
    when(typeRepository.findAll()).thenReturn(List.of(typeEntity));
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);
    List<ProductType> result = typeService.getAll();
    Assertions.assertNotNull(result);
  }

  // getByName
  // UTCID01 - getByName: valid
  @Test
  void testUTCID01_getByName_AllValid() {
    String name = "Valid Type Name";
    when(typeRepository.findByTypeName(name)).thenReturn(Optional.of(typeEntity));
    when(typeMapper.toDTO(typeEntity)).thenReturn(type);
    ProductType result = typeService.getByName(name);
    Assertions.assertNotNull(result);
  }
}
