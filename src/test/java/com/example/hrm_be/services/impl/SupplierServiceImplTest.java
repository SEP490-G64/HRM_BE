package com.example.hrm_be.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.repositories.SupplierRepository;
import com.example.hrm_be.services.SupplierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(SupplierServiceImpl.class)
@Transactional
public class SupplierServiceImplTest {

  @Autowired private SupplierService supplierService;
  @Autowired private SupplierRepository supplierRepository;

  // Helper to create a valid Supplier entity
  private Supplier createValidSupplier() {
    return new Supplier()
            .setSupplierName("Valid Supplier Name")
            .setAddress("Valid Supplier Address")
            .setPhoneNumber("0912345678")
            .setTaxCode("1234567890")
            .setPhoneNumber("0912345678")
            .setFaxNumber("012-1234567")
            .setStatus(true);
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier Supplier2 = supplierService.getById(Supplier1.getId());
    assertEquals(Supplier1, Supplier2);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> supplierService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    supplierRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, supplierService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Supplier Supplier = createValidSupplier();
    supplierRepository.deleteAll();
    Supplier savedSupplier = supplierService.create(Supplier);
    assertThat(savedSupplier).isNotNull();

    Page<Supplier> result = supplierService.getByPaging(0, 1, "supplierName", "a", true);
    assertEquals(1, result.getTotalElements());
    assertEquals(Supplier.getSupplierName(), result.getContent().get(0).getSupplierName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
            assertThrows(
                    HrmCommonException.class,
                    () -> {
                      supplierService.getByPaging(-1, 1, "supplierName", "a", true);
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
                      supplierService.getByPaging(0, 0, "supplierName", "a", true);
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
                      supplierService.getByPaging(0, 1, "a", "a", true);
                    });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    Supplier Supplier = createValidSupplier();
    Supplier savedSupplier = supplierService.create(Supplier);

    assertThat(savedSupplier).isNotNull();
    assertThat(savedSupplier.getSupplierName()).isEqualTo("Valid Supplier Name");
  }

  // UTCID02 - create: SupplierName null
  @Test
  void testUTCID02_Create_SupplierNameNull() {
    Supplier Supplier = createValidSupplier();
    Supplier.setSupplierName(null);

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID03 - create: SupplierName empty
  @Test
  void testUTCID03_Create_SupplierNameEmpty() {
    Supplier Supplier = createValidSupplier();
    Supplier.setSupplierName("");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID04 - create: SupplierName greater than 100 characters
  @Test
  void testUTCID04_Create_SupplierNameLong() {
    Supplier Supplier = createValidSupplier();
    Supplier.setSupplierName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID05 - create: SupplierName and Address duplicate
  @Test
  void testUTCID05_Create_SupplierNameAndAddressDuplicate() {
    Supplier Supplier = createValidSupplier();
    supplierService.create(Supplier);
    Supplier duplicateSupplierName =
            new Supplier()
                    .setSupplierName("Valid Supplier Name")
                    .setAddress("Valid Supplier Address")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567891")
                    .setPhoneNumber("0912345678")
                    .setFaxNumber("012-1234567")
                    .setStatus(true);

    assertThrows(HrmCommonException.class, () -> supplierService.create(duplicateSupplierName));
  }

  // UTCID06 - create: address null
  @Test
  void testUTCID06_Create_addressNull() {
    Supplier Supplier = createValidSupplier();
    Supplier.setAddress(null);

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID07 - create: address empty
  @Test
  void testUTCID07_Create_addressEmpty() {
    Supplier Supplier = createValidSupplier();
    Supplier.setAddress("");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID08 - create: address greater than 255 characters
  @Test
  void testUTCID08_Create_addressLong() {
    Supplier Supplier = createValidSupplier();
    Supplier.setAddress("A".repeat(256));

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID09 - create: email not match regex
  @Test
  void testUTCID09_Create_emailInvalidFormat() {
    Supplier Supplier = createValidSupplier();
    Supplier.setEmail("Email");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID010 - create: phoneNumber null
  @Test
  void testUTCID010_Create_phoneNumber() {
    Supplier Supplier = createValidSupplier();
    Supplier.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID011 - create: phoneNumber empty
  @Test
  void testUTCID011_Create_phoneNumberEmpty() {
    Supplier Supplier = createValidSupplier();
    Supplier.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID012 - create: phoneNumber not match regex
  @Test
  void testUTCID012_Create_phoneNumberInvalidFormat() {
    Supplier Supplier = createValidSupplier();
    Supplier.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID013 - create: taxCode not match tax code regex
  @Test
  void testUTCID013_Create_taxCodeInvalidFormat() {
    Supplier Supplier = createValidSupplier();
    Supplier.setTaxCode("a");

    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID014 - create: taxCode duplicate
  @Test
  void testUTCID014_Create_taxCodeDuplicate() {
    Supplier Supplier = createValidSupplier();
    supplierService.create(Supplier);
    Supplier duplicateAddressSupplier =
            new Supplier()
                    .setSupplierName("Valid Supplier Name 1")
                    .setAddress("Valid Supplier Address 1")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567890")
                    .setPhoneNumber("0912345678")
                    .setFaxNumber("012-1234567")
                    .setStatus(true);
    assertThrows(HrmCommonException.class, () -> supplierService.create(duplicateAddressSupplier));
  }

  // UTCID015 - create: faxNumber not match regex
  @Test
  void testUTCID015_Create_faxNumberInvalidFormat() {
    Supplier Supplier = createValidSupplier();
    Supplier.setFaxNumber("a");
    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UTCID016 - create: status null
  @Test
  void testUTCID016_Create_statusNull() {
    Supplier Supplier = createValidSupplier();
    Supplier.setStatus(null);
    assertThrows(HrmCommonException.class, () -> supplierService.create(Supplier));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier savedSupplier = supplierService.create(Supplier);
    Supplier updateSupplier = supplierService.update(savedSupplier);

    assertThat(updateSupplier).isNotNull();
    assertThat(updateSupplier.getSupplierName()).isEqualTo("Valid Supplier Name");
  }

  // UTCID02 - UPDATE: SupplierName null
  @Test
  void testUTCID02_Update_SupplierNameNull() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setSupplierName(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID03 - Update: SupplierName empty
  @Test
  void testUTCID03_Update_SupplierNameEmpty() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setSupplierName("");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID04 - Update: SupplierName greater than 100 characters
  @Test
  void testUTCID04_Update_SupplierNameLong() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setSupplierName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID05 - Update: SupplierName and Address duplicate
  @Test
  void testUTCID05_Update_SupplierNameAndAddressDuplicate() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    supplierService.create(Supplier);
    Supplier secondSupplier =
            new Supplier()
                    .setSupplierName("Valid Supplier Name 1")
                    .setAddress("Valid Supplier Address 1")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567891")
                    .setPhoneNumber("0912345678")
                    .setFaxNumber("012-1234567")
                    .setStatus(true);
    Supplier returnValue = supplierService.create(secondSupplier);
    returnValue.setSupplierName("Valid Supplier Name");
    returnValue.setAddress("Valid Supplier Address");

    assertThrows(HrmCommonException.class, () -> supplierService.update(returnValue));
  }

  // UTCID06 - Update: address null
  @Test
  void testUTCID06_Update_addressNull() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setAddress(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID07 - Update: address empty
  @Test
  void testUTCID07_Update_addressEmpty() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setAddress("");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID08 - Update: address greater than 255 characters
  @Test
  void testUTCID08_Update_addressLong() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setAddress("A".repeat(256));

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID09 - Update: email not match regex
  @Test
  void testUTCID09_Update_emailInvalidFormat() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setEmail("Email");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID010 - Update: phoneNumber null
  @Test
  void testUTCID010_Update_phoneNumberNull() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID011 - Update: phoneNumber empty
  @Test
  void testUTCID011_Update_phoneNumberEmpty() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID012 - Update: phoneNumber not match regex
  @Test
  void testUTCID012_Update_phoneNumberInvalidFormat() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID013 - update: taxCode not match tax code regex
  @Test
  void testUTCID013_Update_taxCodeInvalidFormat() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setTaxCode("a");

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID014 - update: taxCode duplicate
  @Test
  void testUTCID014_Update_taxCodeDuplicate() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    supplierService.create(Supplier);
    Supplier secondSupplier =
            new Supplier()
                    .setSupplierName("Valid Supplier Name 1")
                    .setAddress("Valid Supplier Address 1")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567891")
                    .setPhoneNumber("0912345678")
                    .setFaxNumber("012-1234567")
                    .setStatus(true);
    Supplier returnValue = supplierService.create(secondSupplier);
    returnValue.setTaxCode("1234567890");

    assertThrows(HrmCommonException.class, () -> supplierService.update(secondSupplier));
  }

  // UTCID015 - update: faxNumber not match regex
  @Test
  void testUTCID015_Update_faxNumberInvalidFormat() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setFaxNumber("a");
    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID016 - update: status null
  @Test
  void testUTCID016_Update_statusNull() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setStatus(null);
    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID017 - Update: id null
  @Test
  void testUTCID017_Update_idNull() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    Supplier1.setId(null);

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier1));
  }

  // UTCID018 - Update: id not exist
  @Test
  void testUTCID018_Update_idNotExist() {
    supplierRepository.deleteAll();
    Supplier Supplier = createValidSupplier();
    Supplier.setId(1L);

    assertThrows(HrmCommonException.class, () -> supplierService.update(Supplier));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    Supplier Supplier = createValidSupplier();
    Supplier Supplier1 = supplierService.create(Supplier);
    supplierService.delete(Supplier1.getId());
    assertEquals(supplierService.getById(Supplier1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> supplierService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    supplierRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> supplierService.delete(nonExistingId));
  }
}
