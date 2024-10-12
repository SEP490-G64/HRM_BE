package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.PurchaseMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Purchase;
import com.example.hrm_be.models.entities.PurchaseEntity;
import com.example.hrm_be.repositories.PurchaseRepository;
import com.example.hrm_be.services.PurchaseService;
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
public class PurchaseServiceImpl implements PurchaseService {
  @Autowired private PurchaseRepository purchaseRepository;

  @Autowired private PurchaseMapper purchaseMapper;

  // Method to get Purchase by ID, converts entity to DTO if found, returns null if not found
  @Override
  public Purchase getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> purchaseRepository.findById(e).map(b -> purchaseMapper.toDTO(b)))
        .orElse(null);
  }

  // Method to retrieve a paginated list of Purchase entities, sorted by the specified field
  @Override
  public Page<Purchase> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create a pageable object for pagination with sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

    // Fetch paginated results, map each entity to DTO
    return purchaseRepository.findAll(pageable).map(dao -> purchaseMapper.toDTO(dao));
  }

  // Method to create a new Purchase record
  @Override
  public Purchase create(Purchase purchase) {
    // If the purchase object is null, throw a custom exception
    if (purchase == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.EXIST);
    }

    // Convert DTO to entity, save it to the database, and then convert it back to DTO
    return Optional.ofNullable(purchase)
        .map(purchaseMapper::toEntity)
        .map(e -> purchaseRepository.save(e))
        .map(e -> purchaseMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing Purchase record
  @Override
  public Purchase update(Purchase purchase) {
    // Find the existing Purchase entity by its ID
    PurchaseEntity oldPurchaseEntity = purchaseRepository.findById(purchase.getId()).orElse(null);

    // If the Purchase entity does not exist, throw a custom exception
    if (oldPurchaseEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.NOT_EXIST);
    }

    // Update fields of the existing entity, save the updated entity, and convert it to DTO
    return Optional.ofNullable(oldPurchaseEntity)
        .map(
            op ->
                // Use builder pattern to update entity fields
                op.toBuilder()
                    .amount(purchase.getAmount())
                    .remainDebt(purchase.getRemainDebt())
                    .purchaseDate(purchase.getPurchaseDate())
                    .build())
        .map(purchaseRepository::save)
        .map(purchaseMapper::toDTO)
        .orElse(null);
  }

  // Method to delete a Purchase record by ID
  @Override
  public void delete(Long id) {
    // If the ID is blank, return without performing any action
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Find the existing Purchase entity by its ID
    PurchaseEntity oldPurchaseEntity = purchaseRepository.findById(id).orElse(null);

    // If the entity does not exist, throw a custom exception
    if (oldPurchaseEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.NOT_EXIST);
    }

    // Delete the entity by its ID
    purchaseRepository.deleteById(id);
  }
}
