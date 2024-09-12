package com.example.hrm_be.services.impl;


import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.USER;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.components.RoleMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.repositories.UserRoleMapRepository;
import com.example.hrm_be.services.UserRoleMapService;
import com.example.hrm_be.services.UserService;
import java.util.List;
import java.util.Optional;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

  @Lazy @Autowired
  UserRepository userRepository;
  @Lazy @Autowired
  UserMapper userMapper;

  @Lazy @Autowired
  RoleMapper roleMapper;

  @Lazy @Autowired
  UserRoleMapService userRoleMapService;
  @Lazy @Autowired
  UserRoleMapRepository userRoleMapRepository;


  @Override
  public String getAuthenticatedUserEmail() throws UsernameNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      if (userDetails != null) {
        String loggedEmail = userDetails.getUsername();
        log.info("LOGGED user: {}", loggedEmail);
        return loggedEmail;
      } else {
        throw new UsernameNotFoundException(HrmConstant.ERROR.AUTH.NOT_FOUND);
      }
    }
    throw new UsernameNotFoundException(HrmConstant.ERROR.AUTH.NOT_FOUND);
  }

  @Override
  public Page<User> getByPaging(
      int pageNo, int pageSize, String sortBy, String sortDirection, String keyword) {
    /** TODO Only allow admin user to call this func */
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }
    Pageable pageable =
        PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    return userRepository.findByKeyword(keyword, pageable).map(userMapper::toDTO);
  }

  @Override
  public User getById(Long id) {
    /** TODO Only allow admin user to call this func */
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }
    return Optional.ofNullable(id)
        .flatMap(e -> userRepository.findById(id))
        .map(userMapper::toDTO)
        .orElse(null);
  }

  @Override
  public User create(User user) {
    /** TODO Only allow admin user to call this func */
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    if (user == null
        || userRepository.existsByEmail(user.getEmail())
        || userRepository.existsByUserName(user.getUserName())) {
      throw new HrmCommonException(USER.EXIST);
    }
    return Optional.of(user)
        .map(userMapper::toEntity)
        .map(userRepository::save)
        .map(
            e -> {
              userRoleMapService.setUserRoleForUser(e.getId());
              return e;
            })
        .map(userMapper::toDTO)
        .orElse(null);
  }

  @Override
  public User update(User user) {
    /** TODO Only allow admin user to update other user Normal user can update their account only */
    User toUpdateUser = user.toBuilder().build();
    if (!isAdmin()) {
      String loggedEmail = this.getAuthenticatedUserEmail();
      toUpdateUser = this.getByEmail(loggedEmail);
    }

    if (!userRepository.existsById(toUpdateUser.getId())) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }
    return Optional.of(toUpdateUser)
        .flatMap(u -> userRepository.findById(u.getId()))
        .map(
            ue ->
                ue.toBuilder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phone(user.getPhone())
                    .build())
        .map(userRepository::save)
        .map(userMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(@NonNull Long id) {
    /** TODO Only allow admin user to delete other user */
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    if (!userRepository.existsById(id)) {
      throw new HrmCommonException(USER.NOT_EXIST);
    }
    // TODO should delete license as well
    userRepository.deleteById(id);
  }

  @Deprecated
  @Override
  public User findLoggedInfoByEmail(String email) {
    if (!StringUtils.hasText(email)) {
      return null;
    }

    UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
    if (userEntity == null || userEntity.getUserRoleMap() == null) {
      return null;
    }

    List<UserRoleMapEntity> userRoleMapEntities = userRoleMapRepository.findByUser(userEntity);
    log.info("User role's size: {}", userRoleMapEntities.size());

    userEntity.setUserRoleMap(userRoleMapEntities);
    return userMapper.toDTO(userEntity);
  }

  @Override
  public User createAdmin(User user) {
    return Optional.ofNullable(user)
        .map(userMapper::toEntity)
        .map(userRepository::save)
        .map(
            e -> {
              userRoleMapService.setAdminRoleForUser(e.getId());
              return e;
            })
        .map(userMapper::toDTO)
        .orElse(null);
  }

  @Override
  public User getByEmail(@NonNull String email) {
    // TODO check admin, if is admin, can getByEmail of other user, while not, can only get current
    return userRepository.findByEmail(email).map(userMapper::toDTO).orElse(null);
  }

  @Override
  public void deleteByIds(List<Long> ids) {
    /** TODO Only allow admin user to call this func */
    if (!isAdmin()) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    userRepository.deleteByIds(ids);
  }

  @Override
  public boolean isAdmin() {
    String userEmail = this.getAuthenticatedUserEmail();
    return userRoleMapRepository.existsByEmailAndRole(userEmail, RoleType.ADMIN);
  }

  @Override
  public List<Role> findRolesByEmail(@NonNull String email) {
    return userRepository.findRolesByEmail(email).stream().map(r -> roleMapper.toDTO(r)).toList();
  }
}
