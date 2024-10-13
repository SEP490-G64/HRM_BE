package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
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
@Import(InboundBatchDetailServiceImpl.class)
@Transactional
@Disabled
class InboundBatchDetailServiceImplTest {

  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private InboundBatchDetailServiceImpl inboundBatchDetailService;

  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;

  @Autowired private InboundMapper inboundMapper;

  @Autowired private BatchMapper batchMapper;

  @Test
  void testCreate_ShouldCreateInboundBatchDetail() {
    // Prepare test data
    Batch batch = new Batch();
    batch.setBatchCode("123");
    batch.setExpireDate(LocalDateTime.now().minusDays(60));
    batch.setInboundPrice(BigDecimal.ONE);
    batch.setProduceDate(LocalDateTime.now().plusDays(60));
    batch.setProduct(new Product());

    Inbound inbound = new Inbound();
    inbound.setIsApproved(false);
    inbound.setCreatedBy(new User());
    inbound.setCreatedDate(LocalDateTime.now());
    inbound.setFromBranch(new Branch());
    inbound.setInboundDate(LocalDateTime.now());
    inbound.setInboundType(InboundType.CHUYEN_KHO_NOI_BO);
    inbound.setTaxable(true);
    inbound.setTotalPrice(BigDecimal.ONE);
    inbound.setToBranch(new Branch());
    inbound.setStatus(InboundStatus.CHO_DUYET);

    InboundBatchDetail inboundBatchDetail = new InboundBatchDetail();
    inboundBatchDetail.setQuantity(10);
    inboundBatchDetail.setInbound(inbound);
    inboundBatchDetail.setBatch(batch);

    // Call the service method to create
    InboundBatchDetail created = inboundBatchDetailService.create(inboundBatchDetail);

    // Assertions
    assertNotNull(created);
    assertEquals(10, created.getQuantity());
  }

  @Test
  void testUpdate_ShouldUpdateInboundBatchDetail_WhenEntityExists() {
    // Prepare test data
    Inbound inbound = new Inbound();
    inbound.setIsApproved(false);
    inbound.setCreatedBy(new User());
    inbound.setCreatedDate(LocalDateTime.now());
    inbound.setFromBranch(new Branch());
    inbound.setInboundDate(LocalDateTime.now());
    inbound.setInboundType(InboundType.CHUYEN_KHO_NOI_BO);
    inbound.setTaxable(true);
    inbound.setTotalPrice(BigDecimal.ONE);
    inbound.setToBranch(new Branch());
    inbound.setStatus(InboundStatus.CHO_DUYET);

    Batch batch = new Batch();
    batch.setBatchCode("123");
    batch.setExpireDate(LocalDateTime.now().minusDays(60));
    batch.setInboundPrice(BigDecimal.ONE);
    batch.setProduceDate(LocalDateTime.now().plusDays(60));
    batch.setProduct(new Product());

    InboundBatchDetailEntity oldDetailEntity = new InboundBatchDetailEntity();
    oldDetailEntity.setQuantity(5);
    oldDetailEntity.setInbound(inboundMapper.toEntity(inbound));
    oldDetailEntity.setBatch(batchMapper.toEntity(batch));
    inboundBatchDetailRepository.saveAndFlush(oldDetailEntity);

    InboundBatchDetail updatedDetail = new InboundBatchDetail();
    updatedDetail.setId(oldDetailEntity.getId()).setQuantity(15);

    // Call the service method to update
    InboundBatchDetail updated = inboundBatchDetailService.update(updatedDetail);

    // Fetch the updated entity from the database
    Optional<InboundBatchDetailEntity> updatedEntity =
        inboundBatchDetailRepository.findById(oldDetailEntity.getId());

    // Assertions
    assertTrue(updatedEntity.isPresent());
    assertEquals(15, updatedEntity.get().getQuantity());
  }

  @Test
  void testUpdate_ShouldThrowException_WhenEntityDoesNotExist() {
    // Prepare test data
    InboundBatchDetail updatedDetail = new InboundBatchDetail();
    updatedDetail.setId(999L); // Non-existing ID
    updatedDetail.setQuantity(10);

    // Call the service method and expect an exception
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundBatchDetailService.update(updatedDetail);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDelete_ShouldDeleteInboundBatchDetail() {
    // Prepare test data
    Inbound inbound = new Inbound();
    inbound.setIsApproved(false);
    inbound.setCreatedBy(new User());
    inbound.setCreatedDate(LocalDateTime.now());
    inbound.setFromBranch(new Branch());
    inbound.setInboundDate(LocalDateTime.now());
    inbound.setInboundType(InboundType.CHUYEN_KHO_NOI_BO);
    inbound.setTaxable(true);
    inbound.setTotalPrice(BigDecimal.ONE);
    inbound.setToBranch(new Branch());
    inbound.setStatus(InboundStatus.CHO_DUYET);

    Batch batch = new Batch();
    batch.setBatchCode("123");
    batch.setExpireDate(LocalDateTime.now().minusDays(60));
    batch.setInboundPrice(BigDecimal.ONE);
    batch.setProduceDate(LocalDateTime.now().plusDays(60));
    batch.setProduct(new Product());

    InboundBatchDetailEntity entity = new InboundBatchDetailEntity();
    entity.setQuantity(5);
    entity.setBatch(batchMapper.toEntity(batch));
    entity.setInbound(inboundMapper.toEntity(inbound));

    inboundBatchDetailRepository.saveAndFlush(entity);

    // Call the service method to delete
    inboundBatchDetailService.delete(entity.getId());

    // Verify that the entity is deleted
    Optional<InboundBatchDetailEntity> deletedEntity =
        inboundBatchDetailRepository.findById(entity.getId());
    assertTrue(deletedEntity.isEmpty());
  }

  @Test
  void testDelete_ShouldThrowException_WhenEntityDoesNotExist() {
    // Call the service method and expect an exception for non-existent ID
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundBatchDetailService.delete(999L);
            });

    // Assert exception message
    assertEquals(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST, exception.getMessage());
  }
}
