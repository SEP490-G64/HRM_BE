package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SupplierService {
  Supplier getById(Long id);

  Page<Supplier> getByPaging(int pageNo, int pageSize, String sortBy);

  Supplier create(Supplier supplier);

  Supplier update(Supplier supplier);

  void delete(Long id);
}
