package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.AllowedProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllowedProductRepository extends JpaRepository<AllowedProductEntity, Long> {
  List<AllowedProductEntity> findAllByProductNameContainsIgnoreCase(String searchStr);
}
