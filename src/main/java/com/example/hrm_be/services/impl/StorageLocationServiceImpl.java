package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.StorageLocation;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
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
    @Autowired
    private BranchMapper branchMapper;

  @Override
  public StorageLocation getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(
            e -> storageLocationRepository.findById(e).map(b -> storageLocationMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<StorageLocation> getByPaging(int pageNo, int pageSize, Long branchId, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Tìm kiếm theo tên
    return storageLocationRepository
        .findByShelfNameContainingIgnoreCaseAndBranchId(name, branchId, pageable)
        .map(dao -> storageLocationMapper.toDTO(dao));
  }

  @Override
  public StorageLocation create(StorageLocation storageLocation) {
    // Check if the provided specialCondition object is null and throw an exception if true
    if (storageLocation == null) {
      throw new HrmCommonException(HrmConstant.ERROR.STORAGE_LOCATION.EXIST);
    }

    // Convert the DTO to an entity, save it, and then map the saved entity back to a DTO
    return Optional.ofNullable(storageLocation)
            .map(storageLocationMapper::toEntity)
            .map(e -> storageLocationRepository.save(e))
            .map(e -> storageLocationMapper.toDTO(e))
            .orElse(null);
  }

  @Override
  public StorageLocation update(StorageLocation storageLocation) {
    // Retrieve the existing StorageLocation entity by its ID
    StorageLocationEntity oldStorageLocationEntity =
            storageLocationRepository.findById(storageLocation.getId()).orElse(null);

    // If the entity doesn't exist, throw an exception
    if (oldStorageLocationEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.SPECIAL_CONDITION.NOT_EXIST);
    }

    // Update the existing entity with new values, save it, and map it back to a DTO
    return Optional.ofNullable(oldStorageLocationEntity)
            .map(
                    op ->
                            op.toBuilder()
                                    .aisle(storageLocation.getAisle())
                                    .shelfLevel(storageLocation.getShelfLevel())
                                    .shelfName(storageLocation.getShelfName())
                                    .rowNumber(storageLocation.getRowNumber())
                                    .branch(storageLocation.getBranch() != null ?
                                            branchMapper.toEntity(storageLocation.getBranch()) : null)
                                    .active(storageLocation.getActive())
                                    .shelfName(storageLocation.getShelfName())
                                    .zone(storageLocation.getZone())
                                    .specialCondition(
                                            storageLocation.getSpecialCondition())
                                    .build())
            .map(storageLocationRepository::save)
            .map(storageLocationMapper::toDTO)
            .orElse(null);
  }

  @Override
  public StorageLocation save(StorageLocation storageLocation) {
    return Optional.ofNullable(storageLocation)
        .map(e -> storageLocationMapper.toEntity(e))
        .map(e -> storageLocationRepository.save(e))
        .map(e -> storageLocationMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null || !storageLocationRepository.existsById(id)) {
      throw new HrmCommonException(HrmConstant.ERROR.STORAGE_LOCATION.NOT_EXIST);
    }
    storageLocationRepository.deleteById(id);
  }
}
