package com.example.hrm_be.controllers.branch;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BranchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/branch")
@Tag(name = "Admin-Branches API")
@SecurityRequirement(name = "Authorization")
public class AdminBranchController {
  // Injected service for handling branch operations
  private final BranchService branchService;

  // Retrieves a paginated list of Branch entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Branch>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "") BranchType branchType) {
    Page<Branch> branchPage = branchService.getByPaging(page, size, sortBy, keyword, branchType);

    // Build the response with pagination details
    BaseOutput<List<Branch>> response =
        BaseOutput.<List<Branch>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(branchPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(branchPage.getTotalElements())
            .data(branchPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a Branch by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Branch>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch branch by ID
    Branch branch = branchService.getById(id);

    // Build the response with the found branch data
    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(branch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new Branch
  @PostMapping()
  protected ResponseEntity<BaseOutput<Branch>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Branch branch) {
    // Validate the request body
    if (branch == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the branch
    Branch createdBranch = branchService.create(branch);

    // Build the response with the created branch data
    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(createdBranch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Branch
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Branch>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Branch branch) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the branch to update
    branch.setId(id);

    // Update the branch
    Branch updateBranch = branchService.update(branch);

    // Build the response with the updated branch data
    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(updateBranch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a Branch by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the branch by ID
    branchService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
