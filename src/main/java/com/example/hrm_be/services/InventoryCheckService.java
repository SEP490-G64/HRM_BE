package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InventoryCheck;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InventoryCheckService {
  InventoryCheck getById(Long id);

  Page<InventoryCheck> getByPaging(int pageNo, int pageSize, String sortBy);

  InventoryCheck create(InventoryCheck inventoryCheck);

  InventoryCheck update(InventoryCheck inventoryCheck);

  InventoryCheck approve(Long id);

  void delete(Long id);
}
