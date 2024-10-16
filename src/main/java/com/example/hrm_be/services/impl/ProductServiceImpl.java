package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHPRODUCT;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.CATEGORY;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.MANUFACTURER;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.TYPE;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.UNIT_OF_MEASUREMENT;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.models.entities.StorageLocationEntity;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.repositories.BranchProductRepository;
import com.example.hrm_be.repositories.ManufacturerRepository;
import com.example.hrm_be.repositories.ProductCategoryRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.repositories.ProductTypeRepository;
import com.example.hrm_be.repositories.SpecialConditionRepository;
import com.example.hrm_be.repositories.StorageLocationRepository;
import com.example.hrm_be.repositories.UnitOfMeasurementRepository;
import com.example.hrm_be.services.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ProductServiceImpl implements ProductService {
  @Autowired private ProductRepository productRepository;
  @Autowired private AllowedProductRepository allowedProductRepository;
  @Autowired private StorageLocationRepository storageLocationRepository;
  @Autowired private ProductTypeRepository productTypeRepository;

  @Autowired private ProductCategoryRepository productCategoryRepository;

  @Autowired private UnitOfMeasurementRepository unitOfMeasurementRepository;

  @Autowired private ManufacturerRepository manufacturerRepository;
  @Autowired private ProductMapper productMapper;
  @Autowired private SpecialConditionMapper specialConditionMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private SpecialConditionRepository specialConditionRepository;
  @Autowired private StorageLocationMapper storageLocationMapper;
  @Autowired private BatchRepository batchRepository;
  @Autowired private BranchProductRepository branchProductRepository;

  @Override
  public Product getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> productRepository.findById(e).map(b -> productMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Product> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String searchType,
      String searchValue) {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

    if (searchValue != null && !searchValue.isEmpty()) {
      // Search by name if searchType is "name"
      if ("name".equalsIgnoreCase(searchType)) {
        return productRepository
            .findProductEntitiesByProductNameContainingIgnoreCase(searchValue, pageable)
            .map(dao -> productMapper.toDTO(dao));
      }
      // Search by code if searchType is "code"
      else if ("code".equalsIgnoreCase(searchType)) {
        return productRepository
            .findProductEntitiesByRegistrationCodeContainingIgnoreCase(searchValue, pageable)
            .map(dao -> productMapper.toDTO(dao));
      }
    }

    // Return all products if no search value is provided
    return productRepository.findAll(pageable).map(dao -> productMapper.toDTO(dao));
  }

  @Override
  @Transactional
  public Product create(Product product) {
    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }
    if (product.getType() != null && !productTypeRepository.existsById(product.getType().getId())) {
      throw new HrmCommonException(TYPE.NOT_EXIST);
    }

    // Check if the category exists
    if (product.getCategory() != null
        && !productCategoryRepository.existsById(product.getCategory().getId())) {
      throw new HrmCommonException(CATEGORY.NOT_EXIST);
    }

    // Check if the base unit exists
    if (product.getBaseUnit() != null
        && !unitOfMeasurementRepository.existsById(product.getBaseUnit().getId())) {
      throw new HrmCommonException(UNIT_OF_MEASUREMENT.NOT_EXIST);
    }

    // Check if the manufacturer exists
    if (product.getManufacturer() != null
        && !manufacturerRepository.existsById(product.getManufacturer().getId())) {
      throw new HrmCommonException(MANUFACTURER.NOT_EXIST);
    }
    ProductEntity savedProduct = productRepository.save(productMapper.toEntity(product));

    // Add SpecialCondition
    if (product.getSpecialConditions() != null && !product.getSpecialConditions().isEmpty()) {
      List<SpecialConditionEntity> specialConditions = new ArrayList<>();

      for (SpecialCondition specialConditionDTO : product.getSpecialConditions()) {
        // Create a new SpecialConditionEntity
        SpecialConditionEntity specialConditionEntity = new SpecialConditionEntity();
        specialConditionEntity.setHandlingInstruction(specialConditionDTO.getHandlingInstruction());
        specialConditionEntity.setConditionType(specialConditionDTO.getConditionType());

        // Set the product to this special condition
        specialConditionEntity.setProduct(savedProduct);

        // Add to the list for any future use or associations
        specialConditions.add(specialConditionMapper.toEntity(specialConditionDTO));
      }
      List<SpecialConditionEntity> savedSpecialCondition =
          specialConditionRepository.saveAll(specialConditions);
      specialConditionRepository.assignToProductByProductIdAndIds(
          savedProduct.getId(),
          savedSpecialCondition.stream()
              .map(SpecialConditionEntity::getId)
              .collect(Collectors.toList()));
    }
    // Add BranchProduct for Product
    BranchProductEntity branchProductEntity = new BranchProductEntity();

    BranchProduct branchProduct = product.getBranchProducts().get(0);
    if (branchProduct == null) {
      throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
    }
    if (branchProduct.getStorageLocation() != null) {
      StorageLocationEntity savedStorageLocation =
          storageLocationRepository.save(
              storageLocationMapper.toEntity(branchProduct.getStorageLocation()));
      branchProductEntity.setStorageLocation(savedStorageLocation);
    }

    branchProductEntity.setBranch(branchMapper.toEntity(branchProduct.getBranch()));
    branchProductEntity.setProduct(savedProduct);

    branchProductRepository.save(branchProductEntity);

    return Optional.ofNullable(savedProduct).map(e -> productMapper.toDTO(e)).orElse(null);
  }

  @Override
  public Product update(Product product) {
    ProductEntity oldProductEntity = productRepository.findById(product.getId()).orElse(null);
    if (oldProductEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    return Optional.ofNullable(oldProductEntity)
        .map(
            op ->
                op.toBuilder()
                    .productName(product.getProductName())
                    .status(product.getStatus())
                    .build())
        .map(productRepository::save)
        .map(productMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    ProductEntity productEntity = productRepository.findById(id).orElse(null);
    if (productEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    productRepository.deleteById(id);
  }

  @Override
  public Page<Product> getByPagingAndCateId(int pageNo, int pageSize, String sortBy, Long cateId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndCategoryId(cateId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public Page<Product> getByPagingAndTypeId(int pageNo, int pageSize, String sortBy, Long TypeId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndTypeId(TypeId, pageable)
        .map(dao -> productMapper.toDTO(dao));
  }

  @Override
  public List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList) {
    List<AllowedProductEntity> savedProducts = new ArrayList<>();

    for (Map<String, Object> productJson : productJsonList) {
      AllowedProductEntity product = new AllowedProductEntity();

      if (productJson.containsKey("tenThuoc")) {
        product.setProductName((String) productJson.get("tenThuoc"));
      }
      if (productJson.containsKey("id")) {
        product.setProductCode((String) productJson.get("id"));
      }
      if (productJson.containsKey("soDangKy")) {
        product.setRegistrationCode((String) productJson.get("soDangKy"));
      }
      if (productJson.containsKey("images")) {
        List<String> images = (List<String>) productJson.get("images");
        if (images != null && !images.isEmpty()) {
          product.setUrlImage(images.get(0)); // Assuming first image as URL
        }
      }
      if (productJson.containsKey("hoatChat")) {
        product.setActiveIngredient((String) productJson.get("hoatChat"));
      }
      if (productJson.containsKey("taDuoc")) {
        product.setExcipient((String) productJson.get("taDuoc"));
      }
      if (productJson.containsKey("baoChe")) {
        product.setFormulation((String) productJson.get("baoChe"));
      }

      // Save product to the database
      AllowedProductEntity savedProduct = allowedProductRepository.save(product);
      savedProducts.add(savedProduct);
    }

    return savedProducts;
  }
}
