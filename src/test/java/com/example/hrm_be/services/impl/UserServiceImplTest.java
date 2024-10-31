package com.example.hrm_be.services.impl;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.services.BranchService;
import com.example.hrm_be.services.RoleService;
import com.example.hrm_be.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import({UserServiceImpl.class, BranchServiceImpl.class, RoleServiceImpl.class})
@Transactional
public class UserServiceImplTest {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private UserService userService;
  @Autowired private RoleService roleService;
  @Autowired private BranchService branchService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // Helper to create a valid branch entity
  private Branch createValidBranch() {
    return new Branch()
        .setBranchName("Valid Branch Name")
        .setBranchType("MAIN")
        .setLocation("Valid Location")
        .setContactPerson("Valid Contact Person")
        .setPhoneNumber("0912345678")
        .setCapacity("500")
        .setActiveStatus("true");
  }

  private User createValidUser() {
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getAdminRole());
    return new User()
        .setUserName("hrmuser")
        .setBranch(branchService.getById("1"))
        .setPassword("test123123")
        .setEmail("hrmuser@gmail.com")
        .setFirstName("A")
        .setLastName("Nguyen Van")
        .setPhone("0981234567")
        .setRoles(roles)
        .setStatus(UserStatusType.ACTIVATE);
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    User user = createValidUser();
    User result = userService.create(user);
    Long userID = result.getId();
    userService.delete(userID);
    Assertions.assertNull(userService.getById(userID));
  }
}
