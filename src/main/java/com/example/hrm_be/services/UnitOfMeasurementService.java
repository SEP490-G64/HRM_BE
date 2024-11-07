package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface UnitOfMeasurementService {
  Boolean existById(Long id);

  UnitOfMeasurement getById(Long id);

  Page<UnitOfMeasurement> getByPaging(int pageNo, int pageSize, String sortBy, String keyword);

  UnitOfMeasurement create(UnitOfMeasurement unitOfMeasurement);

  UnitOfMeasurement update(UnitOfMeasurement unitOfMeasurement);

  void delete(Long id);

  UnitOfMeasurement getByName(String name);
}
