package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.requests.CreateInventoryCheckRequest;
import com.example.hrm_be.models.responses.InventoryUpdate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

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

  Flux<InventoryUpdate> streamInventoryCheckUpdates(Long inventoryCheckId)
      throws InterruptedException;

  ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> listClients();

  boolean closeInventoryCheck(Long inventoryCheckId);

  SseEmitter createEmitter(Long userId);
}
