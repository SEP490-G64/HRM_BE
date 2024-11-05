package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ProductTypeMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.entities.ProductTypeEntity;
import com.example.hrm_be.repositories.ProductTypeRepository;
import com.example.hrm_be.services.ProductTypeService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductTypeServiceImpl implements ProductTypeService {
  // Injects the repository to interact with the database
  @Autowired private ProductTypeRepository productTypeRepository;
  // Injects the mapper to convert between DTO and Entity objects
  @Autowired private ProductTypeMapper productTypeMapper;

  @Override
  public Boolean existById(Long id) {
    return productTypeRepository.existsById(id);
  }

  @Override
  public List<ProductType> getAll() {
    List<ProductTypeEntity> productTypeEntities = productTypeRepository.findAll();
    return productTypeEntities.stream()
        .map(dao -> productTypeMapper.toDTO(dao))
        .collect(Collectors.toList());
  }

  // Retrieves a ProductType by ID
  @Override
  public ProductType getById(Long id) {
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

    return Optional.ofNullable(id)
        .flatMap(e -> productTypeRepository.findById(e).map(b -> productTypeMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of ProductType entities, allowing sorting and searching by name
  @Override
  public Page<ProductType> getByPaging(int pageNo, int pageSize, String sortBy, String keyword) {
    if (pageNo < 0 || pageSize < 1) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (sortBy == null) {
      sortBy = "id";
    }
    if (!Objects.equals(sortBy, "id")
        && !Objects.equals(sortBy, "typeName")
        && !Objects.equals(sortBy, "typeDescription")) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (keyword == null) {
      keyword = "";
    }

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return productTypeRepository
        .findByTypeNameContainingIgnoreCase(keyword, pageable)
        .map(dao -> productTypeMapper.toDTO(dao));
  }

  // Creates a new ProductType
  @Override
  public ProductType create(ProductType type) {
    if (type == null || !commonValidate(type)) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

    // Validation: Ensure the name does not already exist
    if (productTypeRepository.existsByTypeName(type.getTypeName())) {
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
    if (type == null || type.getId() == null || !commonValidate(type)) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

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
    // Validation: Check if the ID is null
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.INVALID);
    }

    // Retrieve the existing type entity by ID
    ProductTypeEntity oldTypeEntity = productTypeRepository.findById(id).orElse(null);
    if (oldTypeEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.TYPE.NOT_EXIST);
    }

    // Delete the type by ID
    productTypeRepository.deleteById(id);
  }

  @Override
  public ProductType getByName(String productTypeName) {
    return Optional.ofNullable(productTypeName)
        .flatMap(e -> productTypeRepository.findByTypeName(e).map(b -> productTypeMapper.toDTO(b)))
        .orElse(null);
  }

  // This method will validate category field input values
  private boolean commonValidate(ProductType type) {
    if (type.getTypeName() == null
        || type.getTypeName().trim().isEmpty()
        || type.getTypeName().length() > 100) {
      return false;
    }
    if (type.getTypeDescription() != null && type.getTypeDescription().length() > 500) {
      return false;
    }
    return true;
  }
}
