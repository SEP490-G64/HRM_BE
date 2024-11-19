package com.example.hrm_be.services;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ProductSupplierService {
  ProductSuppliersEntity findByProductAndSupplier(ProductEntity product, SupplierEntity supplier);

  ProductSuppliersEntity save(ProductSuppliersEntity productSuppliersEntity);

  void delete(Long id);

  public List<ProductSuppliersEntity> saveAll(
          List<ProductSuppliersEntity> productSuppliersEntities);
}
