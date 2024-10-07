package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.requests.RegisterRequest;
import com.example.hrm_be.models.requests.user.UserCreateRequest;
import com.example.hrm_be.models.requests.user.UserUpdateRequest;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Page;

public interface UserService {

  String getAuthenticatedUserEmail();

  Page<User> getByPaging(
      int pageNo, int pageSize, String sortBy, String sortDirection, String keyword);

  Page<User> getRegistrationRequests();

  User getById(Long id);

  User create(UserCreateRequest user);

  User update(UserUpdateRequest user, boolean profile);

  void delete(@NonNull Long id);

  User findLoggedInfoByEmail(String email);

  User createAdmin(User user);

  User getByEmail(@NonNull String email);

  void deleteByIds(List<Long> ids);

  boolean isAdmin();

  List<Role> findRolesByEmail(@NonNull String email);

  User register(RegisterRequest registerRequest);

  User verifyUser(Long userId, boolean accept);

  User activateUser(Long userId);
}
