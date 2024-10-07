package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface UnitOfMeasurementService {
  UnitOfMeasurement getById(Long id);

  Page<UnitOfMeasurement> getByPaging(int pageNo, int pageSize, String sortBy, String name);

  UnitOfMeasurement create(UnitOfMeasurement unitOfMeasurement);

  UnitOfMeasurement update(UnitOfMeasurement unitOfMeasurement);

  void delete(Long id);
}

