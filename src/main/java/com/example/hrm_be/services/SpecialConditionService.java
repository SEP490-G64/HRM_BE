package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.SpecialCondition;
import java.util.List;

import com.example.hrm_be.models.entities.SpecialConditionEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SpecialConditionService {
  SpecialCondition getById(Long id);

  Page<SpecialCondition> getByPaging(int pageNo, int pageSize, String sortBy);

  SpecialCondition create(SpecialCondition specialCondition);

  SpecialCondition update(SpecialCondition specialCondition);

  void delete(Long id);

  void assignToProductByProductIdAndIds(@NonNull Long productId, @NonNull List<Long> ids);

  List<SpecialConditionEntity> saveAll(List<SpecialConditionEntity> specialConditionEntities);

  void deleteAll(List<SpecialConditionEntity> specialConditionEntities);
}
