package com.example.hrm_be.services.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.hrm_be.common.utils.TestUtils;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ProductStatus;
import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.SpecialConditionMapper;
import com.example.hrm_be.components.UnitConversionMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@Ignore
@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
  @InjectMocks private ProductServiceImpl productService; // Service to be tested

  @Mock private ProductRepository productRepository;
  @Mock private ProductMapper productMapper;
  @Mock private UserService userService;
  @Mock private ProductTypeService productTypeService;
  @Mock private ProductCategoryService productCategoryService;
  @Mock private UnitOfMeasurementService unitOfMeasurementService;
  @Mock private ManufacturerService manufacturerService;
  @Mock private SpecialConditionService specialConditionService;
  @Mock private UnitConversionService unitConversionService;
  @Mock private BranchProductService branchProductService;
  @Mock private StorageLocationService storageLocationService;
  @Mock private SpecialConditionMapper specialConditionMapper;
  @Mock private UnitConversionMapper unitConversionMapper;

  private Product product;
  private ProductEntity productEntity;
  private BranchProduct branchProduct;
  private ProductType productType;
  private ProductCategory productCategory;
  private UnitOfMeasurement unitOfMeasurement;
  private Manufacturer manufacturer;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    TestUtils.mockAuthenticatedUser("dsdadmin@gmail.com", RoleType.MANAGER);

    productType = new ProductType();
    productType.setId(1L);
    productType.setTypeName("type test");
    productType.setTypeDescription("type description test");
    productType = productTypeService.create(productType);

    productCategory = new ProductCategory();
    productCategory.setId(1L);
    productCategory.setCategoryName("category test");
    productCategory.setCategoryDescription("category description test");
    productCategory = productCategoryService.create(productCategory);

    unitOfMeasurement = new UnitOfMeasurement();
    unitOfMeasurement.setId(1L);
    unitOfMeasurement.setUnitName("unit test");
    unitOfMeasurement = unitOfMeasurementService.create(unitOfMeasurement);

    manufacturer = new Manufacturer();
    manufacturer.setId(1L);
    manufacturer.setManufacturerName("manufacturer test");
    manufacturer = manufacturerService.create(manufacturer);

    branchProduct = new BranchProduct();
    branchProduct.setQuantity(BigDecimal.valueOf(100));
    branchProduct.setMinQuantity(10);
    branchProduct.setMaxQuantity(200);
    branchProductService.save(branchProduct);
    ArrayList<BranchProduct> branchProductList = new ArrayList<>();
    branchProductList.add(branchProduct);

    // Sample Product entity and DTO setup
    product = new Product();
    product.setId(1L);
    product.setSellPrice(BigDecimal.valueOf(100));
    product.setRegistrationCode("REG001");
    product.setType(productType); // Assuming a product type exists
    product.setCategory(productCategory); // Assuming a category exists
    product.setBaseUnit(unitOfMeasurement); // Assuming base unit exists
    product.setStatus(ProductStatus.CON_HANG);
    product.setBranchProducts(new ArrayList<>(branchProductList));
    Product product1 = product.setManufacturer(manufacturer); // Assuming manufacturer exists

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setStatus(ProductStatus.CON_HANG);
  }

  @Test
  public void testGetById_ProductExistsAndStatusValid() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(productMapper.convertToDTOWithoutProductInBranchProduct(productEntity))
        .thenReturn(product);

    // Act
    Product result = productService.getById(1L);

    // Assert
    assertNotNull(result);
    verify(productRepository).findById(1L);
    verify(productMapper).convertToDTOWithoutProductInBranchProduct(productEntity);
  }

  @Test
  public void testGetById_ProductNotFound() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Product result = productService.getById(1L);

    // Assert
    assertNull(result);
    verify(productRepository).findById(1L);
  }

  @Test
  public void testGetById_ProductDeleted() {
    // Arrange
    ProductEntity deletedProduct = new ProductEntity();
    deletedProduct.setStatus(ProductStatus.DA_XOA);
    when(productRepository.findById(1L)).thenReturn(Optional.of(deletedProduct));

    // Act
    Product result = productService.getById(1L);

    // Assert
    assertNull(result);
    verify(productRepository).findById(1L);
  }

  @Test
  public void testCreate_ProductIsNull() {
    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(null);
            });
    assertEquals(HrmConstant.ERROR.REQUEST.INVALID_BODY, exception.getMessage());
  }

  @Test
  public void testCreate_ManagerWithSellPrice() {
    // Arrange
    when(userService.isManager()).thenReturn(true);
    product.setSellPrice(BigDecimal.valueOf(200));

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.ROLE.NOT_ALLOWED, exception.getMessage());
  }

  @Test
  public void testCreate_RegistrationCodeExists() {
    // Arrange
    when(productRepository.existsByRegistrationCode("REG001")).thenReturn(true);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.PRODUCT.REGISTRATION_EXIST, exception.getMessage());
  }

  @Test
  public void testCreate_ProductTypeNotExist() {
    // Arrange
    when(productTypeService.existById(1L)).thenReturn(false);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.TYPE.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testCreate_CategoryNotExist() {
    // Arrange
    when(productCategoryService.existById(1L)).thenReturn(false);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.CATEGORY.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testCreate_UnitOfMeasurementNotExist() {
    // Arrange
    when(unitOfMeasurementService.existById(1L)).thenReturn(false);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.UNIT_OF_MEASUREMENT.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testCreate_ManufacturerNotExist() {
    // Arrange
    when(manufacturerService.existById(1L)).thenReturn(false);

    // Act & Assert
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              productService.create(product);
            });
    assertEquals(HrmConstant.ERROR.MANUFACTURER.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testCreate_Success() {
    // Arrange
    when(productRepository.existsByRegistrationCode("REG001")).thenReturn(false);
    when(productTypeService.existById(1L)).thenReturn(true);
    when(productCategoryService.existById(1L)).thenReturn(true);
    when(unitOfMeasurementService.existById(1L)).thenReturn(true);
    when(manufacturerService.existById(1L)).thenReturn(true);
    when(productRepository.save(any())).thenReturn(productEntity);
    when(productMapper.toEntity(any())).thenReturn(productEntity);
    when(productMapper.toDTO(any())).thenReturn(product);

    // Act
    Product result = productService.create(product);

    // Assert
    assertNotNull(result);
    verify(productRepository).save(any());
    verify(specialConditionService).saveAll(any());
    verify(unitConversionService).saveAll(any());
    verify(branchProductService).save(any());
  }
}
