package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Dashboard;
import com.example.hrm_be.models.dtos.DashboardInboundOutboundStream;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReportService {
  Dashboard getOverviewDashboard(Long branchId);

  List<DashboardInboundOutboundStream> getDashboardChart(Long branchId, String timeRange);
}
