package com.example.hrm_be.controllers.user;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/user")
@Tag(name = "Admin-Users API")
@SecurityRequirement(name = "Authorization")
public class AdminUserController {

  private final UserService userService;

  @GetMapping("")
  public ResponseEntity<BaseOutput<List<User>>> getAllByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<User> userPage = userService.getByPaging(page, size, sortBy, sortDirection, keyword);
    BaseOutput<List<User>> response =
        BaseOutput.<List<User>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(userPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(userPage.getTotalElements())
            .data(userPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseOutput<User>> getById(
      @PathVariable("id") @NotBlank(message = "error.request.path.variable.id.invalid") Long id) {
    if (id == null) {
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED.FAILED)
              .errors(List.of(REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    User user = userService.getById(id);

    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString())
            .data(user)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<BaseOutput<User>> getByEmail(
      @PathVariable("email") @NotBlank(message = REQUEST.INVALID_PATH_VARIABLE) String email) {
    if (StringUtils.isAllBlank(email)) {
      BaseOutput<User> response =
          BaseOutput.<User>builder().errors(List.of(REQUEST.INVALID_PATH_VARIABLE)).build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    User user = userService.getByEmail(email);

    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(user).build();
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<BaseOutput<User>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") User user) {

    if (user == null) {
      BaseOutput<User> response =
          BaseOutput.<User>builder().errors(List.of(REQUEST.INVALID_BODY)).build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    User createdUser = userService.create(user);
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(createdUser).build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BaseOutput<User>> update(
      @PathVariable("id") @NotBlank(message = "error.request.path.variable.id.invalid") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") User user) {
    if (id == null) {
      BaseOutput<User> response =
          BaseOutput.<User>builder().errors(List.of(REQUEST.INVALID_PATH_VARIABLE)).build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    user.setId(id);
    User createdBid = userService.update(user);
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(createdBid).build();
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseOutput<String>> delete(
      @PathVariable("id") @NotBlank(message = "error.request.path.variable.id.invalid") Long id) {
    if (id <= 0) {
      BaseOutput<String> response =
          BaseOutput.<String>builder().errors(List.of(REQUEST.INVALID_PATH_VARIABLE)).build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    userService.delete(id);
    return ResponseEntity.ok(BaseOutput.<String>builder().data(HttpStatus.OK.toString()).build());
  }

  @DeleteMapping()
  public ResponseEntity<BaseOutput<String>> deleteByIds(
      @RequestBody @NotBlank(message = "error.id.invalid") List<Long> ids) {
    if (ids == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .message("error.id.invalid")
              .errors(List.of(REQUEST.INVALID_BODY))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    userService.deleteByIds(ids);
    return ResponseEntity.ok(
        BaseOutput.<String>builder().message(HttpStatus.OK.toString()).build());
  }
}
