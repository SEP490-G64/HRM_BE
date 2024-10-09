package com.example.hrm_be.controllers.profile;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/profile")
@Tag(name = "Staff-Profile API")
@SecurityRequirement(name = "Authorization")
public class StaffProfileController {
  private final UserService userService;

  // GET: /api/v1/staff/profile
  // Retrieve user profile data
  @GetMapping
  protected ResponseEntity<BaseOutput<User>> getProfile() {
    // Get email of logged in user
    String email = userService.getAuthenticatedUserEmail();
    if (email == null) {
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Get detailed data of logged in user
    User user = userService.findLoggedInfoByEmail(email);

    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString())
            .data(user)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/profile
  // Update user profile data
  @PutMapping
  protected ResponseEntity<BaseOutput<User>> updateProfile(
      @RequestBody @NotNull(message = "error.request.body.invalid") User user) {
    // Update profile
    User updateUser = userService.update(user, true);

    // Build the response with the updated profile data
    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString())
            .data(updateUser)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }
}
