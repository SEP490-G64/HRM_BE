package com.example.hrm_be.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
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

  // SEARCH
  @Test
  void testSearchValidBranchName() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    Page<Branch> result = branchService.getByPaging(0, 1, "branchName", "Valid Branch Name", null, null);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements()); // Expecting one result
    assertEquals(branch.getBranchName(), result.getContent().get(0).getBranchName());
  }

  @Test
  void testSearchInvalidPageNo() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.getByPaging(-1, 1, "branchName", "Valid Branch Name", null, null);
            });

    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  @Test
  void testSearchInvalidPageSize() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.getByPaging(0, 0, "branchName", "Valid Branch Name", null, null);
            });

    assertEquals(HrmConstant.ERROR.PAGE.INVALID, exception.getMessage());
  }

  // CREATE
  @Test
  void testCreateValidBranch() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    assertThat(savedBranch.getBranchName()).isEqualTo("Valid Branch Name");
  }

  // Branch Name tests
  @Test
  void testCreateNullBranchName() {
    Branch branch = createValidBranch();
    branch.setBranchName(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateEmptyBranchName() {
    Branch branch = createValidBranch();
    branch.setBranchName("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateLongBranchName() {
    Branch branch = createValidBranch();
    branch.setBranchName("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateDuplicateBranchName() {
    Branch branch = createValidBranch();
    branchService.create(branch);
    Branch duplicateBranchName =
        new Branch()
            .setBranchName("Valid Branch Name")
            .setBranchType(BranchType.MAIN)
            .setLocation("Valid Location 123123")
            .setContactPerson("Valid Contact Person")
            .setPhoneNumber("0912345678")
            .setCapacity(500)
            .setActiveStatus(true);

    assertThrows(HrmCommonException.class, () -> branchService.create(duplicateBranchName));
  }

  // Location tests
  @Test
  void testCreateNullLocation() {
    Branch branch = createValidBranch();
    branch.setLocation(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateEmptyLocation() {
    Branch branch = createValidBranch();
    branch.setLocation("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateLongLocation() {
    Branch branch = createValidBranch();
    branch.setLocation("A".repeat(257));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateDuplicateLocation() {
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

  // Contact Person tests
  @Test
  void testCreateLongContactPerson() {
    Branch branch = createValidBranch();
    branch.setContactPerson("A".repeat(101));

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // Phone Number tests
  @Test
  void testCreateNullPhoneNumber() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateEmptyPhoneNumber() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber("");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateLongPhoneNumber() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber("012345678901");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateInvalidPhoneNumberFormat() {
    Branch branch = createValidBranch();
    branch.setPhoneNumber("INVALID_PHONE");

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // Capacity tests
  @Test
  void testCreateNullCapacity() {
    Branch branch = createValidBranch();
    branch.setCapacity(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateNegativeCapacity() {
    Branch branch = createValidBranch();
    branch.setCapacity(-1);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  @Test
  void testCreateExcessiveCapacity() {
    Branch branch = createValidBranch();
    branch.setCapacity(100001);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // Branch Type tests
  @Test
  void testCreateNullBranchType() {
    Branch branch = createValidBranch();
    branch.setBranchType(null);

    assertThrows(HrmCommonException.class, () -> branchService.create(branch));
  }

  // UPDATE
  @Test
  void testUpdateValid() {
    Branch branch = createValidBranch();
    Branch savedBranch = branchService.create(branch);

    assertThat(savedBranch).isNotNull();
    assertThat(savedBranch.getBranchName()).isEqualTo("Valid Branch Name");
    savedBranch
        .setBranchName("Valid Branch Name 123123")
        .setBranchType(BranchType.SUB)
        .setLocation("Valid Location 123123123")
        .setContactPerson("Valid Contact Person 123123")
        .setPhoneNumber("0912345578")
        .setCapacity(700)
        .setActiveStatus(false);
    assertThat(branchService.update(savedBranch)).isNotNull();
  }

  // Branch Name tests
  @Test
  void testUpdateNullBranchName() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setBranchName(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateEmptyBranchName() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setBranchName("");
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateLongBranchName() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setBranchName("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateDuplicateBranchName() {
    Branch firstBranch = createValidBranch();
    branchService.create(firstBranch);
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

  // Location tests
  @Test
  void testUpdateNullLocation() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setLocation(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateEmptyLocation() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setLocation("");
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateLongLocation() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setLocation("A".repeat(257));
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateDuplicateLocation() {
    Branch firstBranch = createValidBranch();
    branchService.create(firstBranch);
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

  // Contact Person tests
  @Test
  void testUpdateLongContactPerson() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setContactPerson("A".repeat(101));
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  // Phone Number tests
  @Test
  void testUpdateNullPhoneNumber() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setPhoneNumber(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateEmptyPhoneNumber() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setPhoneNumber("");
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateLongPhoneNumber() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setPhoneNumber("012345678901");
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateInvalidPhoneNumberFormat() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setPhoneNumber("INVALID_PHONE");
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  // Capacity tests
  @Test
  void testUpdateNullCapacity() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setCapacity(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateNegativeCapacity() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setCapacity(-1);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  @Test
  void testUpdateExcessiveCapacity() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setCapacity(100001);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  // Branch Type tests
  @Test
  void testUpdateNullBranchType() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    createdBranch.setBranchType(null);
    assertThrows(HrmCommonException.class, () -> branchService.update(createdBranch));
  }

  // DELETE
  @Test
  void testDeleteValidBranch() {
    Branch branch = createValidBranch();

    Branch createdBranch = branchService.create(branch);
    assertThat(createdBranch).isNotNull();

    // Act
    branchService.delete(createdBranch.getId());

    // Assert
    assertEquals(
        branchService.getById(createdBranch.getId()), null); // Ensure the branch is deleted
  }

  @Test
  void testDeleteNonExistingBranch() {
    // Arrange
    Long nonExistingId = 999L; // Assuming this ID does not exist

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              branchService.delete(nonExistingId);
            });

    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }
}
