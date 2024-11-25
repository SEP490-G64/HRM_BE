package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import com.example.hrm_be.repositories.UnitOfMeasurementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UnitServiceImplTest {

  @Mock private UnitOfMeasurementMapper unitMapper;
  @Mock private UnitOfMeasurementRepository unitRepository;
  @InjectMocks private UnitOfMeasurementServiceImpl unitService;

  private UnitOfMeasurement unit;
  private UnitOfMeasurementEntity unitEntity;

  @BeforeEach
  public void setup() {
    unit = UnitOfMeasurement.builder().unitName("Valid Unit Name").build();

    unitEntity = UnitOfMeasurementEntity.builder().unitName("Valid Unit Name").build();
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_idValid() {
    Long id = 1L;
    when(unitRepository.findById(id)).thenReturn(Optional.of(unitEntity));
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);
    UnitOfMeasurement result = unitService.getById(id);
    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> unitService.getById(id));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(unitRepository.findById(id)).thenReturn(Optional.empty());
    Assertions.assertNull(unitService.getById(id));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("unitName").descending());
    List<UnitOfMeasurementEntity> units = Collections.singletonList(unitEntity);
    Page<UnitOfMeasurementEntity> page = new PageImpl<>(units, pageable, units.size());

    when(unitRepository.findByUnitNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);

    Page<UnitOfMeasurement> result = unitService.getByPaging(0, 10, "unitName", "");

    Assertions.assertNotNull(result);
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(HrmCommonException.class, () -> unitService.getByPaging(-1, 10, "unitName", ""));
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(HrmCommonException.class, () -> unitService.getByPaging(0, 0, "unitName", ""));
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    assertThrows(HrmCommonException.class, () -> unitService.getByPaging(-1, 10, "a", ""));
  }

  // SEARCH
  // UTCID05 - getByPaging: All valid
  @Test
  void testUTCID05_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<UnitOfMeasurementEntity> units = Collections.singletonList(unitEntity);
    Page<UnitOfMeasurementEntity> page = new PageImpl<>(units, pageable, units.size());

    when(unitRepository.findByUnitNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);

    Page<UnitOfMeasurement> result = unitService.getByPaging(0, 10, null, "");

    Assertions.assertNotNull(result);
  }

  // UTCID06 - getByPaging: All valid
  @Test
  void testUTCID06_GetByPaging_AllValid() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    List<UnitOfMeasurementEntity> units = Collections.singletonList(unitEntity);
    Page<UnitOfMeasurementEntity> page = new PageImpl<>(units, pageable, units.size());

    when(unitRepository.findByUnitNameContainingIgnoreCase("", pageable)).thenReturn(page);
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);

    Page<UnitOfMeasurement> result = unitService.getByPaging(0, 10, null, null);

    Assertions.assertNotNull(result);
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    when(unitMapper.toEntity(unit)).thenReturn(unitEntity);
    when(unitRepository.save(unitEntity)).thenReturn(unitEntity);
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);

    UnitOfMeasurement result = unitService.create(unit);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - create: UnitName null
  @Test
  void testUTCID02_Create_nameNull() {
    unit.setUnitName(null);
    assertThrows(HrmCommonException.class, () -> unitService.create(unit));
  }

  // UTCID03 - create: UnitName empty
  @Test
  void testUTCID03_Create_nameEmpty() {
    unit.setUnitName("");
    assertThrows(HrmCommonException.class, () -> unitService.create(unit));
  }

  // UTCID04 - create: UnitName greater than 100 characters
  @Test
  void testUTCID04_Create_nameLong() {
    unit.setUnitName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> unitService.create(unit));
  }

  // UTCID05 - create: unitName duplicate
  @Test
  void testUTCID05_Create_unitNameDuplicate() {
    unit.setUnitName("Valid Unit Name");
    unitEntity.setUnitName("Valid Unit Name");
    when(unitRepository.existsByUnitName(unit.getUnitName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> unitService.create(unit));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    unit.setId(1L);
    unitEntity.setId(1L);

    when(unitRepository.findById(unit.getId())).thenReturn(Optional.of(unitEntity));
    when(unitRepository.save(unitEntity)).thenReturn(unitEntity);
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);

    UnitOfMeasurement result = unitService.update(unit);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - Update: UnitName null
  @Test
  void testUTCID02_Update_nameNull() {
    unit.setUnitName(null);
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // UTCID03 - Update: UnitName empty
  @Test
  void testUTCID03_Update_nameEmpty() {
    unit.setUnitName("");
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // UTCID04 - Update: UnitName greater than 100 characters
  @Test
  void testUTCID04_Update_nameLong() {
    unit.setUnitName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // UTCID05 - Update: UnitName and Description duplicate
  @Test
  void testUTCID05_Update_nameDuplicate() {
    unit.setUnitName("new Name");
    unitEntity.setUnitName("Old Name");

    lenient().when(unitRepository.existsByUnitName(unit.getUnitName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // UTCID06 - Update: id null
  @Test
  void testUTCID06_Update_idNull() {
    unit.setId(null);
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // UTCID07 - Update: id not exist
  @Test
  void testUTCID07_Update_idNotExist() {
    unit.setId(1L);
    when(unitRepository.findById(unit.getId())).thenReturn(Optional.empty());
    assertThrows(HrmCommonException.class, () -> unitService.update(unit));
  }

  // EXISTBYID
  // UTCID01 -exist: all valid
  @Test
  void testUTCID01_Exist_AllValid() {
    Long id = 1L;
    boolean exist = unitRepository.existsById(id);
    Assertions.assertNotNull(exist);
  }

  // UTCID02 -exist: id is null
  @Test
  void testUTCID02_Exist_IdNull() {
    Long id = null;
    assertThrows(HrmCommonException.class, () -> unitService.existById(id));
  }

  // GETBYNAME
  // UTCID01 -BETBYNAME: all valid
  @Test
  void testUTCID01_GetByName_AllValid() {
    String name = "Valid Unit Name";
    when(unitRepository.findByUnitName(name)).thenReturn(Optional.of(unitEntity));
    when(unitMapper.toDTO(unitEntity)).thenReturn(unit);
    UnitOfMeasurement result = unitService.getByName(name);
    Assertions.assertNotNull(result);
  }
}
