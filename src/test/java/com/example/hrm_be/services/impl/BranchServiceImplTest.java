package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchServiceImplTest {

  @Mock private BranchMapper branchMapper;

  @Mock private BranchRepository branchRepository;

  @InjectMocks private BranchServiceImpl branchService;

  private Branch branch;
  private BranchEntity branchEntity;

  @BeforeEach
  public void setup() {
    branch =
        Branch.builder()
            .branchName("Valid Branch Name")
            .branchType(BranchType.MAIN)
            .location("Valid Location")
            .contactPerson("Valid Contact Person")
            .phoneNumber("0912345678")
            .capacity(500)
            .activeStatus(true)
            .build();

    branchEntity =
        BranchEntity.builder()
            .branchName("Valid Branch Name")
            .branchType(BranchType.MAIN)
            .location("Valid Location")
            .contactPerson("Valid Contact Person")
            .phoneNumber("0912345678")
            .capacity(500)
            .activeStatus(true)
            .build();
  }

  @Test
  void testUTCID01_Get_AllValid() {
    // Create a Pageable object with sorting by "branchName"
    Pageable pageable = PageRequest.of(0, 10, Sort.by("branchName"));

    // Create a mock list of BranchEntity objects
    List<BranchEntity> branches = Collections.singletonList(branchEntity);
    // Create a Page object using the list of BranchEntity and the Pageable
    Page<BranchEntity> page = new PageImpl<>(branches, pageable, branches.size());

    // Mock the repository method findByBranchNameOrLocationAndBranchType to return the Page of
    // BranchEntity
    when(branchRepository.findByBranchNameOrLocationAndBranchType(
            "a", BranchType.MAIN, true, pageable))
        .thenReturn(page);

    // Mock the branchMapper's toDTO method to return the Branch DTO
    when(branchMapper.toDTO(Mockito.any(BranchEntity.class))).thenReturn(branch);

    // Call the method to be tested
    Page<Branch> result =
        branchService.getByPaging(0, 10, "branchName", "a", BranchType.MAIN, Boolean.TRUE);

    // Verify that the result is not null
    Assertions.assertNotNull(result);
  }

  @Test
  void testUTCID01_Get_idValid() {
    Long id = 1L;

    when(branchRepository.findById(id)).thenReturn(Optional.of(branchEntity));

    // Mock the branchMapper's toDTO method to return the Branch DTO
    when(branchMapper.toDTO(Mockito.any(BranchEntity.class))).thenReturn(branch);

    Branch result = branchService.getById(id);

    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    // Given: id is null
    Long id = null;
    // Then: The service method should throw HrmCommonException when trying to get a branch by null
    // id
    assertThrows(HrmCommonException.class, () -> branchService.getById(id));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    Long id = 1L;
    when(branchRepository.findById(id)).thenReturn(Optional.empty());
    Assertions.assertNull(branchService.getById(id));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    // Create a Pageable object with sorting by "branchName"
    Pageable pageable = PageRequest.of(0, 10, Sort.by("branchName"));
    // Create a mock list of BranchEntity objects
    List<BranchEntity> branches = Collections.singletonList(branchEntity);
    // Create a Page object using the list of BranchEntity and the Pageable
    Page<BranchEntity> page = new PageImpl<>(branches, pageable, branches.size());
    // Mock the repository method findByBranchNameOrLocationAndBranchType to return the Page of
    // BranchEntity
    when(branchRepository.findByBranchNameOrLocationAndBranchType(
            "a", BranchType.MAIN, true, pageable))
        .thenReturn(page);
    // Mock the branchMapper's toDTO method to return the Branch DTO
    when(branchMapper.toDTO(Mockito.any(BranchEntity.class))).thenReturn(branch);
    // Call the method to be tested
    Page<Branch> result =
        branchService.getByPaging(0, 10, "branchName", "a", BranchType.MAIN, Boolean.TRUE);
    // Verify that the result is not null
    Assertions.assertNotNull(result);
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    assertThrows(
        HrmCommonException.class,
        () -> {
          branchService.getByPaging(-1, 10, "branchName", "a", BranchType.MAIN, Boolean.TRUE);
        });
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    assertThrows(
        HrmCommonException.class,
        () -> {
          branchService.getByPaging(0, 0, "branchName", "a", BranchType.MAIN, Boolean.TRUE);
        });
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    assertThrows(
        HrmCommonException.class,
        () -> {
          branchService.getByPaging(0, 0, "a", "a", BranchType.MAIN, Boolean.TRUE);
        });
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    // Mock the mapper to convert branch DTO to entity
    when(branchMapper.toEntity(branch)).thenReturn(branchEntity);

    // Mock saving the branch entity and returning the same entity
    when(branchRepository.save(branchEntity)).thenReturn(branchEntity);

    // Mock converting the saved entity back to a DTO (if necessary)
    when(branchMapper.toDTO(branchEntity)).thenReturn(branch);

    // Call the create method and verify it's not null
    Branch result = branchService.create(branch);
    Assertions.assertNotNull(result); // Verify that the returned result is not null
  }

  // UTCID02 - create: branchName null
  @Test
  void testUTCID02_Create_branchNameNull() {
    branch.setBranchName(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID03 - create: branchName empty
  @Test
  void testUTCID03_Create_branchNameEmpty() {
    branch.setBranchName("");
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID04 - create: branchName greater than 100 characters
  @Test
  void testUTCID04_Create_branchNameLong() {
    branch.setBranchName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID06 - create: location null
  @Test
  void testUTCID06_Create_locationNull() {
    branch.setLocation(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID07 - create: location empty
  @Test
  void testUTCID07_Create_locationEmpty() {
    branch.setLocation("");
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID08 - create: location greater than 256 characters
  @Test
  void testUTCID08_Create_locationLong() {
    branch.setLocation("A".repeat(256));
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID09 - create: location duplicate
  @Test
  void testUTCID09_Create_locationDuplicate() {
    branchEntity.setLocation("location same");
    branch.setLocation("location same");
    when(branchRepository.existsByLocation(branchEntity.getLocation())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID010 - create: contactPerson greater than 100 characters

  @Test
  void testUTCID010_Create_contactPersonLong() {
    branch.setContactPerson("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID011 - create: phoneNumber null
  @Test
  void testUTCID011_Create_phoneNumberNull() {
    branch.setPhoneNumber(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID012 - create: phoneNumber empty
  @Test
  void testUTCID012_Create_phoneNumberEmpty() {
    branch.setPhoneNumber("");
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID013 - create: phoneNumber not match regex
  @Test
  void testUTCID013_Create_phoneNumberInvalidFormat() {
    branch.setPhoneNumber("INVALID_PHONE");
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID014 - create: capacity not null && greater than 100,000
  @Test
  void testUTCID014_Create_capacityExcessive() {
    branch.setCapacity(100001);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID015 - create: capacity not null && negative number
  @Test
  void testUTCID015_Create_capacityNegative() {
    branch.setCapacity(0);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID016 - create: branchType null
  @Test
  void testUTCID016_Create_branchTypeNull() {
    branch.setBranchType(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID017 - create: activeStatus null
  @Test
  void testUTCID017_Create_activeStatusNull() {
    branch.setActiveStatus(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    // Set up the mock branch and branchEntity
    branch.setId(1L); // Set the ID of the branch you're testing
    branchEntity.setId(1L);
    // Set other properties as needed to match what your `branch` object should contain

    // Mock repository to return the existing branch entity
    when(branchRepository.findById(branch.getId())).thenReturn(Optional.of(branchEntity));

    // Mock the branch repository save method to return the updated branchEntity
    when(branchRepository.save(any(BranchEntity.class))).thenReturn(branchEntity);

    // Mock the conversion of branchEntity to branch DTO
    when(branchMapper.toDTO(Mockito.any(BranchEntity.class))).thenReturn(branch);

    // Call the update method
    Branch result = branchService.update(branch);

    // Verify the result is not null
    Assertions.assertNotNull(result);
  }

  // UTCID02 - UPDATE: branchName null
  @Test
  void testUTCID02_Update_branchNameNull() {
    branch.setId(1L);
    branch.setBranchName(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID03 - Update: branchName empty
  @Test
  void testUTCID03_Update_branchNameEmpty() {
    branch.setId(1L);
    branch.setBranchName("");
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID04 - Update: branchName greater than 100 characters
  @Test
  void testUTCID04_Update_branchNameLong() {
    branch.setId(1L);
    branch.setBranchName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID06 - Update: location null
  @Test
  void testUTCID06_Update_locationNull() {
    branch.setId(1L);
    branch.setLocation(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID07 - Update: location empty
  @Test
  void testUTCID07_Update_locationEmpty() {
    branch.setId(1L);
    branch.setLocation("");
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID08 - Update: location greater than 255 characters
  @Test
  void testUTCID08_Update_locationLong() {
    branch.setId(1L);
    branch.setLocation("A".repeat(256));
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID09 - Update: location duplicate
  @Test
  void testUTCID09_Update_locationDuplicate() {
    // Set up the branch object with a new name
    branch.setId(1L);
    branch.setLocation("New Branch Location");

    branchEntity.setId(1L);
    branchEntity.setLocation("Old Branch Location");

    // Simulate the repository returning the existing branch entity
    when(branchRepository.findById(branch.getId())).thenReturn(Optional.of(branchEntity));
    when(branchRepository.existsByLocation(branch.getLocation()))
        .thenReturn(true); // Simulate name conflict

    // Call the update method and expect an exception (the name is changed and conflicts with an
    // existing name)
    Assertions.assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID010 - Update: contactPerson greater than 100 characters
  @Test
  void testUTCID010_Update_contactPersonLong() {
    branch.setId(1L);
    branch.setContactPerson("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID011 - Update: phoneNumber null
  @Test
  void testUTCID011_Update_phoneNumberNull() {
    branch.setId(1L);
    branch.setPhoneNumber(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID012 - Update: phoneNumber empty
  @Test
  void testUTCID012_Update_phoneNumberEmpty() {
    branch.setId(1L);
    branch.setPhoneNumber("");
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID013 - Update: phoneNumber not match regex
  @Test
  void testUTCID013_Update_phoneNumberInvalidFormat() {
    branch.setId(1L);
    branch.setPhoneNumber("INVALID_PHONE");
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID014 - Update: capacity not null && greater than 100,000
  @Test
  void testUTCID014_Update_capacityExcessive() {
    branch.setId(1L);
    branch.setCapacity(100001);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID015 - Update: capacity not null && negative number
  @Test
  void testUTCID015_Update_capacityNegative() {
    branch.setId(1L);
    branch.setCapacity(0);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID016 - Update: branchType null
  @Test
  void testUTCID016_Update_branchTypeNull() {
    branch.setId(1L);
    branch.setBranchType(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID017 - Update: activeStatus null
  @Test
  void testUTCID017_Update_activeStatusNull() {
    branch.setId(1L);
    branch.setActiveStatus(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID018 - Update: id null
  @Test
  void testUTCID018_Update_idNull() {
    branch.setId(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID019 - Update: id not exist
  @Test
  void testUTCID019_Update_idNotExist() {
    branch.setId(1L);
    // Simulate the repository returning the existing branch entity
    when(branchRepository.findById(branch.getId())).thenReturn(Optional.empty());
    // Call the update method and expect an exception (the name is changed and conflicts with an
    // existing name)
    Assertions.assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    branch.setId(1L);
    when(branchRepository.findById(branch.getId())).thenReturn(Optional.of(branchEntity));
    branchService.delete(branch.getId());
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> branchService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    branch.setId(1L);
    when(branchRepository.findById(branch.getId())).thenReturn(Optional.empty());
    assertThrows(HrmCommonException.class, () -> branchService.delete(branch.getId()));
  }
}
