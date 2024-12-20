package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.dtos.UserRoleMap;
import java.util.List;

public interface UserRoleMapService {
  List<UserRoleMap> findByUser(User user);

  void setManagerRoleForUser(Long userId);

  void setStaffRoleForUser(Long userId);

  void setAdminRoleForUser(Long userId);
}
