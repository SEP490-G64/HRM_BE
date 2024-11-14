package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.USER;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.RoleMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import com.example.hrm_be.models.requests.ChangePasswordRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.repositories.UserRoleMapRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.ExcelUtility;
import com.example.hrm_be.utils.PasswordGenerator;
import com.example.hrm_be.utils.ValidateUtil;
import io.micrometer.common.lang.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

  @Lazy @Autowired private UserRepository userRepository;
  @Lazy @Autowired private UserMapper userMapper;
  @Lazy @Autowired private RoleMapper roleMapper;

  @Lazy @Autowired private BranchMapper branchMapper;

  @Lazy @Autowired private UserRoleMapService userRoleMapService;
  @Lazy @Autowired private UserRoleMapRepository userRoleMapRepository;

  @Lazy @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private EmailService emailService;
  @Lazy @Autowired private BranchService branchService;
  @Lazy @Autowired private RoleService roleService;

  @Override
  public String getAuthenticatedUserEmail() throws UsernameNotFoundException {
    // Get the current authentication from the security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if the authentication is not null and is authenticated
    if (authentication != null && authentication.isAuthenticated()) {
      // Retrieve user details from the authentication principal
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      if (userDetails != null) {
        // Extract and log the logged user's email
        String loggedEmail = userDetails.getUsername();
        log.info("LOGGED user: {}", loggedEmail);
        return loggedEmail; // Return the logged user's email
      } else {
        // Throw an exception if user details are not found
        throw new UsernameNotFoundException(HrmConstant.ERROR.AUTH.NOT_FOUND);
      }
    }
    // Throw an exception if the authentication is not found
    throw new UsernameNotFoundException(HrmConstant.ERROR.AUTH.NOT_FOUND);
  }

  @Override
  public Page<User> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String keyword,
      @Nullable UserStatusType status) {
    /** TODO Only allow admin user to call this function */
    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }
    if (ValidateUtil.validateGetByPaging(pageNo, pageSize, sortBy, User.class)) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    Sort.Direction convertDirection;
    try {
      convertDirection = Sort.Direction.fromString(sortDirection);
    } catch (IllegalArgumentException e) {
      convertDirection = Sort.Direction.ASC;
    }

    // Create a pageable request based on provided parameters
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(convertDirection, sortBy));

    // Fetch users by keyword and map to DTO
    return userRepository.searchUsers(keyword, status, pageable).map(userMapper::toDTO);
  }

  @Override
  public Page<User> getRegistrationRequests() {
    /** TODO Only allow admin user to call this function */
    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Create a pageable request
    Pageable pageable = PageRequest.of(0, 10);

    // Fetch users by keyword and map to DTO
    return userRepository.findByStatus(UserStatusType.PENDING, pageable).map(userMapper::toDTO);
  }

  @Override
  public User getById(Long id) {
    /** TODO Only allow admin user to call this function */
    // Check if the logged user is an admin
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.USER.INVALID);
    }

    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    UserEntity user = userRepository.findById(id).orElse(null);
    // Validate that the user exists before attempting to delete
    if (user == null || user.getStatus() == UserStatusType.DELETED) {
      return null;
    }

    // Retrieve user by ID and map to DTO
    return userMapper.toDTO(user); // Return null if update fails
  }

  @Override
  public User create(User user) {
    // Only allow admin user to call this function
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    if (!validateUser(user)) {
      throw new HrmCommonException(HrmConstant.ERROR.USER.INVALID);
    }

    // Validate user details and check for existing users with the same email or username
    if (userRepository.existsByEmail(user.getEmail())
        || userRepository.existsByUserName(user.getUserName())) {
      throw new HrmCommonException(HrmConstant.ERROR.USER.EXIST);
    }

    // Generate and encode random password
    String rawPassword = PasswordGenerator.generateRandomPassword();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // Map User DTO to Entity
    UserEntity e = userMapper.toEntity(user);

    // Set user properties
    e.setStatus(UserStatusType.ACTIVATE); // Set user status to ACTIVE
    e.setPassword(encodedPassword); // Set encoded password for the user entity

    // Set branch if the user has one
    if (user.getBranch() != null) {
      e.setBranch(branchMapper.toEntity(user.getBranch()));
    }

    // Save the user entity in the repository
    UserEntity userEntity = userRepository.save(e);

    // Handle role assignment if roles exist
    if (user.getRoles() != null && !user.getRoles().isEmpty()) {
      List<UserRoleMapEntity> userRoleMapEntities =
          user.getRoles().stream()
              .map(
                  role -> {
                    UserRoleMapEntity userRoleMapEntity = new UserRoleMapEntity();
                    userRoleMapEntity.setUser(userEntity); // Use the saved UserEntity 'e'
                    userRoleMapEntity.setRole(roleMapper.toEntity(role)); // Set the role entity
                    return userRoleMapEntity;
                  })
              .collect(Collectors.toList()); // Collect to a List

      // Save role mappings if necessary
      if (!userRoleMapEntities.isEmpty()) {
        userRoleMapRepository.saveAll(userRoleMapEntities); // Save role mappings
      }
    } else {
      userRoleMapService.setStaffRoleForUser(e.getId());
    }

    // Send email notification with the raw password
    emailService.sendEmail(
        user.getEmail(),
        "Tài khoản ứng dụng Quản lý kho Hệ thống nhà thuốc của bạn",
        "Chào bạn,\n\n"
            + "Chúng tôi xin thông báo rằng tài khoản của bạn đã được tạo thành công trên Ứng dụng"
            + " Quản lý kho Hệ thống nhà thuốc.\n\n"
            + "Dưới đây là thông tin tài khoản của bạn:\n"
            + "Tài khoản: "
            + user.getEmail()
            + "\n"
            + "Mật khẩu: "
            + rawPassword
            + "\n\n"
            + "Chúng tôi khuyên bạn nên thay đổi mật khẩu sau khi đăng nhập lần đầu tiên để bảo vệ"
            + " tài khoản của mình.\n\n"
            + "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!\n\n"
            + "Trân trọng,\n"
            + "Đội ngũ hỗ trợ khách hàng");

    // Return the saved User as a DTO
    return userMapper.toDTO(e); // Convert the saved entity back to a DTO
  }

  @Override
  public User update(User user, boolean profile) {
    /** TODO Only allow admin user to update other users. */
    UserEntity oldUserEntity = null;

    if (!validateUser(user) || (!profile && user.getId() == null)) {
      throw new HrmCommonException(HrmConstant.ERROR.USER.INVALID);
    }

    // Check if the action is Admin update user or User update profile
    if (!profile) {
      // Check if the logged user is an admin
      if (!isAdmin()) {
        throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
      }
      // Find user in current database by id
      oldUserEntity = userRepository.findById(user.getId()).orElse(null);
    } else {
      // Get data of logged in user before updating
      String email = this.getAuthenticatedUserEmail();
      oldUserEntity = userRepository.findByEmail(email).orElse(null);
    }

    // Check if the user exists before updating
    if (oldUserEntity == null || oldUserEntity.getStatus() == UserStatusType.DELETED) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }

    // Validate user details and check for existing users with the same email or username different
    // from current user
    if ((userRepository.existsByEmail(user.getEmail())
            && !Objects.equals(oldUserEntity.getEmail(), user.getEmail()))
        || (userRepository.existsByUserName(user.getUserName())
            && !Objects.equals(oldUserEntity.getUserName(), user.getUserName()))) {
      throw new HrmCommonException(HrmConstant.ERROR.USER.EXIST);
    }

    // Update user details and save
    UserEntity finalOldUserEntity = oldUserEntity;
    return Optional.of(oldUserEntity)
        .map(
            ue -> {
              UserEntity.UserEntityBuilder builder =
                  ue.toBuilder()
                      .firstName(user.getFirstName())
                      .lastName(user.getLastName())
                      .phone(user.getPhone())
                      .userName(user.getUserName())
                      .email(user.getEmail());

              if (!profile) {
                // Only set new status if status is not null or not update user profile
                if (user.getStatus() != null && !profile) {
                  builder.status(user.getStatus());
                }

                // Only set new branch if branch is not null or not update user profile
                if (user.getBranch() != null && !profile) {
                  builder.branch(branchMapper.toEntity(user.getBranch()));
                }
              }
              return builder.build();
            })
        .map(
            e -> {
              if (!profile) {
                UserEntity userEntity = userMapper.toEntity(user);
                // Handle role assignment if roles exist
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                  if (!Objects.equals(
                      user.getRoles().get(0).getId(),
                      finalOldUserEntity.getUserRoleMap().get(0).getId())) {
                    List<UserRoleMapEntity> userRoleMapEntities =
                        userRoleMapRepository.findByUser(finalOldUserEntity);
                    userRoleMapEntities.get(0).setUser(userEntity);
                    userRoleMapEntities.get(0).setRole(roleMapper.toEntity(user.getRoles().get(0)));

                    // Save role mappings if necessary
                    if (!userRoleMapEntities.isEmpty()) {
                      userRoleMapRepository.saveAll(userRoleMapEntities); // Save role mappings
                    }
                  }
                } else {
                  userRoleMapService.setStaffRoleForUser(e.getId());
                }
              }
              return e;
            })
        .map(userRepository::save)
        .map(userMapper::toDTO)
        .orElse(null); // Return null if update fails
  }

  @Override
  public void delete(@NonNull Long id) {
    /** TODO Only allow admin user to delete other users */
    if (id == null) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }

    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    UserEntity user = userRepository.findById(id).orElse(null);
    // Validate that the user exists before attempting to delete
    if (user == null || user.getStatus() == UserStatusType.DELETED) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }

    Optional.ofNullable(user)
        .map(e -> e.setStatus(UserStatusType.DELETED))
        .map(userRepository::save); // Delete the user
  }

  @Deprecated
  @Override
  public User findLoggedInfoByEmail(String email) {
    // Return null if the email is not valid
    if (!StringUtils.hasText(email)) {
      return null;
    }

    // Find user by email
    UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
    if (userEntity == null || userEntity.getUserRoleMap() == null) {
      return null; // Return null if user not found or no roles assigned
    }

    // Retrieve roles associated with the user
    List<UserRoleMapEntity> userRoleMapEntities = userRoleMapRepository.findByUser(userEntity);
    log.info("User role's size: {}", userRoleMapEntities.size());

    // Set user roles and return the mapped DTO
    userEntity.setUserRoleMap(userRoleMapEntities);
    return userMapper.toDTO(userEntity);
  }

  @Override
  public User createAdmin(User user) {
    // Create and save a new admin user, then assign admin role
    Branch branch =
        branchService.getByLocationContains(
            "199 Đường Giải Phóng - P. Đồng Tâm - Q. Hai Bà Trưng - TP. Hà Nội");
    return Optional.ofNullable(user)
        .map(u -> u.setBranch(branch))
        .map(userMapper::toEntity)
        .map(userRepository::save)
        .map(
            e -> {
              userRoleMapService.setAdminRoleForUser(e.getId()); // Assign admin role
              return e;
            })
        .map(userMapper::toDTO)
        .orElse(null); // Return null if admin creation fails
  }

  @Override
  public User getByEmail(@NonNull String email) {
    // TODO check admin, if admin, can getByEmail of other user, while not, can only get current
    // user
    // Retrieve user by email and map to DTO
    return userRepository.findByEmail(email).map(userMapper::toDTO).orElse(null);
  }

  @Override
  public void deleteByIds(List<Long> ids) {
    /** TODO Only allow admin user to call this function */

    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    if (ids == null || ids.isEmpty()) {
      throw new HrmCommonException(USER.INVALID);
    }

    // Delete users by their IDs
    userRepository.deleteByIds(ids);
  }

  @Override
  public boolean isAdmin() {
    // Get the email of the authenticated user and check if they have admin role
    String userEmail = this.getAuthenticatedUserEmail();
    return userRoleMapRepository.existsByEmailAndRole(userEmail, RoleType.ADMIN);
  }

  @Override
  public boolean isManager() {
    // Get the email of the authenticated user and check if they have admin role
    String userEmail = this.getAuthenticatedUserEmail();
    return userRoleMapRepository.existsByEmailAndRole(userEmail, RoleType.MANAGER);
  }

  @Override
  public List<Role> findRolesByEmail(@NonNull String email) {
    // Retrieve roles associated with the given email
    return userRepository.findRolesByEmail(email).stream().map(r -> roleMapper.toDTO(r)).toList();
  }

  @Override
  public User register(RegisterRequest registerRequest) {
    // Check if a user with the provided email or username already exists
    if (userRepository.existsByUserName(registerRequest.getUserName())
        || userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new HrmCommonException(USER.EXIST); // Throw exception if user exists
    }

    // Check if confirm password equals to password
    if (!Objects.equals(registerRequest.getPassword(), registerRequest.getConfirmPassword())) {
      throw new HrmCommonException(USER.NOT_MATCH_CONFIRM_PASSWORD);
    }

    return Optional.of(registerRequest)
        .map(u -> userMapper.toEntity(u))
        .map(
            e -> {
              e.setStatus(UserStatusType.PENDING);
              return userRepository.save(e);
            })
        .map(
            e -> {
              userRoleMapService.setStaffRoleForUser(e.getId());
              return e;
            })
        .map(userMapper::toDTO)
        .orElse(null); // Return null if registration fails
  }

  @Override
  public User verifyUser(Long userId, boolean accept) {
    // Only allow admin to verify registration requests
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Find user by ID to verify their registration
    UserEntity verifyUser = userRepository.findById(userId).orElse(null);
    if (verifyUser == null) {
      throw new HrmCommonException(USER.NOT_EXIST); // Throw exception if user not found
    }

    // Set status based on acceptance and save
    return Optional.ofNullable(verifyUser)
        .map(
            accept
                ? e -> e.setStatus(UserStatusType.ACTIVATE)
                : e -> e.setStatus(UserStatusType.REJECTED))
        .map(userRepository::save)
        .map(userMapper::toDTO)
        .orElse(null); // Return null if verification fails
  }

  @Override
  public User activateUser(Long userId) {
    // Only allow admin to verify registration requests
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Find user by ID to verify their registration
    UserEntity verifyUser = userRepository.findById(userId).orElse(null);
    if (verifyUser == null) {
      throw new HrmCommonException(USER.NOT_EXIST); // Throw exception if user not found
    }

    // Set status based on account current status and save
    return Optional.ofNullable(verifyUser)
        .map(
            Objects.equals(verifyUser.getStatus(), UserStatusType.ACTIVATE)
                ? e -> e.setStatus(UserStatusType.DEACTIVATE)
                : e -> e.setStatus(UserStatusType.ACTIVATE))
        .map(userRepository::save)
        .map(userMapper::toDTO)
        .orElse(null); // Return null if verification fails
  }

  @Override
  public void resetPassword(User user, String newPassword) {
    // Hash the new password before saving it
    user.setPassword(newPassword);
    userRepository.save(userMapper.toEntity(user)); // Save the updated user
  }

  public List<String> importFile(MultipartFile file) {
    if (file == null) {
      throw new RuntimeException("Not a valid file");
    }

    // Mapper to convert each Excel row into a User object
    Function<Row, User> rowMapper =
        (Row row) -> {
          User user = new User();
          try {
            // Mapping fields from the Excel row to the User object
            user.setUserName(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null);
            user.setEmail(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null);
            user.setPhone(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null);
            user.setFirstName(row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null);
            user.setLastName(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null);

            // Map branch if available
            if (row.getCell(5) != null) {
              Branch branch =
                  branchService.getByLocationContains(row.getCell(5).getStringCellValue());
              if (branch != null) {
                user.setBranch(branch);
              }
            }

            // Map roles if available
            if (row.getCell(6) != null) {
              String roleTypeStr = row.getCell(6).getStringCellValue().toUpperCase();
              try {
                RoleType roleType = RoleType.valueOf(roleTypeStr);
                Role role = roleService.getRoleByType(roleType);
                if (role != null) {
                  user.setRoles(
                      Collections.singletonList(role)); // Set role as a single-element list
                }
              } catch (IllegalArgumentException e) {
                // Handle invalid RoleType value
                throw new IllegalArgumentException("Invalid role type: " + roleTypeStr);
              }
            }
          } catch (Exception e) {
            // Handle any unexpected parsing errors
            throw new RuntimeException("Error parsing row: " + e.getMessage(), e);
          }
          return user;
        };

    // List to store any validation errors
    List<String> errors = new ArrayList<>();
    List<User> usersToSave = new ArrayList<>();

    // Read and validate each row from the Excel file
    try {
      Workbook workbook = new XSSFWorkbook(file.getInputStream());
      Sheet sheet = workbook.getSheetAt(0);

      for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
          try {
            User user = rowMapper.apply(row); // Convert row to User object
            // List to hold errors for the current row
            List<String> rowErrors = new ArrayList<>();
            // Validate the User object
            if (user.getUserName() == null || user.getUserName().isEmpty()) {
              rowErrors.add("UserName is missing at row " + (rowIndex + 1));
            }
            if (user.getEmail() == null || !user.getEmail().contains("@")) {
              rowErrors.add("Invalid Email at row " + (rowIndex + 1));
            }
            if (userRepository.existsByEmail(user.getEmail())
                || userRepository.existsByUserName(user.getUserName())) {
              rowErrors.add("User with email or user name already exists at row " + (rowIndex + 1));
            }

            // If there are no errors, add to the usersToSave list
            if (rowErrors.isEmpty()) {
              usersToSave.add(user);
            } else {
              // Log the errors
              errors.addAll(rowErrors);
            }
          } catch (Exception e) {
            // Log parsing error for the row
            errors.add("Error parsing row " + (rowIndex + 1) + ": " + e.getMessage());
          }
        }
      }

      workbook.close();
    } catch (IOException e) {
      errors.add("Failed to parse Excel file: " + e.getMessage());
    }

    // Save all valid users to the database
    if (!usersToSave.isEmpty()) {
      for (User user : usersToSave) {
        create(user);
      }
    }

    return errors; // Return the list of errors
  }

  @Override
  public ByteArrayInputStream exportFile() throws IOException {
    // Define the headers for the User export
    String[] headers = {
      "Tên tài khoản", "Email", "Số điện thoại", "Họ", "Tên", "Chi nhánh làm việc", "Vai trò"
    };

    // Row mapper to convert a User object to a list of cell values
    Function<User, List<String>> rowMapper =
        (User user) -> {
          List<String> cellValues = new ArrayList<>();
          cellValues.add(user.getUserName() != null ? user.getUserName() : "");
          cellValues.add(user.getEmail() != null ? user.getEmail() : "");
          cellValues.add(user.getPhone() != null ? user.getPhone() : "");
          cellValues.add(user.getFirstName() != null ? user.getFirstName() : "");
          cellValues.add(user.getLastName() != null ? user.getLastName() : "");

          // Check for branch information
          if (user.getBranch() != null) {
            cellValues.add(user.getBranch().getLocation());
          } else {
            cellValues.add("");
          }

          // Map roles to a comma-separated string
          if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String roles =
                user.getRoles().stream()
                    .map(
                        role ->
                            role.getType()
                                .name()) // Converts RoleType to its name (ADMIN, STAFF, etc.)
                    .collect(Collectors.joining(", "));
            cellValues.add(roles);
          } else {
            cellValues.add("No roles assigned");
          }

          return cellValues;
        };

    // Fetch user data (this would typically come from your database/repository)
    List<User> users = getByPaging(0, Integer.MAX_VALUE, "id", "ASC", "", null).stream().toList();

    // Call the utility to export data
    try {
      return ExcelUtility.exportToExcelWithErrors(users, headers, rowMapper);
    } catch (IOException e) {
      throw new RuntimeException("Error exporting user data to Excel", e);
    }
  }

  @Override
  public void changePassword(User user, ChangePasswordRequest request) {

    // Check if entered old password equals to current user password
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new HrmCommonException(USER.WRONG_OLD_PASSWORD);
    }

    // Check if confirm password equals to entered new password
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new HrmCommonException(USER.NOT_MATCH_CONFIRM_PASSWORD);
    }

    // Hash the new password before saving it
    this.resetPassword(user, request.getNewPassword());
  }

  private boolean validateUser(User user) {
    if (user == null) {
      return false;
    }
    if (user.getUserName() == null
        || user.getUserName().isEmpty()
        || user.getUserName().length() < 5
        || user.getUserName().length() > 20) {
      return false;
    }
    if (user.getEmail() == null || !user.getEmail().matches(HrmConstant.REGEX.EMAIL)) {
      return false;
    }
    if (user.getBranch() == null || branchService.getById(user.getBranch().getId()) == null) {
      return false;
    }
    return user.getRoles() != null && !user.getRoles().isEmpty();
  }

  @Override
  public Optional<Long> findBranchIdByUserEmail(String loggedEmail) {
    return userRepository.findBranchIdByUserEmail(loggedEmail);
  }
}
