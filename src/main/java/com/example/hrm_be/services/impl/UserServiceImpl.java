package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.USER;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.RoleMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import com.example.hrm_be.models.requests.ChangePasswordRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.repositories.RoleRepository;
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.repositories.UserRoleMapRepository;
import com.example.hrm_be.services.EmailService;
import com.example.hrm_be.services.UserRoleMapService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.PasswordGenerator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

  @Lazy @Autowired UserRepository userRepository;
  @Lazy @Autowired UserMapper userMapper;

  @Lazy @Autowired RoleRepository roleRepository;
  @Lazy @Autowired RoleMapper roleMapper;

  @Lazy @Autowired BranchMapper branchMapper;

  @Lazy @Autowired UserRoleMapService userRoleMapService;
  @Lazy @Autowired UserRoleMapRepository userRoleMapRepository;

  @Lazy @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private EmailService emailService;

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
      int pageNo, int pageSize, String sortBy, String sortDirection, String keyword) {
    /** TODO Only allow admin user to call this function */
    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Create a pageable request based on provided parameters
    Pageable pageable =
        PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

    // Fetch users by keyword and map to DTO
    return userRepository
        .searchUsers(keyword, UserStatusType.PENDING, pageable)
        .map(userMapper::toDTO);
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
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Retrieve user by ID and map to DTO
    return Optional.ofNullable(id)
        .flatMap(e -> userRepository.findById(id))
        .map(userMapper::toDTO)
        .orElse(null); // Return null if user not found
  }

  @Override
  public User create(User user) {
    // Only allow admin user to call this function
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Validate user details and check for existing users with the same email or username
    if (user == null ||
            userRepository.existsByEmail(user.getEmail()) ||
            userRepository.existsByUserName(user.getUserName())) {
      throw new HrmCommonException(USER.EXIST);
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
      List<UserRoleMapEntity> userRoleMapEntities = user.getRoles().stream()
              .map(role -> {
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
    }
    else {
      userRoleMapService.setStaffRoleForUser(e.getId());
    }

    // Send email notification with the raw password
    emailService.sendEmail(
            user.getEmail(),
            "Tài khoản ứng dụng Quản lý kho Hệ thống nhà thuốc của bạn",
            "Chào bạn,\n\n" +
                    "Chúng tôi xin thông báo rằng tài khoản của bạn đã được tạo thành công trên Ứng dụng Quản lý kho Hệ thống nhà thuốc.\n\n" +
                    "Dưới đây là thông tin tài khoản của bạn:\n" +
                    "Tài khoản: " + user.getEmail() + "\n" +
                    "Mật khẩu: " + rawPassword + "\n\n" +
                    "Chúng tôi khuyên bạn nên thay đổi mật khẩu sau khi đăng nhập lần đầu tiên để bảo vệ tài khoản của mình.\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ hỗ trợ khách hàng"
    );

    // Return the saved User as a DTO
    return userMapper.toDTO(e); // Convert the saved entity back to a DTO
  }

  @Override
  public User update(User user, boolean profile) {
    /** TODO Only allow admin user to update other users. */
    UserEntity oldUserEntity = null;

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
      oldUserEntity = userMapper.toEntity(this.findLoggedInfoByEmail(email));
    }

    // Check if the user exists before updating
    if (oldUserEntity == null) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }

    // Validate user details and check for existing users with the same email or username different
    // from current user
    if (user == null
        || (userRepository.existsByEmail(user.getEmail())
            && !Objects.equals(oldUserEntity.getEmail(), user.getEmail()))
        || (userRepository.existsByUserName(user.getUserName())
            && !Objects.equals(oldUserEntity.getUserName(), user.getUserName()))) {
      throw new HrmCommonException(USER.EXIST);
    }

    // Update user details and save
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

              // Only set new status if status is not null or not update user profile
              if (user.getStatus() != null && !profile) {
                builder.status(UserStatusType.valueOf(user.getStatus()));
              }

              // Only set new branch if branch is not null or not update user profile
              if (user.getBranch() != null && !profile) {
                builder.branch(branchMapper.toEntity(user.getBranch()));
              }
              return builder.build();
              
            })
        .map(
            e -> {
              if (!profile) {
                UserEntity userEntity = userMapper.toEntity(user);
                // Handle role assignment if roles exist
                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                  List<UserRoleMapEntity> userRoleMapEntities = user.getRoles().stream()
                          .map(role -> {
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

    // Check if the logged user is an admin
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Validate that the user exists before attempting to delete
    if (!userRepository.existsById(id)) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }

    // TODO should delete license as well
    userRepository.deleteById(id); // Delete the user
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
    return Optional.ofNullable(user)
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
            Objects.equals(verifyUser.getStatus().toString(), UserStatusType.ACTIVATE.toString())
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
}
