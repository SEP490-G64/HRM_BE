package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.ProductSuppliers;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProductSupplierService {
  ProductSuppliers findByProductAndSupplier(ProductEntity product, SupplierEntity supplier);

  ProductSuppliers save(ProductSuppliersEntity productSuppliersEntity);

  void delete(Long id);
}
