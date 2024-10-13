package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.repositories.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

  @Mock private SupplierRepository supplierRepository;

  @Mock private SupplierMapper supplierMapper;

  @InjectMocks private SupplierServiceImpl supplierService;

  private SupplierEntity supplierEntity;
  private Supplier supplierDTO;

  @BeforeEach
  void setup() {
    supplierEntity = new SupplierEntity();
    supplierEntity.setId(1L);
    supplierEntity.setSupplierName("Test Supplier");
    supplierEntity.setAddress("123 Test Street");
    supplierEntity.setTaxCode("12345");

    supplierDTO = new Supplier();
    supplierDTO.setId(1L);
    supplierDTO.setSupplierName("Test Supplier");
    supplierDTO.setAddress("123 Test Street");
    supplierDTO.setTaxCode("12345");
  }

  @Test
  void shouldGetById() {
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    Supplier result = supplierService.getById(1L);

    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    assertEquals("123 Test Street", result.getAddress());
    verify(supplierRepository, times(1)).findById(1L);
  }

  @Test
  void shouldReturnNullWhenSupplierNotFound() {
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

    Supplier result = supplierService.getById(1L);

    assertNull(result);
    verify(supplierRepository, times(1)).findById(1L);
  }

  @Test
  void shouldGetByPaging() {
    Page<SupplierEntity> page = new PageImpl<>(List.of(supplierEntity));
    when(supplierRepository.findBySupplierNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
            anyString(), anyString(), any(Pageable.class)))
        .thenReturn(page);
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    Page<Supplier> result = supplierService.getByPaging(0, 10, "supplierName", "");

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals("Test Supplier", result.getContent().get(0).getSupplierName());
    verify(supplierRepository, times(1))
        .findBySupplierNameContainsIgnoreCaseOrAddressContainsIgnoreCase(
            anyString(), anyString(), any(Pageable.class));
  }

  @Test
  void shouldCreateSupplier() {
    when(supplierMapper.toEntity(any(Supplier.class))).thenReturn(supplierEntity);
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(false);
    when(supplierRepository.existsByTaxCode(anyString())).thenReturn(false); // Check tax code
    when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(supplierEntity);
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    Supplier result = supplierService.create(supplierDTO);

    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    verify(supplierRepository, times(1)).save(any(SupplierEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingExistingSupplier() {
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(true);

    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.create(supplierDTO));

    assertEquals(HrmConstant.ERROR.SUPPLIER.EXIST, exception.getMessage());
    verify(supplierRepository, never()).save(any(SupplierEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingWithExistingTaxCode() {
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(false);
    when(supplierRepository.existsByTaxCode(anyString())).thenReturn(true);

    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.create(supplierDTO));

    assertEquals(HrmConstant.ERROR.SUPPLIER.TAXCODE_NOT_EXIST, exception.getMessage());
    verify(supplierRepository, never()).save(any(SupplierEntity.class));
  }

  @Test
  void shouldUpdateSupplier() {
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(false); // Name/Address doesn't exist for another entity
    when(supplierRepository.existsByTaxCode(anyString()))
        .thenReturn(false); // Tax code doesn't exist for another entity
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);
    when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(supplierEntity);

    Supplier result = supplierService.update(supplierDTO);

    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    verify(supplierRepository, times(1)).save(any(SupplierEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentSupplier() {
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.update(supplierDTO));

    assertEquals(HrmConstant.ERROR.SUPPLIER.NOT_EXIST, exception.getMessage());
    verify(supplierRepository, never()).save(any(SupplierEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingWithExistingNameAndAddress() {
    // Simulate finding the supplier to update (the one being updated)
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));

    // Simulate another supplier with the same name and address exists
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(true);

    // Update both the name and address to be different to trigger the exception
    supplierDTO.setSupplierName("Different Supplier Name");
    supplierDTO.setAddress("Different Address");

    // Now, the exception should be thrown since name/address duplication exists
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.update(supplierDTO));

    // Verify the correct exception is thrown with the expected message
    assertEquals(HrmConstant.ERROR.SUPPLIER.EXIST, exception.getMessage());

    // Ensure the save method is never called since the exception is thrown
    verify(supplierRepository, never()).save(any(SupplierEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingWithExistingTaxCode() {
    // Simulate finding the supplier to update
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));

    // Simulate another supplier exists with the same tax code
    when(supplierRepository.existsByTaxCode(anyString())).thenReturn(true);

    // Ensure the supplierDTO has a different tax code than the existing supplier
    supplierDTO.setTaxCode("Different Tax Code");

    // Now, the exception should be thrown since tax code duplication exists
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.update(supplierDTO));

    assertEquals(HrmConstant.ERROR.SUPPLIER.TAXCODE_NOT_EXIST, exception.getMessage());
    verify(supplierRepository, never()).save(any(SupplierEntity.class));
  }

  @Test
  void shouldDeleteSupplier() {
    // Simulate the supplier entity being present in the repository
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));

    // No action required when deleteById is called
    doNothing().when(supplierRepository).deleteById(anyLong());

    // Assert that no exception is thrown when deleting the supplier
    assertDoesNotThrow(() -> supplierService.delete(1L));

    // Verify that the repository's deleteById method was called once with the correct ID
    verify(supplierRepository, times(1)).deleteById(1L);
  }

  @Test
  void shouldDeleteSupplierWithBlankId() {
    assertDoesNotThrow(() -> supplierService.delete(null));
    verify(supplierRepository, never()).deleteById(anyLong());
  }
}
