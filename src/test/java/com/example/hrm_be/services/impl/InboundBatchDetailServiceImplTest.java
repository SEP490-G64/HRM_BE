package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundBatchDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.entities.InboundBatchDetailEntity;
import com.example.hrm_be.repositories.InboundBatchDetailRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InboundBatchDetailServiceImplTest {

  @Mock private InboundBatchDetailRepository inboundBatchDetailRepository;

  @Mock private InboundBatchDetailMapper inboundBatchDetailMapper;

  @InjectMocks private InboundBatchDetailServiceImpl inboundBatchDetailService;

  private InboundBatchDetail inboundBatchDetailDto;
  private InboundBatchDetailEntity inboundBatchDetailEntity;

  @BeforeEach
  void setUp() {
    inboundBatchDetailDto = new InboundBatchDetail();
    inboundBatchDetailDto.setId(1L);
    inboundBatchDetailDto.setQuantity(100);

    inboundBatchDetailEntity = InboundBatchDetailEntity.builder().id(1L).quantity(100).build();
  }

  @Test
  void testCreateInboundBatchDetail_Success() {
    // Arrange
    when(inboundBatchDetailMapper.toEntity(inboundBatchDetailDto))
        .thenReturn(inboundBatchDetailEntity);
    when(inboundBatchDetailRepository.save(inboundBatchDetailEntity))
        .thenReturn(inboundBatchDetailEntity);
    when(inboundBatchDetailMapper.toDTO(inboundBatchDetailEntity))
        .thenReturn(inboundBatchDetailDto);

    // Act
    InboundBatchDetail result = inboundBatchDetailService.create(inboundBatchDetailDto);

    // Assert
    assertNotNull(result);
    assertEquals(inboundBatchDetailDto, result);
    verify(inboundBatchDetailRepository, times(1)).save(inboundBatchDetailEntity);
  }

  @Test
  void testCreateInboundBatchDetail_NullInput_ThrowsException() {
    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> inboundBatchDetailService.create(null));

    // Assert
    assertEquals(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.EXIST, exception.getMessage());
  }

  @Test
  void testUpdateInboundBatchDetail_Success() {
    // Arrange
    when(inboundBatchDetailRepository.findById(1L))
        .thenReturn(Optional.of(inboundBatchDetailEntity));
    when(inboundBatchDetailRepository.save(inboundBatchDetailEntity))
        .thenReturn(inboundBatchDetailEntity);
    when(inboundBatchDetailMapper.toDTO(inboundBatchDetailEntity))
        .thenReturn(inboundBatchDetailDto);

    // Act
    InboundBatchDetail result = inboundBatchDetailService.update(inboundBatchDetailDto);

    // Assert
    assertNotNull(result);
    assertEquals(inboundBatchDetailDto.getQuantity(), result.getQuantity());
    verify(inboundBatchDetailRepository, times(1)).save(inboundBatchDetailEntity);
  }

  @Test
  void testUpdateInboundBatchDetail_NotFound_ThrowsException() {
    // Arrange
    when(inboundBatchDetailRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> inboundBatchDetailService.update(inboundBatchDetailDto));

    // Assert
    assertEquals(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteInboundBatchDetail_Success() {
    // Arrange
    when(inboundBatchDetailRepository.findById(1L))
        .thenReturn(Optional.of(inboundBatchDetailEntity));

    // Act
    inboundBatchDetailService.delete(1L);

    // Assert
    verify(inboundBatchDetailRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDeleteInboundBatchDetail_NotFound_ThrowsException() {
    // Arrange
    when(inboundBatchDetailRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> inboundBatchDetailService.delete(1L));

    // Assert
    assertEquals(HrmConstant.ERROR.INBOUND_BATCH_DETAIL.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testDeleteInboundBatchDetail_InvalidId() {
    // Act
    inboundBatchDetailService.delete(null);

    // Assert
    verify(inboundBatchDetailRepository, never()).deleteById(anyLong());
  }
}
