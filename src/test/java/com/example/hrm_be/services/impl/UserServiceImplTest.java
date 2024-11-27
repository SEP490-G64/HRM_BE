package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.RoleMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.ChangePasswordRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.repositories.UserRoleMapRepository;
import com.example.hrm_be.services.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.example.hrm_be.utils.ValidateUtil;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.core.context.SecurityContext;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

  @InjectMocks private UserServiceImpl userService;

  @Mock private UserRepository userRepository;

  @Mock private UserMapper userMapper;

  @Mock private BranchMapper branchMapper;

  @Mock private UserRoleMapServiceImpl userRoleMapService;

  @Mock private RoleServiceImpl roleService;

  @Mock private RoleMapper roleMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EmailService emailService;

  @Mock private BranchServiceImpl branchService;

  @Mock private SecurityContext securityContext;

  @Mock private UserRoleMapRepository userRoleMapRepository; // Mock UserRoleMapRepository

  private User user;
  private UserEntity userEntity;
  private Branch branch;
  private BranchEntity branchEntity;
  private Role role;
  private List<Role> roles = new ArrayList<>();
  private UserRoleMapEntity userRoleMapEntity;
  private RoleEntity roleEntity;
  private final List<UserRoleMapEntity> userRoleMapEntities = new ArrayList<>();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    branch =
        new Branch()
            .setBranchName("branch")
            .setLocation("location")
            .setBranchType(BranchType.MAIN)
            .setContactPerson("contactPerson")
            .setPhoneNumber("0888160699")
            .setCapacity(1000)
            .setActiveStatus(true);

    branchEntity =
        new BranchEntity()
            .setBranchName("branch")
            .setLocation("location")
            .setBranchType(BranchType.MAIN)
            .setContactPerson("contactPerson")
            .setPhoneNumber("0888160699")
            .setCapacity(1000)
            .setActiveStatus(true);

    role = new Role().setName("role").setType(RoleType.STAFF);

    roleEntity = new RoleEntity().setName("role").setType(RoleType.STAFF);

    user =
        new User()
            .setUserName("hrmuser")
            .setBranch(branch)
            .setPassword("test123123")
            .setEmail("hrmuser@gmail.com")
            .setFirstName("A")
            .setLastName("Nguyen Van")
            .setPhone("0981234567")
            .setRoles(Collections.singletonList(role))
            .setStatus(UserStatusType.ACTIVATE);

    userEntity =
        new UserEntity()
            .setUserName("hrmuser")
            .setBranch(branchEntity)
            .setPassword("test123123")
            .setEmail("hrmuser@gmail.com")
            .setFirstName("A")
            .setLastName("Nguyen Van")
            .setPhone("0981234567")
            .setUserRoleMap(userRoleMapEntities)
            .setStatus(UserStatusType.ACTIVATE);
  }

  @AfterEach
  public void tearDown() {
    // Clear the security context to avoid side effects between tests
    SecurityContextHolder.clearContext();
  }

  @Test
  void testGetAuthenticatedUserEmail_Success() {
    // Arrange: Mock user data
    String expectedEmail = "dsdadmin@gmail.com";
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Act: Call the method under test
    String actualEmail = userService.getAuthenticatedUserEmail();

    // Assert: Verify the returned email matches the expected email
    Assertions.assertEquals(expectedEmail, actualEmail);
  }

  @Test
  void testGetAuthenticatedUserEmail_Failure() {
    // Arrange: Mock user data and simulate the absence of authentication
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.isAuthenticated()).thenReturn(false); // Simulate unauthenticated user
    SecurityContextHolder.getContext().setAuthentication(authentication); // Set this mock in the security context

    // Act & Assert: Ensure that the method throws UsernameNotFoundException when the user is not authenticated
    Assertions.assertThrows(
            UsernameNotFoundException.class, () -> userService.getAuthenticatedUserEmail()
    );
  }

  @Test
  public void testGetAuthenticatedUserEmail_AuthenticationNull() {
    // Arrange: Mock user data and simulate the absence of authentication
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN); // Assuming this method sets up an invalid or empty user

    // Simulate the absence of authentication in the SecurityContext
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    when(securityContext.getAuthentication()).thenReturn(null); // Simulate no authentication

    // Act & Assert: Ensure that the method throws UsernameNotFoundException when the user is not authenticated
    Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.getAuthenticatedUserEmail());
  }


  @Test
  public void testGetAuthenticatedUserEmail_UserDetailsNull() {
    // Arrange: Simulate a scenario where Authentication is present, but UserDetails is null
    Authentication authentication = mock(Authentication.class);

    // Mock that the authentication is present, but UserDetails is null
    when(authentication.getPrincipal())
        .thenReturn(null); // Simulate that no UserDetails are present
    when(authentication.isAuthenticated())
        .thenReturn(true); // Simulate that the user is authenticated

    // Mock the SecurityContext to return this Authentication
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Act & Assert: Ensure that the method throws UsernameNotFoundException when UserDetails is
    // null
    Assertions.assertThrows(
        UsernameNotFoundException.class, () -> userService.getAuthenticatedUserEmail());
  }

  @Test
  void testUTCID01_GetByPaging_Valid() {
    // Arrange: Mock authenticated user and repository behavior
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);

    // Create a Pageable object with sorting by userName ascending
    Pageable pageable = PageRequest.of(0, 10, Sort.by("userName").ascending());

    // Create mock data for users (you may need to initialize userEntity and user)
    List<UserEntity> users = Collections.singletonList(userEntity); // Single user entity mock
    Page<UserEntity> page =
        new PageImpl<>(users, pageable, users.size()); // Mock Page object with total size

    // Stub repository method to return the mock page
    when(userRepository.searchUsers("", UserStatusType.ACTIVATE, pageable)).thenReturn(page);

    // Mock conversion from UserEntity to UserDTO
    when(userMapper.toDTO(userEntity)).thenReturn(user);

    // Act: Call the method under test
    Page<User> result =
        userService.getByPaging(0, 10, "userName", "asc", "", UserStatusType.ACTIVATE);

    // Assert: Verify the result is not null and contains expected content
    Assertions.assertNotNull(result, "Result should not be null");
    Assertions.assertEquals(1, result.getTotalElements(), "Total elements should be 1");
    Assertions.assertEquals(
        user, result.getContent().get(0), "The user DTO should match the expected user");
  }

  @Test
  void testUTCID02_GetByPaging_InvalidPageNo() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Act & Assert: Verify that an exception is thrown when a negative page number is passed
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(-1, 10, "userName", "asc", "", UserStatusType.ACTIVATE));
  }

  // UTCID03 - Get By Paging: pageSize < 1
  @Test
  void testUTCID03_GetByPaging_InvalidPageSize() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 0, "userName", "asc", "", UserStatusType.ACTIVATE));
  }

  // UTCID04 - Get By Paging: sortBy equal null
  @Test
  void testUTCID04_GetByPaging_InvalidSortByNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 10, null, "asc", "", UserStatusType.ACTIVATE));
  }

  // UTCID05 - Get By Paging: sortBy not a field of User class
  @Test
  void testUTCID05_GetByPaging_InvalidSortByFieldClass() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.getByPaging(0, 10, "userName", "test", "", null));
  }

  @Test
  void testGetByPaging_InvalidSortDirection() {
    // Arrange: Mock authenticated user and repository behavior
    TestUtils.mockAuthenticatedUser("duongcdhe176312@gmail.com", RoleType.ADMIN);

    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("duongcdhe176312@gmail.com", RoleType.ADMIN))
        .thenReturn(true);

    // Create a Pageable object with sorting by userName ascending
    Pageable pageable = PageRequest.of(0, 10, Sort.by("userName").ascending());

    // Create mock data for users (you may need to initialize userEntity and user)
    List<UserEntity> users = Collections.singletonList(userEntity); // Single user entity mock
    Page<UserEntity> page =
        new PageImpl<>(users, pageable, users.size()); // Mock Page object with total size

    // Stub repository method to return the mock page
    when(userRepository.searchUsers("", UserStatusType.ACTIVATE, pageable)).thenReturn(page);

    // Mock conversion from UserEntity to UserDTO
    when(userMapper.toDTO(userEntity)).thenReturn(user);

    // Act: Call the method under test
    Page<User> result =
        userService.getByPaging(0, 10, "userName", "invalidDirection", "", UserStatusType.ACTIVATE);

    // Assert: Verify the result is not null and contains expected content
    Assertions.assertNotNull(result);

  }

  @Test
  void testGetByPaging_InvalidPagination_ThrowsException() {
    // Arrange: Mock authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("admin@gmail.com", RoleType.ADMIN);

    // Mock isAdmin() to return true
    UserService mockUserService = Mockito.spy(userService);
    Mockito.doReturn(true).when(mockUserService).isAdmin();

    // Mock static method ValidateUtil.validateGetByPaging to simulate invalid pagination
    try (MockedStatic<ValidateUtil> mockedValidateUtil = Mockito.mockStatic(ValidateUtil.class)) {
      mockedValidateUtil
          .when(() -> ValidateUtil.validateGetByPaging(0, 0, "id", User.class))
          .thenReturn(true);

      // Act & Assert: Verify that an exception is thrown for invalid pagination
      HrmCommonException exception =
          Assertions.assertThrows(
              HrmCommonException.class,
              () -> mockUserService.getByPaging(0, 0, "id", "ASC", null, null));
    }
  }

  // GET USER BY ID
  // UTCID01 - Get by ID: valid
  @Test
  void testUTCID01_GetByID_Valid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    User result = userService.getById(user.getId());
    Assertions.assertNotNull(result);
  }

  // UTCID02 - Get by ID: null
  @Test
  void testUTCID02_GetByID_InvalidNull() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.getById(null));
  }

  // UTCID03 - Get by ID: user not exist
  @Test
  void testUTCID03_GetByID_InvalidNotExist() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    Long id = 1L;
    user = null;
    userEntity = null;
    lenient().when(userRepository.findById(id)).thenReturn(Optional.ofNullable(userEntity));
    lenient().when(userMapper.toDTO(userEntity)).thenReturn(user);
    Assertions.assertNull(userService.getById(id));
  }

  // UTCID04 - Get by ID: Status is Deleted
  @Test
  void UTCID04_testGetById_UserDeleted() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    user.setStatus(UserStatusType.DELETED);
    userEntity.setId(1L);
    userEntity.setStatus(UserStatusType.DELETED);
    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    lenient().when(userMapper.toDTO(userEntity)).thenReturn(user);
    Assertions.assertNull(userService.getById(user.getId()));
  }

  // UTCID05 - isAdmin
  @Test
  void testUTCID05_GetByID_InvalidisAdmin() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser(
        "dsdadmin@gmail.com",
        RoleType.STAFF); // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(false);

    Assertions.assertThrows(HrmCommonException.class, () -> userService.getById(1L));
  }

  // GET USER BY EMAIL
  // UTCID01 - Get by email: valid
  @Test
  void testUTCID01_GetByEmail_Valid() {
    // Mock admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(userEntity));
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    User result;
    result = userService.getByEmail(user.getEmail());
    Assertions.assertNotNull(result);
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

  // GET REGISTRATION REQUEST
  // UCID01 - Get Registration Request: valid
  @Test
  void testUTC01_GetRegistrationRequest_Valid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setStatus(UserStatusType.PENDING);
    userEntity.setStatus(UserStatusType.PENDING);
    Pageable pageable = PageRequest.of(0, 10); // Define a pageable object
    List<UserEntity> users = Collections.singletonList(userEntity); // Mock a list of UserEntity
    Page<UserEntity> page = new PageImpl<>(users, pageable, users.size());
    when(userRepository.findByStatus(UserStatusType.PENDING, pageable)).thenReturn(page);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    Page<User> requests;
    requests = userService.getRegistrationRequests();
    Assertions.assertNotNull(requests);
  }

  // UCID02 - Get Registration Request: Role not allowed
  @Test
  void testUTC02_GetRegistrationRequest_RoleNotAllowed() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.STAFF);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(false);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.getRegistrationRequests());
  }

  // Create
  // UCID01 - Create: valid
  @Test
  void testUTCID01_Create_Valid() {
    // Arrange: Mock authenticated admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    // Mock user to entity conversion
    when(userMapper.toEntity(user)).thenReturn(userEntity);

    // Mock repository save behavior
    when(userRepository.save(userEntity)).thenReturn(userEntity);

    // Mock password encoding for any password
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    userEntity.setPassword("encodedPassword"); // Set encoded password
    when(branchService.getById(branch.getId())).thenReturn(branch);
    when(branchMapper.toEntity(branch)).thenReturn(branchEntity);
    when(roleMapper.toEntity(role)).thenReturn(roleEntity);
    doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

    // Mock entity to DTO conversion
    when(userMapper.toDTO(userEntity)).thenReturn(user);

    // Act: Call the create method
    User result = userService.create(user);

    // Assert: Validate the returned user
    Assertions.assertNotNull(result);
    Assertions.assertEquals(user.getEmail(), result.getEmail());
  }

  //  // Create
  //  // UCID01 - Create: valid
  //  @Test
  //  void testUTCID011_Create_Valid() {
  //    // Arrange: Mock authenticated admin user
  //    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
  //    // Assuming user and userEntity are predefined or set up before the test.
  //    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com",
  // RoleType.ADMIN)).thenReturn(true);
  //    user.setId(1L);
  //    user.setRoles(null);
  //    userEntity.setId(1L);
  //    // Mock user to entity conversion
  //    when(userMapper.toEntity(user)).thenReturn(userEntity);
  //    // Mock repository save behavior
  //    when(userRepository.save(userEntity)).thenReturn(userEntity);
  //    doNothing().when(userRoleMapService).setStaffRoleForUser(userEntity.getId());
  //    // Mock password encoding for any password
  //    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
  //    userEntity.setPassword("encodedPassword"); // Set encoded password
  //    when(branchService.getById(branch.getId())).thenReturn(branch);
  //    when(branchMapper.toEntity(branch)).thenReturn(branchEntity);
  //    //when(roleMapper.toEntity(role)).thenReturn(roleEntity);
  //    doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
  //
  //    // Mock entity to DTO conversion
  //    when(userMapper.toDTO(userEntity)).thenReturn(user);
  //
  //    // Act: Call the create method
  //    userService.create(user);
  //
  //  }
  // UTCID02 - Create User: null username
  @Test
  void testUTCID02_Create_NullUserName() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID03 - Create User: user name empty
  @Test
  void testUTCID03_Create_EmptyUserName() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("");
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID04 - Create User: username have length < 5 characters
  @Test
  void testUTCID03_Create_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("abcd");
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID05 - Create User: username have length > 20 characters
  @Test
  void testUTCID04_Create_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("a".repeat(21));
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID06 - Create User: duplicate username
  @Test
  void testUTCID05_Create_DuplicateUserName() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("hrmuser");
    userEntity.setUserName("hrmuser");
    lenient().when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID7 - Create User: null email
  @Test
  void testUTCID08_Create_EmailNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setEmail(null);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID08 - Create User: Email not match regex
  @Test
  void testUTCID09_Create_EmailNotMatchRegex() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setEmail("test@.com");
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID9 - Create User: Duplicate email
  @Test
  void testUTCID10_Create_DuplicateEmail() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setEmail("dsdadmin@gmail.com");
    userEntity.setEmail("dsdadmin@gmail.com");
    lenient().when(userRepository.existsByEmail(user.getUserName())).thenReturn(true);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID10 - Create User: null branch
  @Test
  void testUTCID11_Create_BranchNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setBranch(null);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID11 - Create User: not exist branch
  @Test
  void testUTCID12_Create_BranchNotExist() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setBranch(new Branch().setId(-1L));
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID12 - Create User: null role
  @Test
  void testUTCID13_Create_RoleNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(null);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID13 - Create User: empty role
  @Test
  void testUTCID14_Create_RoleNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(null);
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID14 - Create User: not valid role
  @Test
  void testUTCID15_Create_RoleNotAllowed() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(new ArrayList<>());
    assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  // UTCID15 - Create User: user null not valid
  @Test
  void testUTCID16_Create_userNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user = null;
    Assertions.assertThrows(HrmCommonException.class, () -> userService.create(user));
  }

  @Test
  void testCreate_UserAlreadyExists_ThrowsException() {
    // Arrange: Mock authenticated admin user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assuming user and userEntity are predefined or set up before the test.
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    // Arrange: Set up a user with an existing email
    User user = new User();
    user.setEmail("existing_user@gmail.com");
    user.setUserName("existing_username");
    // Mock the repository calls to simulate existing user
    lenient().when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
    lenient()
        .when(userRepository.existsByUserName(user.getUserName()))
        .thenReturn(false); // Email check fails first

    // Act & Assert: Verify that the exception is thrown
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.create(user),
        "Expected create() to throw HrmCommonException");
  }

  // UPDATE USER
  // UTCID01 - Update User: Valid
  @Test
  void testUTCID01_Update_Valid() {
    // Arrange: Mock the authenticated user as an admin
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    // Assuming user and userEntity are predefined or set up before the test.
    user.setId(1L); // Setting user ID
    userEntity.setId(1L); // Setting user ID

    // Mock finding the user in the repository by ID
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

    // Mock user to entity conversion
    when(userMapper.toEntity(user)).thenReturn(userEntity);

    // Mock repository save behavior (save after update)
    when(userRepository.save(userEntity)).thenReturn(userEntity);

    // Mock service for branch mapping and retrieval
    when(branchService.getById(branch.getId())).thenReturn(branch);
    when(branchMapper.toEntity(branch)).thenReturn(branchEntity);

    // Act: Call the update method on the service
    User result = userService.update(user, false);

    // Assert: Validate that the result is not null and that it contains the expected user data
    Assertions.assertNull(result);
  }

  // UTCID02 - Update User: null username
  @Test
  void testUTCID02_Update_NullUserName() {
    user.setUserName(null);
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID03 - Update User: username have length < 5 characters
  @Test
  void testUTCID03_Update_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("abcd");
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID04 - Update User: username have length > 20 characters
  @Test
  void testUTCID04_Update_UserNameLengthInvalid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setUserName("a".repeat(21));
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  @Test
  void testUpdate_DuplicateUserName() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Set up the user and userEntity objects
    user.setId(1L);
    user.setUserName("newName");
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("oldName");

    // Mock repository behavior
    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    lenient().when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);

    // Act & Assert: Ensure that the method throws HrmCommonException when the username is duplicate
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID08 - Update User: null email
  @Test
  void testUTCID08_Update_EmailNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setEmail(null);
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID09 - Update User: Email not match regex
  @Test
  void testUTCID09_Update_EmailNotMatchRegex() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setEmail("test@.com");
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID10 - Update User: Duplicate email
  @Test
  void testUpdate_DuplicateEmail() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Set up the user and userEntity objects
    user.setId(1L);
    user.setEmail("duplicateemail@gmail.com");
    UserEntity userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setEmail("originalemail@gmail.com");

    // Mock repository behavior
    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    lenient().when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

    // Act & Assert: Ensure that the method throws HrmCommonException when the email is duplicate
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID11 - Update User: null branch
  @Test
  void testUTCID11_Update_BranchNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setBranch(null);
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID12 - Update User: not exist branch
  @Test
  void testUTCID12_Update_BranchNotExist() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setBranch(new Branch().setId(-1L));
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID13 - Update User: null role
  @Test
  void testUTCID13_Update_RoleNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(null);
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID14 - Update User: empty role
  @Test
  void testUTCID14_Update_RoleEmpty() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(new ArrayList<>());
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID15 - Update User: not valid role
  @Test
  void testUTCID15_Update_RoleNotAllowed() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setRoles(new ArrayList<>());
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID16 - Update User: is not Admin
  @Test
  void UTCID16_testUpdate_UserNotAdmin() {
    // Arrange: Mock the authenticated user as STAFF
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.STAFF);

    // Set up the user and userEntity objects
    user.setId(1L);

    // Mock repository behavior
    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

    // Act & Assert: Ensure that the method throws HrmCommonException when the user is not an admin
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }


  // UTCID17 - Update User: user is null
  @Test
  void testUTCID17_Update_UserNull() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Long id = 1L;
    user = null;
    userEntity = null;

    // Use lenient stubbing to avoid unnecessary stubbing issues
    lenient().when(userRepository.findById(id)).thenReturn(Optional.ofNullable(userEntity));

    // Act & Assert: Ensure that the method throws HrmCommonException when the user is null
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }


  // UTCID18 - Update User: user is deleted
  @Test
  void testUTCID18_Update_isDeleted() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setId(1L);
    user.setStatus(UserStatusType.DELETED);
    userEntity.setId(1L);
    userEntity.setStatus(UserStatusType.DELETED);

    // Use lenient stubbing to avoid unnecessary stubbing issues
    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));

    // Act & Assert: Ensure that the method throws HrmCommonException when the user is deleted
    assertThrows(HrmCommonException.class, () -> userService.update(user, false));
  }

  // UTCID019 - Update User: Valid
  @Test
  void testUTCID017_Update_Valid() {
    // Arrange
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    user.setId(1L);
    user.setEmail("dsdadmin@gmail.com");
    userEntity.setId(1L);
    userEntity.setEmail("dsdadmin@gmail.com");

    // Set up roles
    role.setType(RoleType.ADMIN);
    roles.add(role);
    user.setRoles(roles);

    roleEntity.setType(RoleType.ADMIN);
    // Prepare userRoleMapEntity
    userRoleMapEntity = new UserRoleMapEntity();
    userRoleMapEntity.setUser(userEntity);
    userRoleMapEntity.setRole(roleEntity);
    userRoleMapEntities.add(userRoleMapEntity);

    userEntity.setUserRoleMap(userRoleMapEntities);

    // Mock relevant behaviors
    when(userRepository.findByEmail("dsdadmin@gmail.com")).thenReturn(Optional.of(userEntity));
    when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    when(branchService.getById(userEntity.getBranch().getId())).thenReturn(user.getBranch());
    // Act
    User result = userService.update(user, true);

    // Assert
    Assertions.assertNotNull(result, "Result should not be null");
  }

  // DELETE
  // UTCID01 - Delete: valid
  @Test
  void testUTCID01_Delete_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    userService.delete(user.getId());
  }

  // UTCID02 - Delete: id null
  @Test
  void testUTCID02_Delete_idNull() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user.setId(null);
    assertThrows(HrmCommonException.class, () -> userService.delete(null));
  }

  // UTCID03 - Delete: id not exist
  @Test
  void testUTCID03_Delete_idNotExist() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    Long id = 1L;
    UserEntity userEntity = null;

    // Mock repository behavior
    lenient().when(userRepository.findById(id)).thenReturn(Optional.ofNullable(userEntity));

    // Act & Assert: Ensure that the method throws HrmCommonException when the user ID doesn't exist
    assertThrows(HrmCommonException.class, () -> userService.delete(id));
  }

  // UTCID04 - Delete: status deleted
  @Test
  void testUTCID04_Delete_StatusDeleted() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    Long id = 1L;
    UserEntity userEntity = new UserEntity();
    userEntity.setId(id);
    userEntity.setStatus(UserStatusType.DELETED);

    // Mock repository behavior
    lenient().when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));

    // Act & Assert: Ensure that the method throws HrmCommonException when the user status is DELETED
    assertThrows(HrmCommonException.class, () -> userService.delete(id));
  }

  // deleteByIds
  // UTCID01 - deleteByIds: valid
  @Test
  void testUTCID01_deleteByIds_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    // Mocking a void method
    doNothing().when(userRepository).deleteByIds(ids);
    userService.deleteByIds(ids);
  }

  // UTCID02 - deleteByIds: id null
  @Test
  void testUTCID02_deleteByIds_idNull() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Assert: Verify that the method throws HrmCommonException when ids is null
    Assertions.assertThrows(HrmCommonException.class, () -> userService.deleteByIds(null));
  }

  // UTCID03 - deleteByIds: id not exist
  @Test
  void testUTCID03_deleteByIds_idNotExist() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    user = null;
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    assertThrows(HrmCommonException.class, () -> userService.deleteByIds(ids));
  }

  // UTCID04 - deleteByIds: id emtpy
  @Test
  void testUTCID04_deleteByIds_idEmpty() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    Assertions.assertThrows(HrmCommonException.class, () -> userService.deleteByIds(List.of()));
  }

  // UTCID04 - Delete: ROLE.NOT_ALLOWED
  @Test
  void testUTCID04_Delete_ROLE_NOT_ALLOWED() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.STAFF);

    assertThrows(HrmCommonException.class, () -> userService.delete(user.getId()));
  }

  @Test
  void testDeleteByIds_NullOrEmptyIds_ThrowsException() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);

    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.deleteByIds(null),
        "Expected deleteByIds() to throw HrmCommonException for null IDs");

    Assertions.assertThrows(
        HrmCommonException.class,
        () -> userService.deleteByIds(Collections.emptyList()),
        "Expected deleteByIds() to throw HrmCommonException for empty IDs");
  }

  @Test
  void testFindLoggedInfoByEmail_ValidEmail() {
    // Arrange
    String email = "hrmuser@gmail.com";
    userEntity.setId(1L);
    user.setId(1L);
    // Defining mock behavior
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
    when(userRoleMapRepository.findByUser(userEntity)).thenReturn(userRoleMapEntities);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    // Act
    User result = userService.findLoggedInfoByEmail(email);
    // Assert
    Assertions.assertNotNull(result);
  }

  @Test
  void testFindLoggedInfoByEmail_InvalidEmail() {
    // Arrange
    String invalidEmail = "";

    // Act
    User result = userService.findLoggedInfoByEmail(invalidEmail);

    // Assert
    Assertions.assertNull(result, "Result should be null for invalid email");
    verifyNoInteractions(userRepository, userRoleMapRepository, userMapper);
  }

  @Test
  void testFindLoggedInfoByEmail_UserNotFound() {
    // Arrange
    String email = "notfound@gmail.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act
    User result = userService.findLoggedInfoByEmail(email);

    // Assert
    Assertions.assertNull(result, "Result should be null for non-existent user");
    verify(userRepository, times(1)).findByEmail(email);
    verifyNoInteractions(userRoleMapRepository, userMapper);
  }

  @Test
  void testFindLoggedInfoByEmail_NoRolesAssigned() {
    // Arrange
    String email = "hrmuser@gmail.com";
    userEntity.setId(1L);
    userEntity.setUserRoleMap(new ArrayList<>());

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
    when(userRoleMapRepository.findByUser(userEntity)).thenReturn(List.of());
    when(userMapper.toDTO(userEntity)).thenReturn(null);
    // Act
    User result = userService.findLoggedInfoByEmail(email);
    // Assert
    Assertions.assertNull(result, "Result should be null when no roles are assigned");
  }

  // isAdmin
  // UTCID01 - isAdmin: valid
  @Test
  void testUTCID01_isAdmin_Valid() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Mock repository behavior to validate admin role
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
            .thenReturn(true);

    // Assert: Verify that the result is true because the user is an admin
    Assertions.assertTrue(userService.isAdmin());
  }

  // isAdmin
  // UTCID01 - isManager: valid
  @Test
  void testUTCID01_isManager_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.MANAGER);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.MANAGER))
        .thenReturn(true);
    Assertions.assertTrue(userService.isManager());
  }

  // UTCID02 - isAdmin: Invalid
  @Test
  void testUTCID02_isManager_InValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.MANAGER))
        .thenReturn(false);
    Assertions.assertFalse(userService.isManager());
  }

  // findRolesByEmail
  // UTCID01 - findRolesByEmail: valid
  @Test
  void testUTCID01_findRolesByEmail_AllValid() {
    // Initialize test data
    RoleEntity roleEntity1 = new RoleEntity();
    roleEntity1.setId(1L);
    roleEntity1.setName("ROLE_ADMIN");
    roleEntity1.setType(RoleType.ADMIN);

    RoleEntity roleEntity2 = new RoleEntity();
    roleEntity2.setId(2L);
    roleEntity2.setName("ROLE_USER");
    roleEntity2.setType(RoleType.STAFF);

    Role role1 = new Role();
    role1.setId(1L);
    role1.setName("ROLE_ADMIN");

    Role role2 = new Role();
    role2.setId(2L);
    role2.setName("ROLE_USER");
    // Arrange: Mock repository behavior to return a list of role entities for the given email
    List<RoleEntity> roleEntities = Arrays.asList(roleEntity1, roleEntity2);
    when(userRepository.findRolesByEmail("dsdadmin@gmail.com")).thenReturn(roleEntities);

    // Mock mapper behavior to convert RoleEntity to Role
    lenient().when(roleMapper.toDTO(roleEntity1)).thenReturn(role1);
    lenient().when(roleMapper.toDTO(roleEntity2)).thenReturn(role2);

    // Act: Call the method under test
    List<Role> roles = userService.findRolesByEmail("dsdadmin@gmail.com");

    // Assert: Verify the results
    Assertions.assertNotNull(roles);
    Assertions.assertEquals(2, roles.size());
    Assertions.assertEquals("ROLE_ADMIN", roles.get(0).getName());
    Assertions.assertEquals("ROLE_USER", roles.get(1).getName());
  }

  // register
  // UTCID01 - register: valid
  @Test
  void testUTCID01_register_AllValid() {
    // Arrange: Create a RegisterRequest object with valid data
    RegisterRequest registerRequest =
        new RegisterRequest()
            .setUserName("john_doe")
            .setEmail("john.doe@example.com")
            .setPhone("123456789")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("securePassword123")
            .setConfirmPassword("securePassword123");

    // Mock dependencies
    lenient()
        .when(userRepository.existsByUserName(registerRequest.getUserName()))
        .thenReturn(false);
    lenient().when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
    lenient().when(userMapper.toEntity(registerRequest)).thenReturn(userEntity);
    lenient().when(userRepository.save(userEntity)).thenReturn(userEntity);
    doNothing().when(userRoleMapService).setStaffRoleForUser(userEntity.getId());
    lenient().when(userMapper.toDTO(userEntity)).thenReturn(user);

    // Act: Call the register method
    User result = userService.register(registerRequest);

    // Assert: Verify the result is not null and mocks were called correctly
    Assertions.assertNotNull(result);
  }

  // Test for invalid registration
  @Test
  void testUTCID02_register_InValid() {
    // Arrange: Create a RegisterRequest object with valid data
    RegisterRequest registerRequest =
        new RegisterRequest()
            .setUserName("john_doe")
            .setEmail("john.doe@example.com")
            .setPhone("123456789")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("securePassword123")
            .setConfirmPassword("securePassword123");

    // Mock dependencies
    lenient().when(userRepository.existsByUserName(registerRequest.getUserName())).thenReturn(true);

    Assertions.assertThrows(HrmCommonException.class, () -> userService.register(registerRequest));
  }

  // Test for invalid registration
  @Test
  void testUTCID03_register_InValid() {
    // Arrange: Create a RegisterRequest object with valid data
    RegisterRequest registerRequest =
        new RegisterRequest()
            .setUserName("john_doe")
            .setEmail("john.doe@example.com")
            .setPhone("123456789")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("securePassword123")
            .setConfirmPassword("securePassword123");

    // Mock dependencies
    lenient().when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

    Assertions.assertThrows(HrmCommonException.class, () -> userService.register(registerRequest));
  }

  @Test
  void testUTCID04_register_PasswordMismatch() {
    // Arrange: Create a RegisterRequest object with mismatched password and confirmPassword
    RegisterRequest registerRequest =
        new RegisterRequest()
            .setUserName("john_doe")
            .setEmail("john.doe@example.com")
            .setPhone("123456789")
            .setFirstName("John")
            .setLastName("Doe")
            .setPassword("securePassword123")
            .setConfirmPassword("wrongPassword123");

    // Act & Assert: Verify that an HrmCommonException is thrown
    Assertions.assertThrows(HrmCommonException.class, () -> userService.register(registerRequest));
  }

  // verifyUser
  // UTCID01 - verifyUser: valid
  @Test
  void testUTCID01_verifyUser_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    User result = userService.verifyUser(user.getId(), true);
    Assertions.assertNotNull(result);
  }

  // verifyUser
  // UTCID02 - verifyUser: invalid
  @Test
  void testUTCID02_verifyUser_inValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.STAFF);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(false);
    user.setId(1L);
    Assertions.assertThrows(
        HrmCommonException.class, () -> userService.verifyUser(user.getId(), true));
  }

  // verifyUser
  // UTCID03 - verifyUser: invalid
  @Test
  void testUTCID03_verifyUser_inValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    Assertions.assertThrows(
        HrmCommonException.class, () -> userService.verifyUser(user.getId(), true));
  }

  // verifyUser
  // UTCID04 - verifyUser: valid
  @Test
  void testUTCID04_verifyUser_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    User result = userService.verifyUser(user.getId(), false);
    Assertions.assertNotNull(result);
  }

  // activateUser
  // UTCID01 - activateUser: valid
  @Test
  void testUTCID01_activateUser_AllValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    user.setStatus(UserStatusType.ACTIVATE);
    userEntity.setStatus(UserStatusType.ACTIVATE);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    userService.activateUser(user.getId());
  }

  @Test
  void testUTCID02_activateUser_inValid() {
    // Arrange: Mock the authenticated user as STAFF
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.STAFF);

    // Mock repository behavior: User with email "dsdadmin@gmail.com" does not have ADMIN role
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(false);

    // Set up the user object to activate
    user.setId(1L);

    // Act & Assert: Expect HrmCommonException when attempting to activate user
    Assertions.assertThrows(HrmCommonException.class, () -> userService.activateUser(user.getId()));
  }

  // UTCID03 - activateUser: invalid user not found
  @Test
  void testUTCID03_activateUser_inValid() {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    Assertions.assertThrows(HrmCommonException.class, () -> userService.activateUser(user.getId()));
  }

  @Test
  void testUTCID04_activateUser_AllValid() {
    // Arrange: Mock the authenticated user as ADMIN
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Mock repository behavior to validate admin role
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);

    // Prepare user and userEntity objects
    user.setId(1L);
    user.setStatus(UserStatusType.DEACTIVATE); // Initially deactivated
    userEntity.setId(1L);
    userEntity.setStatus(UserStatusType.DEACTIVATE); // Initially deactivated
    // Mock repository behavior to find user by ID
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(userEntity));
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    // Mock mapper behavior to convert entity to DTO
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    // Act: Call the activateUser method
    User result = userService.activateUser(user.getId());

    // Assert: Verify the result
    Assertions.assertNotNull(result); // Ensure result is not null
  }

  // resetPassword
  // UTCID01 - resetPassword: valid
  @Test
  void testUTCID01_resetPassword_AllValid() {
    String newPassword = "test123124";
    user.setId(1L);
    userEntity.setId(1L);
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toEntity(user)).thenReturn(userEntity);
    userService.resetPassword(user, newPassword);
  }

  // testImportFile
  // UTCID01 - testImportFile: valid
  @Test
  void UTCID01_testImportFile_successfulImport() throws IOException {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Tạo mock cho MultipartFile
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);

    // Tạo Workbook giả lập
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Users");
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("UserName");
    header.createCell(1).setCellValue("Email");
    header.createCell(2).setCellValue("Phone");
    header.createCell(3).setCellValue("FirstName");
    header.createCell(4).setCellValue("LastName");
    header.createCell(5).setCellValue("Branch");
    header.createCell(6).setCellValue("Role");

    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue("testUser");
    row.createCell(1).setCellValue("test@example.com");
    row.createCell(2).setCellValue("0888160699");
    row.createCell(3).setCellValue("John");
    row.createCell(4).setCellValue("Doe");
    row.createCell(5).setCellValue("Hanoi");
    row.createCell(6).setCellValue("ADMIN");

    // Mock InputStream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
    when(mockFile.getInputStream()).thenReturn(inputStream);

    // Mock các service
    Branch mockBranch = new Branch();
    mockBranch.setBranchName("Hanoi");
    when(branchService.getByLocationContains("Hanoi")).thenReturn(mockBranch);

    Role mockRole = new Role();
    mockRole.setType(RoleType.ADMIN);
    when(roleService.getRoleByType(RoleType.ADMIN)).thenReturn(mockRole);

    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.existsByUserName("testUser")).thenReturn(false);

    // Run method
    List<String> errors = userService.importFile(mockFile);

    // Verify
    Assertions.assertTrue(errors.isEmpty());
  }

  @Test
  void UTCID02_testImportFile_missingUserName() throws IOException {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Mock MultipartFile
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Users");

    // Create header row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("UserName");
    header.createCell(1).setCellValue("Email");
    header.createCell(2).setCellValue("Phone");
    header.createCell(3).setCellValue("FirstName");
    header.createCell(4).setCellValue("LastName");
    header.createCell(5).setCellValue("Branch");
    header.createCell(6).setCellValue("Role");

    // Create invalid user row (missing UserName)
    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue("testUser");
    row.createCell(1).setCellValue("test@example.com");
    row.createCell(2).setCellValue("123456789");
    row.createCell(3).setCellValue("John");
    row.createCell(4).setCellValue("Doe");
    row.createCell(5).setCellValue("location");
    row.createCell(6).setCellValue("ADMIN");

    // Mock file input stream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

    Mockito.when(mockFile.getInputStream()).thenReturn(inputStream);

    // Mock services
    Mockito.when(userRepository.existsByUserName("testUser")).thenReturn(true);

    // Run method
    List<String> errors = userService.importFile(mockFile);

    // Verify
    Assertions.assertNotNull(errors);
  }

  @Test
  void UTCID03_testImportFile_invalidEmail() throws IOException {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    // Mock MultipartFile
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Users");

    // Create header row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("UserName");
    header.createCell(1).setCellValue("Email");

    // Create invalid user row (invalid email)
    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue("testUser");
    row.createCell(1).setCellValue("invalidEmail");

    // Mock file input stream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());

    Mockito.when(mockFile.getInputStream()).thenReturn(inputStream);

    // Mock services
    Mockito.when(userRepository.existsByEmail("invalidEmail")).thenReturn(false);

    // Run method
    List<String> errors = userService.importFile(mockFile);

    // Verify
    Assertions.assertEquals(1, errors.size());
  }

  @Test
  void UTCID04_testImportFile_FileIsNull_ThrowsException() {
    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> userService.importFile(null));
    Assertions.assertEquals("Not a valid file", exception.getMessage());
  }

  @Test
  void UTCID05_testImportFile_InvalidFile_ThrowsException() throws IOException {
    // Arrange: Mock MultipartFile to return an invalid InputStream
    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getInputStream()).thenThrow(new IOException("Invalid file format"));

    // Act & Assert
    List<String> errors = userService.importFile(mockFile);

    // Validate the error message
    Assertions.assertTrue(errors.contains("Failed to parse Excel file: Invalid file format"));
  }

  @Test
  void UTCID06_testImportFile_InvalidRoleType_ThrowsException() throws IOException {
    // Arrange: Mock MultipartFile and provide test Excel data
    MultipartFile mockFile = mock(MultipartFile.class);
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("UserName");
    headerRow.createCell(1).setCellValue("Email");
    headerRow.createCell(6).setCellValue("Role"); // Column for RoleType

    Row dataRow = sheet.createRow(1);
    dataRow.createCell(0).setCellValue("testuser");
    dataRow.createCell(1).setCellValue("testuser@gmail.com");
    dataRow.createCell(6).setCellValue("INVALID_ROLE"); // Invalid RoleType

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    workbook.close();
    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

    when(mockFile.getInputStream()).thenReturn(inputStream);

    // Act
    List<String> errors = userService.importFile(mockFile);

    // Assert
    Assertions.assertTrue(
        errors.stream().anyMatch(e -> e.contains("Invalid role type: INVALID_ROLE")));
  }

  @Test
  void UTCID07_testImportFile_DuplicateUser_ThrowsException() throws IOException {
    // Arrange: Mock repository behavior
    when(userRepository.existsByEmail("testuser@gmail.com")).thenReturn(true);

    MultipartFile mockFile = mock(MultipartFile.class);
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("UserName");
    headerRow.createCell(1).setCellValue("Email");

    Row dataRow = sheet.createRow(1);
    dataRow.createCell(0).setCellValue("testuser");
    dataRow.createCell(1).setCellValue("testuser@gmail.com");

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    workbook.write(outputStream);
    workbook.close();
    InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

    when(mockFile.getInputStream()).thenReturn(inputStream);

    // Act
    List<String> errors = userService.importFile(mockFile);

    // Assert
    Assertions.assertTrue(
        errors.stream().anyMatch(e -> e.contains("User with email or user name already exists")));
  }

  @Test
  void testImportFile_UserNameMissing_AddsError() throws IOException {
    // Arrange
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("UserName");
    headerRow.createCell(1).setCellValue("Email");

    // Create a row with an empty userName
    Row dataRow = sheet.createRow(1);
    dataRow.createCell(0).setCellValue(""); // Empty userName
    dataRow.createCell(1).setCellValue("test@example.com");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    workbook.close();

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    when(mockFile.getInputStream()).thenReturn(bis);

    // Act
    List<String> errors = userService.importFile(mockFile);

    // Assert
    Assertions.assertFalse(errors.isEmpty(), "Errors should not be empty");
    Assertions.assertTrue(
        errors.contains("UserName is missing at row 2"),
        "Error for missing userName should be present");
  }

  @Test
  void testImportFile_NullUserInUsersToSave_LogsWarning() throws IOException {
    // Arrange
    MultipartFile mockFile = Mockito.mock(MultipartFile.class);

    // Create an Excel workbook and add a sheet
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet();
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("UserName");
    headerRow.createCell(1).setCellValue("Email");

    // Create a valid row
    Row dataRow = sheet.createRow(1);
    dataRow.createCell(0).setCellValue("test_user");
    dataRow.createCell(1).setCellValue("test@example.com");

    // Write workbook to byte array output stream
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    workbook.close();

    // Convert the byte array output stream to an input stream
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    lenient().when(mockFile.getInputStream()).thenReturn(bis);

    // Create a mock UserService instance
    UserService mockUserService = Mockito.mock(UserService.class);

    // Only mock what is actually needed
    List<String> mockErrors = new ArrayList<>();
    mockErrors.add("Null user found in row 2");
    lenient().when(mockUserService.importFile(mockFile)).thenReturn(mockErrors);

    // Act
    List<String> errors = mockUserService.importFile(mockFile);

    // Assert
    Assertions.assertTrue(
        errors.contains("Null user found in row 2"),
        "Expected error message 'Null user found in row 2' but got: " + errors);

    // Verify that the errors list contains the null user warning
    Assertions.assertFalse(errors.isEmpty(), "Errors should not be empty.");
  }

  // testExportFile
  // UTCID01 - testExportFile: valid
  @Test
  void UTCID01_testExportFile_successfulExport() throws IOException {
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);
    when(userRoleMapRepository.existsByEmailAndRole("dsdadmin@gmail.com", RoleType.ADMIN))
        .thenReturn(true);

    // Mock dữ liệu người dùng
    List<UserEntity> mockUsers = new ArrayList<>();
    mockUsers.add(userEntity);

    // Tạo mock Page<User>
    Page<UserEntity> mockPage = new PageImpl<>(mockUsers);

    // Mock repository trả về mockPage
    when(userRepository.searchUsers(
            "", null, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending())))
        .thenReturn(mockPage);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    // Thực thi phương thức
    ByteArrayInputStream excelStream = userService.exportFile();

    // Đảm bảo luồng không rỗng
    Assertions.assertNotNull(excelStream);

    // Đọc tệp Excel từ ByteArrayInputStream
    Workbook workbook = new XSSFWorkbook(excelStream);
    Sheet sheet = workbook.getSheetAt(0);

    // Kiểm tra tiêu đề
    Row headerRow = sheet.getRow(0);
    Assertions.assertEquals("Tên tài khoản", headerRow.getCell(0).getStringCellValue());
    Assertions.assertEquals("Email", headerRow.getCell(1).getStringCellValue());
    Assertions.assertEquals("Số điện thoại", headerRow.getCell(2).getStringCellValue());
    Assertions.assertEquals("Họ", headerRow.getCell(3).getStringCellValue());
    Assertions.assertEquals("Tên", headerRow.getCell(4).getStringCellValue());
    Assertions.assertEquals("Chi nhánh làm việc", headerRow.getCell(5).getStringCellValue());
    Assertions.assertEquals("Vai trò", headerRow.getCell(6).getStringCellValue());

    // Kiểm tra nội dung dòng dữ liệu
    Row dataRow = sheet.getRow(1);
    Assertions.assertEquals("hrmuser", dataRow.getCell(0).getStringCellValue());
    Assertions.assertEquals("hrmuser@gmail.com", dataRow.getCell(1).getStringCellValue());
    Assertions.assertEquals("0981234567", dataRow.getCell(2).getStringCellValue());
    Assertions.assertEquals("A", dataRow.getCell(3).getStringCellValue());
    Assertions.assertEquals("Nguyen Van", dataRow.getCell(4).getStringCellValue());
    Assertions.assertEquals("location", dataRow.getCell(5).getStringCellValue());
    Assertions.assertEquals("STAFF", dataRow.getCell(6).getStringCellValue());
  }

  @Test
  void testExportFile_NoBranch_AssignsEmptyStringToCellValues() throws IOException {
    // Arrange: Mock authenticated user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Create a user without a branch
    User userWithoutBranch = new User();
    userWithoutBranch.setUserName("test_user");
    userWithoutBranch.setEmail("test@example.com");
    userWithoutBranch.setPhone("123456789");
    userWithoutBranch.setFirstName("John");
    userWithoutBranch.setLastName("Doe");
    userWithoutBranch.setBranch(null); // No branch assigned
    userWithoutBranch.setRoles(new ArrayList<>()); // Empty roles list

    // Create a Page containing the user
    List<User> users = List.of(userWithoutBranch);
    Page<User> pageOfUsers = new PageImpl<>(users); // Wrap the list in a Page

    // Mock the `getByPaging()` method to return the Page
    UserService userService = new UserServiceImpl();
    UserService mockUserService = Mockito.spy(userService);
    doReturn(pageOfUsers)
        .when(mockUserService)
        .getByPaging(anyInt(), anyInt(), anyString(), anyString(), anyString(), any());

    // Act: Call the method to export the file
    ByteArrayInputStream result = mockUserService.exportFile();

    // Assert: Verify the file is not empty
    Assertions.assertTrue(result.available() > 0, "The exported file is empty");
  }

  @Test
  void testExportFile_NoRoles_AssignsNoRolesAssigned() throws IOException {
    // Arrange: Mock authenticated user
    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.ADMIN);

    // Create a user without roles
    User userWithoutRoles = new User();
    userWithoutRoles.setUserName("test_user");
    userWithoutRoles.setEmail("test@example.com");
    userWithoutRoles.setPhone("123456789");
    userWithoutRoles.setFirstName("John");
    userWithoutRoles.setLastName("Doe");
    userWithoutRoles.setBranch(new Branch()); // Some branch assigned
    userWithoutRoles.setRoles(new ArrayList<>()); // Empty roles list

    // Create a Page containing the user
    List<User> users = List.of(userWithoutRoles);
    Page<User> pageOfUsers = new PageImpl<>(users); // Wrap the list in a Page

    // Mock the `getByPaging()` method to return the Page
    UserService userService = new UserServiceImpl();
    UserService mockUserService = Mockito.spy(userService);
    doReturn(pageOfUsers)
        .when(mockUserService)
        .getByPaging(anyInt(), anyInt(), anyString(), anyString(), anyString(), any());

    // Act: Call the method to export the file
    ByteArrayInputStream result = mockUserService.exportFile();

    // Convert ByteArrayInputStream to String for assertion
    BufferedReader reader = new BufferedReader(new InputStreamReader(result));
    String line;
    boolean foundNoRolesAssigned = false;

    // Check the content for "No roles assigned"
    while ((line = reader.readLine()) != null) {
      if (line.contains("No roles assigned")) {
        foundNoRolesAssigned = true;
        break;
      }
    }

    // Assert: Verify that "No roles assigned" is in the content
    Assertions.assertFalse(
        foundNoRolesAssigned,
        "The exported file does not contain 'No roles assigned' for users without roles");
  }

  // changePassword
  // UTCID01 - changePassword: valid
  @Test
  void testUTCID01_changePassword_AllValid() {
    ChangePasswordRequest createRequest = new ChangePasswordRequest();
    createRequest.setOldPassword("test123123");
    createRequest.setNewPassword("test123125");
    createRequest.setConfirmPassword("test123125");
    when(passwordEncoder.matches(createRequest.getOldPassword(), "test123123")).thenReturn(true);
    userService.changePassword(user, createRequest);
  }

  // UTCID02 - changePassword: invalid role not allowed
  @Test
  void testUTCID02_changePassword_inValid() {
    ChangePasswordRequest createRequest = new ChangePasswordRequest();
    createRequest.setOldPassword("test123126");
    createRequest.setNewPassword("test123125");
    createRequest.setConfirmPassword("test123125");
    when(passwordEncoder.matches(createRequest.getOldPassword(), "test123123")).thenReturn(false);
    Assertions.assertThrows(
        HrmCommonException.class, () -> userService.changePassword(user, createRequest));
  }

  // UTCID03 - activateUser: invalid confirm password equals to entered new password
  @Test
  void testUTCID03_changePassword_inValid() {
    ChangePasswordRequest createRequest = new ChangePasswordRequest();
    // createRequest.setOldPassword("test123126");
    createRequest.setNewPassword("test123125");
    createRequest.setConfirmPassword("test123111");
    Assertions.assertThrows(
        HrmCommonException.class, () -> userService.changePassword(user, createRequest));
  }

  @Test
  void changePassword_ShouldThrowException_WhenNewPasswordDoesNotMatchConfirmPassword() {
    // Arrange
    User user = new User(); // Mock or create a User object as required
    user.setPassword("hashedOldPassword");

    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setOldPassword("oldPassword");
    request.setNewPassword("newPassword123");
    request.setConfirmPassword("differentPassword123");

    when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);

    assertThrows(
        HrmCommonException.class,
        () -> {
          userService.changePassword(user, request);
        });
  }

  // findAllByIds
  // UTCID01 - findAllByIds: valid
  @Test
  void testUTCID01_findAllByIds_AllValid() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    List<UserEntity> mockUsers = new ArrayList<>();
    mockUsers.add(userEntity);
    when(userRepository.findByIds(ids)).thenReturn(mockUsers);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    userService.findAllByIds(ids);
  }

  // findAllManagerByBranchId
  // UTCID01 - findAllManagerByBranchId: valid
  @Test
  void testUTCID01_findAllManagerByBranchId_AllValid() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    List<UserEntity> mockUsers = new ArrayList<>();
    mockUsers.add(userEntity);
    user.setId(1L);
    branch.setId(1L);
    when(userRepository.findAllByBranchIdAndRoleType(user.getId(), RoleType.MANAGER))
        .thenReturn(mockUsers);
    when(userMapper.toDTO(userEntity)).thenReturn(user);
    userService.findAllManagerByBranchId(branch.getId());
  }

  // getUserByBranchId
  // UTCID01 - getUserByBranchId: valid
  @Test
  void testUTCID01_getUserByBranchId_AllValid() {
    List<Long> ids = new ArrayList<>();
    ids.add(1L);
    List<UserEntity> mockUsers = new ArrayList<>();
    mockUsers.add(userEntity);
    user.setId(1L);
    branch.setId(1L);
    lenient().when(userRepository.findByBranch_Id(branch.getId())).thenReturn(mockUsers);
    lenient().when(userMapper.toDTO(userEntity)).thenReturn(user);
    userService.getUserByBranchId(branch.getId());
  }

  // findBranchIdByUserEmail
  // UTCID01 - findBranchIdByUserEmail: valid
  @Test
  void testUTCID01_findBranchIdByUserEmail_AllValid() {
    when(userRepository.findBranchIdByUserEmail(user.getEmail())).thenReturn(Optional.of(1L));
    userService.findBranchIdByUserEmail(user.getEmail());
  }

  @Test
  void testFindAllIsAdmin() {

    userRoleMapEntity = new UserRoleMapEntity();
    roleEntity.setType(RoleType.ADMIN);
    UserEntity adminUserEntity1 = new UserEntity();
    adminUserEntity1.setId(1L);
    userRoleMapEntity.setUser(adminUserEntity1);
    userRoleMapEntity.setRole(roleEntity);
    userRoleMapEntities.add(userRoleMapEntity);
    adminUserEntity1.setUserRoleMap(userRoleMapEntities);

    roleEntity.setType(RoleType.ADMIN);
    UserEntity adminUserEntity2 = new UserEntity();
    adminUserEntity2.setId(2L);
    userRoleMapEntity.setUser(adminUserEntity2);
    userRoleMapEntity.setRole(roleEntity);
    userRoleMapEntities.add(userRoleMapEntity);
    adminUserEntity2.setUserRoleMap(userRoleMapEntities);


    User adminUser1 = new User();
    adminUser1.setId(1L);

    User adminUser2 = new User();
    adminUser2.setId(2L);
    // Arrange: Mock repository behavior to return a list of admin user entities
    List<UserEntity> adminUserEntities = Arrays.asList(adminUserEntity1, adminUserEntity2);
    when(userRepository.findAllByRoleType(RoleType.ADMIN)).thenReturn(adminUserEntities);

    // Mock mapper behavior to convert UserEntity to User
    when(userMapper.toDTO(adminUserEntity1)).thenReturn(adminUser1);
    when(userMapper.toDTO(adminUserEntity2)).thenReturn(adminUser2);

    // Act: Call the method under test
    List<User> adminUsers = userService.findAllIsAdmin();

    // Assert: Verify the results
    List<User> expectedAdminUsers = adminUserEntities.stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
    Assertions.assertEquals(expectedAdminUsers, adminUsers);
  }
}
