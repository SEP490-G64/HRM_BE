package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.StorageLocation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface StorageLocationService {
  StorageLocation getById(Long id);

  Page<StorageLocation> getByPaging(
      int pageNo, int pageSize, Long branchId, String sortBy, String name);

  StorageLocation create(StorageLocation storageLocation);

  StorageLocation update(StorageLocation storageLocation);

  StorageLocation save(StorageLocation supplier);

  void delete(Long id);
}
