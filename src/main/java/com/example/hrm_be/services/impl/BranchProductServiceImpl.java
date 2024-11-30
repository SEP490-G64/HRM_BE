package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.services.BranchProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  public BranchProduct save(BranchProduct branchProduct) {
    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(branchProduct)
        .map(e -> branchProductMapper.toEntity(e))
        .map(e -> branchProductRepository.save(e))
        .map(e -> branchProductMapper.toDTO(e))
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

    branchProductRepository.updateBranchProductStatus(
        ProductStatus.DA_XOA, id); // Delete the inbound
    // entity
    // by ID
  }

  @Override
  public void updateBranchProductInInbound(
      BranchEntity toBranch, ProductEntity product, BigDecimal quantity) {
    // Check if BranchProductEntity already exists
    BranchProductEntity branchProduct =
        branchProductRepository
            .findByBranch_IdAndProduct_Id(toBranch.getId(), product.getId())
            .orElse(new BranchProductEntity());

    // If it exists, update the quantity, otherwise create a new one
    if (branchProduct.getId() != null) {
      branchProduct.setQuantity(
          branchProduct.getQuantity() != null
              ? branchProduct.getQuantity().add(quantity)
              : quantity); // Update existing quantity
      branchProduct.setLastUpdated(LocalDateTime.now());
    } else {
      branchProduct.setProduct(product);
      branchProduct.setBranch(toBranch);
      branchProduct.setQuantity(quantity);
      branchProduct.setMinQuantity(null); // Set default min quantity, or use business
      // logic
      branchProduct.setMaxQuantity(null); // Set default max quantity, or use business
      // logic
      branchProduct.setLastUpdated(LocalDateTime.now());
    }

    // Save the BranchProductEntity
    branchProductRepository.save(branchProduct);
  }

  @Override
  public BigDecimal findTotalQuantityForProduct(Long productId) {
    return branchProductRepository.findTotalQuantityForProduct(productId);
  }

  @Override
  public void saveAll(List<BranchProductEntity> branchProducts) {
    branchProductRepository.saveAll(branchProducts);
  }

  // Method to get BranchProduct with quantity below minQuantity
  @Override
  public List<BranchProduct> findBranchProductsWithQuantityBelowMin(Long branchId) {
    return branchProductRepository.findBranchProductsWithQuantityBelowMin(branchId).stream()
        .map(branchProductMapper::toDTO)
        .collect(Collectors.toList());
  }

  // Method to get BranchProduct with quantity above maxQuantity
  @Override
  public List<BranchProduct> findBranchProductsWithQuantityAboveMax(Long branchId) {
    return branchProductRepository.findBranchProductsWithQuantityAboveMax(branchId).stream()
        .map(branchProductMapper::toDTO)
        .collect(Collectors.toList());
  }

  // Method to get BranchProduct with quantity equal to 0
  @Override
  public List<BranchProduct> findBranchProductsWithQuantityIsZero(Long branchId) {
    return branchProductRepository.findBranchProductsWithQuantityIsZero(branchId).stream()
        .map(branchProductMapper::toDTO)
        .collect(Collectors.toList());
  }
}
