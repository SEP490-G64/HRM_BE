package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import org.springframework.data.domain.Page;

public interface InventoryCheckProductDetailsService {

  InventoryCheckProductDetails getById(Long id);

  Page<InventoryCheckProductDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  InventoryCheckProductDetails create(InventoryCheckProductDetails inventoryCheckDetails);

  InventoryCheckProductDetails update(InventoryCheckProductDetails inventoryCheckDetails);

   void deleteByInventoryCheckId(Long checkId);

  InventoryCheckProductDetails findByCheckIdAndProductId(Long checkId,Long productId);

}
