package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductIngredientEntity;
import com.example.hrm_be.services.impl.ProductIngredientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductIngredientRepositoryTest {

    @Mock
    private ProductIngredientRepository productIngredientRepository; // Mock repository

    @InjectMocks
    private ProductIngredientServiceImpl productIngredientServiceImpl; // Service sử dụng repository

    private ProductIngredientEntity ingredientEntity;

    @BeforeEach
    void setup() {
        // Khởi tạo đối tượng trước mỗi test case
        ingredientEntity = new ProductIngredientEntity();
        ingredientEntity.setName("Test Ingredient");
        ingredientEntity.setDescription("Test Description");
    }

    @Test
    void shouldSaveProductIngredient() {
        // Thiết lập mock để trả về entity đã lưu
        when(productIngredientRepository.save(any(ProductIngredientEntity.class))).thenReturn(ingredientEntity);

        // Gọi phương thức save
        ProductIngredientEntity savedEntity = productIngredientRepository.save(ingredientEntity);

        // Kiểm tra rằng nguyên liệu đã được lưu và có ID
        assertNotNull(savedEntity);
        assertEquals("Test Ingredient", savedEntity.getName());
        assertEquals("Test Description", savedEntity.getDescription());
    }

    @Test
    void shouldFindProductIngredientByName() {
        // Thiết lập mock để trả về true khi kiểm tra sự tồn tại theo tên
        when(productIngredientRepository.existsByName("Test Ingredient")).thenReturn(true);

        // Kiểm tra sự tồn tại của nguyên liệu theo tên
        boolean exists = productIngredientRepository.existsByName("Test Ingredient");

        // Kiểm tra kết quả
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenCheckingNonExistentProductIngredientByName() {
        // Thiết lập mock để trả về false khi kiểm tra sự tồn tại theo tên
        when(productIngredientRepository.existsByName("Nonexistent Ingredient")).thenReturn(false);

        // Kiểm tra sự tồn tại của nguyên liệu chưa được lưu
        boolean exists = productIngredientRepository.existsByName("Nonexistent Ingredient");

        // Kiểm tra kết quả
        assertFalse(exists);
    }

    @Test
    void shouldFindProductIngredientById() {
        // Thiết lập mock để trả về ingredientEntity khi tìm kiếm theo ID
        when(productIngredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredientEntity));

        // Tìm kiếm nguyên liệu bằng ID
        Optional<ProductIngredientEntity> foundEntity = productIngredientRepository.findById(1L);

        // Kiểm tra kết quả không null và thông tin đúng
        assertTrue(foundEntity.isPresent());
        assertEquals("Test Ingredient", foundEntity.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentProductIngredientById() {
        // Thiết lập mock để trả về Optional.empty() khi tìm kiếm theo ID không tồn tại
        when(productIngredientRepository.findById(999L)).thenReturn(Optional.empty());

        // Tìm kiếm nguyên liệu với ID không tồn tại
        Optional<ProductIngredientEntity> foundEntity = productIngredientRepository.findById(999L);

        // Kiểm tra kết quả là rỗng
        assertFalse(foundEntity.isPresent());
    }
}
