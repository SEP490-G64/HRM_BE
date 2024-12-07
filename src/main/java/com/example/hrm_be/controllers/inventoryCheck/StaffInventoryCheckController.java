package com.example.hrm_be.controllers.inventoryCheck;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.requests.CreateInventoryCheckRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InventoryCheckService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/inventory-check")
@Tag(name = "Staff-Inventory-Checks API")
@SecurityRequirement(name = "Authorization")
public class StaffInventoryCheckController {
  private final InventoryCheckService inventoryCheckService;

  // GET: /api/v1/staff/inventory-check
  // Retrieves a paginated list of InventoryCheck entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<InventoryCheck>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "DESC") String direction,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) InventoryCheckStatus status) {
    Page<InventoryCheck> InventoryCheckPage =
        inventoryCheckService.getByPaging(
            page, size, sortBy, direction, branchId, keyword, startDate, endDate, status);

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

  // GET: /api/v1/staff/inventory-check/{id}
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
    InventoryCheck InventoryCheck = inventoryCheckService.getInventoryCheckDetailById(id);

    // Build the response with the found InventoryCheck data
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(InventoryCheck)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/inventory-check
  // Creates a new InventoryCheck
  @PostMapping()
  protected ResponseEntity<BaseOutput<InventoryCheck>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") InventoryCheck InventoryCheck) {
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

  // PUT: /api/v1/staff/inventory-check/{id}
  // Updates an existing InventoryCheck
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheck>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") InventoryCheck InventoryCheck) {
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

  // PUT: /api/v1/staff/inventory-check/approve/{id}
  // Approve an Inventory Check by Manager
  @PutMapping("/approve/{id}")
  protected ResponseEntity<BaseOutput<InventoryCheck>> approve(
      @PathVariable("id") Long id, @RequestParam boolean accept) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InventoryCheck> response =
          BaseOutput.<InventoryCheck>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the InventoryCheck
    InventoryCheck updateInventoryCheck = inventoryCheckService.approve(id, accept);

    // Build the response with the updated InventoryCheck data
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInventoryCheck)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/inventory-check/{id}
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

  @PostMapping("/create-init-check")
  public ResponseEntity<BaseOutput<InventoryCheck>> createInitCheck(
      @RequestParam(required = false) LocalDateTime startDate) {
    // Check if the type is valid using the exists method
    InventoryCheck check = inventoryCheckService.createInitInventoryCheck(startDate);
    return ResponseEntity.ok(
        BaseOutput.<InventoryCheck>builder().data(check).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/submit-draft")
  public ResponseEntity<BaseOutput<InventoryCheck>> submitDraft(
      @RequestBody CreateInventoryCheckRequest request) {
    InventoryCheck check = inventoryCheckService.saveInventoryCheck(request);
    return ResponseEntity.ok(
        BaseOutput.<InventoryCheck>builder().data(check).status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/{id}/submit")
  public ResponseEntity<BaseOutput<InventoryCheck>> submitToSystem(
      @PathVariable(name = "id") Long id) {
    InventoryCheck check = inventoryCheckService.submitInventoryCheckToSystem(id);
    BaseOutput<InventoryCheck> response =
        BaseOutput.<InventoryCheck>builder()
            .message(HttpStatus.OK.toString())
            .data(check)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}/update-status")
  public ResponseEntity<BaseOutput<String>> updateStatus(
      @RequestParam String type, @PathVariable(name = "id") Long id) {
    inventoryCheckService.updateInventoryCheckStatus(InventoryCheckStatus.valueOf(type), id);
    return ResponseEntity.ok(BaseOutput.<String>builder().status(ResponseStatus.SUCCESS).build());
  }

  @GetMapping(path = "/{inventoryCheckId}/stream", produces = "text/event-stream")

  public SseEmitter streamInventoryCheckUpdates(
      @PathVariable Long inventoryCheckId, @RequestParam("authToken") String authToken) {
    log.info("Starting stream for inventoryCheckId: {}", inventoryCheckId);
    return inventoryCheckService.createEmitter(inventoryCheckId);
  }

  @PostMapping("/close/{inventoryCheckId}")
  public ResponseEntity<String> closeInventoryCheckStream(@PathVariable Long inventoryCheckId) {
    boolean closed = inventoryCheckService.closeInventoryCheck(inventoryCheckId);
    if (closed) {
      return ResponseEntity.ok(
          "Stream closed successfully for inventoryCheckId: " + inventoryCheckId);
    } else {
      return ResponseEntity.status(404)
          .body("No active stream found for inventoryCheckId: " + inventoryCheckId);
    }
  }
}
