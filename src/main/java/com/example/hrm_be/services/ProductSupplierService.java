package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.ProductSuppliers;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductSupplierService {
  ProductSuppliersEntity findByProductAndSupplier(ProductEntity product, SupplierEntity supplier);

  List<ProductSuppliersEntity> saveAll(List<ProductSuppliersEntity> productSuppliersEntities);
}
