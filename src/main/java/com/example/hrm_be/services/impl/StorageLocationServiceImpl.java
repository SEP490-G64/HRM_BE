package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.STORAGE_LOCATION;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.StorageLocation;
import com.example.hrm_be.models.entities.StorageLocationEntity;
import com.example.hrm_be.repositories.StorageLocationRepository;
import com.example.hrm_be.services.StorageLocationService;
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
public class StorageLocationServiceImpl implements StorageLocationService {
  @Autowired private StorageLocationRepository storageLocationRepository;

  @Autowired private StorageLocationMapper storageLocationMapper;
  @Autowired private BranchProductMapper branchProductMapper;

  @Override
  public StorageLocation getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(
            e -> storageLocationRepository.findById(e).map(b -> storageLocationMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<StorageLocation> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Tìm kiếm theo tên
    return storageLocationRepository
        .findByShelfNameContainingIgnoreCase(name, pageable)
        .map(dao -> storageLocationMapper.toDTO(dao));
  }

  @Override
  public StorageLocation create(StorageLocation storageLocation) {
    if (storageLocation == null) {
      throw new HrmCommonException(STORAGE_LOCATION.NOT_EXIST);
    }
    return Optional.ofNullable(storageLocation)
        .map(e -> storageLocationMapper.toEntity(e))
        .map(e -> storageLocationRepository.save(e))
        .map(e -> storageLocationMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public StorageLocation update(StorageLocation storageLocation) {
    StorageLocationEntity oldStorageLocationEntity =
        storageLocationRepository.findById(storageLocation.getId()).orElse(null);
    if (oldStorageLocationEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.STORAGE_LOCATION.NOT_EXIST);
    }
    return Optional.ofNullable(oldStorageLocationEntity)
        .map(op -> op.toBuilder().shelfName(storageLocation.getShelfName()).build())
        .map(storageLocationRepository::save)
        .map(storageLocationMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      return;
    }
    storageLocationRepository.deleteById(id);
  }
}
