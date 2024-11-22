package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.models.entities.InboundEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InboundRepository
    extends JpaRepository<InboundEntity, Long>, JpaSpecificationExecutor<InboundEntity> {

  boolean existsByInboundCode(String inboundCode);

  @Query("SELECT i FROM InboundEntity i WHERE i.id = :inboundId")
  Optional<InboundEntity> findInboundById(@Param("inboundId") Long inboundId);

  @Modifying
  @Transactional
  @Query("UPDATE InboundEntity i SET i.status = :status WHERE i.id = :id")
  void updateInboundStatus(@Param("status") InboundStatus status, @Param("id") Long id);

  @Query(
      "SELECT SUM(COALESCE(i.totalPrice, 0.0)) FROM InboundEntity i WHERE (:branchId IS NULL OR"
          + " i.toBranch.id = :branchId)")
  BigDecimal getTotalInboundValue(@Param("branchId") Long branchId);

  @Query(
      value =
          "WITH DateRange AS (\n"
              + "    SELECT generate_series(\n"
              + "        CAST(:startDate AS DATE),  -- Tham số startDate\n"
              + "        CAST(:endDate AS DATE),    -- Tham số endDate\n"
              + "        interval '1 day'  -- Cố định giá trị là '1 day' cho \"Ngày\"\n"
              + "    )::date AS date\n"
              + "),\n"
              + "InboundData AS (\n"
              + "    SELECT \n"
              + "        CAST(i.created_date AS DATE) AS period_start,  -- Chỉ dùng ngày mà không"
              + " cần tính theo tuần/tháng/quý/năm\n"
              + "        SUM(i.total_price) AS inbound\n"
              + "    FROM inbound i\n"
              + "    WHERE CAST(i.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR i.to_branch_id = :branchId)  -- Điều kiện chi"
              + " nhánh\n"
              + "    GROUP BY CAST(i.created_date AS DATE)  -- Nhóm theo ngày\n"
              + "),\n"
              + "OutboundData AS (\n"
              + "    SELECT \n"
              + "        CAST(o.created_date AS DATE) AS period_start,  -- Chỉ dùng ngày mà không"
              + " cần tính theo tuần/tháng/quý/năm\n"
              + "        SUM(o.total_price) AS outbound\n"
              + "    FROM outbound o\n"
              + "    WHERE CAST(o.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR o.from_branch_id = :branchId)  -- Điều kiện chi"
              + " nhánh\n"
              + "    GROUP BY CAST(o.created_date AS DATE)  -- Nhóm theo ngày\n"
              + ")\n"
              + "SELECT \n"
              + "    dr.date AS time,\n"
              + "    COALESCE(i.inbound, 0) AS inbound,\n"
              + "    COALESCE(o.outbound, 0) AS outbound\n"
              + "FROM DateRange dr\n"
              + "LEFT JOIN InboundData i ON dr.date = i.period_start\n"
              + "LEFT JOIN OutboundData o ON dr.date = o.period_start\n"
              + "ORDER BY dr.date ASC;\n",
      nativeQuery = true)
  List<Object[]> getInboundOutboundDataByDay(
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("branchId") Long branchId);

  @Query(
      value =
          "WITH DateRange AS (\n"
              + "    SELECT generate_series(\n"
              + "        DATE_TRUNC('week', CAST(:startDate AS DATE)),\n"
              + "        DATE_TRUNC('week', CAST(:endDate AS DATE)),\n"
              + "        '1 week'  -- Thay '1 month' bằng '1 week' để tạo dãy ngày theo tuần\n"
              + "    )::date AS date\n"
              + "),\n"
              + "InboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('week', CAST(i.created_date AS DATE)) AS period_start,  --"
              + " Trunc date theo tuần\n"
              + "        SUM(i.total_price) AS inbound\n"
              + "    FROM inbound i\n"
              + "    WHERE CAST(i.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR i.to_branch_id = :branchId)  -- Kiểm tra điều kiện"
              + " branchId\n"
              + "    GROUP BY \n"
              + "        DATE_TRUNC('week', CAST(i.created_date AS DATE))\n"
              + "),\n"
              + "OutboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('week', CAST(o.created_date AS DATE)) AS period_start,  --"
              + " Trunc date theo tuần\n"
              + "        SUM(o.total_price) AS outbound\n"
              + "    FROM outbound o\n"
              + "    WHERE CAST(o.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR o.from_branch_id = :branchId)  -- Kiểm tra điều kiện"
              + " branchId\n"
              + "    GROUP BY \n"
              + "        DATE_TRUNC('week', CAST(o.created_date AS DATE))\n"
              + ")\n"
              + "SELECT \n"
              + "    dr.date AS time,\n"
              + "    COALESCE(i.inbound, 0) AS inbound,\n"
              + "    COALESCE(o.outbound, 0) AS outbound\n"
              + "FROM DateRange dr\n"
              + "LEFT JOIN InboundData i ON dr.date = i.period_start\n"
              + "LEFT JOIN OutboundData o ON dr.date = o.period_start\n"
              + "ORDER BY dr.date ASC;",
      nativeQuery = true)
  List<Object[]> getInboundOutboundDataByWeek(
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("branchId") Long branchId);

  @Query(
      value =
          "WITH DateRange AS (\n"
              + "    SELECT generate_series(\n"
              + "        DATE_TRUNC('month', CAST(:startDate AS DATE)),\n"
              + "        DATE_TRUNC('month', CAST(:endDate AS DATE)),\n"
              + "        '1 month'\n"
              + "    )::date AS date\n"
              + "),\n"
              + "InboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('month', CAST(i.created_date AS DATE)) AS period_start,\n"
              + "        SUM(i.total_price) AS inbound\n"
              + "    FROM inbound i\n"
              + "    WHERE CAST(i.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "\tAND (:branchId IS NULL OR i.to_branch_id = :branchId)\n"
              + "    GROUP BY \n"
              + "        DATE_TRUNC('month', CAST(i.created_date AS DATE))\n"
              + "),\n"
              + "OutboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('month', CAST(o.created_date AS DATE)) AS period_start,\n"
              + "        SUM(o.total_price) AS outbound\n"
              + "    FROM outbound o\n"
              + "    WHERE CAST(o.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + "\tGROUP BY \n"
              + "        DATE_TRUNC('month', CAST(o.created_date AS DATE))\n"
              + ")\n"
              + "SELECT \n"
              + "    dr.date AS time,\n"
              + "    COALESCE(i.inbound, 0) AS inbound,\n"
              + "    COALESCE(o.outbound, 0) AS outbound\n"
              + "FROM DateRange dr\n"
              + "LEFT JOIN InboundData i ON dr.date = i.period_start\n"
              + "LEFT JOIN OutboundData o ON dr.date = o.period_start\n"
              + "ORDER BY dr.date ASC;",
      nativeQuery = true)
  List<Object[]> getInboundOutboundDataByMonth(
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("branchId") Long branchId);

  @Query(
      value =
          "WITH DateRange AS (\n"
              + "    SELECT generate_series(\n"
              + "        DATE_TRUNC('quarter', CAST(:startDate AS DATE)),\n"
              + // Sử dụng 'quarter' thay vì '3 months'
              "        DATE_TRUNC('quarter', CAST(:endDate AS DATE)),\n"
              + "        '3 months'  -- Đảm bảo tạo dãy theo quý\n"
              + "    )::date AS date\n"
              + "),\n"
              + "InboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('quarter', CAST(i.created_date AS DATE)) AS period_start,\n"
              + // Nhóm theo quý
              "        SUM(i.total_price) AS inbound\n"
              + "    FROM inbound i\n"
              + "    WHERE CAST(i.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR i.to_branch_id = :branchId)\n"
              + // Kiểm tra điều kiện branchId
              "    GROUP BY \n"
              + "        DATE_TRUNC('quarter', CAST(i.created_date AS DATE))\n"
              + "),\n"
              + "OutboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('quarter', CAST(o.created_date AS DATE)) AS period_start,\n"
              + // Nhóm theo quý
              "        SUM(o.total_price) AS outbound\n"
              + "    FROM outbound o\n"
              + "    WHERE CAST(o.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + // Kiểm tra điều kiện branchId
              "    GROUP BY \n"
              + "        DATE_TRUNC('quarter', CAST(o.created_date AS DATE))\n"
              + ")\n"
              + "SELECT \n"
              + "    dr.date AS time,\n"
              + "    COALESCE(i.inbound, 0) AS inbound,\n"
              + "    COALESCE(o.outbound, 0) AS outbound\n"
              + "FROM DateRange dr\n"
              + "LEFT JOIN InboundData i ON dr.date = i.period_start\n"
              + "LEFT JOIN OutboundData o ON dr.date = o.period_start\n"
              + "ORDER BY dr.date ASC;",
      nativeQuery = true)
  List<Object[]> getInboundOutboundDataByQuarter(
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("branchId") Long branchId);

  @Query(
      value =
          "WITH DateRange AS (\n"
              + "    SELECT generate_series(\n"
              + "        DATE_TRUNC('year', CAST(:startDate AS DATE)),\n"
              + "        DATE_TRUNC('year', CAST(:endDate AS DATE)),\n"
              + "        '1 year'\n"
              + "    )::date AS date\n"
              + "),\n"
              + "InboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('year', CAST(i.created_date AS DATE)) AS period_start,\n"
              + "        SUM(i.total_price) AS inbound\n"
              + "    FROM inbound i\n"
              + "    WHERE CAST(i.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "\tAND (:branchId IS NULL OR i.to_branch_id = :branchId)\n"
              + "    GROUP BY \n"
              + "        DATE_TRUNC('year', CAST(i.created_date AS DATE))\n"
              + "),\n"
              + "OutboundData AS (\n"
              + "    SELECT \n"
              + "        DATE_TRUNC('year', CAST(o.created_date AS DATE)) AS period_start,\n"
              + "        SUM(o.total_price) AS outbound\n"
              + "    FROM outbound o\n"
              + "    WHERE CAST(o.created_date AS DATE) BETWEEN CAST(:startDate AS DATE) AND"
              + " CAST(:endDate AS DATE)\n"
              + "    AND (:branchId IS NULL OR o.from_branch_id = :branchId)\n"
              + "\tGROUP BY \n"
              + "        DATE_TRUNC('year', CAST(o.created_date AS DATE))\n"
              + ")\n"
              + "SELECT \n"
              + "    dr.date AS time,\n"
              + "    COALESCE(i.inbound, 0) AS inbound,\n"
              + "    COALESCE(o.outbound, 0) AS outbound\n"
              + "FROM DateRange dr\n"
              + "LEFT JOIN InboundData i ON dr.date = i.period_start\n"
              + "LEFT JOIN OutboundData o ON dr.date = o.period_start\n"
              + "ORDER BY dr.date ASC;",
      nativeQuery = true)
  List<Object[]> getInboundOutboundDataByYear(
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("branchId") Long branchId);
}
