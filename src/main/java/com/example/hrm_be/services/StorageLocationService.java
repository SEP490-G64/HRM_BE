package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.StorageLocation;
import java.util.List;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface StorageLocationService {
  StorageLocation getById(Long id);

  Page<StorageLocation> getByPaging(int pageNo, int pageSize, String sortBy, String name);

  StorageLocation create(StorageLocation supplier);

  StorageLocation update(StorageLocation supplier);

  void delete(Long id);


}
