package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.models.dtos.Role;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.requests.ChangePasswordRequest;
import com.example.hrm_be.models.requests.RegisterRequest;
import io.micrometer.common.lang.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  String getAuthenticatedUserEmail();

  Page<User> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String keyword,
      @Nullable UserStatusType status);

  Page<User> getRegistrationRequests();

  User getById(Long id);

  User create(User user);

  User update(User user, boolean profile);

  void delete(@NonNull Long id);

  User findLoggedInfoByEmail(String email);

  User createAdmin(User user);

  User getByEmail(@NonNull String email);

  void deleteByIds(List<Long> ids);

  boolean isAdmin();

  boolean isManager();

  List<Role> findRolesByEmail(@NonNull String email);

  User register(RegisterRequest registerRequest);

  User verifyUser(Long userId, boolean accept);

  User activateUser(Long userId);

  List<String> importFile(MultipartFile file);

  ByteArrayInputStream exportFile() throws IOException;

  void resetPassword(User user, String newPassword);

  void changePassword(User user, ChangePasswordRequest request);

  Optional<Long> findBranchIdByUserEmail(String loggedEmail);
}
