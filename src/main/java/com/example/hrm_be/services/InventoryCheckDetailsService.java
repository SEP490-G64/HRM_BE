package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsCreateRequest;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InventoryCheckDetailsService {
    InventoryCheckDetails getById(Long id);

    Page<InventoryCheckDetails> getByPaging(int pageNo, int pageSize, String sortBy);

    InventoryCheckDetails create(InventoryCheckDetailsCreateRequest inventoryCheckDetails);

    InventoryCheckDetails update(InventoryCheckDetailsUpdateRequest inventoryCheckDetails);

    void delete(Long id);
}
