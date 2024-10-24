package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductRepository
    extends JpaRepository<BranchProductEntity, Long>,
        JpaSpecificationExecutor<BranchProductEntity> {

  Optional<BranchProductEntity> findByBranchAndProduct(BranchEntity branch, ProductEntity product);
}
