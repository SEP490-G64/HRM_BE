package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Dashboard;
import com.example.hrm_be.models.dtos.DashboardInboundOutboundStream;
import com.example.hrm_be.models.dtos.Purchase;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ReportService {
    Dashboard getOverviewDashboard(Long branchId);
    List<DashboardInboundOutboundStream> getDashboardChart(Long branchId, String timeRange);
}
