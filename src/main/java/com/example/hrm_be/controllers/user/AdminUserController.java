package com.example.hrm_be.controllers.user;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.ExcelUtility;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/user")
@Tag(name = "Admin-Users API")
@SecurityRequirement(name = "Authorization")
public class AdminUserController {

  private final UserService userService;

  // GET: /api/v1/admin/user
  // Retrieve a list of users with paging, sorting, and searching by name
  @GetMapping("")
  public ResponseEntity<BaseOutput<List<User>>> getAllByPaging(
      @RequestParam(defaultValue = "0") int page, // Page number for pagination
      @RequestParam(defaultValue = "10") int size, // Size of each page
      @RequestParam(required = false, defaultValue = "id") String sortBy, // Sort by specified field
      @RequestParam(required = false, defaultValue = "ASC")
          String sortDirection, // Sort direction (ASC or DESC)
      @RequestParam(required = false, defaultValue = "")
          String keyword) { // Search keyword for filtering
    // Call the user service to get paginated user data
    Page<User> userPage = userService.getByPaging(page, size, sortBy, sortDirection, keyword);

    // Create a response object containing the user data and metadata
    BaseOutput<List<User>> response =
        BaseOutput.<List<User>>builder()
            .message(HttpStatus.OK.toString()) // Set response message to OK
            .totalPages(userPage.getTotalPages()) // Total number of pages
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the page
            .total(userPage.getTotalElements()) // Total number of users
            .data(userPage.getContent()) // List of users in the current page
            .status(
                com.example.hrm_be.commons.enums.ResponseStatus
                    .SUCCESS) // Set response status to SUCCESS
            .build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/admin/user
  // Retrieve a list of registration requests
  @GetMapping("/registration-requests")
  public ResponseEntity<BaseOutput<List<User>>> getRegistrationRequests(
      @RequestParam(defaultValue = "0") int page) {
    // Call the user service to get paginated registration request data
    Page<User> userPage = userService.getRegistrationRequests();

    // Create a response object containing the registration request data and metadata
    BaseOutput<List<User>> response =
        BaseOutput.<List<User>>builder()
            .message(HttpStatus.OK.toString()) // Set response message to OK
            .totalPages(userPage.getTotalPages()) // Total number of pages
            .currentPage(page) // Current page number
            .total(userPage.getTotalElements()) // Total number of registration requests
            .data(userPage.getContent()) // List of registration requests in the current page
            .status(
                com.example.hrm_be.commons.enums.ResponseStatus
                    .SUCCESS) // Set response status to SUCCESS
            .build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/admin/user/{id}
  // Retrieve a user by their ID
  @GetMapping("/{id}")
  public ResponseEntity<BaseOutput<User>> getById(
      @PathVariable("id") @NotNull(message = "error.request.path.variable.id.invalid")
          Long id) { // Validate ID input
    if (id == null) { // Check if ID is null
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .status(
                  com.example.hrm_be.commons.enums.ResponseStatus.FAILED
                      .FAILED) // Set response status to FAILED
              .errors(List.of(REQUEST.INVALID_PATH_VARIABLE)) // Add error message for invalid ID
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Call user service to get user by ID
    User user = userService.getById(id);

    // Create response object containing the retrieved user
    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString()) // Set response message to OK
            .data(user) // Attach the user data
            .status(
                com.example.hrm_be.commons.enums.ResponseStatus
                    .SUCCESS) // Set response status to SUCCESS
            .build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/admin/user/email/{email}
  // Admin retrieves a user by their email
  @GetMapping("/email/{email}")
  public ResponseEntity<BaseOutput<User>> getByEmail(
      @PathVariable("email") @NotNull(message = REQUEST.INVALID_PATH_VARIABLE)
          String email) { // Validate email input
    if (StringUtils.isAllBlank(email)) { // Check if email is blank
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .errors(List.of(REQUEST.INVALID_PATH_VARIABLE))
              .build(); // Create error response
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Call user service to get user by email
    User user = userService.getByEmail(email);

    // Create response object containing the retrieved user
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(user).build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/admin/user
  // Admin creates a new user
  @PostMapping
  public ResponseEntity<BaseOutput<User>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          User user) { // Validate user object
    if (user == null) { // Check if user is null
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .errors(List.of(REQUEST.INVALID_BODY))
              .build(); // Create error response
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Call user service to create a new user
    User createdUser = userService.create(user);

    // Create response object containing the created user
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(createdUser).build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/admin/user/{id}
  // Admin updates a user by ID
  @PutMapping("/{id}")
  public ResponseEntity<BaseOutput<User>> update(
      @PathVariable("id") @NotNull(message = "error.request.path.variable.id.invalid") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          User user) { // Validate user object
    if (id == null) { // Check if ID is null
      BaseOutput<User> response =
          BaseOutput.<User>builder()
              .errors(List.of(REQUEST.INVALID_PATH_VARIABLE))
              .build(); // Create error response
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Set the user ID for the update operation
    user.setId(id);

    // Call user service to update the user
    User createdBid = userService.update(user, false);

    // Create response object containing the updated user
    BaseOutput<User> response =
        BaseOutput.<User>builder().message(HttpStatus.OK.toString()).data(createdBid).build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/admin/user/{id}
  // Admin deletes a user by ID
  @DeleteMapping("/{id}")
  public ResponseEntity<BaseOutput<String>> delete(
      @PathVariable("id") @NotNull(message = "error.request.path.variable.id.invalid")
          Long id) { // Validate ID input
    if (id <= 0) { // Check if ID is less than or equal to zero
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .errors(List.of(REQUEST.INVALID_PATH_VARIABLE))
              .build(); // Create error response
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Call user service to delete the user by ID
    userService.delete(id);

    // Create response object confirming deletion
    return ResponseEntity.ok(BaseOutput.<String>builder().data(HttpStatus.OK.toString()).build());
  }

  // DELETE: /api/v1/admin/user
  // Admin deletes multiple users by IDs
  @DeleteMapping()
  public ResponseEntity<BaseOutput<String>> deleteByIds(
      @RequestBody @NotNull(message = "error.id.invalid") List<Long> ids) { // Validate list of IDs
    if (ids == null) { // Check if IDs list is null
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .message("error.id.invalid") // Set error message
              .errors(List.of(REQUEST.INVALID_BODY)) // Add error for invalid body
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST status
    }

    // Call user service to delete multiple users by IDs
    userService.deleteByIds(ids);

    // Create response object confirming deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder().message(HttpStatus.OK.toString()).build());
  }

  // POST: /api/v1/admin/user/activate/{id}
  // Admin approves a user registration request
  @PostMapping("/activate/{id}")
  public ResponseEntity<BaseOutput<User>> activateUser(@PathVariable("id") Long id) {

    // Check if the provided user ID is null
    if (id == null) {
      // Throw an exception for invalid path variable
      throw new HrmCommonException(REQUEST.INVALID_PATH_VARIABLE);
    }

    // Call the user service to activate the user based on the provided ID and user current status
    User activateUser = userService.activateUser(id);

    // Create a response object containing the verified user
    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString()) // Set the response message to OK
            .data(activateUser) // Attach the activate user data
            .build();

    // Return a response entity with status OK and the activate/deactivate user data
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/auth/verify-user/{id}
  // Admin approve user registration request
  @PostMapping("/verify-user/{id}")
  public ResponseEntity<BaseOutput<User>> verifyUser(
      @PathVariable("id") Long id, @RequestParam boolean accept) {

    // Check if the provided user ID is null
    if (id == null) {
      // Throw an exception for invalid path variable
      throw new HrmCommonException(REQUEST.INVALID_PATH_VARIABLE);
    }

    // Call the user service to verify the user based on the provided ID and acceptance status
    User verifiedUser = userService.verifyUser(id, accept);

    // Create a response object containing the verified user
    BaseOutput<User> response =
        BaseOutput.<User>builder()
            .message(HttpStatus.OK.toString()) // Set the response message to OK
            .data(verifiedUser) // Attach the verified user data
            .build();

    // Return a response entity with status OK and the verified user data
    return ResponseEntity.ok(response);
  }

  // Method to upload an Excel file for importing users
  @PostMapping("/excel/import")
  public ResponseEntity<BaseOutput<String>> uploadFile(@RequestParam("file") MultipartFile file) {
    // Check if the uploaded file is in Excel format
    if (ExcelUtility.hasExcelFormat(file)) {
      try {
        // Call the service to handle the import logic and get any validation errors
        List<String> errors = userService.importFile(file);

        // Check if there were any errors during file processing
        if (errors != null && !errors.isEmpty()) {
          // Return a bad request response with validation errors
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(
                  BaseOutput.<String>builder()
                      .message(
                          "File upload failed with validation errors") // Error message indicating
                      // validation issues
                      .errors(errors) // List of validation errors
                      .build());
        }
        // Return a success response if there are no errors
        return ResponseEntity.ok(
            BaseOutput.<String>builder()
                .message(
                    "The Excel file is uploaded successfully: "
                        + file.getOriginalFilename()) // Success message with the original file name
                .build());
      } catch (Exception exp) {
        // Handle any exceptions that occur during import
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                BaseOutput.<String>builder()
                    .message("File upload failed: " + exp.getMessage()) // Specific error message
                    .build());
      }
    }

    // Return an error response if the file is not a valid Excel format
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            BaseOutput.<String>builder()
                .message("Invalid file format") // Error message for invalid file format
                .errors(List.of("Please upload a valid Excel file")) // User guidance
                .build());
  }

  // Method to download an Excel file containing user data
  @GetMapping("/excel/export")
  public ResponseEntity<InputStreamResource> download() throws IOException {
    // Call the service to export the file and get the input stream
    ByteArrayInputStream inputStream = userService.exportFile();

    InputStreamResource resource = new InputStreamResource(inputStream);

    // Return a response with the file attached
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=user.xlsx") // Set the filename for download
        .contentType(MediaType.MULTIPART_FORM_DATA) // Content type of the response
        .body(resource); // Return the input stream resource
  }
}
