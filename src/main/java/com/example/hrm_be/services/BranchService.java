package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.models.dtos.Branch;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface BranchService {
  // Retrieve a branch by its ID.
  Branch getById(Long id);

  // Get a paginated list of branches based on provided filters.
  Page<Branch> getByPaging(
      int pageNo, int pageSize, String sortBy, String keyword, BranchType branchType);

  // Create a new branch.
  Branch create(Branch branch);

  // Update an existing branch.
  Branch update(Branch branch);

  // Delete a branch by its ID.
  void delete(Long id);

  Branch getByLocation(String location);
}
