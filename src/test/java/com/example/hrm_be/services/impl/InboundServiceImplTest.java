package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.UserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class InboundServiceImplTest {

  @Mock private InboundRepository inboundRepository;

  @Mock private InboundMapper inboundMapper;

  @Mock private UserService userService;

  @Mock private UserMapper userMapper;

  @InjectMocks private InboundServiceImpl inboundService;

  private InboundEntity inboundEntity;
  private Inbound inboundDTO;
  private UserEntity userEntity;
  private User userDTO;

  @BeforeEach
  void setUp() {
    // Initialize mocks before each test
    MockitoAnnotations.initMocks(this);

    // Create dummy data for UserEntity, UserDTO, InboundEntity, and InboundDTO
    userEntity = new UserEntity();
    userEntity.setEmail("test@example.com");

    userDTO = new User();
    userDTO.setEmail("test@example.com");

    inboundEntity = new InboundEntity();
    inboundEntity.setId(1L);
    inboundEntity.setCreatedDate(LocalDateTime.now());
    inboundEntity.setStatus(InboundStatus.CHO_DUYET);

    inboundDTO = new Inbound();
    inboundDTO.setId(1L);
    inboundDTO.setNote("Test note");
  }

  @Test
  void testGetById() {
    // Mock the behavior of inboundRepository and inboundMapper
    when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
    when(inboundMapper.toDTO(inboundEntity)).thenReturn(inboundDTO);

    // Execute the method under test
    Inbound result = inboundService.getById(1L);

    // Assertions
    assertNotNull(result);
    assertEquals(inboundDTO.getId(), result.getId());
    verify(inboundRepository, times(1)).findById(1L);
  }

  @Test
  void testGetById_NotFound() {
    // Mock behavior for not found
    when(inboundRepository.findById(1L)).thenReturn(Optional.empty());

    // Execute the method under test
    Inbound result = inboundService.getById(1L);

    // Assert null when entity is not found
    assertNull(result);
    verify(inboundRepository, times(1)).findById(1L);
  }

  @Test
  void testGetByPaging() {
    // Define a pageable request
    PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

    // Mock repository and mapper
    Page<InboundEntity> page = new PageImpl<>(Arrays.asList(inboundEntity));
    when(inboundRepository.findAll(pageable)).thenReturn(page);
    when(inboundMapper.toDTO(any(InboundEntity.class))).thenReturn(inboundDTO);

    // Execute the method under test
    Page<Inbound> result = inboundService.getByPaging(0, 10, "id");

    // Assertions
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(inboundRepository, times(1)).findAll(pageable);
  }

  @Test
  void testCreate() {
    // Mock user authentication and mapping behavior
    when(userService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
    when(userService.findLoggedInfoByEmail("test@example.com")).thenReturn(userDTO);
    when(userMapper.toEntity(userDTO)).thenReturn(userEntity); // Correct argument type
    when(inboundMapper.toEntity(inboundDTO)).thenReturn(inboundEntity);
    when(inboundRepository.save(inboundEntity)).thenReturn(inboundEntity);
    when(inboundMapper.toDTO(inboundEntity)).thenReturn(inboundDTO);

    // Execute the method under test
    Inbound result = inboundService.create(inboundDTO);

    // Assertions
    assertNotNull(result);
    assertEquals(InboundStatus.CHO_DUYET, inboundEntity.getStatus());
    verify(inboundRepository, times(1)).save(inboundEntity);
  }

  @Test
  void testCreate_NullInbound() {
    // Expecting an exception for null input
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.create(null);
            });

    // Check the exception message
    assertEquals(HrmConstant.ERROR.BRANCH.EXIST, exception.getMessage());
  }

  @Test
  void testUpdate() {
    // Mock behavior for updating an inbound entity
    when(inboundRepository.findById(inboundDTO.getId())).thenReturn(Optional.of(inboundEntity));
    when(inboundRepository.save(inboundEntity)).thenReturn(inboundEntity);
    when(inboundMapper.toDTO(inboundEntity)).thenReturn(inboundDTO);

    // Execute the method under test
    Inbound result = inboundService.update(inboundDTO);

    // Assertions
    assertNotNull(result);
    verify(inboundRepository, times(1)).save(inboundEntity);
  }

  @Test
  void testUpdate_NotFound() {
    // Mock behavior for not finding the entity to update
    when(inboundRepository.findById(inboundDTO.getId())).thenReturn(Optional.empty());

    // Expecting an exception
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.update(inboundDTO);
            });

    // Check the exception message
    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }

  @Test
  void testApprove() {
    // Mock behavior for approving an inbound entity
    when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
    when(userService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
    when(userService.findLoggedInfoByEmail("test@example.com")).thenReturn(userDTO);
    when(userMapper.toEntity(userDTO)).thenReturn(userEntity);
    when(inboundRepository.save(inboundEntity)).thenReturn(inboundEntity);
    when(inboundMapper.toDTO(inboundEntity)).thenReturn(inboundDTO);

    // Execute the method under test
    Inbound result = inboundService.approve(inboundEntity.getId(), true);

    // Assertions
    assertNotNull(result);
    assertTrue(inboundEntity.getIsApproved());
    verify(inboundRepository, times(1)).save(inboundEntity);
  }

  @Test
  void testDelete() {
    // Mock behavior for deleting an inbound entity
    when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));

    // Execute the method under test
    inboundService.delete(1L);

    // Verify deletion
    verify(inboundRepository, times(1)).deleteById(1L);
  }

  @Test
  void testDelete_NotFound() {
    // Mock behavior for not finding the entity to delete
    when(inboundRepository.findById(1L)).thenReturn(Optional.empty());

    // Expecting an exception
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              inboundService.delete(1L);
            });

    // Check the exception message
    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }
}
