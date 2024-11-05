package com.example.hrm_be.services.impl;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.repositories.UnitOfMeasurementRepository;
import com.example.hrm_be.services.UnitOfMeasurementService;
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
@Import(UnitOfMeasurementServiceImpl.class)
@Transactional
public class UnitServiceImplTest {
  @Autowired private UnitOfMeasurementService unitOfMeasurementService;
  @Autowired private UnitOfMeasurementRepository unitOfMeasurementRepository;

  // Helper to create a valid unit entity
  private UnitOfMeasurement createValidUnit() {
    return new UnitOfMeasurement().setUnitName("Valid Unit Name");
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement unit1 = unitOfMeasurementService.create(unit);
    UnitOfMeasurement unit2 = unitOfMeasurementService.getById(unit1.getId());
    assertEquals(unit1, unit2);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    unitOfMeasurementRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, unitOfMeasurementService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    UnitOfMeasurement unit = createValidUnit();
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement savedUnit = unitOfMeasurementService.create(unit);
    assertThat(savedUnit).isNotNull();

    Page<UnitOfMeasurement> result = unitOfMeasurementService.getByPaging(0, 1, "unitName", "a");
    assertEquals(1, result.getTotalElements());
    assertEquals(unit.getUnitName(), result.getContent().get(0).getUnitName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              unitOfMeasurementService.getByPaging(-1, 1, "unitName", "a");
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
              unitOfMeasurementService.getByPaging(0, 0, "unitName", "a");
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
              unitOfMeasurementService.getByPaging(0, 1, "a", "a");
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement savedunit = unitOfMeasurementService.create(unit);

    assertThat(savedunit).isNotNull();
    assertThat(savedunit.getUnitName()).isEqualTo("Valid Unit Name");
  }

  // UTCID02 - create: unitName null
  @Test
  void testUTCID02_Create_unitNameNull() {
    UnitOfMeasurement unit = createValidUnit();
    unit.setUnitName(null);

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.create(unit));
  }

  // UTCID03 - create: unitName empty
  @Test
  void testUTCID03_Create_unitNameEmpty() {
    UnitOfMeasurement unit = createValidUnit();
    unit.setUnitName("");

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.create(unit));
  }

  // UTCID04 - create: unitName greater than 100 characters
  @Test
  void testUTCID04_Create_unitNameLong() {
    UnitOfMeasurement unit = createValidUnit();
    unit.setUnitName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.create(unit));
  }

  // UTCID05 - create: unitName duplicate
  @Test
  void testUTCID05_Create_unitNameDuplicate() {
    UnitOfMeasurement unit = createValidUnit();
    unitOfMeasurementService.create(unit);
    UnitOfMeasurement duplicateunit = new UnitOfMeasurement().setUnitName("Valid Unit Name");

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.create(duplicateunit));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement savedUnit = unitOfMeasurementService.create(unit);
    UnitOfMeasurement updateUnit = unitOfMeasurementService.update(savedUnit);

    assertThat(savedUnit).isNotNull();
    assertThat(updateUnit.getUnitName()).isEqualTo("Valid Unit Name");
  }

  // UTCID02 - UPDATE: unitName null
  @Test
  void testUTCID02_Update_unitNameNull() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement createUnit = unitOfMeasurementService.create(unit);
    createUnit.setUnitName(null);

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(createUnit));
  }

  // UTCID03 - Update: unitName empty
  @Test
  void testUTCID03_Update_unitNameEmpty() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement createUnit = unitOfMeasurementService.create(unit);
    createUnit.setUnitName("");

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(createUnit));
  }

  // UTCID04 - Update: unitName greater than 100 characters
  @Test
  void testUTCID04_Update_unitNameLong() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement createUnit = unitOfMeasurementService.create(unit);
    createUnit.setUnitName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(createUnit));
  }

  // UTCID05 - Update: unitName duplicate
  @Test
  void testUTCID05_Update_unitNameDuplicate() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    unitOfMeasurementService.create(unit);
    UnitOfMeasurement secondUnit = new UnitOfMeasurement().setUnitName("Valid Unit Name 123123");
    UnitOfMeasurement returnValue = unitOfMeasurementService.create(secondUnit);
    returnValue.setUnitName("Valid Unit Name");

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(returnValue));
  }

  // UTCID06 - Update: id null
  @Test
  void testUTCID06_Update_idNull() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement createUnit = unitOfMeasurementService.create(unit);
    createUnit.setId(null);

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(createUnit));
  }

  // UTCID07 - Update: id not exist
  @Test
  void testUTCID07_Update_idNotExist() {
    unitOfMeasurementRepository.deleteAll();
    UnitOfMeasurement unit = createValidUnit();
    unit.setId(1L);

    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.update(unit));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    UnitOfMeasurement unit = createValidUnit();
    UnitOfMeasurement unit1 = unitOfMeasurementService.create(unit);
    unitOfMeasurementService.delete(unit1.getId());
    assertEquals(unitOfMeasurementService.getById(unit1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    unitOfMeasurementRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> unitOfMeasurementService.delete(nonExistingId));
  }
}
