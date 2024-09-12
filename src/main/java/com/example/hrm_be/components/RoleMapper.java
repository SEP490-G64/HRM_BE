package com.example.hrm_be.components;


import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.entities.RoleEntity;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
  public Role toDTO(RoleEntity entity) {
    return Optional.ofNullable(entity)
        .map(e -> Role.builder().id(e.getId()).name(e.getName()).type(e.getType()).build())
        .orElse(null);
  }

  public RoleEntity toEntity(Role dto) {
    return Optional.ofNullable(dto)
        .map(e -> RoleEntity.builder().id(e.getId()).name(e.getName()).type(e.getType()).build())
        .orElse(null);
  }
}
