//package com.example.hrm_be.services.impl;
//
//import com.example.hrm_be.HrmBeApplication;
//import com.example.hrm_be.commons.constants.HrmConstant;
//import com.example.hrm_be.components.ProductMapper;
//import com.example.hrm_be.configs.exceptions.HrmCommonException;
//import com.example.hrm_be.models.dtos.Batch;
//import com.example.hrm_be.models.dtos.Product;
//import com.example.hrm_be.models.entities.ProductEntity;
//import com.example.hrm_be.repositories.BatchRepository;
//import com.example.hrm_be.repositories.ProductRepository;
//import com.example.hrm_be.services.BatchService;
//import com.example.hrm_be.services.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@Testcontainers
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = HrmBeApplication.class)
//@ActiveProfiles("test")
//@Import({BatchServiceImpl.class, ProductMapper.class})
//@Transactional
//public class BatchServiceImplTest {
//
//  @Autowired private BatchService batchService;
//  @Autowired private BatchRepository batchRepository;
//  @Autowired private ProductRepository productRepository;
//  @Autowired private ProductMapper productMapper;
//
//  private Product testProduct;
//
//  @BeforeEach
//  public void setUp() {
//    // Kiểm tra nếu sản phẩm đã tồn tại trong cơ sở dữ liệu để tránh tạo nhiều lần
//    testProduct = productRepository.findByRegistrationCode("Test Product")
//            .orElseGet(() -> {
//              ProductEntity product = new ProductEntity();
//              product.setProductName("Test Product");
//              product.setRegistrationCode("Test Product");
//              // Thiết lập thêm các thuộc tính nếu cần thiết
//              ProductEntity createProduct = productRepository.save(product);
//              return productMapper.convertToBaseInfo(createProduct);
//            });
//  }
//
//  // Helper to create a valid Batch entity
//  private Batch createValidBatch() {
//    return new Batch()
//        .setBatchCode("Valid Batch Code")
//        .setProduceDate(LocalDateTime.of(2023, 1, 1, 0, 0))
//        .setExpireDate(LocalDateTime.of(2025, 1, 1, 0, 0))
//        .setInboundPrice(BigDecimal.ONE)
//        .setProduct(testProduct);
//  }
//
//  // GET
//  // UTCID01 - Get: valid
//  @Test
//  void testUTCID01_Get_AllValid() {
//    Batch Batch = createValidBatch();
//    Batch Batch1 = batchService.create(Batch);
//    Batch Batch2 = batchService.getById(Batch1.getId());
//    assertEquals(Batch1, Batch2);
//  }
//
//  // UTCID02 - Get: id null
//  @Test
//  void testUTCID02_Get_idNull() {
//    assertThrows(HrmCommonException.class, () -> batchService.getById(null));
//  }
//
//  // UTCID03 - Get: id not exist
//  @Test
//  void testUTCID03_Get_idNotExist() {
//    batchRepository.deleteAll();
//    Long nonExistingId = 1L;
//    assertEquals(null, batchService.getById(nonExistingId));
//  }
//
//  // SEARCH
//  // UTCID01 - getByPaging: All valid
//  @Test
//  void testUTCID01_GetByPaging_AllValid() {
//    Batch Batch = createValidBatch();
//    batchService.deleteAll();
//    Batch savedBatch = batchService.create(Batch);
//    assertThat(savedBatch).isNotNull();
//
//    Page<Batch> result = batchService.getByPaging(0, 1, "BatchName", 1L, "a",
//            LocalDateTime.of(2022, 1, 1, 0, 0), true);
//    assertEquals(1, result.getTotalElements());
//    assertEquals(Batch.getBatchName(), result.getContent().get(0).getBatchName());
//  }
//
//  // UTCID02 - getByPaging: pageNo invalid
//  @Test
//  void testUTCID02_GetByPaging_pageNoInvalid() {
//    Exception exception =
//        assertThrows(
//            HrmCommonException.class,
//            () -> {
//              batchService.getByPaging(-1, 1, "BatchName", "a", BatchType.MAIN, true);
//            });
//    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
//  }
//
//  // UTCID03 - getByPaging: pageSize invalid
//  @Test
//  void testUTCID03_GetByPaging_pageSizeInvalid() {
//    Exception exception =
//        assertThrows(
//            HrmCommonException.class,
//            () -> {
//              batchService.getByPaging(0, 0, "BatchName", "a", BatchType.MAIN, true);
//            });
//    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
//  }
//
//  // UTCID04 - getByPaging: sortBy invalid
//  @Test
//  void testUTCID04_GetByPaging_sortByInvalid() {
//    Exception exception =
//        assertThrows(
//            HrmCommonException.class,
//            () -> {
//              batchService.getByPaging(0, 1, "a", "a", Lo, true);
//            });
//    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
//  }
//
//  // CREATE
//  // UTCID01 - create: all valid
//  @Test
//  void testUTCID01_Create_AllValid() {
//    Batch Batch = createValidBatch();
//    Batch savedBatch = batchService.create(Batch);
//
//    assertThat(savedBatch).isNotNull();
//    assertThat(savedBatch.getBatchCode()).isEqualTo("Valid Batch Code");
//  }
//
//  // UTCID02 - create: BatchCode null
//  @Test
//  void testUTCID02_Create_BatchCodeNull() {
//    Batch Batch = createValidBatch();
//    Batch.setBatchCode(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID03 - create: BatchCode empty
//  @Test
//  void testUTCID03_Create_BatchCodeEmpty() {
//    Batch Batch = createValidBatch();
//    Batch.setBatchCode("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID04 - create: BatchCode greater than 100 characters
//  @Test
//  void testUTCID04_Create_BatchCodeLong() {
//    Batch Batch = createValidBatch();
//    Batch.setBatchCode("A".repeat(101));
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID05 - create: BatchCode duplicate
//  @Test
//  void testUTCID05_Create_BatchCodeDuplicate() {
//    Batch Batch = createValidBatch();
//    batchService.create(Batch);
//    Batch duplicateBatchName =
//        new Batch()
//                .setBatchCode("Valid Batch Code")
//                .setProduceDate(LocalDateTime.of(2023, 1, 1, 0, 0))
//                .setExpireDate(LocalDateTime.of(2025, 1, 1, 0, 0))
//                .setInboundPrice(BigDecimal.ONE);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(duplicateBatchName));
//  }
//
//  // UTCID06 - create: location null
//  @Test
//  void testUTCID06_Create_locationNull() {
//    Batch Batch = createValidBatch();
//    Batch.setLocation(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID07 - create: location empty
//  @Test
//  void testUTCID07_Create_locationEmpty() {
//    Batch Batch = createValidBatch();
//    Batch.setLocation("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID08 - create: location greater than 256 characters
//  @Test
//  void testUTCID08_Create_locationLong() {
//    Batch Batch = createValidBatch();
//    Batch.setLocation("A".repeat(256));
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID09 - create: location duplicate
//  @Test
//  void testUTCID09_Create_locationDuplicate() {
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch duplicateLocationBatch =
//        new Batch()
//            .setBatchName("Valid Batch Name 123123")
//            .setBatchType(BatchType.MAIN)
//            .setLocation("Valid Location")
//            .setContactPerson("Valid Contact Person")
//            .setPhoneNumber("0912345678")
//            .setCapacity(500)
//            .setActiveStatus(true);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(duplicateLocationBatch));
//  }
//
//  // UTCID010 - create: contactPerson greater than 100 characters
//  @Test
//  void testUTCID010_Create_contactPersonLong() {
//    Batch Batch = createValidBatch();
//    Batch.setContactPerson("A".repeat(101));
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID011 - create: phoneNumber null
//  @Test
//  void testUTCID011_Create_phoneNumberNull() {
//    Batch Batch = createValidBatch();
//    Batch.setPhoneNumber(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID012 - create: phoneNumber empty
//  @Test
//  void testUTCID012_Create_phoneNumberEmpty() {
//    Batch Batch = createValidBatch();
//    Batch.setPhoneNumber("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID013 - create: phoneNumber not match regex
//  @Test
//  void testUTCID013_Create_phoneNumberInvalidFormat() {
//    Batch Batch = createValidBatch();
//    Batch.setPhoneNumber("INVALID_PHONE");
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID014 - create: capacity not null && greater than 100,000
//  @Test
//  void testUTCID014_Create_capacityExcessive() {
//    Batch Batch = createValidBatch();
//    Batch.setCapacity(100001);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID015 - create: capacity not null && negative number
//  @Test
//  void testUTCID015_Create_capacityNegative() {
//    Batch Batch = createValidBatch();
//    Batch.setCapacity(0);
//
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID016 - create: BatchType null
//  @Test
//  void testUTCID016_Create_BatchTypeNull() {
//    Batch Batch = createValidBatch();
//    Batch.setBatchType(null);
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UTCID017 - create: activeStatus null
//  @Test
//  void testUTCID017_Create_activeStatusNull() {
//    Batch Batch = createValidBatch();
//    Batch.setActiveStatus(null);
//    assertThrows(HrmCommonException.class, () -> batchService.create(Batch));
//  }
//
//  // UPDATE
//  // UTCID01 - UPDATE: all valid
//  @Test
//  void testUTCID01_Update_AllValid() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    Batch savedBatch = BatchService.create(Batch);
//    Batch updateBatch = BatchService.update(savedBatch);
//
//    assertThat(updateBatch).isNotNull();
//    assertThat(updateBatch.getBatchName()).isEqualTo("Valid Batch Name");
//  }
//
//  // UTCID02 - UPDATE: BatchName null
//  @Test
//  void testUTCID02_Update_BatchNameNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setBatchName(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID03 - Update: BatchName empty
//  @Test
//  void testUTCID03_Update_BatchNameEmpty() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setBatchName("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID04 - Update: BatchName greater than 100 characters
//  @Test
//  void testUTCID04_Update_BatchNameLong() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setBatchName("A".repeat(101));
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID05 - Update: BatchName duplicate
//  @Test
//  void testUTCID05_Update_BatchNameDuplicate() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch secondBatch =
//        new Batch()
//            .setBatchName("Valid Batch Name 123123")
//            .setBatchType(BatchType.SUB)
//            .setLocation("Valid Location 123123")
//            .setContactPerson("Valid Contact Person")
//            .setPhoneNumber("0912345678")
//            .setCapacity(500)
//            .setActiveStatus(true);
//    Batch returnValue = BatchService.create(secondBatch);
//    returnValue.setBatchName("Valid Batch Name");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(returnValue));
//  }
//
//  // UTCID06 - Update: location null
//  @Test
//  void testUTCID06_Update_locationNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setLocation(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID07 - Update: location empty
//  @Test
//  void testUTCID07_Update_locationEmpty() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setLocation("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID08 - Update: location greater than 256 characters
//  @Test
//  void testUTCID08_Update_locationLong() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setLocation("A".repeat(256));
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID09 - Update: location duplicate
//  @Test
//  void testUTCID09_Update_locationDuplicate() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch secondBatch =
//        new Batch()
//            .setBatchName("Valid Batch Name 123123")
//            .setBatchType(BatchType.SUB)
//            .setLocation("Valid Location 123123")
//            .setContactPerson("Valid Contact Person")
//            .setPhoneNumber("0912345678")
//            .setCapacity(500)
//            .setActiveStatus(true);
//    Batch returnValue = BatchService.create(secondBatch);
//    returnValue.setLocation("Valid Location");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(returnValue));
//  }
//
//  // UTCID010 - Update: contactPerson greater than 100 characters
//  @Test
//  void testUTCID010_Update_contactPersonLong() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setContactPerson("A".repeat(101));
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID011 - Update: phoneNumber null
//  @Test
//  void testUTCID011_Update_phoneNumberNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setPhoneNumber(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID012 - Update: phoneNumber empty
//  @Test
//  void testUTCID012_Update_phoneNumberEmpty() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setPhoneNumber("");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID013 - Update: phoneNumber not match regex
//  @Test
//  void testUTCID013_Update_phoneNumberInvalidFormat() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setPhoneNumber("INVALID_PHONE");
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID014 - Update: capacity not null && greater than 100,000
//  @Test
//  void testUTCID014_Update_capacityExcessive() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setCapacity(100001);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID015 - Update: capacity not null && negative number
//  @Test
//  void testUTCID015_Update_capacityNegative() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setCapacity(0);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID016 - Update: BatchType null
//  @Test
//  void testUTCID016_Update_BatchTypeNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setBatchType(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID017 - Update: activeStatus null
//  @Test
//  void testUTCID017_Update_activeStatusNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    BatchService.create(Batch);
//    Batch.setActiveStatus(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID018 - Update: id null
//  @Test
//  void testUTCID018_Update_idNull() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    batchService.create(Batch);
//    Batch.setId(null);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // UTCID019 - Update: id not exist
//  @Test
//  void testUTCID019_Update_idNotExist() {
//    BatchRepository.deleteAll();
//    Batch Batch = createValidBatch();
//    batchService.create(Batch);
//    Batch.setId(1L);
//
//    assertThrows(HrmCommonException.class, () -> batchService.update(Batch));
//  }
//
//  // DELETE
//  // UTCID01 - Delete: valid
//  @Test
//  void testUTCID01_Delete_AllValid() {
//    Batch Batch = createValidBatch();
//    Batch Batch1 = batchService.create(Batch);
//    batchService.delete(Batch1.getId());
//    assertEquals(batchService.getById(Batch1.getId()), null);
//  }
//
//  // UTCID02 - Delete: id null
//  @Test
//  void testUTCID02_Delete_idNull() {
//    assertThrows(HrmCommonException.class, () -> batchService.delete(null));
//  }
//
//  // UTCID03 - Delete: id not exist
//  @Test
//  void testUTCID03_Delete_idNotExist() {
//    batchRepository.deleteAll();
//    Long nonExistingId = 2L;
//    assertThrows(HrmCommonException.class, () -> batchService.delete(nonExistingId));
//  }
//}
