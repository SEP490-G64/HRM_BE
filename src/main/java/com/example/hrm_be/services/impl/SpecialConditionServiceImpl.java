package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ConditionType;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionCreateRequest;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionUpdateRequest;
import com.example.hrm_be.repositories.SpecialConditionRepository;
import com.example.hrm_be.services.SpecialConditionService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
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

  @Autowired private EntityManager entityManager;

  @Override
  public SpecialCondition getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(
            e -> specialConditionRepository.findById(e).map(b -> specialConditionMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<SpecialCondition> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return specialConditionRepository
        .findAll(pageable)
        .map(dao -> specialConditionMapper.toDTO(dao));
  }

  @Override
  public SpecialCondition create(SpecialConditionCreateRequest specialCondition) {
    if (specialCondition == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    ProductEntity product;
    if (specialCondition.getProductId() != null) {
      product = entityManager.getReference(ProductEntity.class, specialCondition.getProductId());
      if (product == null) {
        throw new HrmCommonException(
            "Product not found with id: " + specialCondition.getProductId());
      }
    } else {
      product = null;
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(specialCondition)
        .map(e -> specialConditionMapper.toEntity(e, product))
        .map(e -> specialConditionRepository.save(e))
        .map(e -> specialConditionMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public SpecialCondition update(SpecialConditionUpdateRequest specialCondition) {
    SpecialConditionEntity oldSpecialConditionEntity =
        specialConditionRepository.findById(specialCondition.getId()).orElse(null);
    if (oldSpecialConditionEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    return Optional.ofNullable(oldSpecialConditionEntity)
        .map(
            op ->
                op.toBuilder()
                    .conditionType(ConditionType.valueOf(specialCondition.getConditionType()))
                    .handlingInstruction(specialCondition.getHandlingInstruction())
                    .build())
        .map(specialConditionRepository::save)
        .map(specialConditionMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    specialConditionRepository.deleteById(id);
  }
}