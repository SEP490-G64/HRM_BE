package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.repositories.SpecialConditionRepository;
import com.example.hrm_be.services.SpecialConditionService;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SpecialConditionServiceImpl implements SpecialConditionService {

  @Autowired private SpecialConditionRepository specialConditionRepository;

  @Autowired private SpecialConditionMapper specialConditionMapper;

  @Override
  public SpecialCondition getById(Long id) {
    // Retrieve SpecialCondition by ID, map it to DTO if found, return null otherwise
    return Optional.ofNullable(id)
        .flatMap(
            e -> specialConditionRepository.findById(e).map(b -> specialConditionMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<SpecialCondition> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create a pageable object for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

    // Fetch all SpecialCondition records from the repository, map entities to DTOs
    return specialConditionRepository
        .findAll(pageable)
        .map(dao -> specialConditionMapper.toDTO(dao));
  }

  @Override
  public SpecialCondition create(SpecialCondition specialCondition) {
    // Check if the provided specialCondition object is null and throw an exception if true
    if (specialCondition == null) {
      throw new HrmCommonException(HrmConstant.ERROR.SPECIAL_CONDITION.EXIST);
    }

    // Convert the DTO to an entity, save it, and then map the saved entity back to a DTO
    return Optional.ofNullable(specialCondition)
        .map(specialConditionMapper::toEntity)
        .map(e -> specialConditionRepository.save(e))
        .map(e -> specialConditionMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public SpecialCondition update(SpecialCondition specialCondition) {
    // Retrieve the existing SpecialCondition entity by its ID
    SpecialConditionEntity oldSpecialConditionEntity =
        specialConditionRepository.findById(specialCondition.getId()).orElse(null);

    // If the entity doesn't exist, throw an exception
    if (oldSpecialConditionEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.SPECIAL_CONDITION.NOT_EXIST);
    }

    // Update the existing entity with new values, save it, and map it back to a DTO
    return Optional.ofNullable(oldSpecialConditionEntity)
        .map(
            op ->
                op.toBuilder()
                    .conditionType(specialCondition.getConditionType()) // Update condition type
                    .handlingInstruction(
                        specialCondition.getHandlingInstruction()) // Update handling instruction
                    .build())
        .map(specialConditionRepository::save)
        .map(specialConditionMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // If the ID is blank, return without performing any action
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Find the existing entity by ID, throw an exception if it doesn't exist
    SpecialConditionEntity oldSpecialConditionEntity =
        specialConditionRepository.findById(id).orElse(null);
    if (oldSpecialConditionEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.SPECIAL_CONDITION.NOT_EXIST);
    }

    // Delete the entity from the repository
    specialConditionRepository.deleteById(id);
  }

  @Override
  public void assignToProductByProductIdAndIds(@NonNull Long productId, @NonNull List<Long> ids) {

    specialConditionRepository.assignToProductByProductIdAndIds(productId, ids);
  }

  @Override
  public List<SpecialConditionEntity> saveAll(List<SpecialConditionEntity> specialConditionEntities) {
    return specialConditionRepository.saveAll(specialConditionEntities);
  }

  @Override
  public void deleteAll(List<SpecialConditionEntity> specialConditionEntities) {
    specialConditionRepository.deleteAll(specialConditionEntities);
  }
}
