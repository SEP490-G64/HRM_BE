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
import jakarta.persistence.EntityManager;
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
  @Autowired private InventoryCheckRepository inventoryCheckRepository;

  @Autowired private InventoryCheckMapper inventoryCheckMapper;

  @Autowired private EntityManager entityManager;

  @Autowired private UserService userService;
  @Autowired private UserMapper userMapper;

  @Override
  public InventoryCheck getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> inventoryCheckRepository.findById(e).map(b -> inventoryCheckMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<InventoryCheck> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return inventoryCheckRepository.findAll(pageable).map(dao -> inventoryCheckMapper.toDTO(dao));
  }

  @Override
  public InventoryCheck create(InventoryCheck InventoryCheck) {
    if (InventoryCheck == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.EXIST);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(InventoryCheck)
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
    InventoryCheckEntity oldInventoryCheckEntity =
        inventoryCheckRepository.findById(inventoryCheck.getId()).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST);
    }

    return Optional.ofNullable(oldInventoryCheckEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(inventoryCheck.getNote())
                    .status(inventoryCheck.getStatus())
                    .build())
        .map(inventoryCheckRepository::save)
        .map(inventoryCheckMapper::toDTO)
        .orElse(null);
  }

  @Override
  public InventoryCheck approve(Long id) {
    InventoryCheckEntity oldInventoryCheckEntity =
        inventoryCheckRepository.findById(id).orElse(null);
    if (oldInventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldInventoryCheckEntity)
        .map(op -> op.toBuilder().isApproved(true).approvedBy(userEntity).build())
        .map(inventoryCheckRepository::save)
        .map(inventoryCheckMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    InventoryCheckEntity inventoryCheckEntity = inventoryCheckRepository.findById(id).orElse(null);
    if (inventoryCheckEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK.NOT_EXIST);
    }

    inventoryCheckRepository.deleteById(id);
  }
}
