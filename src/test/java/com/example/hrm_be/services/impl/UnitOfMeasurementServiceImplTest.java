package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import com.example.hrm_be.repositories.UnitOfMeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(UnitOfMeasurementServiceImpl.class)
@Transactional
class UnitOfMeasurementServiceImplTest {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();
  @Autowired
  private UnitOfMeasurementServiceImpl unitOfMeasurementService;

  @Autowired
  private UnitOfMeasurementRepository unitOfMeasurementRepository;

  @Test
  void testCreate_ShouldCreateUnitOfMeasurement() {
    // Prepare test data
    UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
    unitOfMeasurement.setUnitName("Kilogram");

    // Call the service method to create
    UnitOfMeasurement created = unitOfMeasurementService.create(unitOfMeasurement);

    // Assertions
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals("Kilogram", created.getUnitName());
  }

  @Test
  void testGetById_ShouldReturnUnitOfMeasurement() {
    // Prepare test data
    UnitOfMeasurementEntity entity = new UnitOfMeasurementEntity();
    entity.setUnitName("Meter");
    unitOfMeasurementRepository.saveAndFlush(entity);

    // Call the service method
    UnitOfMeasurement found = unitOfMeasurementService.getById(entity.getId());

    // Assertions
    assertNotNull(found);
    assertEquals("Meter", found.getUnitName());
  }

  @Test
  void testGetByPaging_ShouldReturnPagedResults() {
    // Prepare test data
    UnitOfMeasurementEntity entity1 = new UnitOfMeasurementEntity();
    entity1.setUnitName("Gram");
    UnitOfMeasurementEntity entity2 = new UnitOfMeasurementEntity();
    entity2.setUnitName("Liter");
    unitOfMeasurementRepository.saveAndFlush(entity1);
    unitOfMeasurementRepository.saveAndFlush(entity2);

    // Call the service method with paging
    Pageable pageable = PageRequest.of(0, 10, Sort.by("unitName").descending());
    Page<UnitOfMeasurement> page = unitOfMeasurementService.getByPaging(0, 10, "unitName", "");

    // Assertions
    assertNotNull(page);
    assertEquals(2, page.getTotalElements());
  }

  @Test
  void testUpdate_ShouldUpdateUnitOfMeasurement_WhenEntityExists() {
    // Prepare test data
    UnitOfMeasurementEntity oldEntity = new UnitOfMeasurementEntity();
    oldEntity.setUnitName("Inch");
    unitOfMeasurementRepository.saveAndFlush(oldEntity);

    UnitOfMeasurement updatedUnitOfMeasurement = new UnitOfMeasurement();
    updatedUnitOfMeasurement.setId(oldEntity.getId());
    updatedUnitOfMeasurement.setUnitName("Centimeter");

    // Call the service method to update
    UnitOfMeasurement updated = unitOfMeasurementService.update(updatedUnitOfMeasurement);

    // Fetch the updated entity from the database
    Optional<UnitOfMeasurementEntity> updatedEntity = unitOfMeasurementRepository.findById(oldEntity.getId());

    // Assertions
    assertTrue(updatedEntity.isPresent());
    assertEquals("Centimeter", updatedEntity.get().getUnitName());
  }

  @Test
  void testUpdate_ShouldThrowException_WhenEntityDoesNotExist() {
    // Prepare test data
    UnitOfMeasurement unitOfMeasurement = new UnitOfMeasurement();
    unitOfMeasurement.setId(999L);  // Non-existing ID
    unitOfMeasurement.setUnitName("Non-existent Unit");

    // Call the service method and expect an exception
    HrmCommonException exception = assertThrows(HrmCommonException.class, () -> {
      unitOfMeasurementService.update(unitOfMeasurement);
    });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.UNIT_OF_MEASUREMENT.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDelete_ShouldDeleteUnitOfMeasurement() {
    // Prepare test data
    UnitOfMeasurementEntity entity = new UnitOfMeasurementEntity();
    entity.setUnitName("Yard");
    unitOfMeasurementRepository.saveAndFlush(entity);

    // Call the service method to delete
    unitOfMeasurementService.delete(entity.getId());

    // Verify that the entity is deleted
    Optional<UnitOfMeasurementEntity> deletedEntity = unitOfMeasurementRepository.findById(entity.getId());
    assertTrue(deletedEntity.isEmpty());
  }

  @Test
  void testDelete_ShouldThrowException_WhenEntityDoesNotExist() {
    // Call the service method and expect an exception for non-existent ID
    HrmCommonException exception = assertThrows(HrmCommonException.class, () -> {
      unitOfMeasurementService.delete(999L);
    });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.UNIT_OF_MEASUREMENT.NOT_EXIST, exception.getMessage());
  }
}
