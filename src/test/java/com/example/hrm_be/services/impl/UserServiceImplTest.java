package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.TestcontainersConfiguration;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.services.BranchService;
import com.example.hrm_be.services.RoleService;
import com.example.hrm_be.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
@Import({UserServiceImpl.class, BranchServiceImpl.class, RoleServiceImpl.class})
@Transactional
public class UserServiceImplTest {
  @Container
  public static PostgreSQLContainer<TestcontainersConfiguration> postgreSQLContainer =
      TestcontainersConfiguration.getInstance();

  @Autowired private UserService userService;
  @Autowired private RoleService roleService;
  @Autowired private BranchService branchService;

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

  // GET BY PAGING
  // UTCID01 - Get By Paging: valid
  @Test
  void testUTCID01_GetByPaging_Valid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    Page<User> result =
        userService.getByPaging(0, 1, "userName", "asc", "hrmuser", UserStatusType.ACTIVATE);
    Assertions.assertEquals(1, result.getTotalElements());
    Assertions.assertEquals("hrmuser@gmail.com", result.getContent().get(0).getEmail());
  }

  // UTCID02 - Get By Paging: pageNo < 0
  @Test
  void testUTCID02_GetByPaging_InvalidPageNo() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    assertThrows(
        HrmCommonException.class,
        () ->
            userService.getByPaging(-1, 1, "userName", "asc", "hrmuser", UserStatusType.ACTIVATE));
  }

  // UTCID03 - Get By Paging: pageSize < 1
  @Test
  void testUTCID03_GetByPaging_InvalidPageSize() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 0, "userName", "asc", "hrmuser", UserStatusType.ACTIVATE));
  }

  // UTCID04 - Get By Paging: sortBy equal null
  @Test
  void testUTCID04_GetByPaging_InvalidSortByNull() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 0, null, "asc", "hrmuser", UserStatusType.ACTIVATE));
  }

  // UTCID05 - Get By Paging: sortBy not a field of User class
  @Test
  void testUTCID05_GetByPaging_InvalidSortByFieldClass() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 0, "test", "asc", "hrmuser", UserStatusType.ACTIVATE));
  }

  // GET USER BY ID
  // UTCID01 - Get by ID: valid
  @Test
  void testUTCID01_GetByID_Valid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = createValidUser();
    User returnUser = userService.create(user);
    User result = userService.getById(returnUser.getId());
    Assertions.assertEquals(result.getEmail(), returnUser.getEmail());
  }

  // UTCID02 - Get by ID: null
  @Test
  void testUTCID02_GetByID_InvalidNull() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    assertThrows(HrmCommonException.class, () -> userService.getById(null));
  }

  // UTCID03 - Get by ID: not exist
  @Test
  void testUTCID03_GetByID_InvalidNotExist() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertNull(userService.getById(-1L));
  }

  // DELETE BY ID
  // UTCID01 - Delete by ID: valid
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

  // UTCID02 - Delete by Id: null
  @Test
  void testUTCID02_Delete_InvalidNull() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    assertThrows(HrmCommonException.class, () -> userService.delete(null));
  }

  // UTCID03 - Delete by ID: not exist
  @Test
  void testUTCID03_Delete_InvalidNotExist() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertNull(userService.getById(-1L));
  }
}
