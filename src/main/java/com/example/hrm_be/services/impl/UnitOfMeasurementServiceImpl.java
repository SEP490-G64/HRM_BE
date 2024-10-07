package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.STORAGE_LOCATION;
import com.example.hrm_be.components.UnitOfMeasurementMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.entities.UnitOfMeasurementEntity;
import com.example.hrm_be.repositories.UnitOfMeasurementRepository;
import com.example.hrm_be.services.UnitOfMeasurementService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitOfMeasurementServiceImpl implements UnitOfMeasurementService {
  @Autowired
  private UnitOfMeasurementRepository unitOfMeasurementRepository;

  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;

  @Override
  public UnitOfMeasurement getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> unitOfMeasurementRepository.findById(e).map(b -> unitOfMeasurementMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<UnitOfMeasurement> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Tìm kiếm theo tên
    return unitOfMeasurementRepository
        .findByUnitNameContainingIgnoreCase(name, pageable)
        .map(dao -> unitOfMeasurementMapper.toDTO(dao));
  }

  @Override
  public UnitOfMeasurement create(UnitOfMeasurement storageLocation) {
    if (storageLocation == null) {
      throw new HrmCommonException(STORAGE_LOCATION.NOT_EXIST);
    }
    return Optional.ofNullable(storageLocation)
        .map(e -> unitOfMeasurementMapper.toEntity(e))
        .map(e -> unitOfMeasurementRepository.save(e))
        .map(e -> unitOfMeasurementMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public UnitOfMeasurement update(UnitOfMeasurement storageLocation) {
    UnitOfMeasurementEntity oldUnitOfMeasurementEntity = unitOfMeasurementRepository.findById(storageLocation.getId()).orElse(null);
    if (oldUnitOfMeasurementEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_EXIST);
    }
    return Optional.ofNullable(oldUnitOfMeasurementEntity)
        .map(op -> op.toBuilder().unitName(op.getUnitName()).build())
        .map(unitOfMeasurementRepository::save)
        .map(unitOfMeasurementMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      return;
    }
    unitOfMeasurementRepository.deleteById(id);
  }
}
