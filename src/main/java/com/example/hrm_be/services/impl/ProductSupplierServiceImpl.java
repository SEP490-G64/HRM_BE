package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.*;
import com.example.hrm_be.components.*;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSupplierServiceImpl implements ProductSupplierService {
  @Autowired private ProductSuppliersRepository productSuppliersRepository;
  @Autowired private ProductSuppliersMapper productSuppliersMapper;

  @Override
  public ProductSuppliers findByProductAndSupplier(
      ProductEntity product, SupplierEntity supplier) {
    return productSuppliersMapper.toDTO(productSuppliersRepository.findByProductAndSupplier(product, supplier).orElse(null));
  }

  @Override
  public ProductSuppliers save(ProductSuppliersEntity productSuppliersEntity) {
    return productSuppliersMapper.toDTO(productSuppliersRepository.save(productSuppliersEntity));
  }

  @Override
  public void delete(Long id) {
    productSuppliersRepository.deleteById(id);
  }
}
