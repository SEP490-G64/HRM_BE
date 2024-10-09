package com.example.hrm_be.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.hrm_be.models.entities.ManufacturerEntity;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ManufacturerRepositoryTest {

  @Mock private ManufacturerRepository manufacturerRepository;

  private ManufacturerEntity manufacturerEntity;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this); // Khởi tạo mock

    // Tạo một ManufacturerEntity mẫu để sử dụng trong các bài kiểm tra
    manufacturerEntity =
        ManufacturerEntity.builder()
            .id(1L)
            .manufacturerName("Tech Supplies")
            .phoneNumber("123456789")
            .email("contact@techsupplies.com")
            .address("123 Tech Street")
            .build();
  }

  @Test
  void shouldSaveManufacturer() {
    // Thiết lập mock để trả về entity đã lưu
    when(manufacturerRepository.save(any(ManufacturerEntity.class))).thenReturn(manufacturerEntity);

    // Gọi phương thức save
    ManufacturerEntity savedEntity = manufacturerRepository.save(manufacturerEntity);

    // Kiểm tra rằng đối tượng đã được lưu thành công
    assertNotNull(savedEntity);
    assertEquals("Tech Supplies", savedEntity.getManufacturerName());
    assertEquals("123 Tech Street", savedEntity.getAddress());
    assertEquals("123456789", savedEntity.getPhoneNumber());
    assertEquals("contact@techsupplies.com", savedEntity.getEmail());

    // Kiểm tra rằng phương thức save đã được gọi một lần với đối tượng bất kỳ
    verify(manufacturerRepository, times(1)).save(any(ManufacturerEntity.class));
  }

  @Test
  void shouldFindManufacturerById() {
    // Thiết lập mock để trả về manufacturerEntity khi tìm kiếm theo ID
    when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(manufacturerEntity));

    // Gọi phương thức findById
    Optional<ManufacturerEntity> foundEntity = manufacturerRepository.findById(1L);

    // Kiểm tra rằng entity đã được tìm thấy và thông tin đúng
    assertTrue(foundEntity.isPresent());
    assertEquals("Tech Supplies", foundEntity.get().getManufacturerName());
    assertEquals("123 Tech Street", foundEntity.get().getAddress());
    assertEquals("123456789", foundEntity.get().getPhoneNumber());
    assertEquals("contact@techsupplies.com", foundEntity.get().getEmail());

    // Kiểm tra rằng phương thức findById đã được gọi với ID 1L
    verify(manufacturerRepository, times(1)).findById(1L);
  }

  @Test
  void shouldReturnEmptyWhenManufacturerNotFoundById() {
    // Thiết lập mock để trả về Optional.empty() khi tìm kiếm với ID không tồn tại
    when(manufacturerRepository.findById(999L)).thenReturn(Optional.empty());

    // Gọi phương thức findById
    Optional<ManufacturerEntity> foundEntity = manufacturerRepository.findById(999L);

    // Kiểm tra rằng không tìm thấy entity
    assertFalse(foundEntity.isPresent());

    // Kiểm tra rằng phương thức findById đã được gọi với ID 999L
    verify(manufacturerRepository, times(1)).findById(999L);
  }

  @Test
  void shouldCheckIfManufacturerExistsByNameAndAddress() {
    // Thiết lập mock để trả về true khi kiểm tra sự tồn tại của manufacturer theo tên và địa chỉ
    when(manufacturerRepository.existsByManufacturerNameAndAddress(
            "Tech Supplies", "123 Tech Street"))
        .thenReturn(true);

    // Gọi phương thức existsByManufacturerNameAndAddress
    boolean exists =
        manufacturerRepository.existsByManufacturerNameAndAddress(
            "Tech Supplies", "123 Tech Street");

    // Kiểm tra rằng kết quả trả về là true
    assertTrue(exists);

    // Kiểm tra rằng phương thức existsByManufacturerNameAndAddress đã được gọi một lần với tên và
    // địa
    // chỉ đúng
    verify(manufacturerRepository, times(1))
        .existsByManufacturerNameAndAddress("Tech Supplies", "123 Tech Street");
  }

  @Test
  void shouldReturnFalseWhenManufacturerNotExistByNameAndAddress() {
    // Thiết lập mock để trả về false khi kiểm tra sự tồn tại của manufacturer không tồn tại
    when(manufacturerRepository.existsByManufacturerNameAndAddress(
            "Nonexistent Manufacturer", "Unknown Address"))
        .thenReturn(false);

    // Gọi phương thức existsByManufacturerNameAndAddress
    boolean exists =
        manufacturerRepository.existsByManufacturerNameAndAddress(
            "Nonexistent Manufacturer", "Unknown Address");

    // Kiểm tra rằng kết quả trả về là false
    assertFalse(exists);

    // Kiểm tra rằng phương thức existsByManufacturerNameAndAddress đã được gọi một lần với tên và
    // địa
    // chỉ không tồn tại
    verify(manufacturerRepository, times(1))
        .existsByManufacturerNameAndAddress("Nonexistent Manufacturer", "Unknown Address");
  }
}
