package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductCategoryMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryMapRepository
    extends JpaRepository<ProductCategoryMapEntity, Long> {}
