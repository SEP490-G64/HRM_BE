package com.example.hrm_be.services.impl;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.repositories.ManufacturerRepository;
import com.example.hrm_be.services.ManufacturerService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(ManufacturerServiceImpl.class)
@Transactional
public class ManufacturerServiceImplTest {

  @Autowired private ManufacturerService manufacturerService;
  @Autowired private ManufacturerRepository manufacturerRepository;

  // Helper to create a valid Manufacturer entity
  private Manufacturer createValidManufacturer() {
    return new Manufacturer()
        .setManufacturerName("Valid Manufacturer Name")
        .setAddress("Valid Manufacturer Address")
        .setPhoneNumber("0912345678")
        .setTaxCode("1234567890")
        .setPhoneNumber("0912345678")
        .setOrigin("Valid Origin")
        .setStatus(true);
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer Manufacturer1 = manufacturerService.create(Manufacturer);
    Manufacturer Manufacturer2 = manufacturerService.getById(Manufacturer1.getId());
    assertEquals(Manufacturer1, Manufacturer2);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> manufacturerService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    manufacturerRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, manufacturerService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerRepository.deleteAll();
    Manufacturer savedManufacturer = manufacturerService.create(Manufacturer);
    assertThat(savedManufacturer).isNotNull();

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 1, "manufacturerName", "a", true);
    assertEquals(1, result.getTotalElements());
    assertEquals(Manufacturer.getManufacturerName(), result.getContent().get(0).getManufacturerName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              manufacturerService.getByPaging(-1, 1, "manufacturerName", "a", true);
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
              manufacturerService.getByPaging(0, 0, "manufacturerName", "a", true);
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
              manufacturerService.getByPaging(0, 1, "a", "a", true);
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer savedManufacturer = manufacturerService.create(Manufacturer);

    assertThat(savedManufacturer).isNotNull();
    assertThat(savedManufacturer.getManufacturerName()).isEqualTo("Valid Manufacturer Name");
  }

  // UTCID02 - create: ManufacturerName null
  @Test
  void testUTCID02_Create_ManufacturerNameNull() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setManufacturerName(null);

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID03 - create: ManufacturerName empty
  @Test
  void testUTCID03_Create_ManufacturerNameEmpty() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setManufacturerName("");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID04 - create: ManufacturerName greater than 100 characters
  @Test
  void testUTCID04_Create_ManufacturerNameLong() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setManufacturerName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID05 - create: ManufacturerName and Address duplicate
  @Test
  void testUTCID05_Create_ManufacturerNameAndAddressDuplicate() {
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer duplicateManufacturerName =
        new Manufacturer()
                .setManufacturerName("Valid Manufacturer Name")
                .setAddress("Valid Manufacturer Address")
                .setPhoneNumber("0912345678")
                .setTaxCode("1234567891")
                .setPhoneNumber("0912345678")
                .setOrigin("Valid Origin")
                .setStatus(true);

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(duplicateManufacturerName));
  }

  // UTCID06 - create: address null
  @Test
  void testUTCID06_Create_addressNull() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setAddress(null);

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID07 - create: address empty
  @Test
  void testUTCID07_Create_addressEmpty() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setAddress("");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID08 - create: address greater than 256 characters
  @Test
  void testUTCID08_Create_addressLong() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setAddress("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID09 - create: email not match regex
  @Test
  void testUTCID09_Create_emailInvalidFormat() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setEmail("Email");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID010 - create: email greater than 256 characters
  @Test
  void testUTCID010_Create_emailLong() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setEmail("A".repeat(245) + "@gmail.com");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID011 - create: phoneNumber not match regex
  @Test
  void testUTCID011_Create_phoneNumberInvalidFormat() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID012 - create: taxCode not match tax code regex
  @Test
  void testUTCID012_Create_taxCodeInvalidFormat() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setTaxCode("a");

    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID013 - create: taxCode duplicate
  @Test
  void testUTCID013_Create_taxCodeDuplicate() {
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer duplicateAddressManufacturer =
            new Manufacturer()
                    .setManufacturerName("Valid Manufacturer Name 1")
                    .setAddress("Valid Manufacturer Address 1")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567890")
                    .setPhoneNumber("0912345678")
                    .setOrigin("Valid Origin")
                    .setStatus(true);
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(duplicateAddressManufacturer));
  }

  // UTCID014 - create: origin greater than 256 characters
  @Test
  void testUTCID014_Create_originLong() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setOrigin("A".repeat(257));
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UTCID015 - create: status null
  @Test
  void testUTCID015_Create_statusNull() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer.setStatus(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(Manufacturer));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer savedManufacturer = manufacturerService.create(Manufacturer);
    Manufacturer updateManufacturer = manufacturerService.update(savedManufacturer);

    assertThat(updateManufacturer).isNotNull();
    assertThat(updateManufacturer.getManufacturerName()).isEqualTo("Valid Manufacturer Name");
  }

  // UTCID02 - UPDATE: ManufacturerName null
  @Test
  void testUTCID02_Update_ManufacturerNameNull() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setManufacturerName(null);

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID03 - Update: ManufacturerName empty
  @Test
  void testUTCID03_Update_ManufacturerNameEmpty() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setManufacturerName("");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID04 - Update: ManufacturerName greater than 100 characters
  @Test
  void testUTCID04_Update_ManufacturerNameLong() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setManufacturerName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID05 - Update: ManufacturerName and Address duplicate
  @Test
  void testUTCID05_Update_ManufacturerNameAndAddressDuplicate() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer secondManufacturer =
        new Manufacturer()
            .setManufacturerName("Valid Manufacturer Name 1")
            .setAddress("Valid Manufacturer Address 1")
            .setPhoneNumber("0912345678")
            .setTaxCode("1234567891")
            .setPhoneNumber("0912345678")
            .setOrigin("Valid Origin")
            .setStatus(true);
    Manufacturer returnValue = manufacturerService.create(secondManufacturer);
    returnValue.setManufacturerName("Valid Manufacturer Name");
    returnValue.setAddress("Valid Manufacturer Address");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(returnValue));
  }

  // UTCID06 - Update: address null
  @Test
  void testUTCID06_Update_addressNull() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setAddress(null);

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID07 - Update: address empty
  @Test
  void testUTCID07_Update_addressEmpty() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setAddress("");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID08 - Update: address greater than 256 characters
  @Test
  void testUTCID08_Update_addressLong() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setAddress("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID09 - Update: email not match regex
  @Test
  void testUTCID09_Update_emailInvalidFormat() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setEmail("Email");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID010 - update: phoneNumber not match regex
  @Test
  void testUTCID010_Update_phoneNumberInvalidFormat() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID011 - Update: phoneNumber greater than 256 characters
  @Test
  void testUTCID011_Update_phoneNumberLong() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setPhoneNumber("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID012 - update: taxCode not match tax code regex
  @Test
  void testUTCID012_Update_taxCodeInvalidFormat() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setTaxCode("a");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID013 - update: taxCode duplicate
  @Test
  void testUTCID013_Update_taxCodeDuplicate() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer secondManufacturer =
            new Manufacturer()
                    .setManufacturerName("Valid Manufacturer Name 1")
                    .setAddress("Valid Manufacturer Address 1")
                    .setPhoneNumber("0912345678")
                    .setTaxCode("1234567891")
                    .setPhoneNumber("0912345678")
                    .setOrigin("Valid Origin")
                    .setStatus(true);
    Manufacturer returnValue = manufacturerService.create(secondManufacturer);
    returnValue.setTaxCode("1234567890");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(secondManufacturer));
  }

  // UTCID014 - update: origin greater than 256 characters
  @Test
  void testUTCID014_Update_originLong() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setOrigin("a");
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID015 - update: status null
  @Test
  void testUTCID015_Update_statusNull() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setStatus(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID016 - Update: id null
  @Test
  void testUTCID016_Update_idNull() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setId(null);

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // UTCID017 - Update: id not exist
  @Test
  void testUTCID017_Update_idNotExist() {
    manufacturerRepository.deleteAll();
    Manufacturer Manufacturer = createValidManufacturer();
    manufacturerService.create(Manufacturer);
    Manufacturer.setId(1L);

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(Manufacturer));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    Manufacturer Manufacturer = createValidManufacturer();
    Manufacturer Manufacturer1 = manufacturerService.create(Manufacturer);
    manufacturerService.delete(Manufacturer1.getId());
    assertEquals(manufacturerService.getById(Manufacturer1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> manufacturerService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    manufacturerRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> manufacturerService.delete(nonExistingId));
  }
}
