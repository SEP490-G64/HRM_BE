package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.services.BranchProductService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BranchProductServiceImpl implements BranchProductService {
  @Autowired private BranchProductRepository branchProductRepository;

  @Autowired private BranchProductMapper branchProductMapper;

  @Override
  public BranchProduct create(BranchProduct branchProduct) {
    if (branchProduct == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCHPRODUCT.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(branchProduct)
        .map(e -> branchProductMapper.toEntity(e))
        .map(e -> branchProductRepository.save(e))
        .map(e -> branchProductMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public BranchProduct update(BranchProduct branchProduct) {
    // Retrieve the existing branch entity by ID
    BranchProductEntity oldBranchProduct =
        branchProductRepository.findById(branchProduct.getId()).orElse(null);
    if (oldBranchProduct == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    // Update the fields of the existing branch entity with new values
    return Optional.ofNullable(oldBranchProduct)
        .map(
            op ->
                op.toBuilder()
                    .quantity(branchProduct.getQuantity())
                    .maxQuantity(branchProduct.getMaxQuantity())
                    .minQuantity(branchProduct.getMinQuantity())
                    .build())
        .map(branchProductRepository::save)
        .map(branchProductMapper::toDTO)
        .orElse(null);
  }

  @Override
  public BranchProduct getByBranchIdAndProductId(Long branchId, Long productId) {
    return branchProductRepository
        .findByBranch_IdAndProduct_Id(branchId, productId)
        .map(branchProductMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null || StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    BranchProductEntity oldBranchProduct = branchProductRepository.findById(id).orElse(null);
    if (oldBranchProduct == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCHPRODUCT.NOT_EXIST); // Error if inbound entity is not found
    }

    branchProductRepository.deleteById(id); // Delete the inbound entity by ID
  }
}
