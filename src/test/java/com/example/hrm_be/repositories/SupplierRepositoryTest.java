package com.example.hrm_be.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.hrm_be.models.entities.SupplierEntity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SupplierRepositoryTest {

  @Mock private SupplierRepository supplierRepository;

  private SupplierEntity supplierEntity;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this); // Khởi tạo mock

    // Tạo một SupplierEntity mẫu để sử dụng trong các bài kiểm tra
    supplierEntity =
        SupplierEntity.builder()
            .id(1L)
            .supplierName("Tech Supplies")
            .phoneNumber("123456789")
            .email("contact@techsupplies.com")
            .address("123 Tech Street")
            .build();
  }

  @Test
  void shouldSaveSupplier() {
    // Thiết lập mock để trả về entity đã lưu
    when(supplierRepository.save(any(SupplierEntity.class))).thenReturn(supplierEntity);

    // Gọi phương thức save
    SupplierEntity savedEntity = supplierRepository.save(supplierEntity);

    // Kiểm tra rằng đối tượng đã được lưu thành công
    assertNotNull(savedEntity);
    assertEquals("Tech Supplies", savedEntity.getSupplierName());
    assertEquals("123 Tech Street", savedEntity.getAddress());
    assertEquals("123456789", savedEntity.getPhoneNumber());
    assertEquals("contact@techsupplies.com", savedEntity.getEmail());

    // Kiểm tra rằng phương thức save đã được gọi một lần với đối tượng bất kỳ
    verify(supplierRepository, times(1)).save(any(SupplierEntity.class));
  }

  @Test
  void shouldFindSupplierById() {
    // Thiết lập mock để trả về supplierEntity khi tìm kiếm theo ID
    when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplierEntity));

    // Gọi phương thức findById
    Optional<SupplierEntity> foundEntity = supplierRepository.findById(1L);

    // Kiểm tra rằng entity đã được tìm thấy và thông tin đúng
    assertTrue(foundEntity.isPresent());
    assertEquals("Tech Supplies", foundEntity.get().getSupplierName());
    assertEquals("123 Tech Street", foundEntity.get().getAddress());
    assertEquals("123456789", foundEntity.get().getPhoneNumber());
    assertEquals("contact@techsupplies.com", foundEntity.get().getEmail());

    // Kiểm tra rằng phương thức findById đã được gọi với ID 1L
    verify(supplierRepository, times(1)).findById(1L);
  }

  @Test
  void shouldReturnEmptyWhenSupplierNotFoundById() {
    // Thiết lập mock để trả về Optional.empty() khi tìm kiếm với ID không tồn tại
    when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

    // Gọi phương thức findById
    Optional<SupplierEntity> foundEntity = supplierRepository.findById(999L);

    // Kiểm tra rằng không tìm thấy entity
    assertFalse(foundEntity.isPresent());

    // Kiểm tra rằng phương thức findById đã được gọi với ID 999L
    verify(supplierRepository, times(1)).findById(999L);
  }

  @Test
  void shouldCheckIfSupplierExistsByNameAndAddress() {
    // Thiết lập mock để trả về true khi kiểm tra sự tồn tại của supplier theo tên và địa chỉ
    when(supplierRepository.existsBySupplierNameAndAddress("Tech Supplies", "123 Tech Street"))
        .thenReturn(true);

    // Gọi phương thức existsBySupplierNameAndAddress
    boolean exists =
        supplierRepository.existsBySupplierNameAndAddress("Tech Supplies", "123 Tech Street");

    // Kiểm tra rằng kết quả trả về là true
    assertTrue(exists);

    // Kiểm tra rằng phương thức existsBySupplierNameAndAddress đã được gọi một lần với tên và địa
    // chỉ đúng
    verify(supplierRepository, times(1))
        .existsBySupplierNameAndAddress("Tech Supplies", "123 Tech Street");
  }

  @Test
  void shouldReturnFalseWhenSupplierNotExistByNameAndAddress() {
    // Thiết lập mock để trả về false khi kiểm tra sự tồn tại của supplier không tồn tại
    when(supplierRepository.existsBySupplierNameAndAddress(
            "Nonexistent Supplier", "Unknown Address"))
        .thenReturn(false);

    // Gọi phương thức existsBySupplierNameAndAddress
    boolean exists =
        supplierRepository.existsBySupplierNameAndAddress(
            "Nonexistent Supplier", "Unknown Address");

    // Kiểm tra rằng kết quả trả về là false
    assertFalse(exists);

    // Kiểm tra rằng phương thức existsBySupplierNameAndAddress đã được gọi một lần với tên và địa
    // chỉ không tồn tại
    verify(supplierRepository, times(1))
        .existsBySupplierNameAndAddress("Nonexistent Supplier", "Unknown Address");
  }
}
