package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.hrm_be.HrmBeApplication;
import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.services.BranchService;
import com.example.hrm_be.services.RoleService;
import com.example.hrm_be.services.UserRoleMapService;
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
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HrmBeApplication.class)
@ActiveProfiles("test")
@Import({UserServiceImpl.class, BranchServiceImpl.class, RoleServiceImpl.class})
@Transactional
public class UserServiceImplTest {
  @Autowired private UserService userService;
  @Autowired private RoleService roleService;
  @Autowired private BranchService branchService;
  @Autowired private UserRoleMapService userRoleMapService;

  private User initValidUser() {
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getStaffRole());
    return new User()
        .setUserName("hrmuser")
        .setBranch(branchService.getById(1L))
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
    User user = initValidUser();
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
    User user = initValidUser();
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
    User user = initValidUser();
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
    User user = initValidUser();
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
    User user = initValidUser();
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
    User user = initValidUser();
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

  // GET USER BY EMAIL
  // UTCID01 - Get by email: valid
  @Test
  void testUTCID01_GetByEmail_Valid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    User returnUser = userService.create(user);
    User result = userService.getByEmail(returnUser.getEmail());
    Assertions.assertEquals(result.getId(), returnUser.getId());
  }

  // UTCID02 - Get by Email: null
  @Test
  void testUTCID02_GetByEmail_InvalidNull() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    assertThrows(NullPointerException.class, () -> userService.getByEmail(null));
  }

  // UTCID03 - Get by Email: not exist
  @Test
  void testUTCID03_GetByEmail_InvalidNotExist() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertNull(userService.getByEmail("testdsd@gmail.com"));
  }

  //  // UTCID04 - Get by Email:  role not allowed
  //  @Test
  //  void testUTCID04_GetByEmail_RoleNotAllowed() {
  //    // Mock admin user
  //    // Mock admin user
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    User returnUser = userService.create(user);
  //    TestUtils.mockAuthenticatedUser(returnUser.getEmail(), RoleType.STAFF);
  //    assertThrows(HrmCommonException.class, () -> userService.getByEmail("dsdadmin@gmail.com"));
  //  }

  // GET REGISTRATION REQUEST
  // UCID01 - Get Registration Request: valid
  @Test
  void testUTC01_GetRegistrationRequest_Valid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    RegisterRequest request =
        new RegisterRequest()
            .setUserName("hrmuser")
            .setPassword("test123123")
            .setEmail("hrmuser@gmail.com")
            .setFirstName("A")
            .setLastName("Nguyen Van")
            .setConfirmPassword("test123123")
            .setPhone("0981234567");
    userService.register(request);
    Page<User> requests = userService.getRegistrationRequests();
    Assertions.assertEquals(1, requests.getTotalElements());
    Assertions.assertEquals("hrmuser@gmail.com", requests.getContent().get(0).getEmail());
  }

  // UCID02 - Get Registration Request: Role not allowed
  @Test
  void testUTC02_GetRegistrationRequest_RoleNotAllowed() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    User returnUser = userService.create(user);
    TestUtils.mockAuthenticatedUser(returnUser.getEmail(), RoleType.STAFF);
    assertThrows(HrmCommonException.class, () -> userService.getRegistrationRequests());
  }

  // CREATE USER
  // UTCID01 - Create User: Valid
  @Test
  void testUTCID01_Create_Valid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    User returnUser = userService.create(user);
    Assertions.assertNotNull(userService.getById(returnUser.getId()));
  }

  // UTCID02 - Create User: null username
  @Test
  void testUTCID02_Create_NullUserName() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setUserName(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID03 - Create User: username have length < 5 characters
  @Test
  void testUTCID03_Create_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setUserName("abcd");
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID04 - Create User: username have length > 20 characters
  @Test
  void testUTCID04_Create_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setUserName("a".repeat(21));
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID05 - Create User: duplicate username
  @Test
  void testUTCID05_Create_DuplicateUserName() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    User returnUser = userService.create(user);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  //  // UTCID06 - Create User: password null
  //  @Test
  //  void testUTCID06_Create_PasswordNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setPassword(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID07 - Create User: password smaller than 8 characters
  //  @Test
  //  void testUTCID07_Create_PasswordLengthInvalid() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setPassword("abcdefg");
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }

  // UTCID08 - Create User: null email
  @Test
  void testUTCID08_Create_EmailNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setEmail(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID09 - Create User: Email not match regex
  @Test
  void testUTCID09_Create_EmailNotMatchRegex() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setEmail("test@.com");
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID10 - Create User: Duplicate email
  @Test
  void testUTCID10_Create_DuplicateEmail() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    userService.create(user);
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getManagerRole());
    User newUser =
        new User()
            .setUserName("hrmuser123")
            .setBranch(branchService.getById(1L))
            .setPassword("test123123123123")
            .setEmail("hrmuser@gmail.com")
            .setFirstName("A")
            .setLastName("Nguyen Van")
            .setPhone("0981234567")
            .setRoles(roles)
            .setStatus(UserStatusType.ACTIVATE);
    assertThrows(HrmCommonException.class, () -> userService.create(newUser));
  }

  // UTCID11 - Create User: null branch
  @Test
  void testUTCID11_Create_BranchNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setBranch(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID12 - Create User: not exist branch
  @Test
  void testUTCID12_Create_BranchNotExist() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setBranch(new Branch().setId(-1L));
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID13 - Create User: null role
  @Test
  void testUTCID13_Create_RoleNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setRoles(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID14 - Create User: empty role
  @Test
  void testUTCID14_Create_RoleNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    user.setRoles(new ArrayList<>());
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  //  // UTCID15 - Create User: null status
  //  @Test
  //  void testUTCID15_Create_RoleNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setStatus(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }

  // UTCID16 - Create User: not valid role
  @Test
  void testUTCID15_Create_RoleNotAllowed() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    User user = initValidUser();
    User returnUser = userService.create(user);
    TestUtils.mockAuthenticatedUser(returnUser.getEmail(), RoleType.STAFF);
    List<Role> roles = new ArrayList<>();
    roles.add(roleService.getManagerRole());
    User newUser =
        new User()
            .setUserName("hrmuser123")
            .setBranch(branchService.getById(1L))
            .setPassword("test123123123123")
            .setEmail("hrmadmin@gmail.com")
            .setFirstName("A")
            .setLastName("Nguyen Van")
            .setPhone("0981234567")
            .setRoles(roles)
            .setStatus(UserStatusType.ACTIVATE);
    assertThrows(HrmCommonException.class, () -> userService.create(newUser));
  }

  // UPDATE USER
  // UTCID01 - Update User: Valid
  //  @Test
  //  void testUTCID01_Update_Valid() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    User returnUser = userService.create(user);
  //    userRoleMapService.setManagerRoleForUser(returnUser.getId());
  //    List<Role> roles = new ArrayList<>();
  //    roles.add(roleService.getManagerRole());
  //    returnUser
  //        .setUserName("hrmuser123")
  //        .setBranch(branchService.getById(1L))
  //        .setPassword("test123123123123")
  //        .setRoles(roles)
  //        .setStatus(UserStatusType.DEACTIVATE);
  //    Assertions.assertNotNull(userService.update(returnUser, false));
  //  }

  //  // UTCID02 - Create User: null username
  //  @Test
  //  void testUTCID02_Create_NullUserName() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setUserName(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID03 - Create User: username have length < 5 characters
  //  @Test
  //  void testUTCID03_Create_UserNameLengthInvalid() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setUserName("abcd");
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID04 - Create User: username have length > 20 characters
  //  @Test
  //  void testUTCID04_Create_UserNameLengthInvalid() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setUserName("a".repeat(21));
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID05 - Create User: duplicate username
  //  @Test
  //  void testUTCID05_Create_DuplicateUserName() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    User returnUser = userService.create(user);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID06 - Create User: password null
  //  @Test
  //  void testUTCID06_Create_PasswordNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setPassword(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID07 - Create User: password smaller than 8 characters
  //  @Test
  //  void testUTCID07_Create_PasswordLengthInvalid() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setPassword("abcdefg");
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID08 - Create User: null email
  //  @Test
  //  void testUTCID08_Create_EmailNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setEmail(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID09 - Create User: Email not match regex
  //  @Test
  //  void testUTCID09_Create_EmailNotMatchRegex() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setEmail("test@.com");
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID10 - Create User: Duplicate email
  //  @Test
  //  void testUTCID10_Create_DuplicateEmail() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    userService.create(user);
  //    List<Role> roles = new ArrayList<>();
  //    roles.add(roleService.getManagerRole());
  //    User newUser = new User()
  //            .setUserName("hrmuser123")
  //            .setBranch(branchService.getById(1L))
  //            .setPassword("test123123123123")
  //            .setEmail("hrmuser@gmail.com")
  //            .setFirstName("A")
  //            .setLastName("Nguyen Van")
  //            .setPhone("0981234567")
  //            .setRoles(roles)
  //            .setStatus(UserStatusType.ACTIVATE);
  //    assertThrows(HrmCommonException.class, () -> userService.create(newUser));
  //  }
  //
  //  // UTCID11 - Create User: null branch
  //  @Test
  //  void testUTCID11_Create_BranchNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setBranch(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID12 - Create User: not exist branch
  //  @Test
  //  void testUTCID12_Create_BranchNotExist() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setBranch(new Branch().setId(-1L));
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID13 - Create User: null role
  //  @Test
  //  void testUTCID13_Create_RoleNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setRoles(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID14 - Create User: empty role
  //  @Test
  //  void testUTCID14_Create_RoleNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setRoles(new ArrayList<>());
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID15 - Create User: null status
  //  @Test
  //  void testUTCID15_Create_RoleNull() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    user.setStatus(null);
  //    assertThrows(HrmCommonException.class, () -> userService.create(user));
  //  }
  //
  //  // UTCID16 - Create User: not valid role
  //  @Test
  //  void testUTCID15_Create_RoleNotAllowed() {
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    User user = initValidUser();
  //    User returnUser = userService.create(user);
  //    TestUtils.mockAuthenticatedUser(returnUser.getEmail(), RoleType.STAFF);
  //    List<Role> roles = new ArrayList<>();
  //    roles.add(roleService.getManagerRole());
  //    User newUser = new User()
  //            .setUserName("hrmuser123")
  //            .setBranch(branchService.getById(1L))
  //            .setPassword("test123123123123")
  //            .setEmail("hrmadmin@gmail.com")
  //            .setFirstName("A")
  //            .setLastName("Nguyen Van")
  //            .setPhone("0981234567")
  //            .setRoles(roles)
  //            .setStatus(UserStatusType.ACTIVATE);
  //    assertThrows(HrmCommonException.class, () -> userService.create(newUser));
  //  }

  // DELETE BY ID
  // UTCID01 - Delete by ID: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    User user = initValidUser();
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
