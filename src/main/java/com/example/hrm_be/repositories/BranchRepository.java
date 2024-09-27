package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.BranchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    boolean existsByLocation(String location);
    Page<BranchEntity> findBy(String branchName, String branchAddress, Pageable pageable);
}
