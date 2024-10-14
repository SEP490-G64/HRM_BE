package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductTypeMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import com.example.hrm_be.repositories.ProductTypeRepository;
import com.example.hrm_be.services.ProductTypeService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ProductTypeServiceImpl implements ProductTypeService {
  // Injects the repository to interact with the database
  @Autowired private ProductTypeRepository productTypeRepository;
  // Injects the mapper to convert between DTO and Entity objects
  @Autowired private ProductTypeMapper productTypeMapper;

  // Retrieves a ProductType by ID
  @Override
  public ProductType getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> productTypeRepository.findById(e).map(b -> productTypeMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of ProductType entities, allowing sorting and searching by name
  @Override
  public Page<ProductType> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return productTypeRepository
        .findByTypeNameContainingIgnoreCase(name, pageable)
        .map(dao -> productTypeMapper.toDTO(dao));
  }

  // Creates a new ProductType
  @Override
  public ProductType create(ProductType type) {
    // Validation: Ensure the type is not null and the name does not already exist
    if (type == null || productTypeRepository.existsByTypeName(type.getTypeName())) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.EXIST);
    }

    // Convert DTO to entity, save it, then convert the saved entity back to DTO
    return Optional.ofNullable(type)
        .map(e -> productTypeMapper.toEntity(e))
        .map(e -> productTypeRepository.save(e))
        .map(e -> productTypeMapper.toDTO(e))
        .orElse(null);
  }

  // Updates an existing ProductType
  @Override
  public ProductType update(ProductType type) {
    // Retrieve the existing type entity by ID
    ProductTypeEntity oldTypeEntity = productTypeRepository.findById(type.getId()).orElse(null);
    if (oldTypeEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.NOT_EXIST);
    }

    // Check if product type name exist except current type
    if (productTypeRepository.existsByTypeName(type.getTypeName())
        && !Objects.equals(type.getTypeName(), oldTypeEntity.getTypeName())) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.EXIST);
    }

    // Update the fields of the existing type entity, save it, and convert it back to DTO
    return Optional.ofNullable(oldTypeEntity)
        .map(
            op ->
                op.toBuilder()
                    .typeName(type.getTypeName())
                    .typeDescription(type.getTypeDescription())
                    .build())
        .map(productTypeRepository::save)
        .map(productTypeMapper::toDTO)
        .orElse(null);
  }

  // Deletes a ProductType by ID
  @Override
  public void delete(Long id) {
    // Validation: Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Retrieve the existing type entity by ID
    ProductTypeEntity oldTypeEntity = productTypeRepository.findById(id).orElse(null);
    if (oldTypeEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.NOT_EXIST);
    }

    // Delete the type by ID
    productTypeRepository.deleteById(id);
  }
}
