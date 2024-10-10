package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.repositories.UnitConversionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(UnitConversionImpl.class)
@Transactional
class UnitConversionImplTest {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private UnitConversionImpl unitConversionService;

  @Autowired private UnitConversionRepository unitConversionRepository;

  @Test
  void testCreate_ShouldCreateUnitConversion() {
    // Prepare test data
    UnitConversion unitConversion = new UnitConversion();
    unitConversion.setFactorConversion(2.0);

    // Call the service method to create
    UnitConversion created = unitConversionService.create(unitConversion);

    // Assertions
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(2.0, created.getFactorConversion());
  }

  @Test
  void testGetById_ShouldReturnUnitConversion() {
    // Prepare test data
    UnitConversionEntity entity = new UnitConversionEntity();
    entity.setFactorConversion(1.5);
    unitConversionRepository.saveAndFlush(entity);

    // Call the service method
    UnitConversion found = unitConversionService.getById(entity.getId());

    // Assertions
    assertNotNull(found);
    assertEquals(1.5, found.getFactorConversion());
  }

  @Test
  void testGetAll_ShouldReturnAllUnitConversions() {
    // Prepare test data
    UnitConversionEntity entity1 = new UnitConversionEntity();
    entity1.setFactorConversion(1.0);
    UnitConversionEntity entity2 = new UnitConversionEntity();
    entity2.setFactorConversion(2.0);
    unitConversionRepository.saveAndFlush(entity1);
    unitConversionRepository.saveAndFlush(entity2);

    // Call the service method
    List<UnitConversion> all = unitConversionService.getAll();

    // Assertions
    assertNotNull(all);
    assertEquals(2, all.size());
  }

  @Test
  void testUpdate_ShouldUpdateUnitConversion_WhenEntityExists() {
    // Prepare test data
    UnitConversionEntity oldEntity = new UnitConversionEntity();
    oldEntity.setFactorConversion(1.5);
    unitConversionRepository.saveAndFlush(oldEntity);

    UnitConversion updatedUnitConversion = new UnitConversion();
    updatedUnitConversion.setId(oldEntity.getId());
    updatedUnitConversion.setFactorConversion(2.0);

    // Call the service method to update
    UnitConversion updated = unitConversionService.update(updatedUnitConversion);

    // Fetch the updated entity from the database
    Optional<UnitConversionEntity> updatedEntity =
        unitConversionRepository.findById(oldEntity.getId());

    // Assertions
    assertTrue(updatedEntity.isPresent());
    assertEquals(2.0, updatedEntity.get().getFactorConversion());
  }

  @Test
  void testUpdate_ShouldThrowException_WhenEntityDoesNotExist() {
    // Prepare test data
    UnitConversion unitConversion = new UnitConversion();
    unitConversion.setId(999L); // Non-existing ID
    unitConversion.setFactorConversion(2.5);

    // Call the service method and expect an exception
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              unitConversionService.update(unitConversion);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.UNIT_CONVERSION.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDelete_ShouldDeleteUnitConversion() {
    // Prepare test data
    UnitConversionEntity entity = new UnitConversionEntity();
    entity.setFactorConversion(1.5);
    unitConversionRepository.saveAndFlush(entity);

    // Call the service method to delete
    unitConversionService.delete(entity.getId());

    // Verify that the entity is deleted
    Optional<UnitConversionEntity> deletedEntity =
        unitConversionRepository.findById(entity.getId());
    assertTrue(deletedEntity.isEmpty());
  }

  @Test
  void testDelete_ShouldThrowException_WhenEntityDoesNotExist() {
    // Call the service method and expect an exception for non-existent ID
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              unitConversionService.delete(999L);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.UNIT_CONVERSION.NOT_EXIST, exception.getMessage());
  }
}
