package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Role;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
  Role getById(Long id);

  Page<Role> getByPaging(int pageNo, int pageSize, String sortBy);

  Role create(Role role);

  Role update(Role role);

  void delete(Long id);

  Role createManagerRole();

  Role getManagerRole();

  Role createStaffRole();

  Role getStaffRole();

  Role createAdminRole();

  Role getAdminRole();
}
