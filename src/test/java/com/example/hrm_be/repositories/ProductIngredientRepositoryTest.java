package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductIngredientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Transactional
@Rollback
class ProductIngredientRepositoryTest {

  @Autowired private ProductIngredientRepository productIngredientRepository;

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
    // Lưu nguyên liệu
    ProductIngredientEntity savedEntity = productIngredientRepository.save(ingredientEntity);

    // Kiểm tra rằng nguyên liệu đã được lưu và có ID
    assertNotNull(savedEntity.getId());
    assertEquals("Test Ingredient", savedEntity.getName());
    assertEquals("Test Description", savedEntity.getDescription());
  }

  @Test
  void shouldFindProductIngredientByName() {
    // Lưu nguyên liệu trước khi tìm kiếm
    productIngredientRepository.save(ingredientEntity);

    // Kiểm tra sự tồn tại của nguyên liệu theo tên
    boolean exists = productIngredientRepository.existsByName("Test Ingredient");

    // Kiểm tra kết quả
    assertTrue(exists);
  }

  @Test
  void shouldReturnFalseWhenCheckingNonExistentProductIngredientByName() {
    // Kiểm tra sự tồn tại của nguyên liệu chưa được lưu
    boolean exists = productIngredientRepository.existsByName("Nonexistent Ingredient");

    // Kiểm tra kết quả
    assertFalse(exists);
  }

  @Test
  void shouldFindProductIngredientById() {
    // Lưu nguyên liệu trước khi tìm kiếm
    ProductIngredientEntity savedEntity = productIngredientRepository.save(ingredientEntity);

    // Tìm kiếm nguyên liệu bằng ID
    Optional<ProductIngredientEntity> foundEntity =
        productIngredientRepository.findById(savedEntity.getId());

    // Kiểm tra kết quả không null và thông tin đúng
    assertTrue(foundEntity.isPresent());
    assertEquals("Test Ingredient", foundEntity.get().getName());
  }

  @Test
  void shouldReturnEmptyWhenFindingNonExistentProductIngredientById() {
    // Tìm kiếm nguyên liệu với ID không tồn tại
    Optional<ProductIngredientEntity> foundEntity = productIngredientRepository.findById(999L);

    // Kiểm tra kết quả là rỗng
    assertFalse(foundEntity.isPresent());
  }
}
