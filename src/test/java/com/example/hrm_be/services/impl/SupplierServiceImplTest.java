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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Sử dụng Mockito để test
class SupplierServiceImplTest {

  @Mock // Tạo mock cho SupplierRepository
  private SupplierRepository supplierRepository;

  @Mock // Tạo mock cho SupplierMapper
  private SupplierMapper supplierMapper;

  @InjectMocks // Tiêm các mock vào SupplierServiceImpl
  private SupplierServiceImpl supplierService;

  private SupplierEntity supplierEntity; // Đối tượng SupplierEntity dùng để test
  private Supplier supplierDTO; // Đối tượng Supplier DTO dùng để test

  @BeforeEach
  void setup() {
    // Khởi tạo các đối tượng trước mỗi test case
    supplierEntity = new SupplierEntity();
    supplierEntity.setId(1L);
    supplierEntity.setSupplierName("Test Supplier");
    supplierEntity.setAddress("123 Test Street");

    supplierDTO = new Supplier();
    supplierDTO.setId(1L);
    supplierDTO.setSupplierName("Test Supplier");
    supplierDTO.setAddress("123 Test Street");
  }

  @Test
  void shouldGetById() {
    // Thiết lập hành vi cho mock repository
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    // Gọi phương thức getById
    Supplier result = supplierService.getById(1L);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    assertEquals("123 Test Street", result.getAddress());
    verify(supplierRepository, times(1))
        .findById(1L); // Đảm bảo rằng phương thức đã được gọi đúng 1 lần
  }

  @Test
  void shouldReturnNullWhenSupplierNotFound() {
    // Thiết lập hành vi khi không tìm thấy nhà cung cấp
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Gọi phương thức getById
    Supplier result = supplierService.getById(1L);

    // Kiểm tra kết quả là null
    assertNull(result);
    verify(supplierRepository, times(1))
        .findById(1L); // Đảm bảo rằng phương thức đã được gọi đúng 1 lần
  }

  @Test
  void shouldGetByPaging() {
    // Thiết lập một page giả cho SupplierEntity
    Page<SupplierEntity> page = new PageImpl<>(List.of(supplierEntity));
    when(supplierRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    // Gọi phương thức getByPaging
    Page<Supplier> result = supplierService.getByPaging(0, 10, "supplierName");

    // Kiểm tra kết quả
    assertNotNull(result);
    assertEquals(1, result.getTotalElements()); // Kiểm tra số lượng nhà cung cấp trong page
    assertEquals(
        "Test Supplier", result.getContent().get(0).getSupplierName()); // Kiểm tra tên nhà cung cấp
    verify(supplierRepository, times(1))
        .findAll(any(Pageable.class)); // Đảm bảo phương thức đã được gọi
  }

  @Test
  void shouldCreateSupplier() {
    // Thiết lập hành vi cho việc chuyển đổi và lưu nhà cung cấp
    when(supplierMapper.toEntity(any(Supplier.class))).thenReturn(supplierEntity);
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(false); // Nhà cung cấp không tồn tại
    when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(supplierEntity);
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);

    // Gọi phương thức create
    Supplier result = supplierService.create(supplierDTO);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    verify(supplierRepository, times(1))
        .save(any(SupplierEntity.class)); // Đảm bảo rằng phương thức save đã được gọi
  }

  @Test
  void shouldThrowExceptionWhenCreatingExistingSupplier() {
    // Thiết lập hành vi khi nhà cung cấp đã tồn tại
    when(supplierRepository.existsBySupplierNameAndAddress(anyString(), anyString()))
        .thenReturn(true);

    // Kiểm tra rằng một ngoại lệ được ném ra khi gọi create
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.create(supplierDTO));

    // Kiểm tra thông điệp ngoại lệ
    assertEquals(HrmConstant.ERROR.ROLE.EXIST, exception.getMessage());
    verify(supplierRepository, never())
        .save(any(SupplierEntity.class)); // Đảm bảo rằng save không được gọi
  }

  @Test
  void shouldUpdateSupplier() {
    // Thiết lập hành vi khi tìm thấy nhà cung cấp cũ để cập nhật
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.of(supplierEntity));
    when(supplierMapper.toDTO(any(SupplierEntity.class))).thenReturn(supplierDTO);
    when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(supplierEntity);

    // Gọi phương thức update
    Supplier result = supplierService.update(supplierDTO);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Supplier", result.getSupplierName());
    verify(supplierRepository, times(1))
        .save(any(SupplierEntity.class)); // Đảm bảo phương thức save đã được gọi
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentSupplier() {
    // Thiết lập hành vi khi không tìm thấy nhà cung cấp
    when(supplierRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Kiểm tra rằng một ngoại lệ được ném ra khi gọi update
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> supplierService.update(supplierDTO));

    // Kiểm tra thông điệp ngoại lệ
    assertEquals(HrmConstant.ERROR.ROLE.NOT_EXIST, exception.getMessage());
    verify(supplierRepository, never())
        .save(any(SupplierEntity.class)); // Đảm bảo rằng save không được gọi
  }

  @Test
  void shouldDeleteSupplier() {
    // Không có hành vi nào cần thiết cho delete
    doNothing().when(supplierRepository).deleteById(anyLong());

    // Gọi phương thức delete và kiểm tra không có ngoại lệ ném ra
    assertDoesNotThrow(() -> supplierService.delete(1L));
    verify(supplierRepository, times(1))
        .deleteById(1L); // Đảm bảo phương thức deleteById đã được gọi
  }

  @Test
  void shouldDeleteSupplierWithBlankId() {
    // Kiểm tra rằng không có hành động nào diễn ra khi ID là null
    assertDoesNotThrow(() -> supplierService.delete(null));
    verify(supplierRepository, never())
        .deleteById(anyLong()); // Đảm bảo rằng deleteById không được gọi
  }
}
