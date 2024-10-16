package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.repositories.BranchProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BranchProductServiceImplTest {

  @Mock private BranchProductRepository branchProductRepository;

  @Mock private BranchProductMapper branchProductMapper;

  @InjectMocks private BranchProductServiceImpl branchProductService;

  private BranchProductEntity branchProductEntity;
  private BranchProduct branchProductDto;

  @BeforeEach
  void setUp() {
    branchProductDto = new BranchProduct();
    branchProductDto.setId(1L);
    branchProductDto.setQuantity(10);
    branchProductDto.setMaxQuantity(20);
    branchProductDto.setMinQuantity(5);

    branchProductEntity =
        BranchProductEntity.builder().id(1L).quantity(10).maxQuantity(20).minQuantity(5).build();
  }

  @Test
  void testCreateBranchProduct_Success() {
    // Arrange
    when(branchProductMapper.toEntity(branchProductDto)).thenReturn(branchProductEntity);
    when(branchProductRepository.save(branchProductEntity)).thenReturn(branchProductEntity);
    when(branchProductMapper.toDTO(branchProductEntity)).thenReturn(branchProductDto);

    // Act
    BranchProduct result = branchProductService.create(branchProductDto);

    // Assert
    assertNotNull(result);
    assertEquals(branchProductDto, result);
    verify(branchProductRepository, times(1)).save(branchProductEntity);
  }

  @Test
  void testCreateBranchProduct_NullInput_ThrowsException() {
    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchProductService.create(null));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCHPRODUCT.EXIST, exception.getMessage());
  }

  @Test
  void testUpdateBranchProduct_Success() {
    // Arrange
    when(branchProductRepository.findById(1L)).thenReturn(Optional.of(branchProductEntity));
    when(branchProductRepository.save(branchProductEntity)).thenReturn(branchProductEntity);
    when(branchProductMapper.toDTO(branchProductEntity)).thenReturn(branchProductDto);

    // Act
    BranchProduct result = branchProductService.update(branchProductDto);

    // Assert
    assertNotNull(result);
    assertEquals(branchProductDto.getQuantity(), result.getQuantity());
    verify(branchProductRepository, times(1)).save(branchProductEntity);
  }

  @Test
  void testUpdateBranchProduct_NotFound_ThrowsException() {
    // Arrange
    when(branchProductRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchProductService.update(branchProductDto));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteBranchProduct_Success() {
    // Arrange
    when(branchProductRepository.findById(1L)).thenReturn(Optional.of(branchProductEntity));

    // Act
    branchProductService.delete(1L);

    // Assert
    verify(branchProductRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteBranchProduct_NotFound_ThrowsException() {
    // Arrange
    when(branchProductRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchProductService.delete(1L));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCHPRODUCT.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteBranchProduct_InvalidId() {
    // Act
    branchProductService.delete(null);

    // Assert
    verify(branchProductRepository, never()).deleteById(anyLong());
  }
}
