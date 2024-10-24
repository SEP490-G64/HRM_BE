package com.example.hrm_be.components;

import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.RoleService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  @Autowired @Lazy private RoleService roleService;
  @Autowired @Lazy private PasswordEncoder passwordEncoder;
  @Autowired @Lazy private UserRoleMapMapper userRoleMapMapper;
  @Autowired @Lazy private RoleMapper roleMapper;
  @Autowired @Lazy private BranchMapper branchMapper;
  @Autowired @Lazy private BranchRepository branchRepository;

  public User toDTO(UserEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  public UserEntity toEntity(User dto) {
    return Optional.ofNullable(dto)
        .map(
            e -> {
              return UserEntity.builder()
                  .id(e.getId())
                  .userName(e.getUserName())
                  .email(e.getEmail())
                  .password(
                      e.getPassword() != null ? passwordEncoder.encode(e.getPassword()) : null)
                  .phone(e.getPhone())
                  .firstName(e.getFirstName())
                  .lastName(e.getLastName())
                  .branch(e.getBranch() != null ? branchMapper.toEntity(e.getBranch()) : null)
                  .status(
                      e.getStatus() != null ? UserStatusType.fromDisplayName(e.getStatus()) : null)
                  .build();
            })
        .orElse(null);
  }

  private User convertToDto(UserEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                User.builder()
                    .id(e.getId())
                    .userName(e.getUserName())
                    .email(e.getEmail())
                    .password(e.getPassword())
                    .phone(e.getPhone())
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .roles(
                        e.getUserRoleMap() != null
                            ? e.getUserRoleMap().stream()
                                .map(UserRoleMapEntity::getRole)
                                .map(urm -> roleMapper.toDTO(urm))
                                .collect(Collectors.toList())
                            : null)
                    .branch(
                        e.getBranch() != null
                            ? branchMapper.convertToDTOBasicInfo(
                                e.getBranch()) // If branch is a single entity
                            : null)
                    .status(String.valueOf(e.getStatus()))
                    .build())
        .orElse(null);
  }

  public User convertToDtoBasicInfo(UserEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                User.builder()
                    .id(e.getId())
                    .userName(e.getUserName())
                    .email(e.getEmail())
                    .password(e.getPassword())
                    .phone(e.getPhone())
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .status(String.valueOf(e.getStatus()))
                    .build())
        .orElse(null);
  }

  // Convert RegisterRequest to UserEntity
  public UserEntity toEntity(RegisterRequest dto) {
    return Optional.ofNullable(dto)
        .map(
            request -> {
              // Create UserEntity from RegisterRequest
              return UserEntity.builder()
                  .userName(request.getUserName())
                  .email(request.getEmail())
                  .phone(request.getPhone())
                  .firstName(request.getFirstName())
                  .lastName(request.getLastName())
                  .password(passwordEncoder.encode(request.getPassword()))
                  .build();
            })
        .orElse(null);
  }
}
