package com.example.hrm_be.controllers.inboundDetails;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InboundDetailsService;
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
@RequestMapping("/api/v1/staff/inbound-details")
@Tag(name = "Staff-Inbound-Details API")
@SecurityRequirement(name = "Authorization")
public class StaffInboundDetailsController {
  private final InboundDetailsService inboundDetailsService;

  // Retrieves a paginated list of InboundDetails entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<InboundDetails>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<InboundDetails> InboundDetailsPage = inboundDetailsService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<InboundDetails>> response =
        BaseOutput.<List<InboundDetails>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(InboundDetailsPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(InboundDetailsPage.getTotalElements())
            .data(InboundDetailsPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a InboundDetails by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<InboundDetails>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InboundDetails> response =
          BaseOutput.<InboundDetails>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch InboundDetails by ID
    InboundDetails InboundDetails = inboundDetailsService.getById(id);

    // Build the response with the found InboundDetails data
    BaseOutput<InboundDetails> response =
        BaseOutput.<InboundDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(InboundDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new InboundDetails
  @PostMapping()
  protected ResponseEntity<BaseOutput<InboundDetails>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") InboundDetails InboundDetails) {
    // Validate the request body
    if (InboundDetails == null) {
      BaseOutput<InboundDetails> response =
          BaseOutput.<InboundDetails>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the InboundDetails
    InboundDetails createdInboundDetails = inboundDetailsService.create(InboundDetails);

    // Build the response with the created InboundDetails data
    BaseOutput<InboundDetails> response =
        BaseOutput.<InboundDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInboundDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing InboundDetails
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<InboundDetails>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") InboundDetails InboundDetails) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InboundDetails> response =
          BaseOutput.<InboundDetails>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the InboundDetails to update
    InboundDetails.setId(id);

    // Update the InboundDetails
    InboundDetails updateInboundDetails = inboundDetailsService.update(InboundDetails);

    // Build the response with the updated InboundDetails data
    BaseOutput<InboundDetails> response =
        BaseOutput.<InboundDetails>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInboundDetails)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a InboundDetails by ID
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

    // Delete the InboundDetails by ID
    inboundDetailsService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
