package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchBatchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.repositories.BranchBatchRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BranchBatchServiceImplTest {

  @Mock private BranchBatchRepository branchBatchRepository;

  @Mock private BranchBatchMapper branchBatchMapper;

  @InjectMocks private BranchBatchServiceImpl branchBatchService;

  private BranchBatchEntity branchBatchEntity;
  private BranchBatch branchBatchDto;

  @BeforeEach
  void setUp() {
    branchBatchDto = new BranchBatch();
    branchBatchDto.setId(1L);
    branchBatchDto.setQuantity(BigDecimal.TEN);

    branchBatchEntity = BranchBatchEntity.builder().id(1L).quantity(BigDecimal.TEN).build();
  }

  @Test
  void testCreateBranchBatch_Success() {
    // Arrange
    when(branchBatchMapper.toEntity(branchBatchDto)).thenReturn(branchBatchEntity);
    when(branchBatchRepository.save(branchBatchEntity)).thenReturn(branchBatchEntity);
    when(branchBatchMapper.toDTO(branchBatchEntity)).thenReturn(branchBatchDto);

    // Act
    BranchBatch result = branchBatchService.create(branchBatchDto);

    // Assert
    assertNotNull(result);
    assertEquals(branchBatchDto, result);
    verify(branchBatchRepository, times(1)).save(branchBatchEntity);
  }

  @Test
  void testCreateBranchBatch_NullInput_ThrowsException() {
    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchBatchService.create(null));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCHBATCH.EXIST, exception.getMessage());
  }

  @Test
  void testUpdateBranchBatch_Success() {
    // Arrange
    when(branchBatchRepository.findById(1L)).thenReturn(Optional.of(branchBatchEntity));
    when(branchBatchRepository.save(branchBatchEntity)).thenReturn(branchBatchEntity);
    when(branchBatchMapper.toDTO(branchBatchEntity)).thenReturn(branchBatchDto);

    // Act
    BranchBatch result = branchBatchService.update(branchBatchDto);

    // Assert
    assertNotNull(result);
    assertEquals(branchBatchDto.getQuantity(), result.getQuantity());
    verify(branchBatchRepository, times(1)).save(branchBatchEntity);
  }

  @Test
  void testUpdateBranchBatch_NotFound_ThrowsException() {
    // Arrange
    when(branchBatchRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchBatchService.update(branchBatchDto));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteBranchBatch_Success() {
    // Arrange
    when(branchBatchRepository.findById(1L)).thenReturn(Optional.of(branchBatchEntity));

    // Act
    branchBatchService.delete(1L);

    // Assert
    verify(branchBatchRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteBranchBatch_NotFound_ThrowsException() {
    // Arrange
    when(branchBatchRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> branchBatchService.delete(1L));

    // Assert
    assertEquals(HrmConstant.ERROR.BRANCHBATCH.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteBranchBatch_InvalidId() {
    // Act
    branchBatchService.delete(null);

    // Assert
    verify(branchBatchRepository, never()).deleteById(anyLong());
  }
}
