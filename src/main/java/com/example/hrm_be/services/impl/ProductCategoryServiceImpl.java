package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductCategoryMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.entities.ProductCategoryEntity;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import com.example.hrm_be.services.ProductCategoryService;
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
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired private ProductCategoryRepository categoryRepository;
    @Autowired private ProductCategoryMapper categoryMapper;

    @Override
    public ProductCategory getById(Long id) {
        return Optional.ofNullable(id)
                .flatMap(e -> categoryRepository.findById(e).map(b -> categoryMapper.toDTO(b)))
                .orElse(null);
    }

    @Override
    public Page<ProductCategory> getByPagingByKeyword(int pageNo, int pageSize, String sortBy, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        return categoryRepository.findByKeyword(keyword,pageable).map(dao -> categoryMapper.toDTO(dao));
    }

    @Override
    public ProductCategory create(ProductCategory category) {
        if (category == null || categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
        }
        return Optional.ofNullable(category)
                .map(e -> categoryMapper.toEntity(e))
                .map(e -> categoryRepository.save(e))
                .map(e -> categoryMapper.toDTO(e))
                .orElse(null);
    }

    @Override
    public ProductCategory update(ProductCategory category) {
        ProductCategoryEntity oldCategoryEntity = categoryRepository.findById(category.getId()).orElse(null);
        if (oldCategoryEntity == null) {
            throw new HrmCommonException(HrmConstant.ERROR.CATEGORY.NOT_EXIST);
        }
        return Optional.ofNullable(oldCategoryEntity)
                .map(
                        op ->
                                op.toBuilder()
                                        .categoryName(category.getCategoryName())
                                        .categoryDescription(category.getCategoryDescription())
                                        .taxRate(category.getTaxRate())
                                        .build())
                .map(categoryRepository::save)
                .map(categoryMapper::toDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        if (StringUtils.isBlank(id.toString())) {
            return;
        }
        categoryRepository.deleteById(id);
    }
}
