package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InventoryCheckDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;
import com.example.hrm_be.repositories.InventoryCheckDetailsRepository;
import com.example.hrm_be.services.InventoryCheckDetailsService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
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
  @Autowired private InventoryCheckDetailsRepository inventoryCheckDetailsRepository;

  @Autowired private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;

  @Autowired private EntityManager entityManager;

  @Override
  public InventoryCheckDetails getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                inventoryCheckDetailsRepository
                    .findById(e)
                    .map(b -> inventoryCheckDetailsMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<InventoryCheckDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return inventoryCheckDetailsRepository
        .findAll(pageable)
        .map(dao -> inventoryCheckDetailsMapper.toDTO(dao));
  }

  @Override
  public InventoryCheckDetails create(InventoryCheckDetails InventoryCheckDetails) {
    if (InventoryCheckDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(InventoryCheckDetails)
        .map(inventoryCheckDetailsMapper::toEntity)
        .map(e -> inventoryCheckDetailsRepository.save(e))
        .map(e -> inventoryCheckDetailsMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InventoryCheckDetails update(InventoryCheckDetails inventoryCheckDetails) {
    InventoryCheckDetailsEntity oldInventoryCheckDetailsEntity =
        inventoryCheckDetailsRepository.findById(inventoryCheckDetails.getId()).orElse(null);
    if (oldInventoryCheckDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.NOT_EXIST);
    }

    return Optional.ofNullable(oldInventoryCheckDetailsEntity)
        .map(
            op ->
                op.toBuilder()
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
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    InventoryCheckDetailsEntity oldInventoryCheckDetailsEntity =
            inventoryCheckDetailsRepository.findById(id).orElse(null);
    if (oldInventoryCheckDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INVENTORY_CHECK_DETAILS.NOT_EXIST);
    }

    inventoryCheckDetailsRepository.deleteById(id);
  }
}
