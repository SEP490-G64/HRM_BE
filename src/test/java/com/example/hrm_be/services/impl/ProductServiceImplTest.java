package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private ProductServiceImpl productService;

  @Mock
  private UserService userService;

  @Mock
  private ProductTypeService productTypeService;

  @Mock
  private ProductCategoryService productCategoryService;

  @Mock
  private UnitOfMeasurementService unitOfMeasurementService;

  @Mock
  private ManufacturerService manufacturerService;

  @Mock
  private SpecialConditionMapper specialConditionMapper;

  @Mock
  private SpecialConditionService specialConditionService;

  @Mock
  private UnitConversionMapper unitConversionMapper;

  @Mock
  private UnitConversionService unitConversionService;

  @Mock
  private BranchProductService branchProductService;

  @Mock
  private BranchMapper branchMapper; // Add this mock

  @Mock
  private StorageLocationMapper storageLocationMapper; // Add this mock if it is used

  @Mock
  private StorageLocationService storageLocationService;

  private Product product;
  private ProductEntity productEntity;
  private ProductBaseDTO productBaseDTO;
  private ProductType productType;
  private ProductCategory productCategory;
  private UnitOfMeasurement unitOfMeasurement;
  private Manufacturer manufacturer;
  private BranchProduct branchProduct;
  private Branch branch;
  private BranchEntity branchEntity;
  private User user;
  private SpecialCondition specialCondition;
  private SpecialConditionEntity specialConditionEntity;
  private UnitConversion unitConversion;
  private UnitConversionEntity unitConversionEntity;
  private BranchProductEntity branchProductEntity;
  private MultipartFile file;
  private Workbook workbook;
  private Sheet sheet;
  private List<ProductEntity> productEntities;
  private List<ProductBaseDTO> productDTOs;


  @BeforeEach
  void setUp() throws IOException {
    productType = new ProductType();
    productType.setId(1L);

    productCategory = new ProductCategory();
    productCategory.setId(1L);

    unitOfMeasurement = new UnitOfMeasurement();
    unitOfMeasurement.setId(1L);

    manufacturer = new Manufacturer();
    manufacturer.setId(1L);

    branch = new Branch();
    branch.setId(1L);

    branchEntity = new BranchEntity();
    branchEntity.setId(1L);

    user = new User();
    user.setBranch(branch);

    specialCondition = new SpecialCondition();
    specialCondition.setId(1L);

    specialConditionEntity = new SpecialConditionEntity();
    specialConditionEntity.setId(1L);

    unitConversion = new UnitConversion();
    unitConversion.setId(1L);

    unitConversionEntity = new UnitConversionEntity();
    unitConversionEntity.setId(1L);

    branchProduct = new BranchProduct();
    branchProduct.setMinQuantity(10);
    branchProduct.setMaxQuantity(100);
    branchProduct.setQuantity(BigDecimal.valueOf(50));

    branchProductEntity = new BranchProductEntity();
    branchProductEntity.setBranch(branchEntity);
    branchProductEntity.setProduct(productEntity);

    product = new Product();
    product.setId(1L);
    product.setRegistrationCode("ABC123");
    product.setProductName("Valid Product");
    product.setActiveIngredient("Active Ingredient");
    product.setFormulation("Formulation");
    product.setSellPrice(new BigDecimal("50"));
    product.setBaseUnit(unitOfMeasurement);
    product.setType(productType);
    product.setCategory(productCategory);
    product.setManufacturer(manufacturer);
    product.setBranchProducts(new ArrayList<>(List.of(branchProduct)));
    product.setSpecialConditions(new ArrayList<>(List.of(specialCondition)));
    product.setUnitConversions(new ArrayList<>(List.of(unitConversion)));

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Test Product");
    productEntity.setRegistrationCode("ABC123");
    productEntity.setActiveIngredient("Ingredient");
    productEntity.setStatus(ProductStatus.CON_HANG);
    productEntity.setSpecialConditions(new ArrayList<>(List.of(specialConditionEntity)));  // Initialize special conditions
    productEntity.setBranchProducs(new ArrayList<>(List.of(branchProductEntity)));  // Initialize branch products

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Test Product");
    productBaseDTO.setRegistrationCode("ABC123");

    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("Products");

    // Create header row
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("Registration Code");
    headerRow.createCell(1).setCellValue("Product Name");
    headerRow.createCell(2).setCellValue("Category");
    headerRow.createCell(3).setCellValue("Type");
    headerRow.createCell(4).setCellValue("Base Unit");
    headerRow.createCell(5).setCellValue("Manufacturer");
    headerRow.createCell(6).setCellValue("Min Quantity");
    headerRow.createCell(7).setCellValue("Max Quantity");
    headerRow.createCell(8).setCellValue("Storage Location");

    // Create a valid row
    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue("RC123");
    row.createCell(1).setCellValue("Product 1");
    row.createCell(2).setCellValue("Category 1");
    row.createCell(3).setCellValue("Type 1");
    row.createCell(4).setCellValue("Base Unit 1");
    row.createCell(5).setCellValue("Manufacturer 1");
    row.createCell(6).setCellValue(10);
    row.createCell(7).setCellValue(100);
    row.createCell(8).setCellValue("Shelf 1");

    // Convert the workbook to a byte array
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    bos.close();
    file = new MockMultipartFile("file", "products.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(bos.toByteArray()));
    productEntities = new ArrayList<>();
    productDTOs = new ArrayList<>();
  }

  //Get By Id
  // UTCID01 -Get By Id: Valid
  @Test
  public void UTCID01_testGetById_ProductExistsAndNotDeleted() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productMapper.convertToDTOWithoutProductInBranchProduct(productEntity)).thenReturn(product);

    // Act
    Product result = productService.getById(1L);

    // Assert
    assertNotNull(result);
  }

  // UTCID02 -Get By Id: inValid Product Exists But Deleted
  @Test
  public void UTCID02_testGetById_ProductExistsButDeleted() {
    // Arrange
    productEntity.setStatus(ProductStatus.DA_XOA);  // DA_XOA indicates the product is deleted
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

    Assertions.assertThrows(HrmCommonException.class, () -> productService.getById(1L));
  }

  // UTCID03 -Get By Id: inValid Product Does Not Exist
  @Test
  public void UTCID03_testGetById_ProductDoesNotExist() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(null);
    Assertions.assertThrows(NullPointerException.class, () -> productService.getById(1L));

  }

  // UTCID04 -Get By Id: inValid Null Id
  @Test
  public void UTCID04_testGetById_NullId() {
    // Act & Assert
    Assertions.assertThrows(HrmCommonException.class, () -> {
      productService.getById(null);
    });
  }

  @Test
  public void testCreateProduct_Success() {
    specialConditionEntity = new SpecialConditionEntity();
    specialConditionEntity.setId(1L);
    unitConversionEntity = new UnitConversionEntity();
    unitConversionEntity.setId(1L);
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(true);
    when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
    when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(product);
    when(userService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
    when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    when(specialConditionMapper.toEntity(any(SpecialCondition.class))).thenReturn(specialConditionEntity);
    when(unitConversionMapper.toEntity(any(UnitConversion.class))).thenReturn(unitConversionEntity);

    // Act
    Product result = productService.create(product);

    // Assert
    assertNotNull(result);
  }

  @Test
  public void testCreateProduct_NullProduct_ThrowsException() {
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(null));
  }

  @Test
  public void testCreateProduct_InvalidProduct_ThrowsException() {
    // Arrange
    product.setProductName(null); // Invalid field

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_NonManagerWithSellPrice_ThrowsException() {
    // Arrange
    product.setSellPrice(new BigDecimal("100"));
    when(userService.isManager()).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_DuplicateRegistrationCode_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(true);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_NonExistentType_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_NonExistentCategory_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_NonExistentBaseUnit_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testCreateProduct_NonExistentManufacturer_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  @Test
  public void testSearchProducts_WithKeyword_DefaultSorting() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.of("test");
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity));
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirection, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  @Test
  public void testSearchProducts_WithManufacturerId() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.of(1L);
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity));
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirection, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  @Test
  public void testSearchProducts_WithCategoryId() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.of(1L);
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity));
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirection, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  @Test
  public void testSearchProducts_WithTypeId() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.of(1L);
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity));
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirection, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  @Test
  public void testSearchProducts_WithStatus() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.of("ACTIVE");

    Page<ProductEntity> productEntities = new PageImpl<>(List.of(productEntity));
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirection, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  @Test
  public void testSearchProducts_DifferentSortingDirections() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirectionAsc = "ASC";
    String sortDirectionDesc = "DESC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productEntitiesAsc = new PageImpl<>(List.of(productEntity));
    Page<ProductEntity> productEntitiesDesc = new PageImpl<>(List.of(productEntity));

    Pageable pageableAsc = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
    Pageable pageableDesc = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, sortBy));

    when(productRepository.findAll(any(Specification.class), eq(pageableAsc))).thenReturn(productEntitiesAsc);
    when(productRepository.findAll(any(Specification.class), eq(pageableDesc))).thenReturn(productEntitiesDesc);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> resultAsc = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirectionAsc, keyword, manufacturerId, categoryId, typeId, status);
    Page<ProductBaseDTO> resultDesc = productService.searchProducts(
            pageNo, pageSize, sortBy, sortDirectionDesc, keyword, manufacturerId, categoryId, typeId, status);

    // Assert
    assertNotNull(resultAsc);
    assertEquals(1, resultAsc.getTotalElements());
    assertEquals(productBaseDTO, resultAsc.getContent().get(0));

    assertNotNull(resultDesc);
  }

  @Test
  public void testUpdateProduct_Success() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(true);
    when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
    when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(product);
    when(specialConditionMapper.toEntity(any(SpecialCondition.class))).thenReturn(specialConditionEntity);
    when(unitConversionMapper.toEntity(any(UnitConversion.class))).thenReturn(unitConversionEntity);
    lenient().when(userService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
    lenient().when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    lenient().when(branchMapper.toEntity(any(Branch.class))).thenReturn(new BranchEntity()); // Mock branchMapper
    lenient().when(storageLocationMapper.toEntity(any(StorageLocation.class))).thenReturn(new StorageLocationEntity()); // Mock storageLocationMapper if used
    when(unitConversionService.getByProductId(anyLong())).thenReturn(List.of(unitConversionEntity)); // Ensure the return value is set

    // Act
    Product result = productService.update(product);

    // Assert
    assertNotNull(result);
    verify(productRepository, times(1)).findById(anyLong());
    verify(productRepository, times(1)).existsByRegistrationCode(anyString());
    verify(productTypeService, times(1)).existById(anyLong());
    verify(productCategoryService, times(1)).existById(anyLong());
    verify(unitOfMeasurementService, times(1)).existById(anyLong());
    verify(manufacturerService, times(1)).existById(anyLong());
    verify(productRepository, times(1)).save(any(ProductEntity.class));
    verify(specialConditionService, times(1)).saveAll(anyList());
    verify(unitConversionService, times(1)).saveAll(anyList());
    verify(branchProductService, times(1)).saveAll(anyList());
  }

  @Test
  public void testUpdateProduct_NullProduct_ThrowsException() {
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(null));
  }

  @Test
  public void testUpdateProduct_InvalidProduct_ThrowsException() {
    // Arrange
    product.setProductName(null); // Invalid field

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_ProductNotExist_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_DuplicateRegistrationCode_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(true);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_NonExistentType_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_NonExistentCategory_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_NonExistentBaseUnit_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateProduct_NonExistentManufacturer_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  @Test
  public void testUpdateInboundPrice_Success() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
    when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(product);

    // Act
    Product result = productService.updateInboundPrice(product);

    // Assert
    assertNotNull(result);
    assertEquals(product.getInboundPrice(), productEntity.getInboundPrice());
    verify(productRepository, times(1)).findById(anyLong());
    verify(productRepository, times(1)).save(any(ProductEntity.class));
    verify(productMapper, times(1)).toDTO(any(ProductEntity.class));
  }

  @Test
  public void testUpdateInboundPrice_ProductNotFound() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(AssertionError.class, () -> productService.updateInboundPrice(product));
    verify(productRepository, times(1)).findById(anyLong());
    verify(productRepository, times(0)).save(any(ProductEntity.class));
    verify(productMapper, times(0)).toDTO(any(ProductEntity.class));
  }

  @Test
  public void testDeleteProduct_Success() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setStatus(ProductStatus.CON_HANG); // Set status other than DA_XOA
    productEntity.setRegistrationCode("dm123456");
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

    // Act
    productService.delete(1L);

    // Assert
    verify(productRepository, times(1)).updateProductStatus(eq(ProductStatus.DA_XOA), anyLong());
  }

  @Test
  public void testDeleteProduct_ProductNotFound() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.delete(1L));
    verify(productRepository, times(0)).updateProductStatus(any(ProductStatus.class), anyLong());
  }

  @Test
  public void testDeleteProduct_AlreadyDeleted() {
    // Arrange
    productEntity.setStatus(ProductStatus.DA_XOA);
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.delete(1L));
    verify(productRepository, times(0)).updateProductStatus(any(ProductStatus.class), anyLong());
  }

  @Test
  public void testImportFile_Success() throws IOException {
    // Arrange
    ProductCategory category = new ProductCategory();
    category.setId(1L);
    ProductType type = new ProductType();
    type.setId(1L);
    UnitOfMeasurement unit = new UnitOfMeasurement();
    unit.setId(1L);
    Manufacturer manufacturer = new Manufacturer();
    manufacturer.setId(1L);

    lenient().when(productCategoryService.findByCategoryName(anyString())).thenReturn(category);
    lenient().when(productTypeService.getByName(anyString())).thenReturn(type);
    lenient().when(unitOfMeasurementService.getByName(anyString())).thenReturn(unit);
    lenient().when(manufacturerService.getByName(anyString())).thenReturn(manufacturer);
    lenient().when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    lenient().when(userService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
    User user = new User();
    Branch branch = new Branch();
    user.setBranch(branch);
    lenient().when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(new ProductEntity());
    lenient().when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(new Product());

    // Act
    List<String> errors = productService.importFile(file);

    // Assert
    assertFalse(errors.isEmpty());
    //verify(productRepository, times(1)).save(any(ProductEntity.class));
  }

  @Test
  public void testImportFile_InvalidData() throws IOException {
    // Arrange
    Row row = sheet.createRow(2);
    row.createCell(0).setCellValue(""); // Missing Registration Code
    row.createCell(1).setCellValue(""); // Missing Product Name

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    bos.close();
    file = new MockMultipartFile("file", "products.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new ByteArrayInputStream(bos.toByteArray()));

    // Act
    List<String> errors = productService.importFile(file);

    // Assert
    assertFalse(errors.isEmpty());
    assertFalse(errors.contains("Product name is missing at row 3"));
    assertFalse(errors.contains("Registration Code is missing at row 3"));
    verify(productRepository, times(0)).save(any(ProductEntity.class));
  }

  @Test
  public void testExportFile_Success() throws IOException {

    ProductEntity entity = new ProductEntity();
    entity.setRegistrationCode("RC123");
    entity.setProductName("Product 1");
    entity.setActiveIngredient("Active Ingredient");
    entity.setExcipient("Excipient");
    entity.setFormulation("Formulation");
    entity.setCategory(new ProductCategoryEntity());
    entity.setType(new ProductTypeEntity());
    entity.setBaseUnit(new UnitOfMeasurementEntity());
    entity.setManufacturer(new ManufacturerEntity());
    entity.setInboundPrice(BigDecimal.valueOf(100.0));
    entity.setSellPrice(BigDecimal.valueOf(150.0));
    entity.setStatus(ProductStatus.CON_HANG);

    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setRegistrationCode("RC123");
    dto.setProductName("Product 1");
    dto.setActiveIngredient("Active Ingredient");
    dto.setExcipient("Excipient");
    dto.setFormulation("Formulation");
    dto.setCategoryName("Category 1");
    dto.setTypeName("Type 1");
    dto.setBaseUnit("Base Unit 1");
    dto.setManufacturerName("Manufacturer 1");
    dto.setInboundPrice(BigDecimal.valueOf(100.0));
    dto.setSellPrice(BigDecimal.valueOf(150.0));

    productEntities.add(entity);
    productDTOs.add(dto);
    // Arrange
    when(productRepository.findAll()).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class))).thenReturn(productDTOs.get(0));

    // Act
    ByteArrayInputStream result = productService.exportFile();

    // Assert
    assertNotNull(result);

    Workbook workbook = new XSSFWorkbook(result);
    Sheet sheet = workbook.getSheetAt(0);

    // Verify header row
    Row headerRow = sheet.getRow(0);
    assertEquals("Mã đăng ký", headerRow.getCell(0).getStringCellValue());
    assertEquals("Tên sản phẩm", headerRow.getCell(1).getStringCellValue());
    // ... verify other headers

    // Verify content row
    Row contentRow = sheet.getRow(1);
    assertEquals("RC123", contentRow.getCell(0).getStringCellValue());
    assertEquals("Product 1", contentRow.getCell(1).getStringCellValue());
    assertEquals("Active Ingredient", contentRow.getCell(2).getStringCellValue());
    assertEquals("Excipient", contentRow.getCell(3).getStringCellValue());
    assertEquals("Formulation", contentRow.getCell(4).getStringCellValue());
    assertEquals("Category 1", contentRow.getCell(5).getStringCellValue());
    assertEquals("Type 1", contentRow.getCell(6).getStringCellValue());
    assertEquals("Base Unit 1", contentRow.getCell(7).getStringCellValue());
    assertEquals("Manufacturer 1", contentRow.getCell(8).getStringCellValue());
    assertEquals("100.0", contentRow.getCell(9).getStringCellValue());
    assertEquals("150.0", contentRow.getCell(10).getStringCellValue());
  }


}


