package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.requests.CreateInventoryCheckRequest;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface InventoryCheckService {
  InventoryCheck getById(Long id);

  InventoryCheck getInventoryCheckDetailById(Long id);

  Page<InventoryCheck> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String direction,
      Long branchId,
      String keyword,
      LocalDateTime startDate,
      LocalDateTime endDate,
      InventoryCheckStatus status);

  InventoryCheck create(InventoryCheck inventoryCheck);

  InventoryCheck update(InventoryCheck inventoryCheck);

  InventoryCheck approve(Long id, boolean accept);

  InventoryCheck createInitInventoryCheck(LocalDateTime startDate);

  InventoryCheck saveInventoryCheck(CreateInventoryCheckRequest initOutbound);

  InventoryCheck submitInventoryCheckToSystem(Long id);

  void updateInventoryCheckStatus(InventoryCheckStatus status, Long id);

  void delete(Long id);

  void broadcastInventoryCheckUpdates(Set<Long> productIds, Set<Long> batchIds, Long branchId);
}
