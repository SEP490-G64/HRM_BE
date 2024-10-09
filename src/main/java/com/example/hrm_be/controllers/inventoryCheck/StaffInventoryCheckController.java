package com.example.hrm_be.controllers.inventoryCheck;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.requests.inventoryCheck.InventoryCheckCreateRequest;
import com.example.hrm_be.models.requests.inventoryCheck.InventoryCheckUpdateRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InventoryCheckService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/staff/inventory-check")
@Tag(name = "Staff-Inventory-Checks API")
@SecurityRequirement(name = "Authorization")
public class StaffInventoryCheckController {
  private final InventoryCheckService inventoryCheckService;

  // Retrieves a paginated list of InventoryCheck entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<InventoryCheck>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<InventoryCheck> InventoryCheckPage = inventoryCheckService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<InventoryCheck>> response =
        BaseOutput.<List<InventoryCheck>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(InventoryCheckPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(InventoryCheckPage.getTotalElements())
            .data(InventoryCheckPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a InventoryCheck by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheck>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InventoryCheck> response =
          BaseOutput.<InventoryCheck>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch InventoryCheck by ID
    InventoryCheck InventoryCheck = inventoryCheckService.getById(id);

    // Build the response with the found InventoryCheck data
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(InventoryCheck)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new InventoryCheck
  @PostMapping()
  protected ResponseEntity<BaseOutput<InventoryCheck>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InventoryCheckCreateRequest InventoryCheck) {
    // Validate the request body
    if (InventoryCheck == null) {
      BaseOutput<InventoryCheck> response =
          BaseOutput.<InventoryCheck>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the InventoryCheck
    InventoryCheck createdInventoryCheck = inventoryCheckService.create(InventoryCheck);

    // Build the response with the created InventoryCheck data
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInventoryCheck)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing InventoryCheck
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheck>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InventoryCheckUpdateRequest InventoryCheck) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InventoryCheck> response =
          BaseOutput.<InventoryCheck>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the InventoryCheck to update
    InventoryCheck.setId(id);

    // Update the InventoryCheck
    InventoryCheck updateInventoryCheck = inventoryCheckService.update(InventoryCheck);

    // Build the response with the updated InventoryCheck data
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInventoryCheck)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a InventoryCheck by ID
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

    // Delete the InventoryCheck by ID
    inventoryCheckService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
