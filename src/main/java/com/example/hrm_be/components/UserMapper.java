package com.example.hrm_be.components;


import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
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

  public User toDTO(UserEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }


  public UserEntity toEntity(User dto) {
    return Optional.ofNullable(dto)
        .map(
            e ->
                UserEntity.builder()
                    .id(e.getId())
                    .userName(e.getUserName())
                    .email(e.getEmail())
                    .password(
                        e.getPassword() != null ? passwordEncoder.encode(e.getPassword()) : null)
                    .phone(e.getPhone())
                    .firstName(e.getFirstName())
                    .lastName(e.getLastName())
                    .build())
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
                    .build())
        .orElse(null);
  }
}
