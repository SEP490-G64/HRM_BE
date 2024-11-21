package com.example.hrm_be.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ManufacturerMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import com.example.hrm_be.repositories.ManufacturerRepository;
import com.example.hrm_be.services.ManufacturerService;
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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ManufacturerServiceImplTest {

  @Mock
  private ManufacturerMapper manufacturerMapper;
  @Mock
  private ManufacturerRepository manufacturerRepository;
  @InjectMocks
  private ManufacturerServiceImpl manufacturerService;

  private Manufacturer manufacturer;
  private ManufacturerEntity manufacturerEntity;

  @BeforeEach
  public void setup() {
    manufacturer = Manufacturer.builder()
            .manufacturerName("Valid Manufacturer Name")
            .address("Valid Manufacturer Address")
            .email("validemail@mail.com")
            .taxCode("1234567890")
            .phoneNumber("0912345678")
            .origin("Valid Origin")
            .status(true)
            .build();

    manufacturerEntity = ManufacturerEntity.builder()
            .manufacturerName("Valid Manufacturer Name")
            .address("Valid Manufacturer Address")
            .email("validemail@mail.com")
            .taxCode("1234567890")
            .phoneNumber("0912345678")
            .origin("Valid Origin")
            .status(true)
            .build();
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_idValid() {
    Long id = 1L;
    when(manufacturerRepository.findById(id)).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);
    Manufacturer result = manufacturerService.getById(id);
    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> manufacturerService.getById(id));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(manufacturerRepository.findById(id)).thenReturn(Optional.empty());
    Assertions.assertNull(manufacturerService.getById(id));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("manufacturerName").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "manufacturerName", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(HrmCommonException.class, () ->
            manufacturerService.getByPaging(-1, 10, "manufacturerName", "", true));
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () ->
            manufacturerService.getByPaging(0, 0, "manufacturerName", "", true));
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    assertThrows(HrmCommonException.class, () ->
                      manufacturerService.getByPaging(0, 1, "a", "a", true));
  }

  // SEARCH
  // UTCID05 - getByPaging: All valid
  @Test
  void testUTCID05_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("address").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "address", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // SEARCH
  // UTCID06 - getByPaging: All valid
  @Test
  void testUTCID06_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("email").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "email", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // SEARCH
  // UTCID07 - getByPaging: All valid
  @Test
  void testUTCID07_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("phoneNumber").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "phoneNumber", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // SEARCH
  // UTCID08 - getByPaging: All valid
  @Test
  void testUTCID08_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("taxCode").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "taxCode", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // SEARCH
  // UTCID09 - getByPaging: All valid
  @Test
  void testUTCID09_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("origin").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "origin", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // SEARCH
  // UTCID010 - getByPaging: All valid
  @Test
  void testUTCID010_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("status").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "status", "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // UTCID011 - getByPaging: All valid
  @Test
  void testUTCID011_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, null, "", true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // UTCID012 - getByPaging: All valid
  @Test
  void testUTCID012_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<ManufacturerEntity> manufacturers = Collections.singletonList(manufacturerEntity);
    Page<ManufacturerEntity> page = new PageImpl<>(manufacturers, pageable, manufacturers.size());

    when(manufacturerRepository.searchManufacturers("", true, pageable)).thenReturn(page);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, null, null, true);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getTotalElements());

  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    when(manufacturerMapper.toEntity(manufacturer)).thenReturn(manufacturerEntity);
    when(manufacturerRepository.save(manufacturerEntity)).thenReturn(manufacturerEntity);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Manufacturer result = manufacturerService.create(manufacturer);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - create: ManufacturerName null
  @Test
  void testUTCID02_Create_nameNull() {
    manufacturer.setManufacturerName(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID03 - create: ManufacturerName empty
  @Test
  void testUTCID03_Create_nameEmpty() {
    manufacturer.setManufacturerName("");
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID04 - create: ManufacturerName greater than 100 characters
  @Test
  void testUTCID04_Create_nameLong() {
    manufacturer.setManufacturerName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID05 - create: ManufacturerName and Address duplicate
  @Test
  void testUTCID05_Create_nameDuplicate() {
    manufacturer.setId(1L);
    manufacturer.setManufacturerName("Name Duplicate");
    manufacturerEntity.setId(1L);
    manufacturerEntity.setManufacturerName("Name Duplicate");
    when(manufacturerRepository.existsByManufacturerNameAndAddress(manufacturerEntity.getManufacturerName(),
            manufacturerEntity.getAddress())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID06 - create: address greater than 256 characters
  @Test
  void testUTCID06_Create_addressLong() {
    manufacturer.setAddress("A".repeat(1001));
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID07 - create: email not match regex
  @Test
  void testUTCID07_Create_emailInvalidFormat() {
    manufacturer.setEmail("Email");
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID08 - create: phoneNumber not match regex
  @Test
  void testUTCID08_Create_phoneNumberInvalidFormat() {
    manufacturer.setPhoneNumber("INVALID_PHONE");
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID09 - create: taxCode not match tax code regex
  @Test
  void testUTCID09_Create_taxCodeInvalidFormat() {
    manufacturer.setTaxCode("a");
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

   //UTCID010 - create: taxCode duplicate
   @Test
   void testUTCID010_Create_taxCodeDuplicate() {
     // Set the tax code for manufacturer to "Duplicate"
     manufacturer.setId(1L);
     manufacturer.setTaxCode("0101234557");

     // Use lenient stubbing
     when(manufacturerRepository.existsByTaxCode(manufacturer.getTaxCode())).thenReturn(true);

     // Execute the method and expect an exception to be thrown due to duplicate tax code
     assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
   }

  // UTCID011 - create: origin greater than 255 characters
  @Test
  void testUTCID011_Create_originLong() {
    manufacturer.setOrigin("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID012 - create: status null
  @Test
  void testUTCID012_Create_statusNull() {
    manufacturer.setStatus(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }

  // UTCID013 - create: manufacturer null
  @Test
  void testUTCID013_Create_manufacturerll() {
    manufacturer = null;
    assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturer));
  }



  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    manufacturer.setId(1L);
    manufacturerEntity.setId(1L);

    when(manufacturerRepository.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerRepository.save(manufacturerEntity)).thenReturn(manufacturerEntity);
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);

    Manufacturer result = manufacturerService.update(manufacturer);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - UPDATE: ManufacturerName null
  @Test
  void testUTCID02_Update_nameNull() {
    manufacturer.setManufacturerName(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID03 - Update: ManufacturerName empty
  @Test
  void testUTCID03_Update_nameEmpty() {
    manufacturer.setManufacturerName("");
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID04 - Update: ManufacturerName greater than 100 characters
  @Test
  void testUTCID04_Update_nameLong() {
    manufacturer.setManufacturerName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID05 - Update: ManufacturerName and Address duplicate
  @Test
  void testUTCID05_Update_nameDuplicate() {
    manufacturer.setId(1L);
    manufacturer.setManufacturerName("new Name");
    manufacturer.setAddress("new Address");
    manufacturerEntity.setId(1L);
    manufacturerEntity.setManufacturerName("Old Name");
    manufacturerEntity.setAddress("Old Address");
    when(manufacturerRepository.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerRepository.existsByManufacturerNameAndAddress(manufacturer.getManufacturerName(),
            manufacturer.getAddress())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID051 - Update: ManufacturerName and Address duplicate
  @Test
  void testUTCID051_Update_nameDuplicate() {
    manufacturer.setId(1L);
    manufacturer.setManufacturerName("new Name");
    manufacturer.setAddress("new Address");
    manufacturerEntity.setId(1L);
    manufacturerEntity.setManufacturerName("new Name");
    manufacturerEntity.setAddress(" Address");
    when(manufacturerRepository.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerRepository.existsByManufacturerNameAndAddress(manufacturer.getManufacturerName(),
            manufacturer.getAddress())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID06 - Update: address greater than 255 characters
  @Test
  void testUTCID06_Update_addressLong() {
    manufacturer.setAddress("A".repeat(256));

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID07 - Update: email not match regex
  @Test
  void testUTCID07_Update_emailInvalidFormat() {
    manufacturer.setEmail("Email");
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID08 - update: phoneNumber not match regex
  @Test
  void testUTCID08_Update_phoneNumberInvalidFormat() {
    manufacturer.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID09 - Update: phoneNumber greater than 256 characters
  @Test
  void testUTCID09_Update_phoneNumberLong() {
    manufacturer.setPhoneNumber("A".repeat(256));

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID010 - update: taxCode not match tax code regex
  @Test
  void testUTCID010_Update_taxCodeInvalidFormat() {
    manufacturer.setTaxCode("a");

    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID011 - update: taxCode duplicate
  @Test
  void testUTCID011_Update_taxCodeDuplicate() {
    manufacturer.setId(1L);
    manufacturer.setTaxCode("0101234557");
    manufacturerEntity.setId(1L);
    manufacturerEntity.setTaxCode("0101234566");
    when(manufacturerRepository.findById(manufacturer.getId())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerRepository.existsByTaxCode(manufacturer.getTaxCode())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID012 - update: origin greater than 255 characters
  @Test
  void testUTCID012_Update_originLong() {
    manufacturer.setOrigin("A".repeat(256));
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID013 - update: status null
  @Test
  void testUTCID013_Update_statusNull() {
    manufacturer.setStatus(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID014 - Update: id null
  @Test
  void testUTCID014_Update_idNull() {
    manufacturer.setId(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID015 - Update: id not exist
  @Test
  void testUTCID015_Update_idNotExist() {
    manufacturer.setId(1L);
    when(manufacturerRepository.findById(manufacturer.getId())).thenReturn(Optional.empty());
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // UTCID016 - Update: manufacturer null
  @Test
  void testUTCID016_Update_manufacturerNull() {
    manufacturer = null;
    assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturer));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    manufacturer.setId(1L);
    when(manufacturerService.existById(manufacturer.getId())).thenReturn(true);
    manufacturerService.delete(manufacturer.getId());
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    manufacturer.setId(null);
    assertThrows(HrmCommonException.class, () -> manufacturerService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    manufacturer.setId(1L);
    when(manufacturerService.existById(manufacturer.getId())).thenReturn(false);
    assertThrows(HrmCommonException.class, () -> manufacturerService.delete(manufacturer.getId()));
  }

  // getAll
  // UTCID01 - getAll: valid
  @Test
  void testUTCID01_getAll_AllValid() {
    when(manufacturerRepository.findAll()).thenReturn(List.of(manufacturerEntity));
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);
    List<Manufacturer> manufacturerList = manufacturerService.getAll();
    Assertions.assertNotNull(manufacturerList);
  }

  // getByName
  // UTCID01 - getByName: valid
  @Test
  void testUTCID01_getByName_AllValid() {
    String name = "a";
    when(manufacturerRepository.findByManufacturerName(name)).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerMapper.toDTO(manufacturerEntity)).thenReturn(manufacturer);
    Manufacturer result = manufacturerService.getByName(name);
    Assertions.assertNotNull(result);
  }
}

