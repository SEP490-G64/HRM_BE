package com.example.hrm_be.controllers.outbound;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.*;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.OutboundService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/outbound")
@Tag(name = "Staff-Outbounds API")
@SecurityRequirement(name = "Authorization")
public class StaffOutboundController {
  private final OutboundService outboundService;

  // GET: /api/v1/staff/outbound
  // Retrieves a paginated list of Outbound entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Outbound>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false, defaultValue = "DESC") String direction,
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) OutboundStatus status,
      @RequestParam(required = false) OutboundType type) {
    Page<Outbound> OutboundPage =
        outboundService.getByPaging(
            page, size, sortBy, direction, branchId, keyword, startDate, endDate, status, type);

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

  // GET: /api/v1/staff/outbound/{id}
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

  // PUT: /api/v1/staff/outbound/approve/{id}
  // Approve an Outbound by Manager
  @PutMapping("/approve/{id}")
  protected ResponseEntity<BaseOutput<Outbound>> approve(
      @PathVariable("id") Long id, @RequestParam boolean accept) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Outbound> response =
          BaseOutput.<Outbound>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the Outbound
    Outbound updateOutbound = outboundService.approve(id, accept);

    // Build the response with the updated Outbound data
    BaseOutput<Outbound> response =
        BaseOutput.<Outbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateOutbound)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/outbound/{id}
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

  @PostMapping("/create-init-outbound")
  public ResponseEntity<BaseOutput<Outbound>> createInnitOutBound(@RequestParam String type) {
    // Check if the type is valid using the exists method
    if (!OutboundType.exists(type)) {
      // Return a bad request if the type is invalid
      return ResponseEntity.badRequest()
          .body(
              BaseOutput.<Outbound>builder()
                  .status(ResponseStatus.FAILED)
                  .message("Invalid Inbound Type")
                  .build());
    }
    Outbound outbound = outboundService.createInnitOutbound(OutboundType.parse(type));
    return ResponseEntity.ok(
        BaseOutput.<Outbound>builder().data(outbound).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/submit-draft")
  public ResponseEntity<BaseOutput<Outbound>> submitDraft(
      @RequestBody CreateOutboundRequest request) {
    Outbound outbound = outboundService.saveOutbound(request);
    return ResponseEntity.ok(
        BaseOutput.<Outbound>builder().data(outbound).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/submit-draft-sell")
  public ResponseEntity<BaseOutput<Outbound>> submitDraftForSell(
      @RequestBody CreateOutboundRequest request) {
    Outbound outbound = outboundService.saveOutboundForSell(request);
    return ResponseEntity.ok(
        BaseOutput.<Outbound>builder().data(outbound).status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/{id}/update-status")
  public ResponseEntity<BaseOutput<String>> updateStatus(
      @RequestParam String type, @PathVariable(name = "id") Long id) {
    outboundService.updateOutboundStatus(OutboundStatus.valueOf(type), id);
    return ResponseEntity.ok(BaseOutput.<String>builder().status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/submit")
  public ResponseEntity<BaseOutput<Outbound>> submitToSystem(
      @RequestBody CreateOutboundRequest request) {
    Outbound outbound = outboundService.submitOutboundToSystem(request);
    BaseOutput<Outbound> response =
        BaseOutput.<Outbound>builder()
            .message(HttpStatus.OK.toString())
            .data(outbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/generate-outbound/{id}")
  public ResponseEntity<BaseOutput<String>> generateOutbound(
      @PathVariable("id") Long id, HttpServletResponse response) {
    // Validate the path variable ID
    if (id == null || id <= 0) {
      BaseOutput<String> responseOutput =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of("Invalid path variable"))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseOutput);
    }

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=outBound.pdf");

    try (ByteArrayOutputStream pdfStream = outboundService.generateOutboundPdf(id);
        ServletOutputStream outputStream = response.getOutputStream()) {

      pdfStream.writeTo(outputStream);
      outputStream.flush();

      BaseOutput<String> responseOutput =
          BaseOutput.<String>builder()
              .status(ResponseStatus.SUCCESS)
              .data("PDF generated successfully")
              .build();
      return ResponseEntity.ok(responseOutput);

    } catch (IOException | DocumentException e) {
      BaseOutput<String> responseOutput =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of("Error generating PDF: " + e.getMessage()))
              .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseOutput);
    }
  }
}
