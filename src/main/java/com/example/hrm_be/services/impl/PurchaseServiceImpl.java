package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.PurchaseMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Purchase;
import com.example.hrm_be.models.entities.PurchaseEntity;
import com.example.hrm_be.repositories.PurchaseRepository;
import com.example.hrm_be.services.PurchaseService;
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
public class PurchaseServiceImpl implements PurchaseService {
  @Autowired private PurchaseRepository purchaseRepository;

  @Autowired private PurchaseMapper purchaseMapper;

  @Autowired private EntityManager entityManager;

  @Override
  public Purchase getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> purchaseRepository.findById(e).map(b -> purchaseMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Purchase> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return purchaseRepository.findAll(pageable).map(dao -> purchaseMapper.toDTO(dao));
  }

  @Override
  public Purchase create(Purchase purchase) {
    if (purchase == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(purchase)
        .map(purchaseMapper::toEntity)
        .map(e -> purchaseRepository.save(e))
        .map(e -> purchaseMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Purchase update(Purchase purchase) {
    PurchaseEntity oldPurchaseEntity = purchaseRepository.findById(purchase.getId()).orElse(null);
    if (oldPurchaseEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.NOT_EXIST);
    }

    return Optional.ofNullable(oldPurchaseEntity)
        .map(
            op ->
                op.toBuilder()
                    .amount(purchase.getAmount())
                    .remainDebt(purchase.getRemainDebt())
                    .purchaseDate(purchase.getPurchaseDate())
                    .build())
        .map(purchaseRepository::save)
        .map(purchaseMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    PurchaseEntity oldPurchaseEntity = purchaseRepository.findById(id).orElse(null);
    if (oldPurchaseEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PURCHASE.NOT_EXIST);
    }

    purchaseRepository.deleteById(id);
  }
}
