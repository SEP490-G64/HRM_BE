package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.PurchaseMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.PurchaseEntity;
import com.example.hrm_be.repositories.*;
import com.example.hrm_be.services.*;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {
  @Autowired private InboundRepository inboundRepository;
  @Autowired private OutboundRepository outboundRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private SupplierService supplierService;
  @Autowired private ProductCategoryRepository productCategoryRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private SupplierRepository supplierRepository;

  @Override
  public Dashboard getOverviewDashboard(Long branchId) {
    Dashboard dashboard = new Dashboard();
    List<BigDecimal> items = new ArrayList<>();
    items.add(inboundRepository.getTotalInboundValue(branchId));
    items.add(outboundRepository.getTotalOutboundValue(branchId));
    items.add(productRepository.getTotalProductCountByBranchId(branchId));
    items.add(BigDecimal.valueOf(supplierService.getByPaging(0, Integer.MAX_VALUE, "id", "", null).getTotalElements()));
    dashboard.setDashboardItems(items);

    List<Object[]> rawCate = productCategoryRepository.getCategoryWithProductPercentage(branchId);
    List<DashboardPairValue> topCate = rawCate.stream()
            .map(row -> new DashboardPairValue(
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? (row[2] instanceof Long ? BigDecimal.valueOf((Long) row[2]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[2]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[3] != null ? (row[3] instanceof Long ? BigDecimal.valueOf((Long) row[3]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[3]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO
            ))
            .collect(Collectors.toList());
    // Tính tổng percentage
    BigDecimal totalPercentageCate = topCate.stream()
            .map(DashboardPairValue::getValue2)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    // Nếu tổng phần trăm khác 100, thêm một mục "Khác"
    if (totalPercentageCate.compareTo(BigDecimal.valueOf(100)) < 0) {
      BigDecimal remainingPercentage = BigDecimal.valueOf(100).subtract(totalPercentageCate);
      topCate.add(new DashboardPairValue("Khác", BigDecimal.ZERO, remainingPercentage));
    }
    dashboard.setTopCategories(topCate);

    List<Object[]> rawType = productTypeRepository.getTypeWithProductPercentage(branchId);
    List<DashboardPairValue> topType = rawType.stream()
            .map(row -> new DashboardPairValue(
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? (row[2] instanceof Long ? BigDecimal.valueOf((Long) row[2]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[2]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[3] != null ? (row[3] instanceof Long ? BigDecimal.valueOf((Long) row[3]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[3]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO
            ))
            .collect(Collectors.toList());
    // Tính tổng percentage
    BigDecimal totalPercentage = topType.stream()
            .map(DashboardPairValue::getValue2)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    // Nếu tổng phần trăm khác 100, thêm một mục "Khác"
    if (totalPercentage.compareTo(BigDecimal.valueOf(100)) < 0) {
      BigDecimal remainingPercentage = BigDecimal.valueOf(100).subtract(totalPercentage);
      topType.add(new DashboardPairValue("Khác", BigDecimal.ZERO, remainingPercentage));
    }
    dashboard.setTopTypes(topType);

    List<Object[]> rawSupplier = supplierRepository.getTopSuppliers(branchId);
    List<DashboardSupplier> topSupplier = rawSupplier.stream()
            .map(row -> new DashboardSupplier(
                    row[0] != null ? (Long) row[0] : null,
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? row[2].toString() : null,
                    row[3] != null ? (Long) row[3] : 0,
                    row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO
            ))
            .collect(Collectors.toList());
    dashboard.setTopFiveSuppliers(topSupplier);

    List<Object[]> rawProduct = productRepository.getTopProduct(branchId);
    List<DashboardProduct> topProduct = rawProduct.stream()
            .map(row -> new DashboardProduct(
                    row[0] != null ? (Long) row[0] : null,
                    row[1] != null ? row[1].toString() : null,
                    row[2] != null ? row[2].toString() : null,
                    row[3] != null ? (row[3] instanceof Long ? BigDecimal.valueOf((Long) row[3]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[3]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[4] != null ? (row[4] instanceof Double ? BigDecimal.valueOf((Double) row[4]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[4]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[5] != null ? (row[5] instanceof Double ? BigDecimal.valueOf((Double) row[5]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[5]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[6] != null ? (row[6] instanceof Double ? BigDecimal.valueOf((Double) row[6]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[6]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[7] != null ? (row[7] instanceof Double ? BigDecimal.valueOf((Double) row[7]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[7]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[8] != null ? (row[8] instanceof Double ? BigDecimal.valueOf((Double) row[8]).setScale(2, RoundingMode.HALF_UP) : (BigDecimal) row[8]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO,
                    row[9] != null ? row[9].toString() : null
            ))
            .collect(Collectors.toList());
    dashboard.setTopFiveProducts(topProduct);

    return dashboard;
  }

  public static String[] calculateDateRange(String timeRange) {
    // Lấy ngày hiện tại
    LocalDate today = LocalDate.now();
    LocalDate startDate = today;  // Khởi tạo startDate mặc định là ngày hiện tại

    // Xử lý dựa trên loại thời gian (timeRange)
    switch (timeRange) {
      case "Năm":
        startDate = today.minusYears(5);
        break;
      case "Quý":
        startDate = today.minusMonths(3 * 15);
        break;
      case "Tháng":
        startDate = today.minusMonths(20);
        break;
      case "Tuần":
        startDate = today.minusWeeks(20);
        break;
      case "Ngày":
        startDate = today.minusDays(20);
        break;
      default:
        startDate = today.minusDays(20);
        break;
    }

    // Chuyển các ngày này thành chuỗi định dạng yyyy-MM-dd
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String startDateStr = startDate.format(formatter);
    String endDateStr = today.format(formatter);

    return new String[]{startDateStr, endDateStr};
  }

  @Override
  public List<DashboardInboundOutboundStream> getDashboardChart(Long branchId, String timeRange) {
    // Ensure timeRange is not null before processing
    if (timeRange == null) {
      timeRange = "Ngày";  // Default value or handle as needed
    }

    String[] dateRange = calculateDateRange(timeRange);
    String startDate = dateRange[0];
    String endDate = dateRange[1];

    List<Object[]> rawResult = switch (timeRange) {
        case "Năm" -> inboundRepository.getInboundOutboundDataByYear(startDate, endDate, branchId);
        case "Quý" -> inboundRepository.getInboundOutboundDataByQuarter(startDate, endDate, branchId);
        case "Tháng" -> inboundRepository.getInboundOutboundDataByMonth(startDate, endDate, branchId);
        case "Tuần" -> inboundRepository.getInboundOutboundDataByWeek(startDate, endDate, branchId);
        default -> inboundRepository.getInboundOutboundDataByDay(startDate, endDate, branchId);
    };
    // Pass correct parameters to the repository

      List<DashboardInboundOutboundStream> result = rawResult.stream()
            .map(row -> new DashboardInboundOutboundStream(
                    row[0] != null ? new SimpleDateFormat("dd-MM-yyyy").format((Date) row[0]) : null, // time
                    row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO, // inbound
                    row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO  // outbound
            ))
            .collect(Collectors.toList());

    return result;
  }
}
