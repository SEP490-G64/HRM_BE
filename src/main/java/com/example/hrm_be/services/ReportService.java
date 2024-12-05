package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Dashboard;
import com.example.hrm_be.models.dtos.DashboardInboundOutboundStream;
import com.example.hrm_be.models.dtos.StockProductReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReportService {
  Dashboard getOverviewDashboard(Long branchId);

  List<DashboardInboundOutboundStream> getDashboardChart(Long branchId, String timeRange);

  Page<StockProductReport> getStockReport(Long branchId, String keyword, Pageable pageable);
}
