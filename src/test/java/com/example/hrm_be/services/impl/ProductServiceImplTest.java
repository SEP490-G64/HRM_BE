package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.responses.AuditHistory;
import com.example.hrm_be.repositories.AllowedProductRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.ExcelUtility;
import jakarta.persistence.criteria.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

  @Mock private ProductRepository productRepository;

  @Mock private ProductMapper productMapper;

  @InjectMocks private ProductServiceImpl productService;

  @Mock private UserService userService;

  @Mock private ProductTypeService productTypeService;

  @Mock private ProductCategoryService productCategoryService;

  @Mock private UnitOfMeasurementService unitOfMeasurementService;

  @Mock private ManufacturerService manufacturerService;

  @Mock private SpecialConditionMapper specialConditionMapper;

  @Mock private SpecialConditionService specialConditionService;

  @Mock private UnitConversionMapper unitConversionMapper;

  @Mock private UnitConversionService unitConversionService;

  @Mock private BranchProductService branchProductService;

  @Mock private BranchMapper branchMapper; // Add this mock

  @Mock private StorageLocationMapper storageLocationMapper; // Add this mock if it is used

  @Mock private StorageLocationService storageLocationService;

  @Mock private InboundDetailsService inboundDetailsService;

  @Mock private InboundDetailsMapper inboundDetailsMapper;

  @Mock private InboundBatchDetailService inboundBatchDetailService;

  @Mock private InboundBatchDetailMapper inboundBatchDetailMapper;

  @Mock private OutboundDetailService outboundDetailService;

  @Mock private OutboundDetailMapper outboundDetailMapper;

  @Mock private OutboundProductDetailService outboundProductDetailService;

  @Mock private OutboundProductDetailMapper outboundProductDetailMapper;

  @Mock private AllowedProductRepository allowedProductRepository;

  @Mock private AllowedProductService allowedProductService;
  @Mock private ProductEntity oldProductEntity;
  private List<Map<String, Object>> productJsonList;
  private AllowedProductEntity allowedProductEntity;
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
  private List<ProductSupplierDTO> productSupplierDTOs;
  private ProductInbound productInbound;
  private List<ProductBaseDTO> productBaseDTOs;
  private List<InboundDetails> inboundDetailEntities;
  private List<InboundBatchDetail> batchInboundDetailEntities;
  private List<OutboundDetail> batchOutboundDetailEntities;
  private List<OutboundProductDetail> productOutboundDetailEntities;
  private List<AuditHistory> auditHistories;
  private List<AllowedProductEntity> allowedProductEntities;

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
    // product.setSellPrice(new BigDecimal("50"));
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
    productEntity.setSpecialConditions(
        new ArrayList<>(List.of(specialConditionEntity))); // Initialize special conditions
    productEntity.setBranchProducs(
        new ArrayList<>(List.of(branchProductEntity))); // Initialize branch products

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Test Product");
    productBaseDTO.setRegistrationCode("ABC123");

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Sheet1");

    // Header row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("RegistrationCode");
    header.createCell(1).setCellValue("ProductName");
    header.createCell(2).setCellValue("CategoryName");
    header.createCell(3).setCellValue("TypeName");
    header.createCell(4).setCellValue("UnitName");
    header.createCell(5).setCellValue("ManufacturerName");
    header.createCell(6).setCellValue("MinQuantity");
    header.createCell(7).setCellValue("MaxQuantity");
    header.createCell(8).setCellValue("StorageLocation");

    // Valid data row
    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue("REG123");
    row.createCell(1).setCellValue("ProductName");
    row.createCell(2).setCellValue("CategoryName");
    row.createCell(3).setCellValue("TypeName");
    row.createCell(4).setCellValue("UnitName");
    row.createCell(5).setCellValue("ManufacturerName");
    row.createCell(6).setCellValue(10);
    row.createCell(7).setCellValue(100);
    row.createCell(8).setCellValue("StorageLocation");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    workbook.close();
    file =
        new MockMultipartFile(
            "file",
            "products.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            bos.toByteArray());
    productEntities = new ArrayList<>();
    productDTOs = new ArrayList<>();
    productSupplierDTOs = new ArrayList<>();
    productInbound = new ProductInbound();
    productInbound.setRegistrationCode("RC123");
    productInbound.setProductName("New Product");
    productInbound.setBaseUnit(new UnitOfMeasurement());
    productBaseDTOs = new ArrayList<>();

    inboundDetailEntities = new ArrayList<>();
    batchInboundDetailEntities = new ArrayList<>();
    batchOutboundDetailEntities = new ArrayList<>();
    productOutboundDetailEntities = new ArrayList<>();
    auditHistories = new ArrayList<>();

    // Initialize InboundDetail
    InboundDetails inboundDetail = new InboundDetails();
    inboundDetailEntities.add(inboundDetail);

    // Initialize AuditHistory
    AuditHistory audit = new AuditHistory();
    audit.setCreatedAt(LocalDateTime.now());
    auditHistories.add(audit);

    productJsonList = new ArrayList<>();
    Map<String, Object> productJson = new HashMap<>();
    productJson.put("tenThuoc", "Test Product");
    productJson.put("id", "TP001");
    productJson.put("soDangKy", "SD001");
    productJson.put("images", List.of("http://example.com/image1.jpg"));
    productJson.put("hoatChat", "Active Ingredient");
    productJson.put("taDuoc", "Excipient");
    productJson.put("baoChe", "Formulation");
    productJsonList.add(productJson);

    allowedProductEntity = new AllowedProductEntity();
    allowedProductEntity.setProductName("Test Product");
    allowedProductEntity.setProductCode("TP001");
    allowedProductEntity.setRegistrationCode("SD001");
    allowedProductEntity.setUrlImage("http://example.com/image1.jpg");
    allowedProductEntity.setActiveIngredient("Active Ingredient");
    allowedProductEntity.setExcipient("Excipient");
    allowedProductEntity.setFormulation("Formulation");

    allowedProductEntities = new ArrayList<>();

    // Initialize AllowedProductEntity
    AllowedProductEntity entity = new AllowedProductEntity();
    entity.setProductName("Test Product");
    allowedProductEntities.add(entity);

    oldProductEntity = new ProductEntity();
  }

  // Get By Id
  // UTCID01 -Get By Id: Valid
  @Test
  public void UTCID01_testGetById_ProductExistsAndNotDeleted() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productMapper.convertToDTOWithoutProductInBranchProduct(productEntity))
        .thenReturn(product);

    // Act
    Product result = productService.getById(1L);

    // Assert
    assertNotNull(result);
  }

  // UTCID02 -Get By Id: inValid Product Exists But Deleted
  @Test
  public void UTCID02_testGetById_ProductExistsButDeleted() {
    // Arrange
    productEntity.setStatus(ProductStatus.DA_XOA); // DA_XOA indicates the product is deleted
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
    Assertions.assertThrows(
        HrmCommonException.class,
        () -> {
          productService.getById(null);
        });
  }

  // CreateProduct
  // UTCID01: CreateProduct - Valid
  @Test
  public void UTCID01_testCreateProduct_Success() {
    when(userService.isManager()).thenReturn(Boolean.TRUE);
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
    when(specialConditionMapper.toEntity(any(SpecialCondition.class)))
        .thenReturn(specialConditionEntity);
    when(unitConversionMapper.toEntity(any(UnitConversion.class))).thenReturn(unitConversionEntity);

    // Act
    Product result = productService.create(product);

    // Assert
    assertNotNull(result);
  }

  // UTCID02: CreateProduct - inValid Null Product
  @Test
  public void UTCID02_testCreateProduct_NullProduct_ThrowsException() {
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(null));
  }

  // UTCID03: CreateProduct - inValid Product
  @Test
  public void UTCID03_testCreateProduct_InvalidProduct_ThrowsException() {
    // Arrange
    product.setProductName(null); // Invalid field

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID04: CreateProduct - inValid Non Manager With Sell Price
  @Test
  public void testCreateProduct_NonManagerWithSellPrice_ThrowsException() {
    // Arrange
    product.setSellPrice(new BigDecimal("100"));
    when(userService.isManager()).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID05: CreateProduct - inValid Duplicate Registration Code
  @Test
  public void UTCID05_testCreateProduct_DuplicateRegistrationCode_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(true);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID06: CreateProduct - inValid Non Existent Type
  @Test
  public void UTCID06_testCreateProduct_NonExistentType_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID07: CreateProduct - inValid Non Existent Category
  @Test
  public void UTCID07_testCreateProduct_NonExistentCategory_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID08: CreateProduct - inValid Non Existent BaseUnit
  @Test
  public void UTCID08_testCreateProduct_NonExistentBaseUnit_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID09: CreateProduct - inValid NonExistentManufacturer
  @Test
  public void UTCID09_testCreateProduct_NonExistentManufacturer_ThrowsException() {
    // Arrange
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID10: CreateProduct - inValid BranchProductNotExist
  @Test
  public void testCreate_BranchProductNotExist() {
    // Arrange
    product.setBranchProducts(new ArrayList<>()); // No branch product

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));

    verify(productRepository, times(0)).save(any(ProductEntity.class));
  }

  // UTCID11: CreateProduct - Valid BranchProductWithStorageLocation
  @Test
  public void UTCID11_testCreate_BranchProductWithStorageLocation() {
    product.setSpecialConditions(new ArrayList<>());
    product.setUnitConversions(new ArrayList<>());
    product.setBranchProducts(new ArrayList<>());
    // Arrange
    when(userService.isManager()).thenReturn(Boolean.TRUE);
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(true);
    when(manufacturerService.existById(anyLong())).thenReturn(true);
    when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
    when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(product);

    BranchProduct branchProduct = new BranchProduct();
    branchProduct.setMinQuantity(10);
    branchProduct.setMaxQuantity(100);
    branchProduct.setQuantity(BigDecimal.valueOf(50));
    StorageLocation storageLocation = new StorageLocation();
    branchProduct.setStorageLocation(storageLocation);
    product.getBranchProducts().add(branchProduct);

    String email = "test@example.com";
    Branch branch = new Branch();
    branch.setId(1L);
    when(userService.getAuthenticatedUserEmail()).thenReturn(email);
    when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    when(storageLocationService.save(any(StorageLocation.class))).thenReturn(storageLocation);

    // Act
    Product result = productService.create(product);

    // Assert
    assertNotNull(result);
    assertEquals(product.getProductName(), result.getProductName());

    verify(productRepository, times(1)).save(any(ProductEntity.class));
    verify(storageLocationService, times(1)).save(any(StorageLocation.class));
  }

  // UTCID012: CreateProduct - RegistrationCode address greater than 30 characters
  @Test
  public void UTCID012_testCreateProduct_Invalid_RegistrationCode() {
    // Arrange
    product.setRegistrationCode("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID013: CreateProduct - Excipient greater than 255 characters
  @Test
  public void UTCID013_testCreateProduct_Invalid_Excipient() {
    // Arrange
    product.setExcipient("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID014: CreateProduct - Formulation greater than 255 characters
  @Test
  public void UTCID014_testCreateProduct_Invalid_Formulation() {
    // Arrange
    product.setFormulation("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID015: CreateProduct - Ingredient greater than 255 characters
  @Test
  public void UTCID015_testCreateProduct_Invalid_Ingredient() {
    // Arrange
    product.setActiveIngredient("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID016: CreateProduct - InboundPrice greater than 1000000000
  @Test
  public void UTCID016_testCreateProduct_Invalid_InboundPrice() {
    // Arrange
    product.setInboundPrice(BigDecimal.valueOf(1000000002));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // UTCID017: CreateProduct - SellPrice greater than 1000000000
  @Test
  public void UTCID017_testCreateProduct_Invalid_SellPrice() {
    // Arrange
    product.setSellPrice(BigDecimal.valueOf(1000000002));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.create(product));
  }

  // SearchProducts
  // UTCID01: SearchProducts - Valid
  @Test
  public void UTCID01_testSearchProducts_WithKeyword_DefaultSorting() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  // UTCID02: SearchProducts - Valid
  @Test
  public void UTCID02_testSearchProducts_WithManufacturerId() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  // UTCID03: SearchProducts - Valid
  @Test
  public void UTCID03_testSearchProducts_WithCategoryId() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  // UTCID04: SearchProducts - Valid
  @Test
  public void UTCID04_testSearchProducts_WithTypeId() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  // UTCID05: SearchProducts - Valid
  @Test
  public void UTCID05_testSearchProducts_WithStatus() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(productBaseDTO, result.getContent().get(0));
  }

  // UTCID06: SearchProducts - Valid
  @Test
  public void UTCID06_testSearchProducts_DifferentSortingDirections() {
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

    when(productRepository.findAll(any(Specification.class), eq(pageableAsc)))
        .thenReturn(productEntitiesAsc);
    when(productRepository.findAll(any(Specification.class), eq(pageableDesc)))
        .thenReturn(productEntitiesDesc);
    when(productMapper.convertToProductBaseDTO(productEntity)).thenReturn(productBaseDTO);

    // Act
    Page<ProductBaseDTO> resultAsc =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirectionAsc,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);
    Page<ProductBaseDTO> resultDesc =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirectionDesc,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(resultAsc);
    assertEquals(1, resultAsc.getTotalElements());
    assertEquals(productBaseDTO, resultAsc.getContent().get(0));

    assertNotNull(resultDesc);
  }

  // UTCID07: SearchProducts - Valid
  @Test
  public void UTCID07_testSearchProducts_StatusNotEqualDA_XOA() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.empty();
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productPage =
        new PageImpl<>(Stream.of(productEntity).collect(Collectors.toList()));
    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(new ProductBaseDTO());

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
  }

  // UTCID08: SearchProducts - Valid
  @Test
  public void UTCID08_testSearchProducts_WithKeyword() {
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

    Page<ProductEntity> productPage =
        new PageImpl<>(Stream.of(productEntity).collect(Collectors.toList()));
    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(new ProductBaseDTO());

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  // UTCID09: SearchProducts - Valid
  @Test
  public void UTCID09_testSearchProducts_WithManufacturerId() {
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.empty();
    Optional<Long> manufacturerId = Optional.of(100L);
    Optional<Long> categoryId = Optional.empty();
    Optional<Long> typeId = Optional.empty();
    Optional<String> status = Optional.empty();

    Page<ProductEntity> productPage =
        new PageImpl<>(Stream.of(productEntity).collect(Collectors.toList()));
    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(new ProductBaseDTO());

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  // UTCID10: SearchProducts - ValidWithCriteria
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  public void UTCID10_testSearchProducts_WithCriteria() {
    // Initialize mock data
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Test Product");
    productEntity.setRegistrationCode("REG123");
    productEntity.setActiveIngredient("Ingredient");
    productEntity.setStatus(ProductStatus.CON_HANG);

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Test Product");
    productBaseDTO.setRegistrationCode("REG123");
    productBaseDTO.setActiveIngredient("Ingredient");
    // Arrange
    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "productName";
    String sortDirection = "ASC";
    Optional<String> keyword = Optional.of("Test");
    Optional<Long> manufacturerId = Optional.of(1L);
    Optional<Long> categoryId = Optional.of(1L);
    Optional<Long> typeId = Optional.of(1L);
    Optional<String> status = Optional.of("CON_HANG");

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, sortBy));
    Page<ProductEntity> productPage = new PageImpl<>(List.of(productEntity), pageable, 1);

    when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(productPage);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTO);

    // Capture the Specification argument
    ArgumentCaptor<Specification<ProductEntity>> specCaptor =
        ArgumentCaptor.forClass(Specification.class);

    // Act
    Page<ProductBaseDTO> result =
        productService.searchProducts(
            pageNo,
            pageSize,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals("Test Product", result.getContent().get(0).getProductName());

    verify(productRepository).findAll(specCaptor.capture(), any(Pageable.class));
    Specification<ProductEntity> capturedSpec = specCaptor.getValue();

    // Verify the Specifications are invoked without detailed path mocks
    assertNotNull(capturedSpec);

    // Mock Root, CriteriaQuery, and CriteriaBuilder
    Root<ProductEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    // Simplified mock behavior to handle specifications without deep path mocks
    when(root.get(anyString())).thenReturn(mock(Path.class));
    when(criteriaBuilder.or(any(Predicate[].class))).thenReturn(mock(Predicate.class));
    when(criteriaBuilder.like(any(Path.class), anyString())).thenReturn(mock(Predicate.class));
    when(criteriaBuilder.equal(any(Path.class), any())).thenReturn(mock(Predicate.class));
    when(criteriaBuilder.notEqual(any(Path.class), anyString())).thenReturn(mock(Predicate.class));

    Predicate predicate = capturedSpec.toPredicate(root, query, criteriaBuilder);

    // Validate that the captured specification created the predicate
    assertNull(predicate);
  }

  // UpdateProduct
  // UTCID01: UpdateProduct - Valid
  @Test
  public void UTCID01testUpdateProduct_Success() {
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
    when(specialConditionMapper.toEntity(any(SpecialCondition.class)))
        .thenReturn(specialConditionEntity);
    when(unitConversionMapper.toEntity(any(UnitConversion.class))).thenReturn(unitConversionEntity);
    lenient().when(userService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
    lenient().when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    lenient()
        .when(branchMapper.toEntity(any(Branch.class)))
        .thenReturn(new BranchEntity()); // Mock branchMapper
    lenient()
        .when(storageLocationMapper.toEntity(any(StorageLocation.class)))
        .thenReturn(new StorageLocationEntity()); // Mock storageLocationMapper if used
    when(unitConversionService.getByProductId(anyLong()))
        .thenReturn(List.of(unitConversionEntity)); // Ensure the return value is set

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

  // UTCID02: UpdateProduct - inValid NullProduct
  @Test
  public void UTCID02_testUpdateProduct_NullProduct_ThrowsException() {
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(null));
  }

  // UTCID03: UpdateProduct - inValid InvalidProduct
  @Test
  public void UTCID03_testUpdateProduct_InvalidProduct_ThrowsException() {
    // Arrange
    product.setProductName(null); // Invalid field

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID04: UpdateProduct - inValid ProductNotExist
  @Test
  public void UTCID04_testUpdateProduct_ProductNotExist_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID05: UpdateProduct - inValid RegistrationCodeAlreadyExists
  @Test
  public void UTCID05_testUpdate_RegistrationCodeAlreadyExists() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Test Product");
    productEntity.setActiveIngredient("Ingredient");
    productEntity.setFormulation("Formulation");
    productEntity.setRegistrationCode("OLD_REG123");

    // Arrange
    Product updatedProduct = new Product();
    updatedProduct.setId(1L);
    updatedProduct.setProductName("Updated Product");
    updatedProduct.setActiveIngredient("Ingredient");
    updatedProduct.setFormulation("Formulation");
    updatedProduct.setRegistrationCode("NEW_REG123");

    // Mock product retrieval
    lenient().when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    lenient().when(productRepository.existsByRegistrationCode("NEW_REG123")).thenReturn(true);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(HrmCommonException.class, () -> productService.update(updatedProduct));
    assertEquals(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST, exception.getMessage());
  }

  // UTCID06: UpdateProduct - inValid NonExistentType
  @Test
  public void UTCID06_testUpdateProduct_NonExistentType_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID07: UpdateProduct - inValid NonExistentCategory
  @Test
  public void UTCID07_testUpdateProduct_NonExistentCategory_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID08: UpdateProduct - inValid NonExistentBaseUnit
  @Test
  public void UTCID08_testUpdateProduct_NonExistentBaseUnit_ThrowsException() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    when(productTypeService.existById(anyLong())).thenReturn(true);
    when(productCategoryService.existById(anyLong())).thenReturn(true);
    when(unitOfMeasurementService.existById(anyLong())).thenReturn(false);

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID09: UpdateProduct - inValid NonExistentManufacturer
  @Test
  public void UTCID09t_estUpdateProduct_NonExistentManufacturer_ThrowsException() {
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

  // UTCID010: UpdateProduct - RegistrationCode address greater than 30 characters
  @Test
  public void UTCID010_testUpdateProduct_Invalid_RegistrationCode() {
    // Arrange
    product.setRegistrationCode("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID011: UpdateProduct - Excipient greater than 255 characters
  @Test
  public void UTCID011_testUpdateProduct_Invalid_Excipient() {
    // Arrange
    product.setExcipient("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID012: UpdateProduct - Formulation greater than 255 characters
  @Test
  public void UTCID012_testUpdateProduct_Invalid_Formulation() {
    // Arrange
    product.setFormulation("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID013: UpdateProduct - Ingredient greater than 255 characters
  @Test
  public void UTCID013_testUpdateProduct_Invalid_Ingredient() {
    // Arrange
    product.setActiveIngredient("a".repeat(10001));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID014: UpdateProduct - InboundPrice greater than 1000000000
  @Test
  public void UTCID014_testUpdateProduct_Invalid_InboundPrice() {
    // Arrange
    product.setInboundPrice(BigDecimal.valueOf(1000000002));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UTCID015: UpdateProduct - SellPrice greater than 1000000000
  @Test
  public void UTCID017_testUpdateProduct_Invalid_SellPrice() {
    // Arrange
    product.setSellPrice(BigDecimal.valueOf(1000000002));
    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.update(product));
  }

  // UpdateInboundPrice
  // UTCID01: UpdateProduct - valid
  @Test
  public void UTCID01_testUpdateInboundPrice_Success() {
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

  // UTCID02: UpdateProduct - invalid ProductNotFound
  @Test
  public void UTCID02_testUpdateInboundPrice_ProductNotFound() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(AssertionError.class, () -> productService.updateInboundPrice(product));
    verify(productRepository, times(1)).findById(anyLong());
    verify(productRepository, times(0)).save(any(ProductEntity.class));
    verify(productMapper, times(0)).toDTO(any(ProductEntity.class));
  }

  // DeleteProduct
  // UTCID01: DeleteProduct - Valid
  @Test
  public void UTCID01_testDeleteProduct_Success() {
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

  // UTCID02: DeleteProduct - inValid ProductNotFound
  @Test
  public void UTCID02_testDeleteProduct_ProductNotFound() {
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.delete(1L));
    verify(productRepository, times(0)).updateProductStatus(any(ProductStatus.class), anyLong());
  }

  // UTCID03: DeleteProduct - inValid AlreadyDeleted
  @Test
  public void UTCID03_testDeleteProduct_AlreadyDeleted() {
    // Arrange
    productEntity.setStatus(ProductStatus.DA_XOA);
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));

    // Act & Assert
    assertThrows(HrmCommonException.class, () -> productService.delete(1L));
    verify(productRepository, times(0)).updateProductStatus(any(ProductStatus.class), anyLong());
  }

  // ImportFile
  // UTCID01: ImportFile - Valid
  @Test
  public void testImportFile_Success() throws IOException {
    // Arrange
    AllowedProductEntity allowedProductEntity = new AllowedProductEntity();
    allowedProductEntity.setProductName("AllowedProductName");
    allowedProductEntity.setRegistrationCode("REG123");
    allowedProductEntity.setActiveIngredient("Ingredient");
    allowedProductEntity.setExcipient("Excipient");
    allowedProductEntity.setFormulation("Formulation");
    when(allowedProductService.getAllowedProductByCode("REG123")).thenReturn(allowedProductEntity);

    ProductCategory category = new ProductCategory();
    when(productCategoryService.findByCategoryName("CategoryName")).thenReturn(category);

    ProductType type = new ProductType();
    when(productTypeService.getByName("TypeName")).thenReturn(type);

    UnitOfMeasurement unit = new UnitOfMeasurement();
    when(unitOfMeasurementService.getByName("UnitName")).thenReturn(unit);

    Manufacturer manufacturer = new Manufacturer();
    when(manufacturerService.getByName("ManufacturerName")).thenReturn(manufacturer);

    lenient().when(productRepository.existsByRegistrationCode(anyString())).thenReturn(false);
    lenient().when(userService.getAuthenticatedUserEmail()).thenReturn("user@example.com");
    User user = new User();
    Branch branch = new Branch();
    user.setBranch(branch);
    lenient().when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(new ProductEntity());
    lenient().when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(new Product());

    // Mock BranchProductService and StorageLocationService behavior
    when(storageLocationService.save(any(StorageLocation.class))).thenReturn(new StorageLocation());
    when(branchProductService.save(any(BranchProduct.class))).thenReturn(null); // Corrected

    // Act
    List<String> errors = productService.importFile(file);

    // Assert
    assertTrue(errors.isEmpty());
    verify(allowedProductService, times(1)).getAllowedProductByCode("REG123");
    verify(productCategoryService, times(1)).findByCategoryName("CategoryName");
    verify(productTypeService, times(1)).getByName("TypeName");
    verify(unitOfMeasurementService, times(1)).getByName("UnitName");
    verify(manufacturerService, times(1)).getByName("ManufacturerName");
    verify(productRepository, times(1)).save(any(ProductEntity.class));
    verify(storageLocationService, times(1)).save(any(StorageLocation.class));
    verify(branchProductService, times(1)).save(any(BranchProduct.class));
  }

  // UTCID02: ImportFile - inValid data
  @Test
  public void UTCID02_testImportFile_InvalidData() throws IOException {
    // Arrange
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Sheet1");

    // Header row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("RegistrationCode");
    header.createCell(1).setCellValue("ProductName");
    header.createCell(2).setCellValue("CategoryName");
    header.createCell(3).setCellValue("TypeName");
    header.createCell(4).setCellValue("UnitName");
    header.createCell(5).setCellValue("ManufacturerName");
    header.createCell(6).setCellValue("MinQuantity");
    header.createCell(7).setCellValue("MaxQuantity");
    header.createCell(8).setCellValue("StorageLocation");

    // Data row with invalid data
    Row row = sheet.createRow(1);
    row.createCell(0).setCellValue(""); // Missing Registration Code
    row.createCell(1).setCellValue(""); // Missing Product Name

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    workbook.write(bos);
    workbook.close();
    file =
        new MockMultipartFile(
            "file",
            "invalid_products.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            bos.toByteArray());

    // Act
    List<String> errors = productService.importFile(file);

    // Assert
    assertFalse(errors.isEmpty());
    assertTrue(errors.contains("Product name is missing at row 2"));
    assertTrue(errors.contains("Registration Code is missing at row 2"));
    verify(productRepository, times(0)).save(any(ProductEntity.class));
  }

  //  @Test
  //  public void testImportFile_IOException() throws IOException {
  //    // Arrange
  //    MultipartFile invalidFile = new MockMultipartFile("file", "products.xlsx",
  // "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
  //
  //    // Act
  //    List<String> errors = productService.importFile(invalidFile);
  //
  //    // Assert
  //    assertFalse(errors.isEmpty());
  //    assertTrue(errors.contains("Failed to parse Excel file: The supplied file was empty (zero
  // bytes long)"));
  //  }

  // UTCID03: ImportFile - inValid data
  @Test
  public void UTCID03_testImportFile_ErrorSavingProducts() throws IOException {
    // Arrange
    AllowedProductEntity allowedProductEntity = new AllowedProductEntity();
    allowedProductEntity.setProductName("AllowedProductName");
    allowedProductEntity.setRegistrationCode("REG123");
    allowedProductEntity.setActiveIngredient("Ingredient");
    allowedProductEntity.setExcipient("Excipient");
    allowedProductEntity.setFormulation("Formulation");
    lenient()
        .when(allowedProductRepository.findByProductCode("REG123"))
        .thenReturn(allowedProductEntity);

    ProductCategory category = new ProductCategory();
    lenient().when(productCategoryService.findByCategoryName("CategoryName")).thenReturn(category);

    ProductType type = new ProductType();
    lenient().when(productTypeService.getByName("TypeName")).thenReturn(type);

    UnitOfMeasurement unit = new UnitOfMeasurement();
    lenient().when(unitOfMeasurementService.getByName("UnitName")).thenReturn(unit);

    Manufacturer manufacturer = new Manufacturer();
    lenient().when(manufacturerService.getByName("ManufacturerName")).thenReturn(manufacturer);

    lenient()
        .when(productRepository.save(any(ProductEntity.class)))
        .thenThrow(new RuntimeException("Database error"));

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
  }

  // ExportFile
  // UTCID01: ExportFile - Valid
  @Test
  public void UTCID01_testExportFile_Success() throws IOException {

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
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productDTOs.get(0));

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

  // UTCID02: ExportFile - inValid ThrowsIOException
  @Test
  public void UTCID02_testExportFileThrowsIOException() throws IOException {
    // Arrange
    List<ProductEntity> productEntities = new ArrayList<>();
    productEntities.add(productEntity);

    when(productRepository.findAll()).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTO);

    try (MockedStatic<ExcelUtility> utilities = Mockito.mockStatic(ExcelUtility.class)) {
      utilities
          .when(() -> ExcelUtility.exportToExcelWithErrors(anyList(), any(), any()))
          .thenThrow(IOException.class);

      // Act & Assert
      Exception exception =
          assertThrows(
              RuntimeException.class,
              () -> {
                productService.exportFile();
              });

      String expectedMessage = "Error exporting product data to Excel";
      String actualMessage = exception.getMessage();

      assertTrue(actualMessage.contains(expectedMessage));
    }

    verify(productRepository, times(1)).findAll();
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // GetAllProductsBySupplier
  // UTCID01: GetAllProductsBySupplier - Valid
  @Test
  public void UTCID01_testGetAllProductsBySupplier_Success() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    entity.setProductSuppliers(new ArrayList<>());
    productEntities.add(entity);

    // Initialize ProductSupplierDTO
    ProductSupplierDTO dto = new ProductSupplierDTO();
    dto.setProductName("Product 1");
    productSupplierDTOs.add(dto);
    // Arrange
    when(productRepository.findProductBySupplierAndName(anyLong(), anyString()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductSupplier(any(ProductEntity.class)))
        .thenReturn(productSupplierDTOs.get(0));

    // Act
    List<ProductSupplierDTO> result = productService.getAllProductsBySupplier(1L, "Product 1");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productSupplierDTOs.get(0), result.get(0));

    verify(productRepository, times(1)).findProductBySupplierAndName(anyLong(), anyString());
    verify(productMapper, times(1)).convertToProductSupplier(any(ProductEntity.class));
  }

  // AddProductInInbound
  // UTCID01: AddProductInInbound - Valid
  @Test
  public void UTCID01_testAddProductInInbound_ProductExists() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setRegistrationCode("RC123");
    productEntity.setProductName("Existing Product");

    product = new Product();
    product.setId(1L);
    product.setRegistrationCode("RC123");
    product.setProductName("Existing Product");

    productInbound = new ProductInbound();
    productInbound.setRegistrationCode("RC123");
    productInbound.setProductName("New Product");
    productInbound.setBaseUnit(new UnitOfMeasurement());
    // Arrange
    when(productRepository.findByRegistrationCode(anyString()))
        .thenReturn(Optional.of(productEntity));
    when(productMapper.convertToBaseInfo(any(ProductEntity.class))).thenReturn(product);

    // Act
    Product result = productService.addProductInInbound(productInbound);

    // Assert
    assertNotNull(result);
    assertEquals("RC123", result.getRegistrationCode());
    assertEquals("Existing Product", result.getProductName());

    verify(productRepository, times(1)).findByRegistrationCode(anyString());
    verify(productMapper, times(1)).convertToBaseInfo(any(ProductEntity.class));
    verify(productMapper, times(0)).toDTO(any(ProductEntity.class));
    verify(productRepository, times(0)).save(any(ProductEntity.class));
  }

  // UTCID02: AddProductInInbound - inValid ProductNotExists
  @Test
  public void UTCID02_testAddProductInInbound_ProductNotExists() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setRegistrationCode("RC123");
    productEntity.setProductName("New Product");

    product = new Product();
    product.setId(1L);
    product.setRegistrationCode("RC123");
    product.setProductName("New Product");

    productInbound = new ProductInbound();
    productInbound.setRegistrationCode("RC123");
    productInbound.setProductName("New Product");
    productInbound.setBaseUnit(new UnitOfMeasurement());
    // Arrange
    when(productRepository.findByRegistrationCode(anyString())).thenReturn(Optional.empty());
    when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    when(productMapper.toDTO(any(ProductEntity.class))).thenReturn(product);
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

    // Act
    Product result = productService.addProductInInbound(productInbound);

    // Assert
    assertNotNull(result);
    assertEquals("RC123", result.getRegistrationCode());
    assertEquals("New Product", result.getProductName());

    verify(productRepository, times(1)).findByRegistrationCode(anyString());
    verify(productMapper, times(0)).convertToBaseInfo(any(ProductEntity.class));
    verify(productMapper, times(1)).toDTO(any(ProductEntity.class));
    verify(productRepository, times(1)).save(any(ProductEntity.class));
  }

  // GetProductInBranch
  // UTCID01: GetProductInBranch - Valid
  @Test
  public void UTCID01_testGetProductInBranch_Success() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);
    // Arrange
    when(productRepository.searchProductByBranchId(anyLong(), anyString(), anyBoolean(), anyLong()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductForSearchInNotes(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getProductInBranch(1L, "Product 1", true, 1L);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(productRepository, times(1))
        .searchProductByBranchId(anyLong(), anyString(), anyBoolean(), anyLong());
    verify(productMapper, times(1))
        .convertToProductForSearchInNotes(any(ProductEntity.class), anyLong());
  }

  // GetBranchProduct
  // UTCID01: GetBranchProduct - Valid
  @Test
  public void UTCID01_testGetBranchProduct_WithoutSellPrice() { // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);

    // Arrange
    when(productRepository.searchProductByBranchId(anyLong(), anyString(), anyBoolean(), anyLong()))
        .thenReturn(productEntities);
    when(productMapper.convertToBranchProduct(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getBranchProduct(1L, "Product 1", true, 1L, false);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(productRepository, times(1))
        .searchProductByBranchId(anyLong(), anyString(), anyBoolean(), anyLong());
    verify(productRepository, times(0))
        .searchProductByBranchIdWithSellPrice(anyLong(), anyString());
    verify(productMapper, times(1)).convertToBranchProduct(any(ProductEntity.class), anyLong());
  }

  // UTCID02: GetBranchProduct - Valid
  @Test
  public void UTCID02_testGetBranchProduct_WithSellPrice() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);
    // Arrange
    when(productRepository.searchProductByBranchIdWithSellPrice(anyLong(), anyString()))
        .thenReturn(productEntities);
    when(productMapper.convertToBranchProduct(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getBranchProduct(1L, "Product 1", true, 1L, true);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(productRepository, times(0))
        .searchProductByBranchId(anyLong(), anyString(), anyBoolean(), anyLong());
    verify(productRepository, times(1))
        .searchProductByBranchIdWithSellPrice(anyLong(), anyString());
    verify(productMapper, times(1)).convertToBranchProduct(any(ProductEntity.class), anyLong());
  }

  // UTCID03: GetBranchProduct - Valid
  @Test
  public void UTCID03_testGetBranchProducts_Success() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product 1");

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Product 1");
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(productEntity));
    when(productMapper.convertToBranchProduct(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTO);

    // Act
    ProductBaseDTO result = productService.getBranchProducts(1L, 1L);

    // Assert
    assertNotNull(result);
    assertEquals(productBaseDTO, result);

    verify(productRepository, times(1)).findById(anyLong());
    verify(productMapper, times(1)).convertToBranchProduct(any(ProductEntity.class), anyLong());
  }

  // UTCID04: GetBranchProduct - inValid ProductNotFound
  @Test
  public void UTCID04_testGetBranchProducts_ProductNotFound() {
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product 1");

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Product 1");
    // Arrange
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Act
    ProductBaseDTO result = productService.getBranchProducts(1L, 1L);

    // Assert
    assertNull(result);

    verify(productRepository, times(1)).findById(anyLong());
    verify(productMapper, times(0)).convertToBranchProduct(any(ProductEntity.class), anyLong());
  }

  // ProcessProductData
  // UTCID01: ProcessProductData - Valid
  @Test
  public void UTCID01_testProcessProductData_Success() {
    // Arrange
    List<ProductBaseDTO> products = new ArrayList<>();

    // Create mock ProductBaseDTO
    ProductBaseDTO product = new ProductBaseDTO();
    product.setId(1L);
    product.setProductName("Product 1");
    product.setProductBaseUnit(new UnitOfMeasurement());
    product.setProductQuantity(BigDecimal.valueOf(10));

    // Create mock BatchDTO
    Batch batch = new Batch();
    batch.setId(1L);
    batch.setBatchCode("Batch1");
    batch.setQuantity(BigDecimal.valueOf(5));

    List<Batch> batches = new ArrayList<>();
    batches.add(batch);

    product.setBatches(batches);
    products.add(product);

    // Act
    List<ProductBatchDTO> result = productService.processProductData(products);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    // Verify the first ProductBatchDTO
    ProductBatchDTO productBatch1 = result.get(0);
    assertEquals(1L, productBatch1.getProduct().getId());
    assertEquals("Product 1", productBatch1.getProduct().getProductName());
    assertEquals(BigDecimal.valueOf(10), productBatch1.getSystemQuantity());

    // Verify the second ProductBatchDTO
    ProductBatchDTO productBatch2 = result.get(1);
    assertEquals(1L, productBatch2.getProduct().getId());
    assertEquals("Product 1", productBatch2.getProduct().getProductName());
    assertEquals(1L, productBatch2.getBatch().getId());
    assertEquals("Batch1", productBatch2.getBatch().getBatchCode());
    assertEquals(BigDecimal.valueOf(5), productBatch2.getSystemQuantity());
  }

  // GetProductInBranchForInventoryCheck
  // UTCID01: GetProductInBranchForInventoryCheck - Valid
  @Test
  public void UTCID01_testGetProductInBranchForInventoryCheck_Success() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setBatches(new ArrayList<>()); // Ensure batches is initialized
    productBaseDTOs.add(dto);
    // Arrange
    when(productRepository.searchAllProductByBranchId(anyLong(), anyString(), any(), any()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductForSearchInNotes(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBatchDTO> result = productService.getProductInBranchForInventoryCheck(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getProduct().getId());
    assertEquals("Product 1", result.get(0).getProduct().getProductName());

    verify(productRepository, times(1))
        .searchAllProductByBranchId(anyLong(), anyString(), any(), any());
    verify(productMapper, times(1))
        .convertToProductForSearchInNotes(any(ProductEntity.class), anyLong());
    // No need to verify processProductData, as it's called internally
  }

  // GetProductByTypeIdInBranchForInventoryCheck
  // UTCID01: GetProductByTypeIdInBranchForInventoryCheck - Valid
  @Test
  public void UTCID01_testGetProductByTypeIdInBranchForInventoryCheck_Success() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    productBaseDTOs = new ArrayList<>();
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setBatches(new ArrayList<>()); // Ensure batches is initialized
    productBaseDTOs.add(dto);
    // Arrange
    when(productRepository.searchAllProductByBranchIdAndTypeId(
            anyLong(), anyLong(), anyString(), any(), any()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductForSearchInNotes(any(ProductEntity.class), anyLong()))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBatchDTO> result =
        productService.getProductByTypeIdInBranchForInventoryCheck(1L, 1L);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).getProduct().getId());
    assertEquals("Product 1", result.get(0).getProduct().getProductName());

    verify(productRepository, times(1))
        .searchAllProductByBranchIdAndTypeId(anyLong(), anyLong(), anyString(), any(), any());
    verify(productMapper, times(1))
        .convertToProductForSearchInNotes(any(ProductEntity.class), anyLong());
    // No need to verify processProductData, as it's called internally
  }

  // RemoveCategoryFromProducts
  // UTCID01: RemoveCategoryFromProducts - Valid
  @Test
  public void UTCID01_testRemoveCategoryFromProducts() {
    // Arrange
    Long cateId = 1L;

    // Act
    productService.removeCategoryFromProducts(cateId);

    // Assert
    verify(productRepository, times(1)).removeCategoryFromProducts(cateId);
  }

  // RemoveTypeFromProducts
  // UTCID01: RemoveTypeFromProducts - Valid
  @Test
  public void UTCID01_testRemoveTypeFromProducts() {
    // Arrange
    Long typeId = 1L;

    // Act
    productService.removeTypeFromProducts(typeId);

    // Assert
    verify(productRepository, times(1)).removeTypeFromProducts(typeId);
  }

  @Test
  public void UTCID01_testFilterProducts_LessThanOrEqual() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setProductQuantity(BigDecimal.valueOf(5));
    productBaseDTOs.add(dto);

    // Create Pageable and Page mock
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
    Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities, pageable, productEntities.size());

    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;
    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));

    when(productRepository.findByQuantityLessThanEqualInBranch(anyInt(), anyLong()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    Page<ProductBaseDTO> result = productService.filterProducts(true, 10, false, false, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(productBaseDTOs.get(0), result.getContent().get(0));
    assertEquals(1, result.getTotalElements());

    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1)).findByQuantityLessThanEqualInBranch(anyInt(), anyLong());
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // UTCID02: FilterProducts - inValid
  @Test
  public void UTCID02_testFilterProducts_Warning() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setProductQuantity(BigDecimal.valueOf(5));
    productBaseDTOs.add(dto);

    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;
    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));

    // Create Pageable and Page mock
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
    Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities, pageable, productEntities.size());
    when(productRepository.findByQuantityLessThanMinQuantityInBranch(eq(branchId)))
        .thenReturn(productEntities);

    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    Page<ProductBaseDTO> result = productService.filterProducts(false, null, true, false, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(productBaseDTOs.get(0), result.getContent().get(0));
    assertEquals(1, result.getTotalElements());

    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1)).findByQuantityLessThanMinQuantityInBranch(eq(branchId));
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // UTCID03: FilterProducts - Valid
  @Test
  public void UTCID03_testFilterProducts_OutOfStock() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    productBaseDTOs = new ArrayList<>();
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setProductQuantity(BigDecimal.ZERO); // Out of stock
    productBaseDTOs.add(dto);

    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;
    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));

    // Create Pageable and Page mock
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
    Page<ProductEntity> productEntityPage = new PageImpl<>(productEntities, pageable, productEntities.size());

    when(productRepository.findByQuantityInBranch(0, branchId)).thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    Page<ProductBaseDTO> result = productService.filterProducts(false, null, false, true, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(productBaseDTOs.get(0), result.getContent().get(0));
    assertEquals(1, result.getTotalElements());

    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1)).findByQuantityInBranch(0, branchId);
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // UTCID04: FilterProducts - Valid
  @Test
  public void UTCID04_testFilterProducts_AllFilters() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);

    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;

    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));

    // Mock repository responses for all filters
    when(productRepository.findByQuantityLessThanEqualInBranch(anyInt(), anyLong()))
        .thenReturn(productEntities);
    when(productRepository.findByQuantityLessThanMinQuantityInBranch(anyLong()))
        .thenReturn(productEntities);
    when(productRepository.findByQuantityInBranch(eq(0), anyLong()))
        .thenReturn(productEntities);

    // Mock mapper
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(dto);

    // Create Pageable
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

    // Act
    Page<ProductBaseDTO> result = productService.filterProducts(true, 10, true, true, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(dto, result.getContent().get(0));
    assertEquals(1, result.getTotalElements());

    // Verify service and repository interactions
    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1))
        .findByQuantityLessThanEqualInBranch(anyInt(), anyLong());
    verify(productRepository, times(1))
        .findByQuantityLessThanMinQuantityInBranch(anyLong());
    verify(productRepository, times(1))
        .findByQuantityInBranch(eq(0), anyLong());
    verify(productMapper, times(3)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // GetProductsWithLossOrNoSellPriceInBranch
  // UTCID01: GetProductsWithLossOrNoSellPriceInBranch - Valid
  @Test
  public void UTCID01_testGetProductsWithLossOrNoSellPriceInBranch() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);
    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;
    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));

    when(productRepository.findProductsWithLossOrNoSellPriceInBranch(anyLong()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getProductsWithLossOrNoSellPriceInBranch();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1)).findProductsWithLossOrNoSellPriceInBranch(anyLong());
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // GetProductsBySellPrice
  // UTCID01: GetProductsBySellPrice - Valid
  @Test
  public void UTCID01_testGetProductsBySellPrice() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    entity.setSellPrice(BigDecimal.valueOf(50));
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    dto.setSellPrice(BigDecimal.valueOf(50));
    productBaseDTOs.add(dto);
    // Arrange
    String userEmail = "test@example.com";
    Long branchId = 1L;
    BigDecimal sellPrice = BigDecimal.valueOf(50);

    when(userService.getAuthenticatedUserEmail()).thenReturn(userEmail);
    when(userService.findBranchIdByUserEmail(userEmail)).thenReturn(Optional.of(branchId));
    when(productRepository.findProductsBySellPrice(sellPrice, branchId))
        .thenReturn(productEntities);
    when(productMapper.convertToProductBaseDTO(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getProductsBySellPrice(sellPrice);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(userService, times(1)).getAuthenticatedUserEmail();
    verify(userService, times(1)).findBranchIdByUserEmail(userEmail);
    verify(productRepository, times(1)).findProductsBySellPrice(sellPrice, branchId);
    verify(productMapper, times(1)).convertToProductBaseDTO(any(ProductEntity.class));
  }

  // GetProductByCateInBranchForInventoryCheck
  // UTCID01: GetProductByCateInBranchForInventoryCheck - Valid
  @Test
  public void UTCID01_testGetProductByCateInBranchForInventoryCheck() {
    // Wrap actual instance in a spy
    productService = Mockito.spy(productService);

    // Initialize mock data
    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Test Product");

    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setId(1L);
    productBaseDTO.setProductName("Test Product");
    // Arrange
    Long branchId = 1L;
    Long cateId = 1L;
    List<ProductEntity> productEntities = new ArrayList<>();
    productEntities.add(productEntity);

    List<ProductBaseDTO> productBaseDTOs = new ArrayList<>();
    productBaseDTOs.add(productBaseDTO);

    when(productRepository.searchAllProductByBranchIdAndCateId(
            eq(branchId), eq(cateId), eq(""), isNull(), isNull()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductForSearchInNotes(any(ProductEntity.class), eq(branchId)))
        .thenReturn(productBaseDTO);
    doReturn(new ArrayList<ProductBatchDTO>()).when(productService).processProductData(anyList());

    // Act
    List<ProductBatchDTO> result =
        productService.getProductByCateInBranchForInventoryCheck(branchId, cateId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size()); // Assuming processProductData returns an empty list

    verify(productRepository, times(1))
        .searchAllProductByBranchIdAndCateId(eq(branchId), eq(cateId), eq(""), isNull(), isNull());
    verify(productMapper, times(1))
        .convertToProductForSearchInNotes(any(ProductEntity.class), eq(branchId));
    verify(productService, times(1)).processProductData(anyList());
  }

  // GetByKeyword
  // UTCID01: GetByKeyword - Valid
  @Test
  public void UTCID01_testGetByKeyword() {
    // Initialize ProductEntity
    ProductEntity entity = new ProductEntity();
    entity.setId(1L);
    entity.setProductName("Product 1");
    productEntities.add(entity);

    // Initialize ProductBaseDTO
    ProductBaseDTO dto = new ProductBaseDTO();
    dto.setId(1L);
    dto.setProductName("Product 1");
    productBaseDTOs.add(dto);
    // Arrange
    String keyword = "product";

    when(productRepository.findProductEntitiesByProductNameIgnoreCase(anyString()))
        .thenReturn(productEntities);
    when(productMapper.convertToProductDto(any(ProductEntity.class)))
        .thenReturn(productBaseDTOs.get(0));

    // Act
    List<ProductBaseDTO> result = productService.getByKeyword(keyword);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(productBaseDTOs.get(0), result.get(0));

    verify(productRepository, times(1)).findProductEntitiesByProductNameIgnoreCase(keyword);
    verify(productMapper, times(1)).convertToProductDto(any(ProductEntity.class));
  }

  // GetProductDetailsInPeriod
  // UTCID01: GetProductDetailsInPeriod - Valid
  @Test
  public void UTCID01_testGetProductDetailsInPeriod() {
    // Arrange
    Long productId = 1L;
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();

    when(inboundDetailsService.getInboundDetailsByProductIdAndPeriod(anyLong(), any(), any()))
        .thenReturn(inboundDetailEntities);
    when(inboundDetailsMapper.toAudit(any())).thenReturn(auditHistories.get(0));

    // Mock other services to return empty lists
    when(inboundBatchDetailService.getInboundBatchDetailsByProductIdAndPeriod(
            anyLong(), any(), any()))
        .thenReturn(batchInboundDetailEntities);
    when(outboundDetailService.getOutboundDetailsByProductIdAndPeriod(anyLong(), any(), any()))
        .thenReturn(batchOutboundDetailEntities);
    when(outboundProductDetailService.getOutboundProductDetailsByProductIdAndPeriod(
            anyLong(), any(), any()))
        .thenReturn(productOutboundDetailEntities);

    // Act
    List<AuditHistory> result =
        productService.getProductDetailsInPeriod(productId, startDate, endDate);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(auditHistories.get(0), result.get(0));

    // Verify only the necessary interactions
    verify(inboundDetailsService, times(1))
        .getInboundDetailsByProductIdAndPeriod(anyLong(), any(), any());
    verify(inboundDetailsMapper, times(1)).toAudit(any());
    verify(inboundBatchDetailService, times(1))
        .getInboundBatchDetailsByProductIdAndPeriod(anyLong(), any(), any());
    verify(outboundDetailService, times(1))
        .getOutboundDetailsByProductIdAndPeriod(anyLong(), any(), any());
    verify(outboundProductDetailService, times(1))
        .getOutboundProductDetailsByProductIdAndPeriod(anyLong(), any(), any());
  }

  // AddProductFromJson
  // UTCID01: AddProductFromJson - Valid
  @Test
  public void UTCID01_testAddProductFromJson() {
    // Arrange
    when(allowedProductRepository.save(any(AllowedProductEntity.class)))
        .thenReturn(allowedProductEntity);

    // Act
    List<AllowedProductEntity> result = productService.addProductFromJson(productJsonList);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(allowedProductEntity, result.get(0));

    verify(allowedProductRepository, times(1)).save(any(AllowedProductEntity.class));
  }

  // GetAllowProducts
  // UTCID01: GetAllowProducts - Valid
  @Test
  public void UTCID01_testGetAllowProducts() {
    // Arrange
    String searchStr = "Test";

    when(allowedProductRepository.findAllByProductNameContainsIgnoreCase(anyString()))
        .thenReturn(allowedProductEntities);

    // Act
    List<AllowedProductEntity> result = productService.getAllowProducts(searchStr);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(allowedProductEntities.get(0), result.get(0));

    verify(allowedProductRepository, times(1)).findAllByProductNameContainsIgnoreCase(searchStr);
  }
}
