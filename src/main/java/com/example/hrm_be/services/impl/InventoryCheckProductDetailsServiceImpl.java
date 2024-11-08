package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InventoryCheckProductDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import com.example.hrm_be.models.dtos.InventoryCheckProductDetails;
import com.example.hrm_be.models.entities.InventoryCheckProductDetailsEntity;
import com.example.hrm_be.repositories.InventoryCheckProductDetailsRepository;
import com.example.hrm_be.services.InventoryCheckProductDetailsService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class InventoryCheckProductDetailsServiceImpl
    implements InventoryCheckProductDetailsService {

  @Autowired private InventoryCheckProductDetailsRepository inventoryCheckProductDetailsRepository;

  @Autowired private InventoryCheckProductDetailsMapper inventoryCheckDetailsMapper;

  @Override
  public InventoryCheckProductDetails getById(Long id) {
    // Retrieve inventory check details by ID and convert to DTO
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                inventoryCheckProductDetailsRepository
                    .findById(e)
                    .map(b -> inventoryCheckDetailsMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<InventoryCheckProductDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create pageable request for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated inventory check details and map to DTOs
    return inventoryCheckProductDetailsRepository
        .findAll(pageable)
        .map(dao -> inventoryCheckDetailsMapper.toDTO(dao));
  }

  @Override
  public InventoryCheckProductDetails create(InventoryCheckProductDetails inventoryCheckDetails) {
    // Validate that inventoryCheckDetails is not null
    if (inventoryCheckDetails == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.EXIST); // Throw error if null
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inventoryCheckDetails)
        .map(inventoryCheckDetailsMapper::toEntity)
        .map(e -> inventoryCheckProductDetailsRepository.save(e))
        .map(e -> inventoryCheckDetailsMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InventoryCheckProductDetails update(InventoryCheckProductDetails inventoryCheckDetails) {
    // Find existing inventory check details by ID
    InventoryCheckProductDetailsEntity oldInventoryCheckProductDetailsEntity =
        inventoryCheckProductDetailsRepository.findById(inventoryCheckDetails.getId()).orElse(null);
    if (oldInventoryCheckProductDetailsEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Update fields of the existing entity
    return Optional.ofNullable(oldInventoryCheckProductDetailsEntity)
        .map(
            op ->
                op.toBuilder()
                    .systemQuantity(inventoryCheckDetails.getSystemQuantity())
                    .countedQuantity(inventoryCheckDetails.getCountedQuantity())
                    .difference(inventoryCheckDetails.getDifference())
                    .reason(inventoryCheckDetails.getReason())
                    .build())
        .map(inventoryCheckProductDetailsRepository::save)
        .map(inventoryCheckDetailsMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void deleteByInventoryCheckId(Long checkId) {
    inventoryCheckProductDetailsRepository.deleteAllByInventoryCheck_Id(checkId);
  }

  @Override
  public InventoryCheckProductDetails findByCheckIdAndProductId(Long checkId, Long productId) {
    return inventoryCheckProductDetailsRepository
        .findByInventoryCheck_IdAndProduct_Id(checkId, productId)
        .map(inventoryCheckDetailsMapper::toDTO)
        .orElse(null);
  }
}
