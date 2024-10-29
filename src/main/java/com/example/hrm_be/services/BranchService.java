package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Branch;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface BranchService {
  // Retrieve a branch by its ID.
  Branch getById(String id);

  // Get a paginated list of branches based on provided filters.
  Page<Branch> getByPaging(
      String pageNo,
      String pageSize,
      String sortBy,
      String keyword,
      String branchType,
      @Nullable String status);

  // Create a new branch.
  Branch create(Branch branch);

  // Update an existing branch.
  Branch update(Branch branch);

  // Delete a branch by its ID.
  void delete(String id);

  Branch getByLocationContains(String location);
}
