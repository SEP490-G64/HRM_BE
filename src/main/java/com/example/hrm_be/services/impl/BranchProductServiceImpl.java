package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.services.BranchProductService;
import java.math.BigDecimal;
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
  @Autowired private BatchMapper batchMapper;
  @Autowired private ProductMapper productMapper;
  @Autowired private BranchMapper branchMapper;

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

  @Override
  public BranchProduct getOrUpdateBranchProduct(Branch toBranch, Product product,
      Integer quantity) {
    // Retrieve or create a new BranchProduct entity
    BranchProductEntity branchProduct = branchProductRepository
        .findByBranch_IdAndProduct_Id(toBranch.getId(), product.getId())
        .orElse(new BranchProductEntity());

    // If it exists, update the quantity
    if (branchProduct.getId() != null) {
      branchProduct.setQuantity(
          branchProduct.getQuantity() != null
              ? branchProduct.getQuantity().add(BigDecimal.valueOf(quantity))
              : BigDecimal.valueOf(quantity)
      );
    } else {
      // Otherwise, set the details for a new entity
      branchProduct.setProduct(productMapper.toEntity(product));
      branchProduct.setBranch(branchMapper.toEntity(toBranch));
      branchProduct.setQuantity(BigDecimal.valueOf(quantity));
      branchProduct.setMinQuantity(0); // Set default min quantity, or use business logic
      branchProduct.setMaxQuantity(0); // Set default max quantity, or use business logic
    }

    // Save and return the updated or new entity
    return branchProductMapper.toDTO(branchProductRepository.save(branchProduct));
  }
}
