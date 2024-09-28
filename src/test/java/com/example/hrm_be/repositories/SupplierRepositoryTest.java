package com.example.hrm_be.repositories;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Transactional
class SupplierRepositoryTest {

    @Autowired
    private SupplierRepository supplierRepository;

    private SupplierEntity supplier;

    // Khởi tạo entity trước mỗi test
    @BeforeEach
    void setup() {
        supplier = new SupplierEntity();
        supplier.setSupplierName("Test Supplier");
        supplier.setAddress("123 Main Street");
    }

    // Test tạo nhà cung cấp mới
    @Test
    void shouldCreateSupplier() {
        SupplierEntity savedSupplier = supplierRepository.save(supplier);

        // Kiểm tra entity đã được lưu thành công
        assertNotNull(savedSupplier);
        assertNotNull(savedSupplier.getId());
        assertEquals("Test Supplier", savedSupplier.getSupplierName());
        assertEquals("123 Main Street", savedSupplier.getAddress());
    }

    // Test lấy tất cả các nhà cung cấp
    @Test
    void shouldListAllSuppliers() {
        supplierRepository.save(supplier);

        // Thêm một nhà cung cấp khác
        SupplierEntity anotherSupplier = new SupplierEntity();
        anotherSupplier.setSupplierName("Another Supplier");
        anotherSupplier.setAddress("456 Another Street");
        supplierRepository.save(anotherSupplier);

        // Liệt kê tất cả các nhà cung cấp
        List<SupplierEntity> suppliers = supplierRepository.findAll();

        // Kiểm tra số lượng nhà cung cấp
        assertFalse(suppliers.isEmpty());
        assertEquals(2, suppliers.size());
    }

    // Test lấy nhà cung cấp theo ID
    @Test
    void shouldFindSupplierById() {
        SupplierEntity savedSupplier = supplierRepository.save(supplier);

        // Tìm nhà cung cấp theo ID
        Optional<SupplierEntity> foundSupplier = supplierRepository.findById(savedSupplier.getId());

        // Kiểm tra nhà cung cấp đã được tìm thấy
        assertTrue(foundSupplier.isPresent());
        assertEquals(savedSupplier.getId(), foundSupplier.get().getId());
    }

    // Test cập nhật nhà cung cấp
    @Test
    void shouldUpdateSupplier() {
        SupplierEntity savedSupplier = supplierRepository.save(supplier);

        // Cập nhật thông tin nhà cung cấp
        savedSupplier.setSupplierName("Updated Supplier");
        savedSupplier.setAddress("456 Updated Street");
        SupplierEntity updatedSupplier = supplierRepository.save(savedSupplier);

        // Kiểm tra thông tin đã được cập nhật
        assertEquals("Updated Supplier", updatedSupplier.getSupplierName());
        assertEquals("456 Updated Street", updatedSupplier.getAddress());
    }

    // Test xóa nhà cung cấp
    @Test
    void shouldDeleteSupplier() {
        SupplierEntity savedSupplier = supplierRepository.save(supplier);

        // Xóa nhà cung cấp
        supplierRepository.deleteById(savedSupplier.getId());

        // Kiểm tra nhà cung cấp đã bị xóa
        Optional<SupplierEntity> deletedSupplier = supplierRepository.findById(savedSupplier.getId());
        assertFalse(deletedSupplier.isPresent());
    }

    // Test kiểm tra xem nhà cung cấp có trùng tên và địa chỉ không
    @Test
    void shouldCheckIfSupplierExistsByNameAndAddress() {
        supplierRepository.save(supplier);

        // Kiểm tra có tồn tại nhà cung cấp với tên và địa chỉ đã cho
        boolean exists = supplierRepository.existsBySupplierNameAndAddress("Test Supplier", "123 Main Street");

        // Kiểm tra kết quả
        assertTrue(exists);
    }

    // Test kiểm tra không tồn tại nhà cung cấp trùng tên và địa chỉ
    @Test
    void shouldReturnFalseIfSupplierDoesNotExistByNameAndAddress() {
        supplierRepository.save(supplier);

        // Kiểm tra không tồn tại nhà cung cấp với tên và địa chỉ khác
        boolean exists = supplierRepository.existsBySupplierNameAndAddress("Non-existent Supplier", "999 Non-existent Street");

        // Kiểm tra kết quả
        assertFalse(exists);
    }
}