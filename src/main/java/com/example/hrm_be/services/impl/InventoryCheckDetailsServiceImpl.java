package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InventoryCheckDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;
import com.example.hrm_be.repositories.InventoryCheckDetailsRepository;
import com.example.hrm_be.services.InventoryCheckDetailsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InventoryCheckDetailsServiceImpl implements InventoryCheckDetailsService {

  @Autowired
  private InventoryCheckDetailsRepository inventoryCheckDetailsRepository;

  @Autowired
  private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;

  @Override
  public InventoryCheckDetails getById(Long id) {
    // Retrieve inventory check details by ID and convert to DTO
    return Optional.ofNullable(id)
            .flatMap(
                    e ->
                            inventoryCheckDetailsRepository.findById(e)
                                    .map(b -> inventoryCheckDetailsMapper.toDTO(b)))
            .orElse(null);
  }

  @Override
  public Page<InventoryCheckDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create pageable request for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated inventory check details and map to DTOs
    return inventoryCheckDetailsRepository.findAll(pageable)
            .map(dao -> inventoryCheckDetailsMapper.toDTO(dao));
  }

  @Override
  public InventoryCheckDetails create(InventoryCheckDetails inventoryCheckDetails) {
    // Validate that inventoryCheckDetails is not null
    if (inventoryCheckDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.EXIST); // Throw error if null
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inventoryCheckDetails)
            .map(inventoryCheckDetailsMapper::toEntity)
            .map(e -> inventoryCheckDetailsRepository.save(e))
            .map(e -> inventoryCheckDetailsMapper.toDTO(e))
            .orElse(null);
  }

  @Override
  public InventoryCheckDetails update(InventoryCheckDetails inventoryCheckDetails) {
    // Find existing inventory check details by ID
    InventoryCheckDetailsEntity oldInventoryCheckDetailsEntity =
            inventoryCheckDetailsRepository.findById(inventoryCheckDetails.getId()).orElse(null);
    if (oldInventoryCheckDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Update fields of the existing entity
    return Optional.ofNullable(oldInventoryCheckDetailsEntity)
            .map(
                    op -> op.toBuilder()
                            .systemQuantity(inventoryCheckDetails.getSystemQuantity())
                            .countedQuantity(inventoryCheckDetails.getCountedQuantity())
                            .difference(inventoryCheckDetails.getDifference())
                            .reason(inventoryCheckDetails.getReason())
                            .build())
            .map(inventoryCheckDetailsRepository::save)
            .map(inventoryCheckDetailsMapper::toDTO)
            .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // Validate the ID
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if invalid
    }

    // Find existing inventory check details by ID
    InventoryCheckDetailsEntity oldInventoryCheckDetailsEntity =
        inventoryCheckDetailsRepository.findById(id).orElse(null);
    if (oldInventoryCheckDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Delete the inventory check details
    inventoryCheckDetailsRepository.deleteById(id);
  }
}
