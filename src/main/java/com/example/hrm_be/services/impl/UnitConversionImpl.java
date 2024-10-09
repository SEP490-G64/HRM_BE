package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.UNIT_CONVERSION;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.UnitConversionMapper;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.repositories.UnitConversionRepository;
import com.example.hrm_be.services.UnitConversionService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitConversionImpl implements UnitConversionService {
  @Autowired private UnitConversionRepository unitConversionRepository;

  @Autowired private UnitConversionMapper unitConversionMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;
  @Autowired private BatchMapper batchMapper;

  @Override
  public UnitConversion getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> unitConversionRepository.findById(e).map(b -> unitConversionMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public List<UnitConversion> getAll() {
    List<UnitConversionEntity> unitConversionEntities = unitConversionRepository.findAll();
    return unitConversionEntities.stream()
        .map(dao -> unitConversionMapper.toDTO(dao))
        .collect(Collectors.toList());
  }

  @Override
  public UnitConversion create(UnitConversion unitConversion) {
    if (unitConversion == null) {
      throw new HrmCommonException(UNIT_CONVERSION.NOT_EXIST);
    }
    return Optional.ofNullable(unitConversion)
        .map(e -> unitConversionMapper.toEntity(e))
        .map(e -> unitConversionRepository.save(e))
        .map(e -> unitConversionMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public UnitConversion update(UnitConversion unitConversion) {
    UnitConversionEntity oldUnitConversionEntity =
        unitConversionRepository.findById(unitConversion.getId()).orElse(null);
    if (oldUnitConversionEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.UNIT_CONVERSION.NOT_EXIST);
    }
    return Optional.ofNullable(oldUnitConversionEntity)
        .map(op -> op.toBuilder().factorConversion(unitConversion.getFactorConversion()).build())
        .map(unitConversionRepository::save)
        .map(unitConversionMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      return;
    }
    unitConversionRepository.deleteById(id);
  }
}