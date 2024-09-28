package com.example.hrm_be.repositories;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.models.entities.ProductIngredientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@Transactional
class ProductIngredientRepositoryTest {

    @Autowired
    private ProductIngredientRepository productIngredientRepository;

    private ProductIngredientEntity ingredientEntity;

    // Khởi tạo đối tượng trước mỗi test
    @BeforeEach
    void setup() {
        ingredientEntity = new ProductIngredientEntity();
        ingredientEntity.setName("Test Ingredient");
        ingredientEntity.setDescription("Test Description");
    }

    // Test lưu nguyên liệu
    @Test
    void shouldSaveProductIngredient() {
        ProductIngredientEntity savedEntity = productIngredientRepository.save(ingredientEntity);

        // Kiểm tra rằng nguyên liệu đã được lưu và có ID
        assertNotNull(savedEntity.getId());
        assertEquals("Test Ingredient", savedEntity.getName());
        assertEquals("Test Description", savedEntity.getDescription());
    }

    // Test tìm nguyên liệu theo tên
    @Test
    void shouldFindProductIngredientByName() {
        productIngredientRepository.save(ingredientEntity);

        // Kiểm tra sự tồn tại của nguyên liệu theo tên
        boolean exists = productIngredientRepository.existsByName("Test Ingredient");

        // Kiểm tra kết quả
        assertTrue(exists);
    }

    // Test không tìm thấy nguyên liệu chưa lưu theo tên
    @Test
    void shouldReturnFalseWhenCheckingNonExistentProductIngredientByName() {
        boolean exists = productIngredientRepository.existsByName("Nonexistent Ingredient");

        // Kiểm tra kết quả
        assertFalse(exists);
    }

    // Test tìm nguyên liệu theo ID
    @Test
    void shouldFindProductIngredientById() {
        ProductIngredientEntity savedEntity = productIngredientRepository.save(ingredientEntity);

        // Tìm kiếm nguyên liệu bằng ID
        Optional<ProductIngredientEntity> foundEntity = productIngredientRepository.findById(savedEntity.getId());

        // Kiểm tra kết quả không null và thông tin đúng
        assertTrue(foundEntity.isPresent());
        assertEquals("Test Ingredient", foundEntity.get().getName());
    }

    // Test không tìm thấy nguyên liệu theo ID không tồn tại
    @Test
    void shouldReturnEmptyWhenFindingNonExistentProductIngredientById() {
        Optional<ProductIngredientEntity> foundEntity = productIngredientRepository.findById(999L);

        // Kiểm tra kết quả là rỗng
        assertFalse(foundEntity.isPresent());
    }
}
