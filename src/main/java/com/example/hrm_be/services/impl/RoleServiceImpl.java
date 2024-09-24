package com.example.hrm_be.services.impl;


import com.example.hrm_be.commons.constants.HrmConstant.ERROR.ROLE;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.components.RoleMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.entities.RoleEntity;
import com.example.hrm_be.repositories.RoleRepository;
import com.example.hrm_be.services.RoleService;
import io.micrometer.common.util.StringUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

  @Autowired private RoleRepository roleRepository;

  @Autowired private RoleMapper roleMapper;

  @Override
  public Page<Role> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    return roleRepository.findAll(pageable).map(dao -> roleMapper.toDTO(dao));
  }

  @Override
  public Role getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> roleRepository.findById(e).map(b -> roleMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Role create(Role role) {
    if (role == null || roleRepository.existsByType(role.getType())) {
      throw new HrmCommonException(ROLE.EXIST);
    }
    return Optional.ofNullable(role)
        .map(e -> roleMapper.toEntity(e))
        .map(e -> roleRepository.save(e))
        .map(e -> roleMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Role update(Role role) {
    RoleEntity oldRoleEntity = roleRepository.findById(role.getId()).orElse(null);
    if (oldRoleEntity == null) {
      throw new HrmCommonException(ROLE.NOT_EXIST);
    }
    return Optional.ofNullable(oldRoleEntity)
        .map(op -> op.toBuilder().name(role.getName()).build())
        .map(roleRepository::save)
        .map(roleMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }
    roleRepository.deleteById(id);
  }

  @Override
  public Role createManagerRole() {
    return Optional.of(
            RoleEntity.builder().type(RoleType.MANAGER).name(RoleType.MANAGER.getValue()).build())
        .map(r -> roleRepository.save(r))
        .map(re -> roleMapper.toDTO(re))
        .orElse(null);
  }

  @Override
  public Role getStaffRole() {
    return roleRepository.findByType(RoleType.STAFF).map(r -> roleMapper.toDTO(r)).orElse(null);
  }

  @Override
  public Role getManagerRole() {
    return roleRepository.findByType(RoleType.MANAGER).map(r -> roleMapper.toDTO(r)).orElse(null);
  }

  @Override
  public Role createStaffRole() {
    return Optional.of(
            RoleEntity.builder().type(RoleType.STAFF).name(RoleType.STAFF.getValue()).build())
        .map(r -> roleRepository.save(r))
        .map(re -> roleMapper.toDTO(re))
        .orElse(null);
  }

  @Override
  public Role getAdminRole() {
    return roleRepository.findByType(RoleType.ADMIN).map(r -> roleMapper.toDTO(r)).orElse(null);
  }

  @Override
  public Role createAdminRole() {
    return Optional.of(
            RoleEntity.builder().type(RoleType.ADMIN).name(RoleType.ADMIN.getValue()).build())
        .map(r -> roleRepository.save(r))
        .map(re -> roleMapper.toDTO(re))
        .orElse(null);
  }
}
