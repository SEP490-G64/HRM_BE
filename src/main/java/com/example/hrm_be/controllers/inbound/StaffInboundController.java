package com.example.hrm_be.controllers.inbound;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InboundService;
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
@RequestMapping("/api/v1/staff/inbound")
@Tag(name = "Staff-Inbounds API")
@SecurityRequirement(name = "Authorization")
public class StaffInboundController {
  private final InboundService inboundService;

  // GET: /api/v1/staff/inbound
  // Retrieves a paginated list of Inbound entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Inbound>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<Inbound> InboundPage = inboundService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<Inbound>> response =
        BaseOutput.<List<Inbound>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(InboundPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(InboundPage.getTotalElements())
            .data(InboundPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/inbound/{id}
  // Retrieves a Inbound by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch Inbound by ID
    Inbound Inbound = inboundService.getById(id);

    // Build the response with the found Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(Inbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/inbound
  // Creates a new Inbound
  @PostMapping()
  protected ResponseEntity<BaseOutput<Inbound>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Inbound Inbound) {
    // Validate the request body
    if (Inbound == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the Inbound
    Inbound createdInbound = inboundService.create(Inbound);

    // Build the response with the created Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/inbound/{id}
  // Updates an existing Inbound
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Inbound Inbound) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the Inbound to update
    Inbound.setId(id);

    // Update the Inbound
    Inbound updateInbound = inboundService.update(Inbound);

    // Build the response with the updated Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/inbound/approve/{id}
  // Approve an Inbound by Manager
  @PutMapping("/approve/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> approve(
      @PathVariable("id") Long id, @RequestParam boolean accept) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the Inbound
    Inbound updateInbound = inboundService.approve(id, accept);

    // Build the response with the updated Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/inbound/{id}
  // Deletes a Inbound by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the Inbound by ID
    inboundService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}