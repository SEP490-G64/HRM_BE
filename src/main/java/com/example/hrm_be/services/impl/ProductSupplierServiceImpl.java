package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant.ERROR.*;
import com.example.hrm_be.components.*;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductSupplierServiceImpl implements ProductSupplierService {
  @Autowired private ProductSuppliersRepository productSuppliersRepository;

  @Override
  public ProductSuppliersEntity findByProductAndSupplier(
      ProductEntity product, SupplierEntity supplier) {
    return productSuppliersRepository.findByProductAndSupplier(product, supplier).orElse(null);
  }

  @Override
  public List<ProductSuppliersEntity> saveAll(
      List<ProductSuppliersEntity> productSuppliersEntities) {
    return productSuppliersRepository.saveAll(productSuppliersEntities);
  }
}
