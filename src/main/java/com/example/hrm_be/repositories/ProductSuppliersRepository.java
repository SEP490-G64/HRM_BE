package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import com.example.hrm_be.models.entities.SupplierEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSuppliersRepository extends JpaRepository<ProductSuppliersEntity, Long> {

  Optional<ProductSuppliersEntity> findByProductAndSupplier(
      ProductEntity product, SupplierEntity supplierEntity);
}
