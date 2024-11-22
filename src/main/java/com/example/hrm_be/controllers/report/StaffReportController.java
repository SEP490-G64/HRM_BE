package com.example.hrm_be.controllers.report;

import com.example.hrm_be.models.dtos.Dashboard;
import com.example.hrm_be.models.dtos.DashboardInboundOutboundStream;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ReportService;
import com.example.hrm_be.services.SpecialConditionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
        List<DashboardInboundOutboundStream> dashboard = reportService.getDashboardChart(branchId, timeRange);

        BaseOutput<List<DashboardInboundOutboundStream>> response =
                BaseOutput.<List<DashboardInboundOutboundStream>>builder()
                        .message(HttpStatus.OK.toString())
                        .data(dashboard)
                        .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                        .build();
        return ResponseEntity.ok(response);
    }
}
