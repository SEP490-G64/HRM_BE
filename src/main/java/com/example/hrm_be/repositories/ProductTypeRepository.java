package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.ProductTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {}
