package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BranchProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductRepository
    extends JpaRepository<BranchProductEntity, Long>,
        JpaSpecificationExecutor<BranchProductEntity> {}
