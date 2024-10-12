package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ManufacturerMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import com.example.hrm_be.repositories.ManufacturerRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class) // Sử dụng Mockito để test
public class ManufacturerServiceImplTest {
  @Mock private ManufacturerRepository manufacturerRepository;

  @Mock private ManufacturerMapper manufacturerMapper;

  @InjectMocks private ManufacturerServiceImpl manufacturerService;

  private ManufacturerEntity manufacturerEntity; // Đối tượng ManufacturerEntity dùng để test
  private Manufacturer manufacturerDTO; // Đối tượng Manufacturer DTO dùng để test

  @BeforeEach
  void setup() {
    // Khởi tạo các đối tượng trước mỗi test case
    manufacturerEntity = new ManufacturerEntity();
    manufacturerEntity.setId(1L);
    manufacturerEntity.setManufacturerName("Test Manufacturer");
    manufacturerEntity.setAddress("123 Test Street");

    manufacturerDTO = new Manufacturer();
    manufacturerDTO.setId(1L);
    manufacturerDTO.setManufacturerName("Test Manufacturer");
    manufacturerDTO.setAddress("123 Test Street");
  }

  @Test
  void shouldGetById() {
    when(manufacturerRepository.findById(anyLong())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerMapper.toDTO(any(ManufacturerEntity.class))).thenReturn(manufacturerDTO);

    Manufacturer result = manufacturerService.getById(1L);

    assertNotNull(result);
    assertEquals("Test Manufacturer", result.getManufacturerName());
    assertEquals("123 Test Street", result.getAddress());
    verify(manufacturerRepository, times(1)).findById(1L);
  }

  @Test
  void shouldReturnNullWhenManufacturerNotFound() {
    when(manufacturerRepository.findById(anyLong())).thenReturn(Optional.empty());

    Manufacturer result = manufacturerService.getById(1L);

    assertNull(result);
    verify(manufacturerRepository, times(1)).findById(1L);
  }

  @Test
  void shouldGetByPaging() {
    Page<ManufacturerEntity> page = new PageImpl<>(List.of(manufacturerEntity));
    when(manufacturerRepository.findByManufacturerNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
            anyString(), anyString(), any(Pageable.class)))
        .thenReturn(page);
    when(manufacturerMapper.toDTO(any(ManufacturerEntity.class))).thenReturn(manufacturerDTO);

    Page<Manufacturer> result = manufacturerService.getByPaging(0, 10, "manufacturerName", "");

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals("Test Manufacturer", result.getContent().get(0).getManufacturerName());
    verify(manufacturerRepository, times(1))
        .findByManufacturerNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
            anyString(), anyString(), any(Pageable.class));
  }

  @Test
  void shouldCreateManufacturer() {
    when(manufacturerMapper.toEntity(any(Manufacturer.class))).thenReturn(manufacturerEntity);
    when(manufacturerRepository.existsByManufacturerNameAndAddress(anyString(), anyString()))
        .thenReturn(false);
    when(manufacturerRepository.save(any(ManufacturerEntity.class))).thenReturn(manufacturerEntity);
    when(manufacturerMapper.toDTO(any(ManufacturerEntity.class))).thenReturn(manufacturerDTO);

    Manufacturer result = manufacturerService.create(manufacturerDTO);

    assertNotNull(result);
    assertEquals("Test Manufacturer", result.getManufacturerName());
    verify(manufacturerRepository, times(1)).save(any(ManufacturerEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingExistingManufacturer() {
    when(manufacturerRepository.existsByManufacturerNameAndAddress(anyString(), anyString()))
        .thenReturn(true);

    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> manufacturerService.create(manufacturerDTO));

    assertEquals(HrmConstant.ERROR.MANUFACTURER.EXIST, exception.getMessage());
    verify(manufacturerRepository, never()).save(any(ManufacturerEntity.class));
  }

  @Test
  void shouldUpdateManufacturer() {
    when(manufacturerRepository.findById(anyLong())).thenReturn(Optional.of(manufacturerEntity));
    when(manufacturerMapper.toDTO(any(ManufacturerEntity.class))).thenReturn(manufacturerDTO);
    when(manufacturerRepository.save(any(ManufacturerEntity.class))).thenReturn(manufacturerEntity);

    Manufacturer result = manufacturerService.update(manufacturerDTO);

    assertNotNull(result);
    assertEquals("Test Manufacturer", result.getManufacturerName());
    verify(manufacturerRepository, times(1)).save(any(ManufacturerEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentManufacturer() {
    when(manufacturerRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> manufacturerService.update(manufacturerDTO));

    assertEquals(HrmConstant.ERROR.MANUFACTURER.NOT_EXIST, exception.getMessage());
    verify(manufacturerRepository, never()).save(any(ManufacturerEntity.class));
  }

  @Test
  void shouldDeleteManufacturer() {
    // Create a mock ManufacturerEntity that simulates the entity in the database
    ManufacturerEntity manufacturer = new ManufacturerEntity();
    manufacturer.setId(1L); // Set the ID to match the one we're deleting

    // Mock the repository to return the manufacturer when findById is called
    when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturer));
    // Mock the deleteById method to do nothing
    doNothing().when(manufacturerRepository).deleteById(anyLong());

    // Now call the delete method and verify
    assertDoesNotThrow(() -> manufacturerService.delete(1L));
    verify(manufacturerRepository, times(1)).deleteById(1L);
  }

  @Test
  void shouldDeleteManufacturerWithBlankId() {
    assertDoesNotThrow(() -> manufacturerService.delete(null));
    verify(manufacturerRepository, never()).deleteById(anyLong());
  }
}
