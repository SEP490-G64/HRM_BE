package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.AllowedProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowedProductRepository extends JpaRepository<AllowedProductEntity, Long> {
  AllowedProductEntity findByProductCode(String code);

  List<AllowedProductEntity> findAllByProductNameContainsIgnoreCase(String searchStr);
}
