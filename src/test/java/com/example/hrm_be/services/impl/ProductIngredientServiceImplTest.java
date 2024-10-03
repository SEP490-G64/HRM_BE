package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
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
class ProductIngredientServiceImplTest {

  @Mock // Tạo mock cho ProductIngredientRepository
  private ProductIngredientRepository ingredientRepository;

  @Mock // Tạo mock cho ProductIngredientMapper
  private ProductIngredientMapper ingredientMapper;

  @InjectMocks // Tiêm các mock vào ProductIngredientServiceImpl
  private ProductIngredientServiceImpl productIngredientService;

  private ProductIngredientEntity
      ingredientEntity; // Đối tượng ProductIngredientEntity dùng để test
  private ProductIngredient ingredientDTO; // Đối tượng ProductIngredient DTO dùng để test

  @BeforeEach
  void setup() {
    // Khởi tạo các đối tượng trước mỗi test case
    ingredientEntity = new ProductIngredientEntity();
    ingredientEntity.setId(1L);
    ingredientEntity.setName("Test Ingredient");
    ingredientEntity.setDescription("Test Description");

    ingredientDTO = new ProductIngredient();
    ingredientDTO.setId(1L);
    ingredientDTO.setName("Test Ingredient");
    ingredientDTO.setDescription("Test Description");
  }

  @Test
  void shouldGetById() {
    // Thiết lập hành vi cho mock repository
    when(ingredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredientEntity));
    when(ingredientMapper.toDTO(any(ProductIngredientEntity.class))).thenReturn(ingredientDTO);

    // Gọi phương thức getById
    ProductIngredient result = productIngredientService.getById(1L);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Ingredient", result.getName());
    assertEquals("Test Description", result.getDescription());
    verify(ingredientRepository, times(1))
        .findById(1L); // Đảm bảo rằng phương thức đã được gọi đúng 1 lần
  }

  @Test
  void shouldReturnNullWhenIngredientNotFound() {
    // Thiết lập hành vi khi không tìm thấy nguyên liệu
    when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Gọi phương thức getById
    ProductIngredient result = productIngredientService.getById(1L);

    // Kiểm tra kết quả là null
    assertNull(result);
    verify(ingredientRepository, times(1))
        .findById(1L); // Đảm bảo rằng phương thức đã được gọi đúng 1 lần
  }

  @Test
  void shouldGetByPaging() {
    // Thiết lập một page giả cho ProductIngredientEntity
    Page<ProductIngredientEntity> page = new PageImpl<>(List.of(ingredientEntity));
    when(ingredientRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(ingredientMapper.toDTO(any(ProductIngredientEntity.class))).thenReturn(ingredientDTO);

    // Gọi phương thức getByPaging
    Page<ProductIngredient> result = productIngredientService.getByPaging(0, 10, "name");

    // Kiểm tra kết quả
    assertNotNull(result);
    assertEquals(1, result.getTotalElements()); // Kiểm tra số lượng nguyên liệu trong page
    assertEquals(
        "Test Ingredient", result.getContent().get(0).getName()); // Kiểm tra tên nguyên liệu
    verify(ingredientRepository, times(1))
        .findAll(any(Pageable.class)); // Đảm bảo phương thức đã được gọi
  }

  @Test
  void shouldCreateIngredient() {
    // Thiết lập hành vi cho việc chuyển đổi và lưu nguyên liệu
    when(ingredientMapper.toEntity(any(ProductIngredient.class))).thenReturn(ingredientEntity);
    when(ingredientRepository.existsByName(anyString()))
        .thenReturn(false); // Nguyên liệu không tồn tại
    when(ingredientRepository.save(any(ProductIngredientEntity.class)))
        .thenReturn(ingredientEntity);
    when(ingredientMapper.toDTO(any(ProductIngredientEntity.class))).thenReturn(ingredientDTO);

    // Gọi phương thức create
    ProductIngredient result = productIngredientService.create(ingredientDTO);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Ingredient", result.getName());
    verify(ingredientRepository, times(1))
        .save(any(ProductIngredientEntity.class)); // Đảm bảo rằng phương thức save đã được gọi
  }

  @Test
  void shouldThrowExceptionWhenCreatingExistingIngredient() {
    // Thiết lập hành vi khi nguyên liệu đã tồn tại
    when(ingredientRepository.existsByName(anyString())).thenReturn(true);

    // Kiểm tra rằng một ngoại lệ được ném ra khi gọi create
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class, () -> productIngredientService.create(ingredientDTO));

    // Kiểm tra thông điệp ngoại lệ
    assertEquals(HrmConstant.ERROR.INGREDIENT.EXIST, exception.getMessage());
    verify(ingredientRepository, never())
        .save(any(ProductIngredientEntity.class)); // Đảm bảo rằng save không được gọi
  }

  @Test
  void shouldUpdateIngredient() {
    // Thiết lập hành vi khi tìm thấy nguyên liệu cũ để cập nhật
    when(ingredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredientEntity));
    when(ingredientMapper.toDTO(any(ProductIngredientEntity.class))).thenReturn(ingredientDTO);
    when(ingredientRepository.save(any(ProductIngredientEntity.class)))
        .thenReturn(ingredientEntity);

    // Gọi phương thức update
    ProductIngredient result = productIngredientService.update(ingredientDTO);

    // Kiểm tra kết quả không null và thông tin đúng
    assertNotNull(result);
    assertEquals("Test Ingredient", result.getName());
    verify(ingredientRepository, times(1))
        .save(any(ProductIngredientEntity.class)); // Đảm bảo phương thức save đã được gọi
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentIngredient() {
    // Thiết lập hành vi khi không tìm thấy nguyên liệu
    when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Kiểm tra rằng một ngoại lệ được ném ra khi gọi update
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class, () -> productIngredientService.update(ingredientDTO));

    // Kiểm tra thông điệp ngoại lệ
    assertEquals(HrmConstant.ERROR.INGREDIENT.NOT_EXIST, exception.getMessage());
    verify(ingredientRepository, never())
        .save(any(ProductIngredientEntity.class)); // Đảm bảo rằng save không được gọi
  }

  @Test
  void shouldDeleteIngredient() {
    // Không có hành vi nào cần thiết cho delete
    doNothing().when(ingredientRepository).deleteById(anyLong());

    // Gọi phương thức delete và kiểm tra không có ngoại lệ ném ra
    assertDoesNotThrow(() -> productIngredientService.delete(1L));
    verify(ingredientRepository, times(1))
        .deleteById(1L); // Đảm bảo phương thức deleteById đã được gọi
  }

  @Test
  void shouldDeleteIngredientWithBlankId() {
    // Kiểm tra rằng không có hành động nào diễn ra khi ID là null
    assertDoesNotThrow(() -> productIngredientService.delete(null));
    verify(ingredientRepository, never())
        .deleteById(anyLong()); // Đảm bảo rằng deleteById không được gọi
  }
}
