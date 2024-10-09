package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.UnitConversion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface UnitConversionService {
  UnitConversion getById(Long id);

  List<UnitConversion> getAll();

  UnitConversion create(UnitConversion unitConversion);

  UnitConversion update(UnitConversion unitConversion);

  void delete(Long id);
}
