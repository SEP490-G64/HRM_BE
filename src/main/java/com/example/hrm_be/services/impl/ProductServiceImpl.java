package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHPRODUCT;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.CATEGORY;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.MANUFACTURER;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.TYPE;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.UNIT_OF_MEASUREMENT;
import com.example.hrm_be.components.*;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.USER;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.BranchProductMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
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
import com.example.hrm_be.repositories.UserRepository;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.*;
import com.example.hrm_be.services.ProductService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import com.example.hrm_be.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.ExcelUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
  @Autowired private ProductRepository productRepository;
  @Autowired private AllowedProductRepository allowedProductRepository;
  @Autowired private StorageLocationRepository storageLocationRepository;

  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductTypeMapper productTypeMapper;

  @Autowired private ProductCategoryRepository productCategoryRepository;
  @Autowired private ProductCategoryMapper productCategoryMapper;

  @Autowired private UnitOfMeasurementRepository unitOfMeasurementRepository;
  @Autowired private UserService userService;

  @Autowired private ManufacturerRepository manufacturerRepository;
  @Autowired private ManufacturerMapper manufacturerMapper;

  @Autowired private ProductMapper productMapper;
  @Autowired private SpecialConditionMapper specialConditionMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private SpecialConditionRepository specialConditionRepository;
  @Autowired private StorageLocationMapper storageLocationMapper;
  @Autowired private BranchProductMapper branchProductMapper;
  @Autowired private BatchRepository batchRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private BranchProductRepository branchProductRepository;
  @Autowired private UnitConversionRepository unitConversionRepository;
  @Autowired private UnitConversionMapper unitConversionMapper;
  @Autowired private UnitOfMeasurementMapper unitOfMeasurementMapper;

  @Override
  public Product getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> productRepository.findById(e).map(b -> productMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<ProductBaseDTO> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      String searchType,
      String searchValue) {
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
    // Return all products if no search value is provided
    return productRepository
        .findAll(pageable)
        .map(dao -> productMapper.convertToProductBaseDTO(dao));
  }

  @Override
  @Transactional
  public Product create(Product product) {
    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    // Only allow managers to set the sell price
    if (!userService.isManager() && product.getSellPrice() != null) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
    }

    // Check if product registration code exists
    if (productRepository.existsByRegistrationCode(product.getRegistrationCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST);
    }

    // Check if type exists
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

    // Add special conditions if available
    if (product.getSpecialConditions() != null && !product.getSpecialConditions().isEmpty()) {
      List<SpecialConditionEntity> specialConditions = new ArrayList<>();
      for (SpecialCondition specialConditionDTO : product.getSpecialConditions()) {
        SpecialConditionEntity specialConditionEntity =
            specialConditionMapper.toEntity(specialConditionDTO);
        specialConditionEntity.setProduct(savedProduct);
        specialConditions.add(specialConditionEntity);
      }
      specialConditionRepository.saveAll(specialConditions);
    }

    // Add unit conversions if available
    if (product.getUnitConversions() != null && !product.getUnitConversions().isEmpty()) {
      List<UnitConversionEntity> unitConversions = new ArrayList<>();
      for (UnitConversion unitConversionDto : product.getUnitConversions()) {
        unitConversionDto.setLargerUnit(product.getBaseUnit());
        unitConversionDto.setProduct(productMapper.toDTO(savedProduct));
        unitConversions.add(unitConversionMapper.toEntity(unitConversionDto));
      }
      unitConversionRepository.saveAll(unitConversions);
    }

    // Initialize branchProducts if null and add a new BranchProduct
    if (product.getBranchProducts() == null) {
      product.setBranchProducts(new ArrayList<>());
    }

    BranchProduct branchProduct = new BranchProduct();

    // Get branch of current registered user
    String email = userService.getAuthenticatedUserEmail();
    Branch branch = userService.findLoggedInfoByEmail(email).getBranch();

    // Create a BranchProductEntity to save
    BranchProductEntity branchProductEntity = new BranchProductEntity();
    branchProductEntity.setBranch(branchMapper.toEntity(branch));

    // Handle storage location if it exists in BranchProduct
    if (branchProduct.getStorageLocation() != null) {
      StorageLocationEntity savedStorageLocation =
          storageLocationRepository.save(
              storageLocationMapper.toEntity(branchProduct.getStorageLocation()));
      branchProductEntity.setStorageLocation(savedStorageLocation);
    }

    // Set quantity details
    branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
    branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
    branchProductEntity.setQuantity(branchProduct.getQuantity());
    branchProductEntity.setProduct(savedProduct);

    branchProductRepository.save(branchProductEntity);

    return Optional.of(savedProduct).map(e -> productMapper.toDTO(e)).orElse(null);
  }

  @Override
  public Product update(Product product) {
    ProductEntity oldProductEntity = productRepository.findById(product.getId()).orElse(null);
    if (oldProductEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }

    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    // Check only manager allow to decide sell price
    if (userService.isManager()) {
      if (product.getSellPrice() != null) {
        throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
      }
    }

    if (productRepository.existsByRegistrationCode(product.getRegistrationCode())
        && !Objects.equals(product.getRegistrationCode(), oldProductEntity.getRegistrationCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST);
    }

    // Check for related entities
    if (product.getType() != null && !productTypeRepository.existsById(product.getType().getId())) {
      throw new HrmCommonException(TYPE.NOT_EXIST);
    }
    if (product.getCategory() != null
        && !productCategoryRepository.existsById(product.getCategory().getId())) {
      throw new HrmCommonException(CATEGORY.NOT_EXIST);
    }
    if (product.getBaseUnit() != null
        && !unitOfMeasurementRepository.existsById(product.getBaseUnit().getId())) {
      throw new HrmCommonException(UNIT_OF_MEASUREMENT.NOT_EXIST);
    }
    if (product.getManufacturer() != null
        && !manufacturerRepository.existsById(product.getManufacturer().getId())) {
      throw new HrmCommonException(MANUFACTURER.NOT_EXIST);
    }

    // Save updated product
    ProductEntity savedProduct = productRepository.save(productMapper.toEntity(product));

    // Handle SpecialConditions
    List<SpecialCondition> newSpecialConditions = product.getSpecialConditions();
    List<SpecialConditionEntity> oldSpecialConditions = oldProductEntity.getSpecialConditions();

    if (newSpecialConditions != null && !newSpecialConditions.isEmpty()) {
      // Prepare for add/update/delete
      List<SpecialConditionEntity> specialConditionsToAdd = new ArrayList<>();
      List<SpecialConditionEntity> specialConditionsToUpdate = new ArrayList<>();
      List<SpecialConditionEntity> specialConditionsToDelete = new ArrayList<>();

      // Create a map of old special conditions by ID for easier comparison
      Map<Long, SpecialConditionEntity> oldSpecialConditionsMap =
          oldSpecialConditions.stream()
              .collect(Collectors.toMap(SpecialConditionEntity::getId, sc -> sc));

      // Process new special conditions
      for (SpecialCondition newCondition : newSpecialConditions) {
        if (newCondition.getId() == null) {
          // New condition -> add
          SpecialConditionEntity specialConditionEntity =
              specialConditionMapper.toEntity(newCondition);
          specialConditionEntity.setProduct(savedProduct);
          specialConditionsToAdd.add(specialConditionEntity);
        } else if (oldSpecialConditionsMap.containsKey(newCondition.getId())) {
          // Existing condition -> check for updates
          SpecialCondition oldCondition =
              specialConditionMapper.toDTO(oldSpecialConditionsMap.get(newCondition.getId()));
          if (!oldCondition.equals(newCondition)) {
            // If there are changes, add to the update list
            SpecialConditionEntity updatedCondition = specialConditionMapper.toEntity(newCondition);
            updatedCondition.setProduct(savedProduct);
            specialConditionsToUpdate.add(updatedCondition);
          }
        }
      }

      // Identify old conditions to delete (those that are not in the new list)
      List<Long> newConditionIds =
          newSpecialConditions.stream()
              .filter(sc -> sc.getId() != null)
              .map(SpecialCondition::getId)
              .collect(Collectors.toList());

      specialConditionsToDelete =
          oldSpecialConditions.stream()
              .filter(oldCondition -> !newConditionIds.contains(oldCondition.getId()))
              .collect(Collectors.toList());

      // Perform the database operations for add/update/delete
      if (!specialConditionsToAdd.isEmpty()) {
        specialConditionRepository.saveAll(specialConditionsToAdd);
      }
      if (!specialConditionsToUpdate.isEmpty()) {
        specialConditionRepository.saveAll(specialConditionsToUpdate);
      }
      if (!specialConditionsToDelete.isEmpty()) {
        specialConditionRepository.deleteAll(specialConditionsToDelete);
      }
    } else {
      // If no new special conditions, delete all old ones
      if (oldSpecialConditions != null && !oldSpecialConditions.isEmpty()) {
        specialConditionRepository.deleteAll(oldSpecialConditions);
      }
    }

    // Handle Unit Conversions
    List<UnitConversion> newUnitConversions = product.getUnitConversions();
    List<UnitConversionEntity> oldUnitConversions =
        unitConversionRepository.getByProductId(savedProduct.getId());

    if (newUnitConversions != null && !newUnitConversions.isEmpty()) {
      // Prepare for add/update/delete
      List<UnitConversionEntity> unitConversionsToAdd = new ArrayList<>();
      List<UnitConversionEntity> unitConversionsToUpdate = new ArrayList<>();
      List<UnitConversionEntity> unitConversionsToDelete = new ArrayList<>();

      // Create a map of old unit conversions by ID for easier comparison
      Map<Long, UnitConversionEntity> oldUnitConversionsMap =
          oldUnitConversions.stream()
              .collect(Collectors.toMap(UnitConversionEntity::getId, uc -> uc));

      // Process new unit conversions
      for (UnitConversion newConversion : newUnitConversions) {
        if (newConversion.getId() == null) {
          // New conversion -> add
          UnitConversionEntity unitConversionEntity = unitConversionMapper.toEntity(newConversion);
          unitConversionEntity.setLargerUnit(savedProduct.getBaseUnit());
          unitConversionEntity.setProduct(savedProduct);
          unitConversionsToAdd.add(unitConversionEntity);
        } else if (oldUnitConversionsMap.containsKey(newConversion.getId())) {
          // Existing conversion -> check for updates
          UnitConversionEntity updatedConversion = unitConversionMapper.toEntity(newConversion);
          updatedConversion.setLargerUnit(savedProduct.getBaseUnit());
          updatedConversion.setProduct(savedProduct);
          unitConversionsToUpdate.add(updatedConversion);
        }
      }

      // Identify old unit conversions to delete (those that are not in the new list)
      List<Long> newConversionIds =
          newUnitConversions.stream()
              .filter(sc -> sc.getId() != null)
              .map(UnitConversion::getId)
              .collect(Collectors.toList());

      unitConversionsToDelete =
          oldUnitConversions.stream()
              .filter(oldCondition -> !newConversionIds.contains(oldCondition.getId()))
              .collect(Collectors.toList());

      // Perform the database operations for add/update/delete
      if (!unitConversionsToAdd.isEmpty()) {
        unitConversionRepository.saveAll(unitConversionsToAdd);
      }
      if (!unitConversionsToUpdate.isEmpty()) {
        unitConversionRepository.saveAll(unitConversionsToUpdate);
      }
      if (!unitConversionsToDelete.isEmpty()) {
        unitConversionRepository.deleteAll(unitConversionsToDelete);
      }
    } else {
      // If no new unit conversions, delete all old ones
      if (oldUnitConversions != null && !oldUnitConversions.isEmpty()) {
        unitConversionRepository.deleteAll(oldUnitConversions);
      }
    }

    List<BranchProductEntity> oldBranchProducts = oldProductEntity.getBranchProducs();
    List<Long> oldBranchProductsIds =
        oldBranchProducts.stream()
            .map(branchProduct -> branchProduct.getBranch().getId())
            .collect(Collectors.toList());

    BranchProduct branchProduct = product.getBranchProducts().get(0);
    if (branchProduct == null) {
      throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
    }

    // Get branch of current registered user
    String email = userService.getAuthenticatedUserEmail();
    Branch branch = userService.findLoggedInfoByEmail(email).getBranch();

    // Check if branch product exist or not
    // S1: Not exist -> add to branchProduct list
    if (!oldBranchProductsIds.contains(branch.getId())) {
      // Update BranchProduct for Product
      BranchProductEntity branchProductEntity = new BranchProductEntity();
      branchProductEntity.setBranch(branchMapper.toEntity(branch));

      StorageLocationEntity storageLocationEntity =
          storageLocationMapper.toEntity(branchProduct.getStorageLocation());
      storageLocationEntity.setId(null);
      StorageLocationEntity savedStorageLocation =
          storageLocationRepository.save(storageLocationEntity);
      branchProductEntity.setStorageLocation(savedStorageLocation);

      branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
      branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
      branchProductEntity.setQuantity(branchProduct.getQuantity());
      branchProductEntity.setProduct(savedProduct);

      oldBranchProducts.add(branchProductEntity);
    }
    // S2: Exist -> Find by branch and update
    else {
      int index = oldBranchProductsIds.indexOf(branch.getId());
      BranchProductEntity branchProductEntity = oldBranchProducts.get(index);
      branchProductEntity.setBranch(branchMapper.toEntity(branch));
      if (branchProduct.getStorageLocation() != null) {
        StorageLocationEntity savedStorageLocation =
            storageLocationRepository.save(
                storageLocationMapper.toEntity(branchProduct.getStorageLocation()));
        branchProductEntity.setStorageLocation(savedStorageLocation);
      }

      branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
      branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
      branchProductEntity.setQuantity(branchProduct.getQuantity());
      branchProductEntity.setProduct(savedProduct);
    }

    branchProductRepository.saveAll(oldBranchProducts);

    return productMapper.toDTO(savedProduct);
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
  public Page<Product> getByPagingAndTypeId(int pageNo, int pageSize, String sortBy, Long typeId) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    // Tìm kiếm theo tên
    return productRepository
        .findProductByPagingAndTypeId(typeId, pageable)
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

  @Override
  public List<AllowedProductEntity> getAllowProducts(String searchStr) {
    return allowedProductRepository.findAllByProductNameContainsIgnoreCase(searchStr);
  }

  @Override
  public Page<BranchProduct> searchProducts(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      Optional<String> keyword,
      Optional<Long> manufacturerId,
      Optional<Long> categoryId,
      Optional<Long> typeId,
      Optional<String> status,
      Optional<Long> branchId) {
    Specification<BranchProductEntity> specification = Specification.where(null);
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));
    // Filter by branchId if present
    if (branchId.isPresent()) {
      Optional<Long> finalBranchId = branchId;
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("branch").get("id"), finalBranchId.get()));
    } else {
      String loggedEmail = userService.getAuthenticatedUserEmail();
      branchId = userRepository.findBranchIdByUserEmail(loggedEmail);
      if (branchId.isEmpty()) {
        throw new HrmCommonException(USER.NOT_ASSIGNED_BRANCH);
      }
      Optional<Long> finalBranchId1 = branchId;
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("branch").get("id"), finalBranchId1.get()));
    }

    // Keyword search for product name, registration code, and active ingredient
    if (keyword.isPresent()) {
      String searchPattern = "%" + keyword.get().toLowerCase() + "%";
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.or(
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("product").get("productName")),
                          searchPattern),
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("product").get("registrationCode")),
                          searchPattern),
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("product").get("activeIngredient")),
                          searchPattern)));
    }

    if (manufacturerId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(
                      root.get("product").get("manufacturer").get("id"), manufacturerId.get()));
    }

    // Filter by categoryId
    if (categoryId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(
                      root.get("product").get("category").get("id"), categoryId.get()));
    }

    // Filter by typeId
    if (typeId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("product").get("type").get("id"), typeId.get()));
    }

    // Filter by status
    if (status.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("product").get("status"), status.get()));
    }
    return branchProductRepository
        .findAll(specification, pageable)
        .map(branchProductMapper::toDTOWithProduct);
  }

  @Override
  public List<String> importFile(MultipartFile file) {
    // Mapper to convert each Excel row into a Product object
    Function<Row, Product> rowMapper =
        (Row row) -> {
          Product product = new Product();
          try {
            // Mapping fields from the Excel row to the Product object
            product.setProductCode(
                row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null);

            // If the product code exists, populate product information from AllowedProductEntity
            if (product.getProductCode() != null) {
              AllowedProductEntity allowedProduct =
                  allowedProductRepository.findByRegistrationCode(product.getProductCode());
              if (allowedProduct != null) {
                product.setProductName(allowedProduct.getProductName());
                product.setRegistrationCode(allowedProduct.getRegistrationCode());
                product.setActiveIngredient(allowedProduct.getActiveIngredient());
                product.setExcipient(allowedProduct.getExcipient());
                product.setFormulation(allowedProduct.getFormulation());
              }
            }

            // Set category if found
            if (row.getCell(2) != null) {
              String categoryName = row.getCell(2).getStringCellValue();
              if (categoryName != null) {
                product.setCategory(
                    productCategoryRepository
                        .findByCategoryName(categoryName)
                        .map(productCategoryMapper::toDTO)
                        .orElse(null));
              }
            }

            // Set type if found
            if (row.getCell(3) != null) {
              String typeName = row.getCell(3).getStringCellValue();
              if (typeName != null) {
                product.setType(
                    productTypeRepository
                        .findByTypeName(typeName)
                        .map(productTypeMapper::toDTO)
                        .orElse(null));
              }
            }

            // Set base unit if found
            if (row.getCell(4) != null) {
              String measurementName = row.getCell(4).getStringCellValue();
              if (measurementName != null) {
                product.setBaseUnit(
                    unitOfMeasurementRepository
                        .findByUnitName(measurementName)
                        .map(unitOfMeasurementMapper::toDTO)
                        .orElse(null));
              }
            }

            // Set manufacturer if found
            if (row.getCell(5) != null) {
              String manufacturerName = row.getCell(5).getStringCellValue();
              if (manufacturerName != null) {
                product.setManufacturer(
                    manufacturerRepository
                        .findByManufacturerName(manufacturerName)
                        .map(manufacturerMapper::toDTO)
                        .orElse(null));
              }
            }

          } catch (Exception e) {
            throw new RuntimeException("Error parsing row: " + e.getMessage(), e);
          }
          return product;
        };

    List<String> errors = new ArrayList<>();
    List<Product> productsToSave = new ArrayList<>();

    // Read and validate each row from the Excel file
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);

      for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
          try {
            Product product = rowMapper.apply(row); // Convert row to Product object

            List<String> rowErrors = new ArrayList<>();

            // Validate the Product object
            if (product.getProductName() == null || product.getProductName().isEmpty()) {
              rowErrors.add("Product name is missing at row " + (rowIndex + 1));
            }
            // You can add more validations as needed, like checking mandatory fields, etc.

            if (rowErrors.isEmpty()) {
              productsToSave.add(product);
            } else {
              errors.addAll(rowErrors);
            }
          } catch (Exception e) {
            errors.add("Error parsing row " + (rowIndex + 1) + ": " + e.getMessage());
          }
        }
      }
    } catch (IOException e) {
      errors.add("Failed to parse Excel file: " + e.getMessage());
    }

    // Save all valid products to the database
    if (!productsToSave.isEmpty()) {
      for (Product product : productsToSave) {
        create(product); // Assuming `create` method persists the Product entity
      }
    }

    return errors; // Return the list of errors
  }

  @Override
  public ByteArrayInputStream exportFile() throws IOException {
    String[] headers = {
      "Regation Code",
      "Product Name",
      "Active Ingredient",
      "Excipient",
      "Formulation",
      "Category",
      "Type",
      "Base Unit",
      "Manufacturer",
      "Sell Price"
    };

    // Row mapper to convert a Product object to a list of cell values
    Function<ProductBaseDTO, List<String>> rowMapper =
        (ProductBaseDTO product) -> {
          List<String> cellValues = new ArrayList<>();
          cellValues.add(
              product.getRegistrationCode() != null ? product.getRegistrationCode() : "");
          cellValues.add(product.getProductName() != null ? product.getProductName() : "");
          cellValues.add(
              product.getActiveIngredient() != null ? product.getActiveIngredient() : "");
          cellValues.add(product.getExcipient() != null ? product.getExcipient() : "");
          cellValues.add(product.getFormulation() != null ? product.getFormulation() : "");
          cellValues.add(product.getCategoryName() != null ? product.getCategoryName() : "");
          cellValues.add(product.getTypeName() != null ? product.getTypeName() : "");
          cellValues.add(product.getBaseUnit() != null ? product.getBaseUnit() : "");
          cellValues.add(
              product.getManufacturerName() != null ? product.getManufacturerName() : "");
          cellValues.add(product.getSellPrice() != null ? product.getSellPrice().toString() : "");

          return cellValues;
        };

    // Fetch product data
    List<ProductBaseDTO> products =
        productRepository.findAll().stream()
            .map(productMapper::convertToProductBaseDTO)
            .collect(Collectors.toList());

    // Export data using utility
    try {
      return ExcelUtility.exportToExcelWithErrors(products, headers, rowMapper);
    } catch (IOException e) {
      throw new RuntimeException("Error exporting product data to Excel", e);
    }
  }

  @Transactional(readOnly = true)
  public List<ProductEntity> getProductWithBranchProducts(Long branchId) {
    // Fetch the product along with only the BranchProductEntity related to the given branchId
    return productRepository.findProductByBranchId(branchId);
  }
}
