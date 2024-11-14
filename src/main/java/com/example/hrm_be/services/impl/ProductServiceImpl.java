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
import com.example.hrm_be.repositories.*;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.ExcelUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
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
  @Autowired private SpecialConditionMapper specialConditionMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private StorageLocationMapper storageLocationMapper;
  @Autowired private UnitConversionMapper unitConversionMapper;
  @Autowired private AllowedProductService allowedProductService;
  @Autowired private BatchRepository batchRepository;

  @Override
  public Product getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                productRepository
                    .findById(e)
                    .map(b -> productMapper.convertToDTOWithoutProductInBranchProduct(b)))
        .orElse(null);
  }

  @Override
  @Transactional
  public Product create(Product product) {
    if (product == null) {
      throw new HrmCommonException(REQUEST.INVALID_BODY);
    }

    // Check only manager allow to sell price
    if (userService.isManager()) {
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

    unsavedProduct.setInboundPrice(product.getInboundPrice());
    ProductEntity saved = productRepository.save(unsavedProduct);
    return productMapper.toDTO(saved);
  }

  @Override
  public void delete(Long id) {
    Product productEntity = getById(id);
    if (productEntity == null) {
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

            if (productRepository.existsByRegistrationCode(product.getRegistrationCode())) {
              rowErrors.add("Registration Code exists at row " + (rowIndex + 1));
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
    try {
      for (Product product : productsToSave) {
        create(product);
      }

    } catch (Exception e) {
      errors.add("Error saving products: " + e.getMessage());
      throw new RuntimeException(
          "Transaction failed, rolling back due to error.", e); // Marks transaction for rollback
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
        searchProducts(
                0,
                Integer.MAX_VALUE,
                "id",
                "ASC",
                Optional.ofNullable(null),
                Optional.ofNullable(null),
                Optional.ofNullable(null),
                Optional.ofNullable(null),
                Optional.ofNullable(null))
            .stream()
            .toList();

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
        .map(productMapper::convertToProductForSearchInNotes)
        .collect(Collectors.toList());
  }
}
