package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryCheckDetailsService {
  InventoryCheckDetails getById(Long id);

  Page<InventoryCheckDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  InventoryCheckDetails create(InventoryCheckDetails inventoryCheckDetails);

  InventoryCheckDetails update(InventoryCheckDetails inventoryCheckDetails);

  InventoryCheckDetails findByCheckIdAndBatchId(Long checkId, Long batchId);

  List<InventoryCheckDetails> findAllByCheckId(Long checkId);

  void deleteByInventoryCheckId(Long checkId);

  void delete(Long id);

  void saveAll(List<InventoryCheckDetails> inventoryCheckDetails);
}
