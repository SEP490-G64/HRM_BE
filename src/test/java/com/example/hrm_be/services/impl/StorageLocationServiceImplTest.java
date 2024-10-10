package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.SecurityConfig;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.models.dtos.StorageLocation;
import com.example.hrm_be.models.entities.StorageLocationEntity;
import com.example.hrm_be.repositories.StorageLocationRepository;
import com.example.hrm_be.services.StorageLocationService;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Testcontainers
class StorageLocationServiceImplTest {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();
  @Autowired
  private StorageLocationService storageLocationService;

  @Autowired
  private StorageLocationRepository storageLocationRepository;

  @Autowired
  private StorageLocationMapper storageLocationMapper;

  @Test
  void testCreateStorageLocation() {
    StorageLocation storageLocation = new StorageLocation();
    storageLocation.setShelfName("Test Shelf");

    // Create a new StorageLocationEntity and save it
    StorageLocation created = storageLocationService.create(storageLocation);
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals("Test Shelf", created.getShelfName());
  }

  @Test
  void testGetById_ShouldReturnStorageLocation() {
    // Prepare test data
    StorageLocationEntity entity = new StorageLocationEntity();
    entity.setShelfName("Test Shelf");
    storageLocationRepository.saveAndFlush(entity);

    // Call the service method
    StorageLocation found = storageLocationService.getById(entity.getId());

    // Assertions
    assertNotNull(found);
    assertEquals("Test Shelf", found.getShelfName());
  }

  @Test
  void testDeleteStorageLocation() {
    // Prepare test data
    StorageLocationEntity entity = new StorageLocationEntity();
    entity.setShelfName("Shelf to delete");
    storageLocationRepository.saveAndFlush(entity);

    // Delete the storage location
    storageLocationService.delete(entity.getId());

    // Verify that it's deleted
    Optional<StorageLocationEntity> deletedEntity = storageLocationRepository.findById(entity.getId());
    assertTrue(deletedEntity.isEmpty());
  }
  @Test
  void testUpdate_ShouldUpdateStorageLocation_WhenEntityExists() {
    // Prepare test data
    StorageLocationEntity oldEntity = new StorageLocationEntity();
    oldEntity.setShelfName("Old Shelf");
    storageLocationRepository.saveAndFlush(oldEntity);

    StorageLocation storageLocationDTO = new StorageLocation();
    storageLocationDTO.setId(oldEntity.getId());
    storageLocationDTO.setShelfName("Updated Shelf");

    // Call the service method to update
    StorageLocation updated = storageLocationService.update(storageLocationDTO);

    // Fetch the updated entity from the database
    Optional<StorageLocationEntity> updatedEntity = storageLocationRepository.findById(oldEntity.getId());

    // Assertions
    assertTrue(updatedEntity.isPresent());
    assertEquals("Updated Shelf", updatedEntity.get().getShelfName());
  }

  @Test
  void testUpdate_ShouldThrowException_WhenEntityDoesNotExist() {
    // Prepare test data
    StorageLocation storageLocationDTO = new StorageLocation();
    storageLocationDTO.setId(999L);  // Non-existing ID
    storageLocationDTO.setShelfName("Non-existent Shelf");

    // Call the service method and expect an exception
    HrmCommonException exception = assertThrows(HrmCommonException.class, () -> {
      storageLocationService.update(storageLocationDTO);
    });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.STORAGE_LOCATION.NOT_EXIST, exception.getMessage());
  }
}