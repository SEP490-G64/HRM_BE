package com.example.hrm_be.services.impl;

import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.ProductSuppliersMapper;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductSuppliers;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.repositories.ProductSuppliersRepository;
import com.example.hrm_be.services.ProductSupplierService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductSupplierServiceImpl implements ProductSupplierService {
  @Autowired private ProductSuppliersRepository productSuppliersRepository;
  @Autowired private ProductMapper productMapper;
  @Autowired private SupplierMapper supplierMapper;
  @Autowired private ProductSuppliersMapper productSuppliersMapper;

  @Transactional
  @Override
  public ProductSuppliers getOrCreateProductSupplier(Product product, Supplier supplier) {
    // Retrieve the ProductSupplier entity if it exists, otherwise create a new one
    return productSuppliersRepository
        .findByProduct_IdAndSupplier_Id(product.getId(), supplier.getId())
        .map(productSuppliersMapper::toDTO)
        .orElseGet(
            () -> {
              ProductSuppliers newProductSupplier = new ProductSuppliers();
              newProductSupplier.setProduct(product);
              newProductSupplier.setSupplier(supplier);
              return productSuppliersMapper.toDTO(
                  productSuppliersRepository.save(
                      productSuppliersMapper.toEntity(newProductSupplier)));
            });
  }

  @Override
  public void saveListProductSupplier(List<ProductSuppliers> productSuppliers) {
    List<ProductSuppliersEntity> productSuppliersEntities =
        productSuppliers.stream()
            .map(productSuppliersMapper::toEntity)
            .collect(Collectors.toList());
    productSuppliersRepository.saveAll(productSuppliersEntities);
  }
}
