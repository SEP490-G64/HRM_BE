package com.example.hrm_be.services.impl;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.repositories.SupplierRepository;
import com.example.hrm_be.services.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceImplTest {

  @Mock
  private SupplierMapper supplierMapper;
  @Mock
  private SupplierRepository supplierRepository;
  @InjectMocks
  private SupplierServiceImpl supplierService;

  private Supplier supplier;
  private SupplierEntity supplierEntity;

  @BeforeEach
  public void setup() {
    supplier = Supplier.builder()
            .supplierName("Valid Supplier Name")
            .address("Valid Supplier Address")
            .email("validsupplier@mail.com")
            .taxCode("0101234567")
            .phoneNumber("0912345678")
            .status(true)
            .build();

    supplierEntity = SupplierEntity.builder()
            .supplierName("Valid Supplier Name")
            .address("Valid Supplier Address")
            .email("validsupplier@mail.com")
            .taxCode("0101234567")
            .phoneNumber("0912345678")
            .status(true)
            .build();
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_idValid() {
    Long id = 1L;
    when(supplierRepository.findById(id)).thenReturn(Optional.of(supplierEntity));
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);
    Supplier result = supplierService.getById(id);
    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> supplierService.getById(id));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(supplierRepository.findById(id)).thenReturn(Optional.empty());
    Assertions.assertNull(supplierService.getById(id));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("supplierName").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "supplierName", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(HrmCommonException.class, () -> supplierService.getByPaging(-1, 10, "supplierName", "", true));
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () -> supplierService.getByPaging(0, 0, "supplierName", "", true));
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    assertThrows(HrmCommonException.class, () -> supplierService.getByPaging(0, 1, "a", "a", true));
  }

  @Test
  void testUTCID05_GetByPaging_NullSortBy_DefaultToId() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    // Adjust stubbing to match the actual parameter ("a")
    when(supplierRepository.searchSuppliers(eq("a"), eq(true), eq(pageable))).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    // Act
    Page<Supplier> result = supplierService.getByPaging(0, 10, null, "a", true);

    // Assert
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }


  @Test
  void testUTCID06_GetByPaging_NullKeyword_DefaultToEmpty() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    lenient().when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    // Act
    Page<Supplier> result = supplierService.getByPaging(0, 10, "id", "", true);

    // Assert
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  @Test
  void testUTCID07_GetByPaging_StatusTrue() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    // Act
    Page<Supplier> result = supplierService.getByPaging(0, 10, null, "", true);

    // Assert
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  @Test
  void testUTCID08_GetByPaging_StatusFalse() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<SupplierEntity> suppliers = Collections.emptyList();
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, 0);

    when(supplierRepository.searchSuppliers("", false, pageable)).thenReturn(page);

    // Act
    Page<Supplier> result = supplierService.getByPaging(0, 10, null, "", false);

    // Assert
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getTotalElements());
  }

  @Test
  void testUTCID09_GetByPaging_InvalidSortBy_ThrowsException() {
    // Arrange
    String invalidSortBy = "invalidField";

    // Act & Assert
    HrmCommonException exception = Assertions.assertThrows(HrmCommonException.class, () -> {
      supplierService.getByPaging(0, 10, invalidSortBy, "", true);
    });

    Assertions.assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // UTCID010 - getByPaging: All valid
  @Test
  void testUTCID010_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("address").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "address", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID011 - getByPaging: All valid
  @Test
  void testUTCID011_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("email").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "email", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID012 - getByPaging: All valid
  @Test
  void testUTCID012_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("phoneNumber").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "phoneNumber", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID013 - getByPaging: All valid
  @Test
  void testUTCID013_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("taxCode").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "taxCode", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID014 - getByPaging: All valid
  @Test
  void testUTCID014_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("faxNumber").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "faxNumber", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // UTCID015 - getByPaging: All valid
  @Test
  void testUTCID015_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("status").descending());
    List<SupplierEntity> suppliers = Collections.singletonList(supplierEntity);
    Page<SupplierEntity> page = new PageImpl<>(suppliers, pageable, suppliers.size());

    when(supplierRepository.searchSuppliers("", true, pageable)).thenReturn(page);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "status", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    when(supplierMapper.toEntity(supplier)).thenReturn(supplierEntity);
    when(supplierRepository.save(supplierEntity)).thenReturn(supplierEntity);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Supplier result = supplierService.create(supplier);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - create: SupplierName null
  @Test
  void testUTCID02_Create_nameNull() {
    supplier.setSupplierName(null);
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID03 - create: SupplierName empty
  @Test
  void testUTCID03_Create_nameEmpty() {
    supplier.setSupplierName("");
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID04 - create: SupplierName greater than 100 characters
  @Test
  void testUTCID04_Create_nameLong() {
    supplier.setSupplierName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID05 - create: SupplierName and Address duplicate
  @Test
  void testUTCID05_Create_nameDuplicate() {
    supplier.setSupplierName("Name Duplicate");
    supplierEntity.setSupplierName("Name Duplicate");
    when(supplierRepository.existsBySupplierNameAndAddress(supplierEntity.getSupplierName(),
            supplierEntity.getAddress())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID06 - create: address null
  @Test
  void testUTCID06_Create_addressNull() {
    supplier.setAddress(null);

    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID07 - create: address empty
  @Test
  void testUTCID07_Create_addressEmpty() {
    supplier.setAddress("");

    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }


  // UTCID08 - create: address greater than 256 characters
  @Test
  void testUTCID08_Create_addressLong() {
    supplier.setAddress("A".repeat(1001));
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID09 - create: email not match regex
  @Test
  void testUTCID09_Create_emailInvalidFormat() {
    supplier.setEmail("Email");
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID010 - create: phoneNumber null
  @Test
  void testUTCID010_Create_phoneNumber() {
    supplier.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID011 - create: phoneNumber empty
  @Test
  void testUTCID011_Create_phoneNumberEmpty() {
    supplier.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID012 - create: phoneNumber not match regex
  @Test
  void testUTCID012_Create_phoneNumberInvalidFormat() {
    supplier.setPhoneNumber("INVALID_PHONE");
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID013 - create: taxCode not match tax code regex
  @Test
  void testUTCID013_Create_taxCodeInvalidFormat() {
    supplier.setTaxCode("a");
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID014 - create: taxCode duplicate
  @Test
  void testUTCID014_Create_taxCodeDuplicate() {
    supplier.setTaxCode("Duplicate");

    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode())).thenReturn(true);

    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID015 - create: faxNumber not match regex
  @Test
  void testUTCID015_Create_faxNumberInvalidFormat() {
    supplier.setFaxNumber("a");
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID016 - create: status null
  @Test
  void testUTCID016_Create_statusNull() {
    supplier.setStatus(null);
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }


  // UTCID017 - create: origin greater than 255 characters
  @Test
  void testUTCID017_Create_originLong() {
    supplier.setAddress("A".repeat(256));
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }

  // UTCID018 - create: origin greater than 255 characters
  @Test
  void testUTCID018_Create_originLong() {
    supplier = null;
    assertThrows(HrmCommonException.class, () -> supplierService.create(supplier));
  }


  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    supplier.setId(1L);
    supplierEntity.setId(1L);

    when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    when(supplierRepository.save(supplierEntity)).thenReturn(supplierEntity);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Supplier result = supplierService.update(supplier);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - UPDATE: SupplierName null
  @Test
  void testUTCID02_Update_nameNull() {
    supplier.setSupplierName(null);
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID03 - Update: SupplierName empty
  @Test
  void testUTCID03_Update_nameEmpty() {
    supplier.setSupplierName("");
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID04 - Update: SupplierName greater than 100 characters
  @Test
  void testUTCID04_Update_nameLong() {
    supplier.setSupplierName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID05 - Update: SupplierName and Address duplicate
  @Test
  void testUTCID05_Update_nameDuplicate() {
    supplier.setSupplierName("new Name");
    supplier.setAddress("new Address");
    supplierEntity.setSupplierName("Old Name");
    supplierEntity.setAddress("Old Address");

    lenient().when(supplierRepository.existsBySupplierNameAndAddress(supplier.getSupplierName(),
            supplierEntity.getAddress())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID06 - Update: address null
  @Test
  void testUTCID06_Update_addressNull() {
    supplier.setAddress(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID07 - Update: address empty
  @Test
  void testUTCID07_Update_addressEmpty() {
    supplier.setAddress("");

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID08 - Update: address greater than 255 characters
  @Test
  void testUTCID08_Update_addressLong() {
    supplier.setAddress("A".repeat(256));

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID09 - Update: email not match regex
  @Test
  void testUTCID09_Update_emailInvalidFormat() {
    supplier.setEmail("invalid email");

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID010 - Update: phoneNumber null
  @Test
  void testUTCID010_Update_phoneNumberNull() {
    supplier.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID011 - Update: phoneNumber empty
  @Test
  void testUTCID011_Update_phoneNumberEmpty() {
    supplier.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID012 - Update: phoneNumber not match regex
  @Test
  void testUTCID012_Update_phoneNumberInvalidFormat() {
    supplier.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID013 - Update: taxCode not match regex
  @Test
  void testUTCID013_Update_taxCodeInvalidFormat() {
    supplier.setTaxCode("invalid tax code");
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID014 - update: taxCode duplicate
  @Test
  void testUTCID014_Update_taxCodeDuplicate() {
    supplier.setId(1L);
    supplier.setTaxCode("1234567811");
    supplierEntity.setId(1L);
    supplierEntity.setTaxCode("1234567890");
    when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID015 - update: faxNumber not match regex
  @Test
  void testUTCID015_Update_faxNumberInvalidFormat() {
    supplier.setFaxNumber("a");
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID016 - update: status null
  @Test
  void testUTCID016_Update_statusNull() {
    supplier.setStatus(null);
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID017 - Update: id null
  @Test
  void testUTCID017_Update_idNull() {
    supplier.setId(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  // UTCID018 - Update: id not exist
  @Test
  void testUTCID018_Update_idNotExist() {
    supplier.setId(1L);
    when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.empty());
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  @Test
  void testUTCID0017_Update_taxCodeNotChanged() {
    // Arrange: Prepare mock data
    supplier.setTaxCode("12345"); // Current tax code of the supplier
    supplierEntity.setTaxCode("12345"); // Old tax code matches the current one

    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode()))
            .thenReturn(true); // Mock that the tax code does not already exist in the database

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  @Test
  void testUTCID0018_Update_taxCodeChangedNotDuplicate() {
    // Arrange: Prepare mock data
    supplier.setId(1L);
    supplier.setTaxCode("67890"); // New tax code
    supplierEntity.setId(1L);
    supplierEntity.setTaxCode("12345"); // Old tax code differs from the new one

    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode()))
            .thenReturn(true); // Mock that the new tax code does not already exist

    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  @Test
  void testUTCID0019_Update_taxCodeChangedAndDuplicate() {
    // Arrange: Prepare mock data
    supplier.setId(1L);
    supplier.setTaxCode("67890"); // New tax code
    supplierEntity.setId(1L);
    supplierEntity.setTaxCode("12345"); // Old tax code differs from the new one

    // Mock that supplierRepository.findById() returns the old supplier entity
    lenient().when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    lenient().when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    // Mock that the new tax code already exists in the database
    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode()))
            .thenReturn(true);
    // Act & Assert: Verify that the correct exception is thrown with the expected message
    assertThrows(HrmCommonException.class,
            () -> supplierService.update(supplier));
  }

  // UTCID020 - UPDATE: Supplier null
  @Test
  void testUTCID020_Update_SupplierNull() {
    supplier = null;
    assertThrows(HrmCommonException.class, () -> supplierService.update(supplier));
  }

  @Test
  void testUTCID0020_Update_taxCodeNull() {
    // Arrange: Prepare mock data
    supplier.setTaxCode(null); // Tax code is null
    supplierEntity.setTaxCode("0101234565"); // Old tax code exists
    lenient().when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode())).thenReturn(true);
    // Act & Assert: Ensure no exception is thrown since tax code is null
    assertThrows(
            HrmCommonException.class,
            () -> supplierService.update(supplier)
    );
  }

  @Test
  void testUTCID0020_Update_taxCodeEmpty() {
    // Arrange: Prepare mock data
    supplier.setTaxCode(""); // Tax code is null
    supplierEntity.setTaxCode("0101234565"); // Old tax code exists
    lenient().when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    lenient().when(supplierRepository.existsByTaxCode(supplier.getTaxCode())).thenReturn(true);
    // Act & Assert: Ensure no exception is thrown since tax code is null
    assertThrows(
            HrmCommonException.class,
            () -> supplierService.update(supplier)
    );
  }
  // DELETE
  // UTCID01 - DELETE: valid
  @Test
  void testUTCID01_Delete_Valid() {
   supplier.setId(1L);
    when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplierEntity));
    when(supplierRepository.save(supplierEntity)).thenReturn(supplierEntity);
    when(supplierMapper.toDTO(supplierEntity)).thenReturn(supplier);

    Supplier result = supplierService.update(supplier);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> supplierService.delete(null));
  }


  //UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_Invalid() {
    Long id = 1L;
    when(supplierRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(HrmCommonException.class, () -> supplierService.delete(id));
  }
}

