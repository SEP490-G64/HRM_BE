package com.example.hrm_be.controllers.branch;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BranchService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/branch")
public class AdminBranchController {
  private final BranchService branchService;

  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Branch>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy) {
    Page<Branch> branchPage = branchService.getByPaging(page, size, sortBy);

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

  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Branch>> getById(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Branch branch = branchService.getById(id);

    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(branch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping()
  protected ResponseEntity<BaseOutput<Branch>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Branch branch) {
    if (branch == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Branch createdBranch = branchService.create(branch);
    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(createdBranch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Branch>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Branch branch) {
    if (id <= 0 || id == null) {
      BaseOutput<Branch> response =
          BaseOutput.<Branch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    branch.setId(id);
    Branch updateBranch = branchService.update(branch);
    BaseOutput<Branch> response =
        BaseOutput.<Branch>builder()
            .message(HttpStatus.OK.toString())
            .data(updateBranch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    branchService.delete(id);
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
