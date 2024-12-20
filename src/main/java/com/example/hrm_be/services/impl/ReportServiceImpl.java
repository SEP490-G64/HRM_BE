package com.example.hrm_be.services.impl;

import com.example.hrm_be.components.ReportMapper;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.repositories.*;
import com.example.hrm_be.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
  @Autowired private BranchProductRepository branchProductRepository;
  @Autowired private BranchBatchRepository branchBatchRepository;
  @Autowired private ReportMapper reportMapper;

  @Override
  public Dashboard getOverviewDashboard(Long branchId) {
    Dashboard dashboard = new Dashboard();
    List<BigDecimal> items = new ArrayList<>();
    items.add(inboundRepository.getTotalInboundValue(branchId));
    items.add(outboundRepository.getTotalOutboundValue(branchId));
    items.add(productRepository.getTotalProductCountByBranchId(branchId));
    items.add(
        BigDecimal.valueOf(
            supplierService.getByPaging(0, Integer.MAX_VALUE, "id", "", null).getTotalElements()));
    dashboard.setDashboardItems(items);

    List<Object[]> rawCate = productCategoryRepository.getCategoryWithProductPercentage(branchId);
    List<DashboardPairValue> topCate =
        rawCate.stream()
            .map(
                row ->
                    new DashboardPairValue(
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null
                            ? (row[2] instanceof Long
                                    ? BigDecimal.valueOf((Long) row[2])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[2])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[3] != null
                            ? (row[3] instanceof Long
                                    ? BigDecimal.valueOf((Long) row[3])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[3])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO))
            .collect(Collectors.toList());
    // Tính tổng percentage
    BigDecimal totalPercentageCate =
        topCate.stream()
            .map(DashboardPairValue::getValue2)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    // Nếu tổng phần trăm khác 100, thêm một mục "Khác"
    if (totalPercentageCate.compareTo(BigDecimal.valueOf(100)) < 0) {
      BigDecimal remainingPercentage = BigDecimal.valueOf(100).subtract(totalPercentageCate);
      topCate.add(new DashboardPairValue("Khác", BigDecimal.ZERO, remainingPercentage));
    }
    dashboard.setTopCategories(topCate);

    List<Object[]> rawType = productTypeRepository.getTypeWithProductPercentage(branchId);
    List<DashboardPairValue> topType =
        rawType.stream()
            .map(
                row ->
                    new DashboardPairValue(
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null
                            ? (row[2] instanceof Long
                                    ? BigDecimal.valueOf((Long) row[2])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[2])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[3] != null
                            ? (row[3] instanceof Long
                                    ? BigDecimal.valueOf((Long) row[3])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[3])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO))
            .collect(Collectors.toList());
    // Tính tổng percentage
    BigDecimal totalPercentage =
        topType.stream()
            .map(DashboardPairValue::getValue2)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    // Nếu tổng phần trăm khác 100, thêm một mục "Khác"
    if (totalPercentage.compareTo(BigDecimal.valueOf(100)) < 0) {
      BigDecimal remainingPercentage = BigDecimal.valueOf(100).subtract(totalPercentage);
      topType.add(new DashboardPairValue("Khác", BigDecimal.ZERO, remainingPercentage));
    }
    dashboard.setTopTypes(topType);

    List<Object[]> rawSupplier = supplierRepository.getTopSuppliers(branchId);
    List<DashboardSupplier> topSupplier =
        rawSupplier.stream()
            .map(
                row ->
                    new DashboardSupplier(
                        row[0] != null ? (Long) row[0] : null,
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null ? row[2].toString() : null,
                        row[3] != null ? (Long) row[3] : 0,
                        row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO))
            .collect(Collectors.toList());
    dashboard.setTopFiveSuppliers(topSupplier);

    List<Object[]> rawProduct = productRepository.getTopProduct(branchId);
    List<DashboardProduct> topProduct =
        rawProduct.stream()
            .map(
                row ->
                    new DashboardProduct(
                        row[0] != null ? (Long) row[0] : null,
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null ? row[2].toString() : null,
                        row[3] != null
                            ? (row[3] instanceof Long
                                    ? BigDecimal.valueOf((Long) row[3])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[3])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[4] != null
                            ? (row[4] instanceof Double
                                    ? BigDecimal.valueOf((Double) row[4])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[4])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[5] != null
                            ? (row[5] instanceof Double
                                    ? BigDecimal.valueOf((Double) row[5])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[5])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[6] != null
                            ? (row[6] instanceof Double
                                    ? BigDecimal.valueOf((Double) row[6])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[6])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[7] != null
                            ? (row[7] instanceof Double
                                    ? BigDecimal.valueOf((Double) row[7])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[7])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[8] != null
                            ? (row[8] instanceof Double
                                    ? BigDecimal.valueOf((Double) row[8])
                                        .setScale(2, RoundingMode.HALF_UP)
                                    : (BigDecimal) row[8])
                                .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                        row[9] != null ? row[9].toString() : null))
            .collect(Collectors.toList());
    dashboard.setTopFiveProducts(topProduct);

    return dashboard;
  }

  public static String[] calculateDateRange(String timeRange) {
    // Lấy ngày hiện tại
    LocalDate today = LocalDate.now();
    LocalDate startDate = today; // Khởi tạo startDate mặc định là ngày hiện tại

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

    return new String[] {startDateStr, endDateStr};
  }

  @Override
  public List<DashboardInboundOutboundStream> getDashboardChart(Long branchId, String timeRange) {
    // Ensure timeRange is not null before processing
    if (timeRange == null) {
      timeRange = "Ngày"; // Default value or handle as needed
    }

    String[] dateRange = calculateDateRange(timeRange);
    String startDate = dateRange[0];
    String endDate = dateRange[1];

    List<Object[]> rawResult =
        switch (timeRange) {
          case "Năm" ->
              inboundRepository.getInboundOutboundDataByYear(startDate, endDate, branchId);
          case "Quý" ->
              inboundRepository.getInboundOutboundDataByQuarter(startDate, endDate, branchId);
          case "Tháng" ->
              inboundRepository.getInboundOutboundDataByMonth(startDate, endDate, branchId);
          case "Tuần" ->
              inboundRepository.getInboundOutboundDataByWeek(startDate, endDate, branchId);
          default -> inboundRepository.getInboundOutboundDataByDay(startDate, endDate, branchId);
        };
    // Pass correct parameters to the repository

    String finalTimeRange = timeRange;
    List<DashboardInboundOutboundStream> result =
        rawResult.stream()
            .map(
                row -> {
                  // Parse the date from row[0] (assuming it's a Date object)
                  Date date = row[0] != null ? (Date) row[0] : null;
                  BigDecimal inbound = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
                  BigDecimal outbound = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;

                  String formattedDate = null;
                  if (date != null) {
                    // Format the date according to the timeRange
                    if ("Ngày".equals(finalTimeRange)) { // Correctly use equals to compare strings
                      formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(date); // Day
                    } else if ("Tuần".equals(finalTimeRange)) {
                      formattedDate =
                          new SimpleDateFormat("dd-MM-yyyy")
                              .format(date); // Week number of the year
                    } else if ("Tháng".equals(finalTimeRange)) {
                      formattedDate = new SimpleDateFormat("MM-yyyy").format(date); // Month
                    } else if ("Quý".equals(finalTimeRange)) {
                      formattedDate =
                          new SimpleDateFormat("MM-yyyy")
                              .format(date); // Format as "Year-Q{quarter}"
                    } else if ("Năm".equals(finalTimeRange)) {
                      formattedDate = new SimpleDateFormat("yyyy").format(date); // Year
                    }
                  }

                  return new DashboardInboundOutboundStream(formattedDate, inbound, outbound);
                })
            .collect(Collectors.toList());

    return result;
  }

  @Override
  public Page<StockProductReport> getStockReport(Long branchId, String keyword, Pageable pageable) {
    Page<BranchProductEntity> branchProducts;

    if (branchId != null) {
      branchProducts =
          branchProductRepository.findByBranchIdAndProductNameOrProductRegistrationCode(
              branchId, keyword, pageable);
    } else {
      throw new IllegalArgumentException("Branch ID is required");
    }

    // Nhóm dữ liệu theo sản phẩm
    Map<Long, StockProductReport> productReportMap = new HashMap<>();

    for (BranchProductEntity branchProduct : branchProducts) {
      // Lấy hoặc tạo mới báo cáo cho sản phẩm nếu chưa có trong map
      StockProductReport productReport =
          productReportMap.computeIfAbsent(
              branchProduct.getProduct().getId(),
              id -> reportMapper.convertToStockProductReport(branchProduct));

      // Lấy thông tin các lô liên quan đến sản phẩm này
      List<BranchBatchEntity> branchBatches =
          branchBatchRepository.findByBranchIdAndProductId(branchId, productReport.getProductId());

      // Thêm các lô vào báo cáo sản phẩm
      for (BranchBatchEntity branchBatch : branchBatches) {
        StockBatchReport batchReport = reportMapper.convertToStockBatchReport(branchBatch);
        productReport.getBatches().add(batchReport);
        if (batchReport.getExpireDate().isBefore(LocalDateTime.now())) {
          productReport.setSellableQuantity(
              productReport.getSellableQuantity().subtract(batchReport.getTotalQuantity()));
        }
      }
    }

    // Trả về danh sách các báo cáo sản phẩm dưới dạng phân trang
    return new PageImpl<>(
        new ArrayList<>(productReportMap.values()), pageable, branchProducts.getTotalElements());
  }
}
