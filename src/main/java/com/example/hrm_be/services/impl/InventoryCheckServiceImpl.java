package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.components.InventoryCheckMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InventoryCheck;
import com.example.hrm_be.models.entities.InventoryCheckEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.repositories.InventoryCheckRepository;
import com.example.hrm_be.services.InventoryCheckService;
import com.example.hrm_be.services.UserService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class InventoryCheckServiceImpl implements InventoryCheckService {

  @Autowired
  private InventoryCheckRepository inventoryCheckRepository;

  @Autowired
  private InventoryCheckMapper inventoryCheckMapper;

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @Override
  public InventoryCheck getById(Long id) {
    // Retrieve an InventoryCheck by ID and convert it to a DTO
    return Optional.ofNullable(id)
            .flatMap(e -> inventoryCheckRepository.findById(e).map(b -> inventoryCheckMapper.toDTO(b)))
            .orElse(null);
  }

  @Override
  public Page<InventoryCheck> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create a Pageable object for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated InventoryChecks and map them to DTOs
    return inventoryCheckRepository.findAll(pageable).map(dao -> inventoryCheckMapper.toDTO(dao));
  }

  @Override
  public InventoryCheck create(InventoryCheck inventoryCheck) {
    // Check if the InventoryCheck is null
    if (inventoryCheck == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.EXIST); // Throw an error if null
    }

    // Get the email of the authenticated user
    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Convert DTO to entity, set metadata, and save it
    return Optional.ofNullable(inventoryCheck)
            .map(inventoryCheckMapper::toEntity)
            .map(
                    e -> {
                      e.setCreatedBy(userEntity);
                      e.setCreatedDate(LocalDateTime.now());
                      e.setStatus(InventoryCheckStatus.CHO_DUYET);
                      e.setIsApproved(false);
                      return inventoryCheckRepository.save(e);
                    })
            .map(e -> inventoryCheckMapper.toDTO(e))
            .orElse(null);
  }

  @Override
  public InventoryCheck update(InventoryCheck inventoryCheck) {
    // Find the existing InventoryCheck by ID
    InventoryCheckEntity oldInventoryCheckEntity =
            inventoryCheckRepository.findById(inventoryCheck.getId()).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    // Update the existing entity with new data
    return Optional.ofNullable(oldInventoryCheckEntity)
            .map(
                    op -> op.toBuilder()
                            .note(inventoryCheck.getNote())
                            .status(inventoryCheck.getStatus())
                            .build())
            .map(inventoryCheckRepository::save)
            .map(inventoryCheckMapper::toDTO)
            .orElse(null);
  }

  @Override
  public InventoryCheck approve(Long id) {
    // Find the existing InventoryCheck by ID
    InventoryCheckEntity oldInventoryCheckEntity =
            inventoryCheckRepository.findById(id).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    // Get the email of the authenticated user
    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Approve the inventory check and set the approver
    return Optional.ofNullable(oldInventoryCheckEntity)
            .map(op -> op.toBuilder()
                    .isApproved(true) // Set approval flag
                    .approvedBy(userEntity) // Set approver
                    .build())
            .map(inventoryCheckRepository::save) // Save updated entity
            .map(inventoryCheckMapper::toDTO) // Convert to DTO
            .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // Check if the ID is valid
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if invalid
    }

    // Find the existing InventoryCheck by ID
    InventoryCheckEntity inventoryCheckEntity =
            inventoryCheckRepository.findById(id).orElse(null);
    if (inventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST); // Throw error if not found
    }

    // Delete the inventory check
    inventoryCheckRepository.deleteById(id);
  }
}
