package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Branch;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface BranchService {
    Branch getById(Long id);

    Page<Branch> getByPaging(int pageNo, int pageSize, String sortBy);

    Branch create(Branch branch);

    Branch update(Branch branch);

    void delete(Long id);
}
