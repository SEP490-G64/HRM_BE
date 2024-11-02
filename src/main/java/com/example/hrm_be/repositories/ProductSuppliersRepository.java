package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductSuppliersEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSuppliersRepository extends JpaRepository<ProductSuppliersEntity, Long> {

  Optional<ProductSuppliersEntity> findByProduct_IdAndSupplier_Id(Long productId, Long supplierId);
}
