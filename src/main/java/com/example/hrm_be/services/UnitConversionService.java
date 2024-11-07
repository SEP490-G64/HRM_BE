package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.UnitConversion;
import java.util.List;

import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import org.springframework.stereotype.Service;

@Service
public interface UnitConversionService {
  UnitConversion getById(Long id);

  List<UnitConversion> getAll();

  UnitConversion create(UnitConversion unitConversion);

  UnitConversion update(UnitConversion unitConversion);

  void delete(Long id);

  List<UnitConversionEntity> saveAll(List<UnitConversionEntity> unitConversionEntities);

  void deleteAll(List<UnitConversionEntity> unitConversionEntities);

  List<UnitConversionEntity> getByProductId(Long productId);

  UnitConversionEntity findByProductIdAndLargerUnitIdAndSmallerUnitId(
          Long productId, Long largerUnitId, Long smallerUnitId);
}
