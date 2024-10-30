package com.example.hrm_be.services.impl;

import static com.example.hrm_be.commons.enums.BranchType.MAIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.BranchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import(BranchServiceImpl.class)
@Transactional
public class BranchServiceImplTest {

  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private BranchService branchService;
  @Autowired private BranchRepository branchRepository;

  // Helper to create a valid branch entity
  private Branch createValidBranch() {
    return new Branch()
        .setBranchName("Valid Branch Name")
        .setBranchType(BranchType.MAIN)
        .setLocation("Valid Location")
        .setContactPerson("Valid Contact Person")
        .setPhoneNumber("0912345678")
        .setCapacity(500)
        .setActiveStatus(true);
  }

  // GET
  // UTCID01 - Get: valid
  @Test
  void testUTCID01_Get_AllValid() {
    Branch branch = createValidBranch();
    Branch branch1 = branchService.create(branch);
    Branch branch2 = branchService.getById(branch1.getId());
    assertEquals(branch1.getBranchName(), branch2.getBranchName());
  }

  // UTCID02 - Get: id null
  @Test
  void testUTCID02_Get_idNull() {
    assertThrows(HrmCommonException.class, () -> branchService.getById(null));
  }

  // UTCID03 - Get: id not exist
  @Test
  void testUTCID03_Get_idNotExist() {
    branchRepository.deleteAll();
    Long nonExistingId = 1L;
    assertEquals(null, branchService.getById(nonExistingId));
  }

  // SEARCH
  // UTCID01 - getByPaging: All valid
  @Test
  void testUTCID01_GetByPaging_AllValid() {
    Branch branch = createValidBranch();
    branchRepository.deleteAll();
    Branch savedBranch = branchService.create(branch);
    assertThat(savedBranch).isNotNull();

    Page<Branch> result = branchService.getByPaging(0, 1, "branchName", "a", BranchType.MAIN, true);
    assertEquals(1, result.getTotalElements());
    assertEquals(branch.getBranchName(), result.getContent().get(0).getBranchName());
  }

  // UTCID02 - getByPaging: pageNo invalid
  @Test
  void testUTCID02_GetByPaging_pageNoInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.getByPaging(-1, 1, "branchName", "a", BranchType.MAIN, true);
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // UTCID03 - getByPaging: pageSize invalid
  @Test
  void testUTCID03_GetByPaging_pageSizeInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.getByPaging(0, 0, "branchName", "a", BranchType.MAIN, true);
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // UTCID04 - getByPaging: sortBy invalid
  @Test
  void testUTCID04_GetByPaging_sortByInvalid() {
    Exception exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.getByPaging(0, 1, "a", "a", BranchType.MAIN, true);
            });
    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  // UTCID01 - create: all valid
  @Test
  void testUTCID01_Create_AllValid() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    assertThat(savedBranch.getBranchName()).isEqualTo("Valid Branch Name");
  }

  // UTCID02 - create: branchName null
  @Test
  void testUTCID02_Create_branchNameNull() {
    Branch branch = createValidBranch();
    branch.setBranchName(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID03 - create: branchName empty
  @Test
  void testUTCID03_Create_branchNameEmpty() {
    Branch branch = createValidBranch();
    branch.setBranchName("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID04 - create: branchName greater than 100 characters
  @Test
  void testUTCID04_Create_branchNameLong() {
    Branch branch = createValidBranch();
    branch.setBranchName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID05 - create: branchName duplicate
  @Test
  void testUTCID05_Create_branchNameDuplicate() {
    Branch branch = createValidBranch();
    branchService.create(branch);
    Branch duplicateBranchName =
        new Branch()
            .setBranchName("Valid Branch Name")
            .setBranchType(MAIN)
            .setLocation("Valid Location 123123")
            .setContactPerson("Valid Contact Person")
            .setPhoneNumber("0912345678")
            .setCapacity(500)
            .setActiveStatus(true);

    assertThrows(HrmCommonException.class, () -> branchService.create(duplicateBranchName));
  }

  // UTCID06 - create: location null
  @Test
  void testUTCID06_Create_locationNull() {
    Branch branch = createValidBranch();
    branch.setLocation(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID07 - create: location empty
  @Test
  void testUTCID07_Create_locationEmpty() {
    Branch branch = createValidBranch();
    branch.setLocation("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID08 - create: location greater than 256 characters
  @Test
  void testUTCID08_Create_locationLong() {
    Branch branch = createValidBranch();
    branch.setLocation("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID09 - create: location duplicate
  @Test
  void testUTCID09_Create_locationDuplicate() {
    Branch branch = createValidBranch();
    branchService.create(branch);
    Branch duplicateLocationBranch =
        new Branch()
            .setBranchName("Valid Branch Name 123123")
            .setBranchType(BranchType.MAIN)
            .setLocation("Valid Location")
            .setContactPerson("Valid Contact Person")
            .setPhoneNumber("0912345678")
            .setCapacity(500)
            .setActiveStatus(true);

    assertThrows(HrmCommonException.class, () -> branchService.create(duplicateLocationBranch));
  }

  // UTCID010 - create: contactPerson greater than 100 characters
  @Test
  void testUTCID010_Create_contactPersonLong() {
    Branch branch = createValidBranch();
    branch.setContactPerson("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID011 - create: phoneNumber null
  @Test
  void testUTCID011_Create_phoneNumberNull() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID012 - create: phoneNumber empty
  @Test
  void testUTCID012_Create_phoneNumberEmpty() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID013 - create: phoneNumber not match regex
  @Test
  void testUTCID013_Create_phoneNumberInvalidFormat() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID014 - create: capacity not null && greater than 100,000
  @Test
  void testUTCID014_Create_capacityExcessive() {
    Branch branch = createValidBranch();
    branch.setCapacity(100001);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID015 - create: capacity not null && negative number
  @Test
  void testUTCID015_Create_capacityNegative() {
    Branch branch = createValidBranch();
    branch.setCapacity(0);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID016 - create: branchType null
  @Test
  void testUTCID016_Create_branchTypeNull() {
    Branch branch = createValidBranch();
    branch.setBranchType(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UTCID017 - create: activeStatus null
  @Test
  void testUTCID017_Create_activeStatusNull() {
    Branch branch = createValidBranch();
    branch.setActiveStatus(null);
    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UPDATE
  // UTCID01 - UPDATE: all valid
  @Test
  void testUTCID01_Update_AllValid() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);
    Branch updateBranch = branchService.update(savedBranch);

    assertThat(updateBranch).isNotNull();
    assertThat(updateBranch.getBranchName()).isEqualTo("Valid Branch Name");
  }

  // UTCID02 - UPDATE: branchName null
  @Test
  void testUTCID02_Update_branchNameNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setBranchName(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID03 - Update: branchName empty
  @Test
  void testUTCID03_Update_branchNameEmpty() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setBranchName("");

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID04 - Update: branchName greater than 100 characters
  @Test
  void testUTCID04_Update_branchNameLong() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setBranchName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID05 - Update: branchName duplicate
  @Test
  void testUTCID05_Update_branchNameDuplicate() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    Branch secondBranch =
        new Branch()
            .setBranchName("Valid Branch Name 123123")
            .setBranchType(BranchType.SUB)
            .setLocation("Valid Location 123123")
            .setContactPerson("Valid Contact Person")
            .setPhoneNumber("0912345678")
            .setCapacity(500)
            .setActiveStatus(true);
    Branch returnValue = branchService.create(secondBranch);
    returnValue.setBranchName("Valid Branch Name");

    assertThrows(HrmCommonException.class, () -> branchService.update(returnValue));
  }

  // UTCID06 - Update: location null
  @Test
  void testUTCID06_Update_locationNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setLocation(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID07 - Update: location empty
  @Test
  void testUTCID07_Update_locationEmpty() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setLocation("");

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID08 - Update: location greater than 256 characters
  @Test
  void testUTCID08_Update_locationLong() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setLocation("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID09 - Update: location duplicate
  @Test
  void testUTCID09_Update_locationDuplicate() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    Branch secondBranch =
        new Branch()
            .setBranchName("Valid Branch Name 123123")
            .setBranchType(BranchType.SUB)
            .setLocation("Valid Location 123123")
            .setContactPerson("Valid Contact Person")
            .setPhoneNumber("0912345678")
            .setCapacity(500)
            .setActiveStatus(true);
    Branch returnValue = branchService.create(secondBranch);
    returnValue.setLocation("Valid Location");

    assertThrows(HrmCommonException.class, () -> branchService.update(returnValue));
  }

  // UTCID010 - Update: contactPerson greater than 100 characters
  @Test
  void testUTCID010_Update_contactPersonLong() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setContactPerson("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID011 - Update: phoneNumber null
  @Test
  void testUTCID011_Update_phoneNumberNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID012 - Update: phoneNumber empty
  @Test
  void testUTCID012_Update_phoneNumberEmpty() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID013 - Update: phoneNumber not match regex
  @Test
  void testUTCID013_Update_phoneNumberInvalidFormat() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID014 - Update: capacity not null && greater than 100,000
  @Test
  void testUTCID014_Update_capacityExcessive() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setCapacity(100001);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID015 - Update: capacity not null && negative number
  @Test
  void testUTCID015_Update_capacityNegative() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setCapacity(0);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID016 - Update: branchType null
  @Test
  void testUTCID016_Update_branchTypeNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setBranchType(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID017 - Update: activeStatus null
  @Test
  void testUTCID017_Update_activeStatusNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setActiveStatus(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID018 - Update: id null
  @Test
  void testUTCID018_Update_idNull() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setId(null);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // UTCID019 - Update: id not exist
  @Test
  void testUTCID019_Update_idNotExist() {
    branchRepository.deleteAll();
    Branch branch = createValidBranch();
    branchService.create(branch);
    branch.setId(1L);

    assertThrows(HrmCommonException.class, () -> branchService.update(branch));
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    Branch branch = createValidBranch();
    Branch branch1 = branchService.create(branch);
    branchService.delete(branch1.getId());
    assertEquals(branchService.getById(branch1.getId()), null);
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    assertThrows(HrmCommonException.class, () -> branchService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    branchRepository.deleteAll();
    Long nonExistingId = 2L;
    assertThrows(HrmCommonException.class, () -> branchService.delete(nonExistingId));
  }
}
