package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.*;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSupplierServiceImpl implements ProductSupplierService {
  @Autowired private ProductSuppliersRepository productSuppliersRepository;

  @Override
  public ProductSuppliersEntity findByProductAndSupplier(ProductEntity product, SupplierEntity supplier) {
    return productSuppliersRepository.findByProductAndSupplier(product, supplier).orElse(null);
  }

  @Override
  public List<ProductSuppliersEntity> saveAll(List<ProductSuppliersEntity> productSuppliersEntities) {
    return productSuppliersRepository.saveAll(productSuppliersEntities);
  }
}
