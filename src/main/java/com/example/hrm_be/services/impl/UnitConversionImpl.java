package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.UNIT_CONVERSION;
import com.example.hrm_be.components.UnitConversionMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitConversionEntity;
import com.example.hrm_be.repositories.UnitConversionRepository;
import com.example.hrm_be.services.UnitConversionService;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    UnitConversionEntity unitConversion = unitConversionRepository.findById(id).orElse(null);
    if (unitConversion == null) {
      throw new HrmCommonException(HrmConstant.ERROR.UNIT_CONVERSION.NOT_EXIST);
    }
    unitConversionRepository.deleteById(id);
  }

  @Override
  public List<UnitConversionEntity> saveAll(List<UnitConversionEntity> unitConversionEntities) {
    return unitConversionRepository.saveAll(unitConversionEntities);
  }

  @Override
  public void deleteAll(List<UnitConversionEntity> unitConversionEntities) {
    unitConversionRepository.deleteAll(unitConversionEntities);
  }

  @Override
  public List<UnitConversionEntity> getByProductId(Long productId) {
    return unitConversionRepository.getByProductId(productId);
  }

  @Override
  public BigDecimal convertToUnit(
      Long productId,
      Long baseUnitId,
      BigDecimal quantity,
      UnitOfMeasurement targetUnit,
      Boolean toBaseUnit) {
    // If the target unit is the same as the base unit, no conversion is needed
    if (targetUnit == null || targetUnit.getId().equals(baseUnitId)) {
      return quantity;
    }

    // Check if conversion is from smaller to larger or larger to smaller
    UnitConversionEntity conversion =
        unitConversionRepository
            .findByProductIdAndLargerUnitIdAndSmallerUnitId(
                productId, baseUnitId, targetUnit.getId())
            .orElse(null);

    if (conversion != null) {
      if (toBaseUnit) {
        return quantity.divide(
            BigDecimal.valueOf(conversion.getFactorConversion()).setScale(2, RoundingMode.HALF_UP));
      } else {
        return quantity.multiply(
            BigDecimal.valueOf(conversion.getFactorConversion()).setScale(2, RoundingMode.HALF_UP));
      }
    } else {
      return BigDecimal.ZERO;
    }
  }
}
