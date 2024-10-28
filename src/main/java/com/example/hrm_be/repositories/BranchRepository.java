package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.models.entities.BranchEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
  // Check if a branch exists by its location.
  boolean existsByLocation(String location);

  // Check if a branch exists by its name.
  boolean existsByBranchName(String name);

  // User query to find branches based on a keyword for branch name or location,
  // with an optional filter for branch type.
  @Query(
      "SELECT b FROM BranchEntity b "
          + "WHERE (LOWER(b.branchName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(b.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
          + "AND (:branchType IS NULL OR b.branchType = :branchType) " +
              "AND (:status IS NULL OR b.activeStatus = :status)")
  Page<BranchEntity> findByBranchNameOrLocationAndBranchType(
      String keyword, BranchType branchType, Boolean status, Pageable pageable);

  Optional<BranchEntity> findByLocationContainsIgnoreCase(String branchName);
}
