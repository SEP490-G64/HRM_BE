package com.example.hrm_be.controllers.inbound;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.services.InboundService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "DESC") String direction,
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) LocalDateTime startDate,
      @RequestParam(required = false) LocalDateTime endDate,
      @RequestParam(required = false) InboundStatus status,
      @RequestParam(required = false) InboundType type) {
    Page<Inbound> InboundPage =
        inboundService.getByPaging(
            page, size, sortBy, direction, branchId, keyword, startDate, endDate, status, type);

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
  protected ResponseEntity<BaseOutput<InboundDetail>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InboundDetail> response =
          BaseOutput.<InboundDetail>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch Inbound by ID
    InboundDetail Inbound = inboundService.getById(id);

    // Build the response with the found Inbound data
    BaseOutput<InboundDetail> response =
        BaseOutput.<InboundDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(Inbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/inbound/approve/{id}
  // Approve an Inbound by Manager
  @PutMapping("/approve/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> approve(
          @PathVariable("id") Long id,
          @RequestParam(name = "accept") boolean accept) {
    // Kiểm tra giá trị null hoặc id không hợp lệ
    if (id == null || id <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(BaseOutput.<Inbound>builder()
                      .status(ResponseStatus.FAILED)
                      .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
                      .build());
    }

    // Gọi service để xử lý logic phê duyệt
    Inbound updateInbound = inboundService.approve(id, accept);

    // Trả về kết quả
    return ResponseEntity.ok(
            BaseOutput.<Inbound>builder()
                    .message(HttpStatus.OK.toString())
                    .data(updateInbound)
                    .status(ResponseStatus.SUCCESS)
                    .build());
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

  @PostMapping("/create-init-inbound")
  public ResponseEntity<BaseOutput<Inbound>> createInitInbound(@RequestParam String type) {
    // Check if the type is valid using the exists method
    if (!InboundType.exists(type)) {
      // Return a bad request if the type is invalid
      return ResponseEntity.badRequest()
          .body(
              BaseOutput.<Inbound>builder()
                  .status(ResponseStatus.FAILED)
                  .message("Invalid Inbound Type")
                  .build());
    }
    Inbound inbound = inboundService.createInnitInbound(InboundType.parse(type));
    return ResponseEntity.ok(
        BaseOutput.<Inbound>builder().data(inbound).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/submit-draft")
  public ResponseEntity<BaseOutput<Inbound>> submitDraft(
      @RequestBody CreateInboundRequest request) {
    Inbound inbound = inboundService.saveInbound(request);
    return ResponseEntity.ok(
        BaseOutput.<Inbound>builder().data(inbound).status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/{id}/update-status")
  public ResponseEntity<BaseOutput<String>> updateStatus(
      @RequestParam String type, @PathVariable(name = "id") Long id) {
    inboundService.updateInboundStatus(InboundStatus.valueOf(type), id);
    return ResponseEntity.ok(BaseOutput.<String>builder().status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/submit")
  public ResponseEntity<BaseOutput<Inbound>> submitToSystem(
      @RequestBody CreateInboundRequest request) {
    Inbound inbound = inboundService.submitInboundToSystem(request);

    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(inbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/generate-receipt/{id}")
  public ResponseEntity<BaseOutput<String>> generateReceipt(
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

    // Set the content type of the response to PDF
    response.setContentType("application/pdf");
    // Set the header to inform the browser that this is a downloadable file named receipt.pdf
    response.setHeader("Content-Disposition", "attachment; filename=receipt.pdf");

    try (ByteArrayOutputStream pdfStream = inboundService.generateInboundPdf(id);
        ServletOutputStream outputStream = response.getOutputStream()) {

      // Write the content of pdfStream to the response's OutputStream
      pdfStream.writeTo(outputStream);
      // Ensure that the data is sent
      outputStream.flush();

      // Build the success response
      BaseOutput<String> responseOutput =
          BaseOutput.<String>builder()
              .status(ResponseStatus.SUCCESS)
              .data("PDF generated successfully")
              .build();
      return ResponseEntity.ok(responseOutput);

    } catch (IOException | DocumentException e) {
      // Handle exceptions if an error occurs during PDF generation
      BaseOutput<String> responseOutput =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of("Error generating PDF: " + e.getMessage()))
              .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseOutput);
    }
  }
}
