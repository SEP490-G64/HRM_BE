package com.example.hrm_be.controllers.outboundDetails;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.OutboundDetailService;
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
@RequestMapping("/api/v1/staff/outboundDetail-details")
@Tag(name = "Staff-Outbound-Details API")
@SecurityRequirement(name = "Authorization")
public class StaffOutboundDetailsController {
  private final OutboundDetailService outboundDetailService;

  // Retrieves a paginated list of OutboundDetail entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<OutboundDetail>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<OutboundDetail> OutboundDetailPage = outboundDetailService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<OutboundDetail>> response =
        BaseOutput.<List<OutboundDetail>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(OutboundDetailPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(OutboundDetailPage.getTotalElements())
            .data(OutboundDetailPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a OutboundDetail by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<OutboundDetail>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<OutboundDetail> response =
          BaseOutput.<OutboundDetail>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch OutboundDetail by ID
    OutboundDetail OutboundDetail = outboundDetailService.getById(id);

    // Build the response with the found OutboundDetail data
    BaseOutput<OutboundDetail> response =
        BaseOutput.<OutboundDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(OutboundDetail)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new OutboundDetail
  @PostMapping()
  protected ResponseEntity<BaseOutput<OutboundDetail>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") OutboundDetail outboundDetail) {
    // Validate the request body
    if (outboundDetail == null) {
      BaseOutput<OutboundDetail> response =
          BaseOutput.<OutboundDetail>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the OutboundDetail
    OutboundDetail createdOutboundDetail = outboundDetailService.create(outboundDetail);

    // Build the response with the created OutboundDetail data
    BaseOutput<OutboundDetail> response =
        BaseOutput.<OutboundDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(createdOutboundDetail)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing OutboundDetail
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<OutboundDetail>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") OutboundDetail outboundDetail) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<OutboundDetail> response =
          BaseOutput.<OutboundDetail>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the OutboundDetail to update
    outboundDetail.setId(id);

    // Update the OutboundDetail
    OutboundDetail updateOutboundDetail = outboundDetailService.update(outboundDetail);

    // Build the response with the updated OutboundDetail data
    BaseOutput<OutboundDetail> response =
        BaseOutput.<OutboundDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(updateOutboundDetail)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a OutboundDetail by ID
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

    // Delete the OutboundDetail by ID
    outboundDetailService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
