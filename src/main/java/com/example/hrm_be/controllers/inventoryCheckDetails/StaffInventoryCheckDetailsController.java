package com.example.hrm_be.controllers.inventoryCheckDetails;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsCreateRequest;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsUpdateRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InventoryCheckDetailsService;
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
@RequestMapping("/api/v1/staff/inventory-check-details")
@Tag(name = "Staff-Inventory-Check-Details API")
@SecurityRequirement(name = "Authorization")
public class StaffInventoryCheckDetailsController {
  private final InventoryCheckDetailsService inventoryCheckDetailsService;

  // Retrieves a paginated list of InventoryCheckDetails entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<InventoryCheckDetails>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<InventoryCheckDetails> InventoryCheckDetailsPage =
        inventoryCheckDetailsService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<InventoryCheckDetails>> response =
        BaseOutput.<List<InventoryCheckDetails>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(InventoryCheckDetailsPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(InventoryCheckDetailsPage.getTotalElements())
            .data(InventoryCheckDetailsPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a InventoryCheckDetails by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheckDetails>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InventoryCheckDetails> response =
          BaseOutput.<InventoryCheckDetails>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch InventoryCheckDetails by ID
    InventoryCheckDetails InventoryCheckDetails = inventoryCheckDetailsService.getById(id);

    // Build the response with the found InventoryCheckDetails data
    BaseOutput<InventoryCheckDetails> response =
        BaseOutput.<InventoryCheckDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(InventoryCheckDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new InventoryCheckDetails
  @PostMapping()
  protected ResponseEntity<BaseOutput<InventoryCheckDetails>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InventoryCheckDetailsCreateRequest InventoryCheckDetails) {
    // Validate the request body
    if (InventoryCheckDetails == null) {
      BaseOutput<InventoryCheckDetails> response =
          BaseOutput.<InventoryCheckDetails>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the InventoryCheckDetails
    InventoryCheckDetails createdInventoryCheckDetails =
        inventoryCheckDetailsService.create(InventoryCheckDetails);

    // Build the response with the created InventoryCheckDetails data
    BaseOutput<InventoryCheckDetails> response =
        BaseOutput.<InventoryCheckDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInventoryCheckDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing InventoryCheckDetails
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheckDetails>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InventoryCheckDetailsUpdateRequest InventoryCheckDetails) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InventoryCheckDetails> response =
          BaseOutput.<InventoryCheckDetails>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the InventoryCheckDetails to update
    InventoryCheckDetails.setId(id);

    // Update the InventoryCheckDetails
    InventoryCheckDetails updateInventoryCheckDetails =
        inventoryCheckDetailsService.update(InventoryCheckDetails);

    // Build the response with the updated InventoryCheckDetails data
    BaseOutput<InventoryCheckDetails> response =
        BaseOutput.<InventoryCheckDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInventoryCheckDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a InventoryCheckDetails by ID
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

    // Delete the InventoryCheckDetails by ID
    inventoryCheckDetailsService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
