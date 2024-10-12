package com.example.hrm_be.controllers.branchProduct;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BranchProductService;
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
@RequestMapping("/api/v1/staff/branch-product")
@Tag(name = "Staff-Branch-Product API")
@SecurityRequirement(name = "Authorization")
public class StaffBranchProduct {
  @Autowired BranchProductService branchProductService;

  @PostMapping()
  protected ResponseEntity<BaseOutput<BranchProduct>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") BranchProduct branchProduct) {
    // Validate the request body
    if (branchProduct == null) {
      BaseOutput<BranchProduct> response =
          BaseOutput.<BranchProduct>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the branch batch
    BranchProduct createdBranchProduct = branchProductService.create(branchProduct);

    // Build the response with the created branch data
    BaseOutput<BranchProduct> response =
        BaseOutput.<BranchProduct>builder()
            .message(HttpStatus.OK.toString())
            .data(createdBranchProduct)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Branch
  @PutMapping("/")
  protected ResponseEntity<BaseOutput<BranchProduct>> update(
      @RequestBody @NotNull(message = "error.request.body.invalid") BranchProduct branchProduct) {
    // Validate the input
    if (branchProduct == null) {
      BaseOutput<BranchProduct> response =
          BaseOutput.<BranchProduct>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the branch
    BranchProduct updateBranchProduct = branchProductService.update(branchProduct);

    // Build the response with the updated branch data
    BaseOutput<BranchProduct> response =
        BaseOutput.<BranchProduct>builder()
            .message(HttpStatus.OK.toString())
            .data(updateBranchProduct)
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
    branchProductService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
