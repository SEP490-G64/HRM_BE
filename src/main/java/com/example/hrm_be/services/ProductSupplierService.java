package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductSuppliers;
import com.example.hrm_be.models.dtos.Supplier;
import java.util.List;

public interface ProductSupplierService {

  ProductSuppliers getOrCreateProductSupplier(Product product, Supplier supplier);

  void saveListProductSupplier(List<ProductSuppliers> productSuppliers);
}
