package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionCreateRequest;
import com.example.hrm_be.models.requests.specialCondition.SpecialConditionUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface SpecialConditionService {
    SpecialCondition getById(Long id);

    Page<SpecialCondition> getByPaging(int pageNo, int pageSize, String sortBy);

    SpecialCondition create(SpecialConditionCreateRequest specialCondition);

    SpecialCondition update(SpecialConditionUpdateRequest specialCondition);

    void delete(Long id);
}
