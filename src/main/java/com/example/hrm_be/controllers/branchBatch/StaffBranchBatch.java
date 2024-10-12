package com.example.hrm_be.controllers.branchBatch;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BranchBatchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/branch-batch")
@Tag(name = "Staff-Branch-Batch API")
@SecurityRequirement(name = "Authorization")
public class StaffBranchBatch {
  @Autowired BranchBatchService branchBatchService;

  @PostMapping()
  protected ResponseEntity<BaseOutput<BranchBatch>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") BranchBatch branchBatch) {
    // Validate the request body
    if (branchBatch == null) {
      BaseOutput<BranchBatch> response =
          BaseOutput.<BranchBatch>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the branch batch
    BranchBatch createdBranchBatch = branchBatchService.create(branchBatch);

    // Build the response with the created branch data
    BaseOutput<BranchBatch> response =
        BaseOutput.<BranchBatch>builder()
            .message(HttpStatus.OK.toString())
            .data(createdBranchBatch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Branch
  @PutMapping("/")
  protected ResponseEntity<BaseOutput<BranchBatch>> update(
      @RequestBody @NotNull(message = "error.request.body.invalid") BranchBatch branchBatch) {
    // Validate the input
    if (branchBatch == null) {
      BaseOutput<BranchBatch> response =
          BaseOutput.<BranchBatch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the branch
    BranchBatch updateBranchBatch = branchBatchService.update(branchBatch);

    // Build the response with the updated branch data
    BaseOutput<BranchBatch> response =
        BaseOutput.<BranchBatch>builder()
            .message(HttpStatus.OK.toString())
            .data(updateBranchBatch)
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
    branchBatchService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
