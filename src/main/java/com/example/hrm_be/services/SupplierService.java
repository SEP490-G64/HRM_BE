package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Supplier;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SupplierService {
  Supplier getById(Long id);

  Page<Supplier> getByPaging(
      int pageNo, int pageSize, String sortBy, String keyword, Boolean status);

  Supplier create(Supplier supplier);

  Supplier update(Supplier supplier);

  void delete(Long id);
}
