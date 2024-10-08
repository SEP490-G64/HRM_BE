package com.example.hrm_be.controllers.outbound;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.requests.outbound.OutboundCreateRequest;
import com.example.hrm_be.models.requests.outbound.OutboundUpdateRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.OutboundService;
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
@RequestMapping("/api/v1/staff/outbound")
public class StaffOutboundController {
  private final OutboundService outboundService;

  // Retrieves a paginated list of Outbound entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Outbound>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<Outbound> OutboundPage = outboundService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<Outbound>> response =
        BaseOutput.<List<Outbound>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(OutboundPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(OutboundPage.getTotalElements())
            .data(OutboundPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a Outbound by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Outbound>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Outbound> response =
          BaseOutput.<Outbound>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch Outbound by ID
    Outbound Outbound = outboundService.getById(id);

    // Build the response with the found Outbound data
    BaseOutput<Outbound> response =
        BaseOutput.<Outbound>builder()
            .message(HttpStatus.OK.toString())
            .data(Outbound)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new Outbound
  @PostMapping()
  protected ResponseEntity<BaseOutput<Outbound>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          OutboundCreateRequest Outbound) {
    // Validate the request body
    if (Outbound == null) {
      BaseOutput<Outbound> response =
          BaseOutput.<Outbound>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the Outbound
    Outbound createdOutbound = outboundService.create(Outbound);

    // Build the response with the created Outbound data
    BaseOutput<Outbound> response =
        BaseOutput.<Outbound>builder()
            .message(HttpStatus.OK.toString())
            .data(createdOutbound)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Outbound
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Outbound>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          OutboundUpdateRequest Outbound) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Outbound> response =
          BaseOutput.<Outbound>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the Outbound to update
    Outbound.setId(id);

    // Update the Outbound
    Outbound updateOutbound = outboundService.update(Outbound);

    // Build the response with the updated Outbound data
    BaseOutput<Outbound> response =
        BaseOutput.<Outbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateOutbound)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a Outbound by ID
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

    // Delete the Outbound by ID
    outboundService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}