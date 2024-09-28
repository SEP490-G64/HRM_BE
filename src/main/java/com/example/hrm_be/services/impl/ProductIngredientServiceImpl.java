package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductIngredientMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductIngredient;
import com.example.hrm_be.models.entities.ProductIngredientEntity;
import com.example.hrm_be.repositories.ProductIngredientRepository;
import com.example.hrm_be.services.ProductIngredientService;
import io.micrometer.common.util.StringUtils;
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
public class ProductIngredientServiceImpl implements ProductIngredientService {
    @Autowired
    private ProductIngredientRepository ingredientRepository;
    @Autowired
    private ProductIngredientMapper ingredientMapper;

    @Override
    public ProductIngredient getById(Long id) {
        return Optional.ofNullable(id)
                .flatMap(e -> ingredientRepository.findById(e).map(b -> ingredientMapper.toDTO(b)))
                .orElse(null);
    }

    @Override
    public Page<ProductIngredient> getByPaging(int pageNo, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        return ingredientRepository.findAll(pageable).map(dao -> ingredientMapper.toDTO(dao));
    }

    @Override
    public ProductIngredient create(ProductIngredient ingredient) {
        if (ingredient == null || ingredientRepository.existsByName(ingredient.getName())) {
            throw new HrmCommonException(HrmConstant.ERROR.INGREDIENT.EXIST);
        }
        return Optional.ofNullable(ingredient)
                .map(e -> ingredientMapper.toEntity(e))
                .map(e -> ingredientRepository.save(e))
                .map(e -> ingredientMapper.toDTO(e))
                .orElse(null);
    }

    @Override
    public ProductIngredient update(ProductIngredient ingredient) {
        ProductIngredientEntity oldIngredientEntity = ingredientRepository.findById(ingredient.getId()).orElse(null);
        if (oldIngredientEntity == null) {
            throw new HrmCommonException(HrmConstant.ERROR.INGREDIENT.NOT_EXIST);
        }
        return Optional.ofNullable(oldIngredientEntity)
                .map(op -> op.toBuilder().name(ingredient.getName()).build())
                .map(ingredientRepository::save)
                .map(ingredientMapper::toDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        if (StringUtils.isBlank(id.toString())) {
            return;
        }
        ingredientRepository.deleteById(id);
    }
}
