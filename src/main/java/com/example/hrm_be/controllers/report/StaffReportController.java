package com.example.hrm_be.controllers.report;

import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/report")
@Tag(name = "Staff-Report API")
@SecurityRequirement(name = "Authorization")
public class StaffReportController {
  private final ReportService reportService;

  // GET: /api/v1/staff/report
  @GetMapping("")
  protected ResponseEntity<BaseOutput<Dashboard>> getOverview(
      @RequestParam(required = false) Long branchId) {
    Dashboard dashboard = reportService.getOverviewDashboard(branchId);

    BaseOutput<Dashboard> response =
        BaseOutput.<Dashboard>builder()
            .message(HttpStatus.OK.toString())
            .data(dashboard)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/chart")
  protected ResponseEntity<BaseOutput<List<DashboardInboundOutboundStream>>> getChart(
      @RequestParam(required = false) Long branchId,
      @RequestParam(required = false) String timeRange) {
    List<DashboardInboundOutboundStream> dashboard =
        reportService.getDashboardChart(branchId, timeRange);

    BaseOutput<List<DashboardInboundOutboundStream>> response =
        BaseOutput.<List<DashboardInboundOutboundStream>>builder()
            .message(HttpStatus.OK.toString())
            .data(dashboard)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/report/stock
  @GetMapping("/stock")
  public ResponseEntity<BaseOutput<List<StockProductReport>>> getStockReport(
      @RequestParam(required = false) Long branchId,
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    // Chuyển đổi tham số sort từ chuỗi thành Pageable
    Pageable pageable = PageRequest.of(page, size);

    // Lấy báo cáo tồn kho
    Page<StockProductReport> report = reportService.getStockReport(branchId, keyword, pageable);

    // Create a response object containing the user data and metadata
    BaseOutput<List<StockProductReport>> response =
        BaseOutput.<List<StockProductReport>>builder()
            .message(HttpStatus.OK.toString()) // Set response message to OK
            .totalPages(report.getTotalPages()) // Total number of pages
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the page
            .total(report.getTotalElements()) // Total number of users
            .data(report.getContent()) // List of users in the current page
            .status(
                com.example.hrm_be.commons.enums.ResponseStatus
                    .SUCCESS) // Set response status to SUCCESS
            .build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/report/inbound-outbound
  @GetMapping("/inbound-outbound")
  public ResponseEntity<BaseOutput<List<InboundOutboundProductReport>>> getInboundOutboundReport(
          @RequestParam(required = false) Long branchId,
          @RequestParam(defaultValue = "") String keyword,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size) {
    // Lấy báo cáo tồn kho
    InboundOutboundProductReportPage report = reportService.getInboundOutboundReport(branchId, keyword, page, size);

    // Create a response object containing the user data and metadata
    BaseOutput<List<InboundOutboundProductReport>> response =
            BaseOutput.<List<InboundOutboundProductReport>>builder()
                    .message(HttpStatus.OK.toString()) // Set response message to OK
                    .totalPages(report.getTotalPages()) // Total number of pages
                    .currentPage(page) // Current page number
                    .pageSize(size) // Size of the page
                    .total(report.getTotalElements()) // Total number of users
                    .data(report.getInboundOutboundProductReports()) // List of users in the current page
                    .status(
                            com.example.hrm_be.commons.enums.ResponseStatus
                                    .SUCCESS) // Set response status to SUCCESS
                    .build();

    // Return the response entity with a status of OK
    return ResponseEntity.ok(response);
  }
}
