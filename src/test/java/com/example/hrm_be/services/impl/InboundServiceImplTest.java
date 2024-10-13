package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
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

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(InboundServiceImpl.class)
@Transactional
@Disabled
class InboundServiceImplTest {

  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private InboundServiceImpl inboundService;

  @Autowired private InboundRepository inboundRepository;

  @Autowired private UserService userService;

  @Disabled("Authen in approve method, not found a way to bypass")
  @Test
  void testCreate_ShouldCreateInbound() {
    // Prepare test data
    Inbound inbound = new Inbound();
    inbound.setInboundType(InboundType.CHUYEN_KHO_NOI_BO); // Specify an enum value
    inbound.setTotalPrice(BigDecimal.valueOf(1000.00));
    inbound.setInboundDate(LocalDateTime.now());
    inbound.setIsApproved(false);
    inbound.setTaxable(true);
    inbound.setStatus(InboundStatus.CHO_DUYET);
    inbound.setCreatedBy(new User()); // Populate with a real user entity if needed
    inbound.setFromBranch(new Branch()); // Populate with a valid branch entity
    inbound.setToBranch(new Branch()); // Populate with a valid branch entity

    // Call the service method to create
    Inbound created = inboundService.create(inbound);

    // Assertions
    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals(InboundStatus.CHO_DUYET, created.getStatus());
  }

  @Test
  void testUpdate_ShouldUpdateInbound_WhenEntityExists() {
    // Prepare test data
    InboundEntity oldEntity = new InboundEntity();
    oldEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
    oldEntity.setTotalPrice(BigDecimal.valueOf(1000.00));
    oldEntity.setInboundDate(LocalDateTime.now());
    oldEntity.setIsApproved(false);
    oldEntity.setTaxable(true);
    oldEntity.setStatus(InboundStatus.CHO_DUYET);
    oldEntity.setCreatedDate(LocalDateTime.now());
    oldEntity.setCreatedBy(new UserEntity()); // Specify a valid user entity
    oldEntity.setFromBranch(new BranchEntity()); // Populate with a valid branch entity
    oldEntity.setToBranch(new BranchEntity()); // Populate with a valid branch entity
    inboundRepository.saveAndFlush(oldEntity);

    Inbound updatedInbound = new Inbound();
    updatedInbound.setId(oldEntity.getId());
    updatedInbound.setTotalPrice(BigDecimal.valueOf(1200.00));
    updatedInbound.setIsApproved(true);
    updatedInbound.setStatus(InboundStatus.DANG_THANH_TOAN);

    // Call the service method to update
    Inbound updated = inboundService.update(updatedInbound);

    // Fetch the updated entity from the database
    Optional<InboundEntity> updatedEntity = inboundRepository.findById(oldEntity.getId());

    // Assertions
    assertTrue(updatedEntity.isPresent());
    assertEquals(BigDecimal.valueOf(1200.00), updatedEntity.get().getTotalPrice());
    assertTrue(updatedEntity.get().getIsApproved());
    assertEquals(InboundStatus.DANG_THANH_TOAN, updatedEntity.get().getStatus());
  }

  @Test
  void testUpdate_ShouldThrowException_WhenEntityDoesNotExist() {
    // Prepare test data
    Inbound inbound = new Inbound();
    inbound.setId(999L); // Non-existing ID
    inbound.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
    inbound.setTotalPrice(BigDecimal.valueOf(1000.00));

    // Call the service method and expect an exception
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.update(inbound);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.INBOUND.NOT_EXIST, exception.getMessage());
  }

  @Disabled("Authen in approve method, not found a way to bypass")
  @Test
  void testApprove_ShouldApproveInbound_WhenEntityExists() {
    // Prepare test data
    InboundEntity entity = new InboundEntity();
    entity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
    entity.setTotalPrice(BigDecimal.valueOf(1000.00));
    entity.setInboundDate(LocalDateTime.now());
    entity.setIsApproved(false);
    entity.setTaxable(true);
    entity.setStatus(InboundStatus.CHO_DUYET);
    entity.setCreatedDate(LocalDateTime.now());
    entity.setCreatedBy(new UserEntity()); // Specify a valid user entity
    entity.setFromBranch(new BranchEntity()); // Populate with a valid branch entity
    entity.setToBranch(new BranchEntity()); // Populate with a valid branch entity
    inboundRepository.saveAndFlush(entity);

    // Call the service method to approve
    Inbound approvedInbound = inboundService.approve(entity.getId(), true);

    // Assertions
    assertNotNull(approvedInbound);
    assertTrue(approvedInbound.getIsApproved());
  }

  @Test
  void testApprove_ShouldThrowException_WhenEntityDoesNotExist() {
    // Call the service method and expect an exception for non-existent ID
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.approve(999L, true);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.INBOUND.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDelete_ShouldDeleteInbound() {
    // Prepare test data
    InboundEntity entity = new InboundEntity();
    entity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
    entity.setTotalPrice(BigDecimal.valueOf(1000.00));
    entity.setInboundDate(LocalDateTime.now());
    entity.setIsApproved(false);
    entity.setTaxable(true);
    entity.setStatus(InboundStatus.CHO_DUYET);
    entity.setCreatedDate(LocalDateTime.now());
    entity.setCreatedBy(new UserEntity()); // Specify a valid user entity
    entity.setFromBranch(new BranchEntity()); // Populate with a valid branch entity
    entity.setToBranch(new BranchEntity()); // Populate with a valid branch entity
    inboundRepository.saveAndFlush(entity);

    // Call the service method to delete
    inboundService.delete(entity.getId());

    // Verify that the entity is deleted
    Optional<InboundEntity> deletedEntity = inboundRepository.findById(entity.getId());
    assertTrue(deletedEntity.isEmpty());
  }

  @Test
  void testDelete_ShouldThrowException_WhenEntityDoesNotExist() {
    // Call the service method and expect an exception for non-existent ID
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.delete(999L);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.INBOUND.NOT_EXIST, exception.getMessage());
  }
}
