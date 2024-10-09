package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ManufacturerService {

  Manufacturer getById(Long id);

  Page<Manufacturer> getByPaging(int pageNo, int pageSize, String sortBy, String name);

  Manufacturer create(Manufacturer supplier);

  Manufacturer update(Manufacturer supplier);

  void delete(Long id);
}
