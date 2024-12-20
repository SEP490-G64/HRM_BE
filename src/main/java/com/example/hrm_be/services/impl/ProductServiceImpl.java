package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCHPRODUCT;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.CATEGORY;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.MANUFACTURER;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.REQUEST;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.TYPE;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.UNIT_OF_MEASUREMENT;
import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.components.*;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.components.StorageLocationMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.models.entities.BranchProductEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.SpecialConditionEntity;
import com.example.hrm_be.models.responses.AuditHistory;
import com.example.hrm_be.repositories.*;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.ExcelUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
  @Autowired private InboundBatchDetailRepository inboundBatchDetailRepository;
  @Autowired private InboundDetailsRepository inboundDetailsRepository;
  @Autowired private AllowedProductRepository allowedProductRepository;

  @Autowired private UserService userService;
  @Autowired private ProductCategoryService productCategoryService;
  @Autowired private ProductTypeService productTypeService;
  @Autowired private ManufacturerService manufacturerService;
  @Autowired private UnitOfMeasurementService unitOfMeasurementService;
  @Autowired private BranchProductService branchProductService;
  @Autowired private StorageLocationService storageLocationService;
  @Autowired private SpecialConditionService specialConditionService;
  @Autowired private UnitConversionService unitConversionService;

  @Autowired private ProductMapper productMapper;
  @Autowired private InboundDetailsMapper inboundDetailsMapper;
  @Autowired private InboundBatchDetailMapper inboundBatchDetailMapper;
  @Autowired private SpecialConditionMapper specialConditionMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private StorageLocationMapper storageLocationMapper;
  @Autowired private OutboundDetailMapper outboundDetailMapper;
  @Autowired private OutboundProductDetailMapper outboundProductDetailMapper;
  @Autowired private UnitConversionMapper unitConversionMapper;
  @Autowired private AllowedProductService allowedProductService;
  @Lazy @Autowired private InboundDetailsService inboundDetailsService;
  @Lazy @Autowired private OutboundDetailService outboundDetailService;
  @Lazy @Autowired private OutboundProductDetailService outboundProductDetailService;
  @Lazy @Autowired private InboundBatchDetailService inboundBatchDetailService;
  @Autowired private BatchRepository batchRepository;
  @Autowired private NotificationService notificationService;

  @Override
  public Product getById(Long id) {
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.ID_NULL);
    }
    ProductEntity productEntity = productRepository.findById(id).orElse(null);
    if (productEntity == null || productEntity.getStatus() == ProductStatus.DA_XOA) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    return productMapper.convertToDTOWithoutProductInBranchProduct(productEntity);
  }

  @Override
  @Transactional
  public Product create(Product product) {
    if (product == null || !commonValidate(product)) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    // Check only manager allow to sell price
    if (!userService.isManager()) {
      if (product.getSellPrice() != null) {
        throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_ALLOWED);
      }
    }

    // Check if product registration code exists
    if (productRepository.existsByRegistrationCode(product.getRegistrationCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST);
    }

    // Check if type exists
    if (product.getType() != null && !productTypeService.existById(product.getType().getId())) {
      throw new HrmCommonException(TYPE.NOT_EXIST);
    }

    // Check if the category exists
    if (product.getCategory() != null
        && !productCategoryService.existById(product.getCategory().getId())) {
      throw new HrmCommonException(CATEGORY.NOT_EXIST);
    }

    // Check if the base unit exists
    if (product.getBaseUnit() != null
        && !unitOfMeasurementService.existById(product.getBaseUnit().getId())) {
      throw new HrmCommonException(UNIT_OF_MEASUREMENT.NOT_EXIST);
    }

    // Check if the manufacturer exists
    if (product.getManufacturer() != null
        && !manufacturerService.existById(product.getManufacturer().getId())) {
      throw new HrmCommonException(MANUFACTURER.NOT_EXIST);
    }

    ProductEntity savedProduct = productRepository.save(productMapper.toEntity(product));

    // Add SpecialCondition
    if (product.getSpecialConditions() != null && !product.getSpecialConditions().isEmpty()) {
      List<SpecialConditionEntity> specialConditions = new ArrayList<>();

      for (SpecialCondition specialConditionDTO : product.getSpecialConditions()) {
        SpecialConditionEntity specialConditionEntity =
            specialConditionMapper.toEntity(specialConditionDTO);

        // Set the product to this special condition
        specialConditionEntity.setProduct(savedProduct);

        // Add to the list for any future use or associations
        specialConditions.add(specialConditionEntity);
      }
      specialConditionService.saveAll(specialConditions);
    }

    // Add Unit Conversion
    if (product.getUnitConversions() != null && !product.getUnitConversions().isEmpty()) {
      List<UnitConversionEntity> unitConversions = new ArrayList<>();

      for (UnitConversion unitConversionDto : product.getUnitConversions()) {
        UnitConversionEntity unitConversionEntity =
            unitConversionMapper.toEntity(unitConversionDto);

        unitConversionEntity.setProduct(savedProduct);
        unitConversionEntity.setLargerUnit(savedProduct.getBaseUnit());
        unitConversions.add(unitConversionEntity);
      }
      unitConversionService.saveAll(unitConversions);
    }

    // Add BranchProduct for Product
    BranchProduct branchProductEntity = new BranchProduct();

    BranchProduct branchProduct = product.getBranchProducts().get(0);
    if (branchProduct == null) {
      throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
    }

    // Get branch of current registered user
    String email = userService.getAuthenticatedUserEmail();
    Branch branch = userService.findLoggedInfoByEmail(email).getBranch();

    branchProductEntity.setBranch(branch);
    if (branchProduct.getStorageLocation() != null) {
      StorageLocation savedStorageLocation =
          storageLocationService.save(branchProduct.getStorageLocation());
      branchProductEntity.setStorageLocation(savedStorageLocation);
    }

    branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
    branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
    branchProductEntity.setQuantity(branchProduct.getQuantity());
    branchProductEntity.setProduct(productMapper.toDTO(savedProduct));

    branchProductService.save(branchProductEntity);

    return Optional.ofNullable(savedProduct).map(e -> productMapper.toDTO(e)).orElse(null);
  }

  @Override
  public Product update(Product product) {
    if (product == null || !commonValidate(product)) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }
    ProductEntity oldProductEntity = productRepository.findById(product.getId()).orElse(null);
    if (oldProductEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }

    if (productRepository.existsByRegistrationCode(product.getRegistrationCode())
        && !Objects.equals(product.getRegistrationCode(), oldProductEntity.getRegistrationCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST);
    }

    // Check for related entities
    if (product.getType() != null && !productTypeService.existById(product.getType().getId())) {
      throw new HrmCommonException(TYPE.NOT_EXIST);
    }
    if (product.getCategory() != null
        && !productCategoryService.existById(product.getCategory().getId())) {
      throw new HrmCommonException(CATEGORY.NOT_EXIST);
    }
    if (product.getBaseUnit() != null
        && !unitOfMeasurementService.existById(product.getBaseUnit().getId())) {
      throw new HrmCommonException(UNIT_OF_MEASUREMENT.NOT_EXIST);
    }
    if (product.getManufacturer() != null
        && !manufacturerService.existById(product.getManufacturer().getId())) {
      throw new HrmCommonException(MANUFACTURER.NOT_EXIST);
    }

    // Save updated product
    ProductEntity savedProduct = productRepository.save(productMapper.toEntity(product));

    // Handle SpecialConditions
    List<SpecialCondition> newSpecialConditions = product.getSpecialConditions();
    List<SpecialConditionEntity> oldSpecialConditions = oldProductEntity.getSpecialConditions();
    if (oldSpecialConditions == null) {
      oldSpecialConditions = new ArrayList<>();
    }

    if (newSpecialConditions != null && !newSpecialConditions.isEmpty()) {
      // Prepare for add/update/delete
      List<SpecialConditionEntity> specialConditionsToAddOrUpdate = new ArrayList<>();
      List<SpecialConditionEntity> specialConditionsToDelete = new ArrayList<>();

      // Create a map of old special conditions by ID for easier comparison
      Map<Long, SpecialConditionEntity> oldSpecialConditionsMap =
          oldSpecialConditions.stream()
              .collect(Collectors.toMap(SpecialConditionEntity::getId, sc -> sc));

      // Process new special conditions
      for (SpecialCondition newCondition : newSpecialConditions) {
        if (newCondition.getId() == null
            || oldSpecialConditionsMap.containsKey(newCondition.getId())) {
          // New condition -> add
          // Old condition -> update
          SpecialConditionEntity specialConditionEntity =
              specialConditionMapper.toEntity(newCondition);
          specialConditionEntity.setProduct(savedProduct);
          specialConditionsToAddOrUpdate.add(specialConditionEntity);
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
      if (!specialConditionsToAddOrUpdate.isEmpty()) {
        specialConditionService.saveAll(specialConditionsToAddOrUpdate);
      }
      if (!specialConditionsToDelete.isEmpty()) {
        specialConditionService.deleteAll(specialConditionsToDelete);
      }
    } else {
      // If no new special conditions, delete all old ones
      if (!oldSpecialConditions.isEmpty()) {
        specialConditionService.deleteAll(oldSpecialConditions);
      }
    }

    // Handle Unit Conversions
    List<UnitConversion> newUnitConversions = product.getUnitConversions();
    List<UnitConversionEntity> oldUnitConversions =
        unitConversionService.getByProductId(savedProduct.getId());

    if (newUnitConversions != null && !newUnitConversions.isEmpty()) {
      // Prepare for add/update/delete
      List<UnitConversionEntity> unitConversionsToAddOrUpdate = new ArrayList<>();
      List<UnitConversionEntity> unitConversionsToDelete = new ArrayList<>();

      // Create a map of old unit conversions by ID for easier comparison
      Map<Long, UnitConversionEntity> oldUnitConversionsMap =
          oldUnitConversions.stream()
              .collect(Collectors.toMap(UnitConversionEntity::getId, uc -> uc));

      // Process new unit conversions
      for (UnitConversion newConversion : newUnitConversions) {
        if (newConversion.getId() == null
            || oldUnitConversionsMap.containsKey(newConversion.getId())) {
          // New conversion -> add
          UnitConversionEntity unitConversionEntity = unitConversionMapper.toEntity(newConversion);
          unitConversionEntity.setLargerUnit(savedProduct.getBaseUnit());
          unitConversionEntity.setProduct(savedProduct);
          unitConversionsToAddOrUpdate.add(unitConversionEntity);
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
      if (!unitConversionsToAddOrUpdate.isEmpty()) {
        unitConversionService.saveAll(unitConversionsToAddOrUpdate);
      }
      if (!unitConversionsToDelete.isEmpty()) {
        unitConversionService.deleteAll(unitConversionsToDelete);
      }
    } else {
      // If no new unit conversions, delete all old ones
      if (oldUnitConversions != null && !oldUnitConversions.isEmpty()) {
        unitConversionService.deleteAll(oldUnitConversions);
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

      StorageLocation storageLocation = branchProduct.getStorageLocation();
      storageLocation.setId(null);
      StorageLocation savedStorageLocation = storageLocationService.save(storageLocation);
      branchProductEntity.setStorageLocation(storageLocationMapper.toEntity(savedStorageLocation));

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
        StorageLocation savedStorageLocation =
            storageLocationService.save(branchProduct.getStorageLocation());
        branchProductEntity.setStorageLocation(
            storageLocationMapper.toEntity(savedStorageLocation));
      }

      branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
      branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
      branchProductEntity.setQuantity(branchProduct.getQuantity());
      branchProductEntity.setProduct(savedProduct);
    }

    branchProductService.saveAll(oldBranchProducts);

    return productMapper.toDTO(savedProduct);
  }

  @Override
  public Product updateInboundPrice(Product product) {
    ProductEntity unsavedProduct = productRepository.findById(product.getId()).orElse(null);
    assert unsavedProduct != null;
    unsavedProduct.setInboundPrice(product.getInboundPrice());
    ProductEntity saved = productRepository.save(unsavedProduct);
    return productMapper.toDTO(saved);
  }

  @Override
  public void delete(Long id) {
    Optional<ProductEntity> productEntityOpt = productRepository.findById(id);
    if (!productEntityOpt.isPresent()
        || productEntityOpt.get().getStatus() == ProductStatus.DA_XOA) {
      throw new HrmCommonException(HrmConstant.ERROR.PRODUCT.NOT_EXIST);
    }
    productRepository.updateProductStatus(ProductStatus.DA_XOA, id);
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
  public Page<ProductBaseDTO> searchProducts(
      int pageNo,
      int pageSize,
      String sortBy,
      String sortDirection,
      Optional<String> keyword,
      Optional<Long> manufacturerId,
      Optional<Long> categoryId,
      Optional<Long> typeId,
      Optional<String> status) {
    Specification<ProductEntity> specification = Specification.where(null);
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

    specification =
        specification.and(
            (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), "DA_XOA"));

    // Keyword search for product name, registration code, and active ingredient
    if (keyword.isPresent()) {
      String searchPattern = "%" + keyword.get().toLowerCase() + "%";
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.or(
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("productName")), searchPattern),
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("registrationCode")), searchPattern),
                      criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("activeIngredient")), searchPattern)));
    }

    if (manufacturerId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("manufacturer").get("id"), manufacturerId.get()));
    }

    // Filter by categoryId
    if (categoryId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("category").get("id"), categoryId.get()));
    }

    // Filter by typeId
    if (typeId.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("type").get("id"), typeId.get()));
    }

    // Filter by status
    if (status.isPresent()) {
      specification =
          specification.and(
              (root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("status"), status.get()));
    }

    return productRepository
        .findAll(specification, pageable)
        .map(productMapper::convertToProductBaseDTO);
  }

  @Override
  public List<String> importFile(MultipartFile file) {
    // Mapper to convert each Excel row into a Product object
    Function<Row, Product> rowMapper =
        (Row row) -> {
          Product product = new Product();
          try {
            // Map fields from the Excel row to the Product object
            product.setRegistrationCode(
                row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null);

            // Populate product information from AllowedProductEntity if registration code exists
            if (product.getRegistrationCode() != null) {
              AllowedProductEntity allowedProduct =
                  allowedProductService.getAllowedProductByCode(product.getRegistrationCode());
              if (allowedProduct != null) {
                product.setProductName(allowedProduct.getProductName());
                product.setRegistrationCode(allowedProduct.getRegistrationCode());
                product.setActiveIngredient(allowedProduct.getActiveIngredient());
                product.setExcipient(allowedProduct.getExcipient());
                product.setFormulation(allowedProduct.getFormulation());
              }
            }

            if (row.getCell(2) != null) {
              String categoryName = row.getCell(2).getStringCellValue();
              product.setCategory(
                  categoryName != null
                      ? productCategoryService.findByCategoryName(categoryName)
                      : null);
            }

            // Set type if found
            if (row.getCell(3) != null) {
              String typeName = row.getCell(3).getStringCellValue();
              product.setType(typeName != null ? productTypeService.getByName(typeName) : null);
            }

            // Set base unit if found
            if (row.getCell(4) != null) {
              String measurementName = row.getCell(4).getStringCellValue();
              product.setBaseUnit(
                  measurementName != null
                      ? unitOfMeasurementService.getByName(measurementName)
                      : null);
            }

            // Set manufacturer if found
            if (row.getCell(5) != null) {
              String manufacturerName = row.getCell(5).getStringCellValue();
              product.setManufacturer(
                  manufacturerName != null
                      ? manufacturerService.getByName(manufacturerName)
                      : null);
            }
            BranchProduct branchProduct = new BranchProduct();

            // Set minimum quantity if found
            if (row.getCell(6) != null) {
              Cell cell = row.getCell(6);
              if (cell.getCellType() == CellType.NUMERIC) {
                int minQuantity = (int) cell.getNumericCellValue(); // Lấy giá trị kiểu số
                branchProduct.setMinQuantity(minQuantity);
              } else {
                int minQuantity = Integer.parseInt(cell.getStringCellValue());
                branchProduct.setMinQuantity(minQuantity);
              }
            }

            // Set maximum quantity if found
            if (row.getCell(7) != null) {
              Cell cell = row.getCell(7);
              if (cell.getCellType() == CellType.NUMERIC) {
                int maxQuantity = (int) cell.getNumericCellValue();
                branchProduct.setMaxQuantity(maxQuantity);
              } else {
                int maxQuantity = Integer.parseInt(cell.getStringCellValue());
                branchProduct.setMaxQuantity(maxQuantity);
              }
            }

            // Set storage location if found
            StorageLocation storageLocation = new StorageLocation();
            if (row.getCell(8) != null) {
              Cell cell = row.getCell(8);
              if (cell.getCellType() == CellType.STRING) {
                String storage = cell.getStringCellValue();
                storageLocation.setShelfName(storage);
                branchProduct.setStorageLocation(storageLocation);
              }
            }

            // Set the branch product for the product
            product.setBranchProducts(Collections.singletonList(branchProduct));

          } catch (Exception e) {
            log.debug("This is custom log for parsing row: " + e.getMessage());
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

            // Validate product name
            if (product.getProductName() == null || product.getProductName().isEmpty()) {
              rowErrors.add("Product name is missing at row " + (rowIndex + 1));
            }

            // Validate registration name
            if (product.getRegistrationCode() == null || product.getRegistrationCode().isEmpty()) {
              rowErrors.add("Registration Code is missing at row " + (rowIndex + 1));
            }

            // Check if registration code already exists
            if (product.getRegistrationCode() != null
                && productRepository.existsByRegistrationCode(product.getRegistrationCode())) {
              rowErrors.add("Registration Code already exists at row " + (rowIndex + 1));
            }

            // Validate category
            if (product.getCategory() == null) {
              rowErrors.add("Category is missing at row " + (rowIndex + 1));
            }

            // Validate type
            if (product.getType() == null) {
              rowErrors.add("Type is missing at row " + (rowIndex + 1));
            }

            // Validate base unit
            if (product.getBaseUnit() == null) {
              rowErrors.add("Base Unit is missing at row " + (rowIndex + 1));
            }

            // Validate manufacturer
            if (product.getManufacturer() == null) {
              rowErrors.add("Manufacturer is missing at row " + (rowIndex + 1));
            }

            // Validate branch products
            if (product.getBranchProducts() == null || product.getBranchProducts().isEmpty()) {
              rowErrors.add("Branch Products are missing at row " + (rowIndex + 1));
            }

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
    // Save all valid products to the database if no errors occurred
    log.debug("This is custom debug: " + productsToSave.size());
    try {
      for (Product product : productsToSave) {
        log.debug("This is custom debug for branch product: " + product.getBranchProducts().size());
        // Convert product to entity and save it
        product.setStatus(ProductStatus.CON_HANG);
        ProductEntity savedProduct = productRepository.save(productMapper.toEntity(product));

        // Get the branch of the current authenticated user
        String email = userService.getAuthenticatedUserEmail();
        Branch userBranch = userService.findLoggedInfoByEmail(email).getBranch();
        if (userBranch == null) {
          log.debug("This is custom debug for branch: yes");
          throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
        }

        // Retrieve the first BranchProduct
        List<BranchProduct> branchProducts = product.getBranchProducts();
        if (branchProducts == null || branchProducts.isEmpty()) {
          throw new HrmCommonException(BRANCHPRODUCT.NOT_EXIST);
        }

        BranchProduct branchProduct = branchProducts.get(0);
        BranchProduct branchProductEntity = new BranchProduct();

        // Set branch information
        branchProductEntity.setBranch(userBranch);

        // Handle storage location
        if (branchProduct.getStorageLocation() != null) {
          StorageLocation savedStorageLocation =
              storageLocationService.save(branchProduct.getStorageLocation());
          branchProductEntity.setStorageLocation(savedStorageLocation);
        }

        // Set other attributes
        branchProductEntity.setMinQuantity(branchProduct.getMinQuantity());
        branchProductEntity.setMaxQuantity(branchProduct.getMaxQuantity());
        branchProductEntity.setQuantity(branchProduct.getQuantity());
        branchProductEntity.setProduct(productMapper.toDTO(savedProduct));

        // Save the BranchProduct entity
        branchProductService.save(branchProductEntity);
      }
    } catch (Exception e) {
      errors.add("Error saving products: " + e.getMessage());
      //      throw new RuntimeException(
      //          "Transaction failed, rolling back due to error.", e); // Marks transaction for
      // rollback
    }

    return errors; // Return the list of errors
  }

  @Override
  public ByteArrayInputStream exportFile() throws IOException {
    String[] headers = {
      "Mã đăng ký",
      "Tên sản phẩm",
      "Hoạt chất",
      "Bào chế",
      "Tá dược",
      "Nhóm sản phẩm",
      "Loại sản phẩm",
      "Đơn vị cơ sở",
      "Nhà sản xuất",
      "Giá nhập",
      "Giá bán"
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
          cellValues.add(
              product.getInboundPrice() != null ? product.getInboundPrice().toString() : "");
          cellValues.add(product.getSellPrice() != null ? product.getSellPrice().toString() : "");

          return cellValues;
        };

    // Fetch product data
    List<ProductBaseDTO> products =
        productRepository.findAll().stream()
            .filter(product -> !ProductStatus.DA_XOA.equals(product.getStatus()))
            .map(productMapper::convertToProductBaseDTO)
            .collect(Collectors.toList());

    // Export data using utility
    try {
      return ExcelUtility.exportToExcelWithErrors(products, headers, rowMapper);
    } catch (IOException e) {
      throw new RuntimeException("Error exporting product data to Excel", e);
    }
  }

  @Override
  public List<ProductSupplierDTO> getAllProductsBySupplier(Long supplierId, String productName) {
    return productRepository.findProductBySupplierAndName(supplierId, productName).stream()
        .map(productMapper::convertToProductSupplier)
        .collect(Collectors.toList());
  }

  @Override
  public Product addProductInInbound(ProductInbound productInbound) {
    Product product =
        productRepository
            .findByRegistrationCode(productInbound.getRegistrationCode())
            .map(productMapper::convertToBaseInfo)
            .orElseGet(
                () -> {
                  Product newProduct = new Product();
                  newProduct.setRegistrationCode(productInbound.getRegistrationCode());
                  newProduct.setProductName(productInbound.getProductName());
                  newProduct.setBaseUnit(
                      productInbound.getBaseUnit() != null ? productInbound.getBaseUnit() : null);
                  return productMapper.toDTO(
                      productRepository.save(productMapper.toEntity(newProduct)));
                });
    return product;
  }

  @Override
  public List<ProductBaseDTO> getProductInBranch(
      Long branchId, String keyword, Boolean checkValid, Long supplierId) {
    return productRepository
        .searchProductByBranchId(branchId, keyword, checkValid, supplierId)
        .stream()
        .map(
            entity ->
                productMapper.convertToProductForSearchInNotes(
                    entity, branchId)) // Pass branchId to the mapper
        .collect(Collectors.toList());
  }

  @Override
  public List<ProductBaseDTO> getBranchProduct(
      Long branchId, String keyword, Boolean checkValid, Long supplierId, Boolean withSellprice) {
    if (withSellprice == null || !withSellprice) {
      return productRepository
          .searchProductByBranchId(branchId, keyword, checkValid, supplierId)
          .stream()
          .map(
              entity ->
                  productMapper.convertToBranchProduct(
                      entity, branchId)) // Pass branchId to the mapper
          .collect(Collectors.toList());
    } else {
      return productRepository.searchProductByBranchIdWithSellPrice(branchId, keyword).stream()
          .map(
              entity ->
                  productMapper.convertToBranchProduct(
                      entity, branchId)) // Pass branchId to the mapper
          .collect(Collectors.toList());
    }
  }

  @Override
  public ProductBaseDTO getBranchProducts(Long branchId, Long productId) {
    ProductEntity product = productRepository.findById(productId).orElse(null);
    if (product == null) {
      return null;
    }
    return productMapper.convertToBranchProduct(product, branchId);
  }

  List<ProductBatchDTO> processProductData(List<ProductBaseDTO> products) {
    List<ProductBatchDTO> allProductBatches = new ArrayList<>();
    for (ProductBaseDTO product : products) {
      ProductBatchDTO productBaseDTO =
          ProductBatchDTO.builder()
              .product(
                  Product.builder()
                      .id(product.getId())
                      .productName(product.getProductName())
                      .baseUnit(product.getProductBaseUnit())
                      .build())
              .systemQuantity(
                  product.getProductQuantity() != null
                      ? product.getProductQuantity()
                      : BigDecimal.ZERO)
              .build();
      List<ProductBatchDTO> batches =
          product.getBatches().stream()
              .map(
                  batch ->
                      ProductBatchDTO.builder()
                          .product(
                              Product.builder()
                                  .id(product.getId())
                                  .productName(product.getProductName())
                                  .baseUnit(product.getProductBaseUnit())
                                  .build())
                          .batch(
                              Batch.builder()
                                  .id(batch.getId())
                                  .batchCode(batch.getBatchCode())
                                  .build())
                          .systemQuantity(batch.getQuantity())
                          .build() // Missing build() for ProductBatchDTO
                  )
              .collect(Collectors.toList());
      allProductBatches.add(productBaseDTO);
      allProductBatches.addAll(batches);
    }
    allProductBatches.sort(
        Comparator.comparing((ProductBatchDTO dto) -> dto.getProduct().getId())
            .thenComparing(
                dto -> dto.getBatch() != null ? dto.getBatch().getId() : Long.MIN_VALUE));
    return allProductBatches;
  }

  public List<ProductBatchDTO> getProductInBranchForInventoryCheck(Long branchId) {
    List<ProductBaseDTO> products =
        productRepository.searchAllProductByBranchId(branchId, "", null, null).stream()
            .map(
                entity ->
                    productMapper.convertToProductForSearchInNotes(
                        entity, branchId)) // Pass branchId to the mapper
            .toList();
    return processProductData(products);
  }

  public List<ProductBatchDTO> getProductByCateInBranchForInventoryCheck(
      Long branchId, Long cateId) {
    List<ProductBaseDTO> products =
        productRepository
            .searchAllProductByBranchIdAndCateId(branchId, cateId, "", null, null)
            .stream()
            .map(
                entity ->
                    productMapper.convertToProductForSearchInNotes(
                        entity, branchId)) // Pass branchId to the mapper
            .toList();
    return processProductData(products);
  }

  public List<ProductBatchDTO> getProductByTypeIdInBranchForInventoryCheck(
      Long branchId, Long typeId) {
    List<ProductBaseDTO> products =
        productRepository
            .searchAllProductByBranchIdAndTypeId(branchId, typeId, "", null, null)
            .stream()
            .map(
                entity ->
                    productMapper.convertToProductForSearchInNotes(
                        entity, branchId)) // Pass branchId to the mapper
            .toList();
    return processProductData(products);
  }

  @Override
  public void removeCategoryFromProducts(Long cateId) {
    productRepository.removeCategoryFromProducts(cateId);
  }

  @Override
  public void removeTypeFromProducts(Long typeId) {
    productRepository.removeTypeFromProducts(typeId);
  }

  public Page<ProductBaseDTO> filterProducts(
      Boolean lessThanOrEqual,
      Integer quantity,
      Boolean warning,
      Boolean outOfStock,
      Pageable pageable) {
    Set<ProductBaseDTO> resultSet = new HashSet<>();
    String userEmail = userService.getAuthenticatedUserEmail();
    Long branchId = userService.findBranchIdByUserEmail(userEmail).orElse(null);
    // Apply "less than or equal" filter if selected
    if (lessThanOrEqual != null && lessThanOrEqual && quantity != null) {
      List<ProductBaseDTO> lessThanEqualProducts =
          productRepository.findByQuantityLessThanEqualInBranch(quantity, branchId).stream()
              .map(productMapper::convertToProductBaseDTO)
              .toList();
      resultSet.addAll(lessThanEqualProducts);
    }

    // Apply "warning threshold" filter if selected
    if (warning != null && warning) {
      List<ProductBaseDTO> warningProducts =
          productRepository.findByQuantityLessThanMinQuantityInBranch(branchId).stream()
              .map(productMapper::convertToProductBaseDTO)
              .toList();
      resultSet.addAll(warningProducts);
    }

    // Apply "out of stock" filter if selected
    if (outOfStock != null && outOfStock) {
      List<ProductBaseDTO> outOfStockProducts =
          productRepository.findByQuantityInBranch(0, branchId).stream()
              .map(productMapper::convertToProductBaseDTO)
              .toList();
      resultSet.addAll(outOfStockProducts);
    }

    List<ProductBaseDTO> resultList = new ArrayList<>(resultSet);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), resultList.size());
    List<ProductBaseDTO> pageContent = resultList.subList(start, end);

    return new PageImpl<>(pageContent, pageable, resultList.size());
  }

  @Override
  public Page<ProductBaseDTO> getProductsWithLossOrNoSellPriceInBranch(Pageable pageable) {
    String userEmail = userService.getAuthenticatedUserEmail();
    Long branchId = userService.findBranchIdByUserEmail(userEmail).orElse(null);

    return productRepository
        .findProductsWithLossOrNoSellPriceInBranch(branchId, pageable)
        .map(productMapper::convertToProductBaseDTO);
  }

  @Override
  public Page<ProductBaseDTO> getProductsBySellPrice(BigDecimal sellPrice, Pageable pageable) {
    String userEmail = userService.getAuthenticatedUserEmail();
    Long branchId = userService.findBranchIdByUserEmail(userEmail).orElse(null);

    return productRepository
        .findProductsBySellPrice(sellPrice, branchId, pageable)
        .map(productMapper::convertToProductBaseDTO);
  }

  @Override
  public List<ProductBaseDTO> getByKeyword(String keyword) {
    return productRepository.findProductEntitiesByProductNameIgnoreCase(keyword).stream()
        .map(productMapper::convertToProductDto)
        .collect(Collectors.toList());
  }

  public List<AuditHistory> getProductDetailsInPeriod(
      Long productId, LocalDateTime startDate, LocalDateTime endDate) {
    // Fetch product details
    List<AuditHistory> productInbound =
        inboundDetailsService
            .getInboundDetailsByProductIdAndPeriod(productId, startDate, endDate)
            .stream()
            .map(inboundDetailsMapper::toAudit)
            .toList();

    List<AuditHistory> batchInbound =
        inboundBatchDetailService
            .getInboundBatchDetailsByProductIdAndPeriod(productId, startDate, endDate)
            .stream()
            .map(inboundBatchDetailMapper::toAudit)
            .toList();

    List<AuditHistory> batchOutbound =
        outboundDetailService
            .getOutboundDetailsByProductIdAndPeriod(productId, startDate, endDate)
            .stream()
            .map(outboundDetailMapper::toAudit)
            .toList();
    List<AuditHistory> productOutbound =
        outboundProductDetailService
            .getOutboundProductDetailsByProductIdAndPeriod(productId, startDate, endDate)
            .stream()
            .map(outboundProductDetailMapper::toAudit)
            .toList();

    return Stream.of(productInbound, batchInbound, batchOutbound, productOutbound)
        .flatMap(Collection::stream)
        .sorted(Comparator.comparing(AuditHistory::getCreatedAt))
        .collect(Collectors.toList());
  }

  boolean commonValidate(Product product) {
    // Validate ProductName (required, non-empty, max length 50)
    if (product.getProductName() == null
        || product.getProductName().trim().isEmpty()
        || product.getProductName().length() > 50) {
      return false;
    }

    // Validate Registration Code (required, non-empty, max length 30)
    if (product.getRegistrationCode() == null
        || product.getRegistrationCode().trim().isEmpty()
        || product.getRegistrationCode().length() > 30) {
      return false;
    }

    // Validate Active Ingredient (required, non-empty, max length 255)
    if (product.getActiveIngredient() == null
        || product.getActiveIngredient().trim().isEmpty()
        || product.getActiveIngredient().length() > 255) {
      return false;
    }

    // Validate Excipient (required, non-empty, max length 255)
    if (product.getExcipient() != null && product.getExcipient().length() > 255) {
      return false;
    }

    // Validate Formulation (required, non-empty, max length 255)
    if (product.getFormulation() == null
        || product.getFormulation().trim().isEmpty()
        || product.getFormulation().length() > 255) {
      return false;
    }

    // Validate Inbound Price (max value 1,000,000,000)
    if (product.getInboundPrice() != null
        && product.getInboundPrice().compareTo(new BigDecimal("1000000000")) > 0) {
      return false;
    }

    // Validate Sell Price (max value 1,000,000,000)
    if (product.getSellPrice() != null
        && product.getSellPrice().compareTo(new BigDecimal("1000000000")) > 0) {
      return false;
    }

    return true;
  }
}
