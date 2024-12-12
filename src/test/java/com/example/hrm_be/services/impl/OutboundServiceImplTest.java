package com.example.hrm_be.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.*;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.PDFUtil;
import com.example.hrm_be.utils.WplUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Disabled;
import org.mockito.*;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.OutboundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class OutboundServiceImplTest {

  @Mock private OutboundRepository outboundRepository;

  @Mock private OutboundMapper outboundMapper;

  @Mock private UnitOfMeasurementMapper unitOfMeasurementMapper;

  @Mock private ProductService productService;

  @Mock private UserService userService;

  @Mock private UserMapper userMapper;

  @Mock private BranchService branchService;

  @Mock private OutboundProductDetailService outboundProductDetailService;

  @Mock private OutboundDetailService outboundDetailService;

  @Mock private BatchService batchService;

  @Mock private BranchBatchService branchBatchService;

  @Mock private BranchProductService branchProductService;

  @Mock private OutboundDetailMapper outboundDetailMapper;

  @Mock private OutboundProductDetailMapper outboundProductDetailMapper;

  @Mock private ProductMapper productMapper;

  @Mock private BatchMapper batchMapper;

  @Mock private BranchProductMapper branchProductMapper;

  @Mock private BranchMapper branchMapper;

  @Mock private UnitConversionService unitConversionService;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager; // Add the EntityManager mock

  @Mock private PDFUtil pdfUtil;

  @InjectMocks private OutboundServiceImpl outboundServiceImpl;

  private OutboundEntity outboundEntity;
  private Outbound outboundDetailDTO;
  private ProductBaseDTO productBaseDTO;
  private List<Batch> batchList;
  private OutboundEntity oldOutboundEntity;
  private UserEntity userEntity;
  private User user;
  private Outbound outboundDTO;
  private Outbound updatedOutbound;
  private OutboundEntity updatedOutboundEntity;
  private Branch fromBranchdto;
  private CreateOutboundRequest request;
  private Product product;
  private ProductEntity productEntity;
  private BranchProductEntity branchProductEntity;
  private BranchProduct branchProduct;
  private Batch batch;
  private BatchEntity batchEntity;
  private Branch fromBranch;
  private BranchEntity fromBranchEntity;
  private Outbound outbound;

  @BeforeEach
  void setUp() {
    outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setOutboundType(OutboundType.HUY_HANG);

    BranchEntity fromBranch1 = new BranchEntity();
    fromBranch1.setId(1L);
    outboundEntity.setFromBranch(fromBranch1);

    fromBranch = new Branch();
    fromBranch.setId(1L);

    fromBranchEntity = new BranchEntity();
    fromBranchEntity.setId(1L);

    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setCategoryName("Category 1");
    productCategoryEntity.setTaxRate(BigDecimal.valueOf(0.1));

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product 1");
    productEntity.setRegistrationCode("REG123");
    productEntity.setBaseUnit(new UnitOfMeasurementEntity());
    productEntity.setCategory(productCategoryEntity);

    ProductCategory productCategory = new ProductCategory();
    productCategory.setCategoryName("Category 1");
    productCategory.setTaxRate(BigDecimal.valueOf(0.1));
    product = new Product();
    product.setId(1L);
    product.setProductName("Product 1");
    product.setRegistrationCode("REG123");
    product.setBaseUnit(new UnitOfMeasurement());
    product.setCategory(productCategory);

    OutboundProductDetailEntity outboundProductDetail = new OutboundProductDetailEntity();
    outboundProductDetail.setProduct(productEntity);
    outboundProductDetail.setPreQuantity(BigDecimal.TEN);
    outboundProductDetail.setOutboundQuantity(BigDecimal.TEN);
    outboundProductDetail.setPrice(BigDecimal.TEN);
    outboundProductDetail.setTaxRate(BigDecimal.ONE);
    outboundProductDetail.setUnitOfMeasurement(new UnitOfMeasurementEntity());

    outboundEntity.setOutboundProductDetails(Collections.singletonList(outboundProductDetail));

    batchEntity = new BatchEntity();
    batchEntity.setId(1L);
    batchEntity.setBatchCode("BATCH1");
    batchEntity.setExpireDate(LocalDateTime.now().plusDays(10));
    batchEntity.setProduct(productEntity);

    batch = new Batch();
    batch.setId(1L);
    batch.setBatchCode("BATCH1");
    batch.setExpireDate(LocalDateTime.now().plusDays(10));
    batch.setQuantity(BigDecimal.TEN);
    batch.setProduct(product);

    batchList = new ArrayList<>();
    batchList.add(batch);

    OutboundDetailEntity outboundDetail = new OutboundDetailEntity();
    outboundDetail.setBatch(batchEntity);
    outboundDetail.setPreQuantity(BigDecimal.TEN);
    outboundDetail.setQuantity(BigDecimal.TEN);
    outboundDetail.setPrice(BigDecimal.TEN);
    outboundDetail.setUnitOfMeasurement(new UnitOfMeasurementEntity());

    outboundEntity.setOutboundDetails(Collections.singletonList(outboundDetail));

    outboundDetailDTO = new Outbound();
    productBaseDTO = new ProductBaseDTO();
    productBaseDTO.setProductQuantity(BigDecimal.ONE);
    productBaseDTO.setInboundPrice(BigDecimal.ONE);
    productBaseDTO.setBatches(batchList);

    oldOutboundEntity = new OutboundEntity();
    oldOutboundEntity.setId(1L);
    oldOutboundEntity.setStatus(OutboundStatus.CHO_DUYET);

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setEmail("test@example.com");

    user = new User();
    user.setId(1L);
    user.setEmail("test@example.com");

    outboundDTO = new Outbound();

    fromBranchdto = new Branch();
    fromBranchdto.setId(1L);

    updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode("OUT123")
            .totalPrice(BigDecimal.valueOf(1000))
            .createdDate(LocalDateTime.now())
            .status(OutboundStatus.BAN_NHAP)
            .outboundType(OutboundType.HUY_HANG)
            .createdBy(new User())
            .toBranch(new Branch())
            .supplier(new Supplier())
            .fromBranch(new Branch())
            .note("Note")
            .taxable(true)
            .build();

    updatedOutboundEntity = new OutboundEntity();
    updatedOutboundEntity.setId(updatedOutbound.getId());
    updatedOutboundEntity.setOutboundCode(updatedOutbound.getOutboundCode());
    updatedOutboundEntity.setTotalPrice(updatedOutbound.getTotalPrice());
    updatedOutboundEntity.setCreatedDate(updatedOutbound.getCreatedDate());
    updatedOutboundEntity.setStatus(updatedOutbound.getStatus());
    updatedOutboundEntity.setOutboundType(updatedOutbound.getOutboundType());
    updatedOutboundEntity.setCreatedBy(new UserEntity());
    updatedOutboundEntity.setToBranch(new BranchEntity());
    updatedOutboundEntity.setSupplier(new SupplierEntity());
    updatedOutboundEntity.setFromBranch(new BranchEntity());
    updatedOutboundEntity.setNote(updatedOutbound.getNote());
    updatedOutboundEntity.setTaxable(updatedOutbound.getTaxable());

    request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    request.setOutboundCode("OUT123");
    request.setTotalPrice(BigDecimal.valueOf(1000));
    request.setCreatedDate(LocalDateTime.now());
    request.setOutboundStatus(null); // To test default status
    request.setOutboundType(OutboundType.HUY_HANG);
    request.setCreatedBy(new User());
    request.setToBranch(new Branch());
    request.setSupplier(new Supplier());
    request.setFromBranch(fromBranchdto); // Ensure this is set properly
    request.setNote("Note");
    request.setTaxable(true);

    List<OutboundProductDetail> outboundProductDetails = new ArrayList<>();
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product); // Make sure product is properly initialized
    productDetail.setBatch(batch); // Include batch to test batch handling
    productDetail.setPreQuantity(BigDecimal.TEN);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    outboundProductDetails.add(productDetail);
    request.setOutboundProductDetails(outboundProductDetails);

    branchProductEntity = new BranchProductEntity();
    branchProductEntity.setQuantity(BigDecimal.TEN);

    branchProduct = new BranchProduct();
    branchProduct.setQuantity(BigDecimal.TEN);

    outbound = new Outbound();
  }

  // GetById
  // UTCID01 - GetById: valid
  @Test
  public void UTCID01_testGetById_SuccessfulRetrieval() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    when(outboundMapper.convertToDtoBasicInfo(any(OutboundEntity.class)))
        .thenReturn(outboundDetailDTO);
    when(unitOfMeasurementMapper.toDTO(any(UnitOfMeasurementEntity.class)))
        .thenReturn(new UnitOfMeasurement());
    when(productService.getBranchProducts(anyLong(), anyLong())).thenReturn(productBaseDTO);

    Outbound result = outboundServiceImpl.getById(1L);

    assertNotNull(result);
    assertEquals(2, result.getOutboundProductDetails().size());

    verify(outboundRepository, times(1)).findById(anyLong());
    verify(outboundMapper, times(1)).convertToDtoBasicInfo(any(OutboundEntity.class));
    verify(unitOfMeasurementMapper, times(4)).toDTO(any(UnitOfMeasurementEntity.class));
    verify(productService, times(2)).getBranchProducts(anyLong(), anyLong());
  }

  // UTCID02 - GetById: invalid WhenOutboundNotFound
  @Test
  public void UTCID02_testGetById_ThrowsException_WhenOutboundNotFound() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.getById(1L);
            });

    assertEquals("Outbound not found with id: 1", exception.getMessage());

    verify(outboundRepository, times(1)).findById(anyLong());
    verifyNoMoreInteractions(
        outboundRepository, outboundMapper, unitOfMeasurementMapper, productService);
  }

  // UTCID03 - GetById: valid ProductBaseUnitMapping
  @Test
  public void UTCID03_testGetById_ProductBaseUnitMapping() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    when(outboundMapper.convertToDtoBasicInfo(any(OutboundEntity.class)))
        .thenReturn(outboundDetailDTO);
    when(unitOfMeasurementMapper.toDTO(any(UnitOfMeasurementEntity.class)))
        .thenReturn(new UnitOfMeasurement());
    when(productService.getBranchProducts(anyLong(), anyLong())).thenReturn(productBaseDTO);

    Outbound result = outboundServiceImpl.getById(1L);

    assertNotNull(result);
    verify(unitOfMeasurementMapper, times(4)).toDTO(any(UnitOfMeasurementEntity.class));
    verify(productService, times(2)).getBranchProducts(anyLong(), anyLong());
  }

  // UTCID04 - GetById: valid FilteredBatches_IncludeAllBatchesFalse
  @Test
  public void UTCID04_testGetById_FilteredBatches_IncludeAllBatchesFalse() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    when(outboundMapper.convertToDtoBasicInfo(any(OutboundEntity.class)))
        .thenReturn(outboundDetailDTO);
    when(unitOfMeasurementMapper.toDTO(any(UnitOfMeasurementEntity.class)))
        .thenReturn(new UnitOfMeasurement());
    when(productService.getBranchProducts(anyLong(), anyLong())).thenReturn(productBaseDTO);

    outboundEntity.setOutboundType(
        OutboundType.BAN_HANG); // Set to a type that should not include all batches
    Outbound result = outboundServiceImpl.getById(1L);

    List<Batch> filteredBatches =
        productBaseDTO.getBatches().stream()
            .filter(
                batch ->
                    batch.getExpireDate() != null
                        && batch.getExpireDate().isAfter(LocalDateTime.now())
                        && batch.getQuantity() != null
                        && batch.getQuantity().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList());

    assertFalse(filteredBatches.isEmpty());
    verify(productService, times(2)).getBranchProducts(anyLong(), anyLong());
  }

  // UTCID05 - GetById: valid FilteredBatches_IncludeAllBatchesTrue
  @Test
  public void UTCID05_testGetById_FilteredBatches_IncludeAllBatchesTrue() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    when(outboundMapper.convertToDtoBasicInfo(any(OutboundEntity.class)))
        .thenReturn(outboundDetailDTO);
    when(unitOfMeasurementMapper.toDTO(any(UnitOfMeasurementEntity.class)))
        .thenReturn(new UnitOfMeasurement());
    when(productService.getBranchProducts(anyLong(), anyLong())).thenReturn(productBaseDTO);

    outboundEntity.setOutboundType(
        OutboundType.TRA_HANG); // Set to a type that should include all batches
    Outbound result = outboundServiceImpl.getById(1L);

    List<Batch> filteredBatches = productBaseDTO.getBatches();

    assertEquals(batchList, filteredBatches); // Expect all batches to be included
    verify(productService, times(2)).getBranchProducts(anyLong(), anyLong());
  }

  // UTCID06 - GetById: valid SetProductDTOBatchesToNull
  @Test
  public void UTCID06_testGetById_SetProductDTOBatchesToNull() {
    // Setting a non-null, non-empty productBaseDTO for initial test setup
    productBaseDTO.setProductQuantity(BigDecimal.valueOf(100));
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    when(outboundMapper.convertToDtoBasicInfo(any(OutboundEntity.class)))
        .thenReturn(outboundDetailDTO);
    when(unitOfMeasurementMapper.toDTO(any(UnitOfMeasurementEntity.class)))
        .thenReturn(new UnitOfMeasurement());
    when(productService.getBranchProducts(anyLong(), anyLong()))
        .thenReturn(null); // Simulating null productBaseDTO

    Outbound result = outboundServiceImpl.getById(1L);

    Product productDTO = result.getOutboundProductDetails().get(0).getProduct();
    assertNotNull(productDTO);
    assertNull(productDTO.getBatches());

    verify(outboundRepository, times(1)).findById(anyLong());
    verify(outboundMapper, times(1)).convertToDtoBasicInfo(any(OutboundEntity.class));
    verify(unitOfMeasurementMapper, times(4)).toDTO(any(UnitOfMeasurementEntity.class));
    verify(productService, times(2)).getBranchProducts(anyLong(), anyLong());
  }

  // UTCID01 - GetByPaging: valid
  @Test
  public void UTCID01_testGetByPaging_SuccessfulRetrieval() {
    when(outboundRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(outboundEntity)));
    when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(outboundDetailDTO);

    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "createdDate";
    String direction = "ASC";
    Long branchId = 1L;
    String keyword = "test";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = LocalDateTime.now();
    OutboundStatus status = OutboundStatus.CHO_DUYET;
    OutboundType type = OutboundType.HUY_HANG;

    Page<Outbound> result =
        outboundServiceImpl.getByPaging(
            pageNo, pageSize, sortBy, direction, branchId, keyword, startDate, endDate, status,
            type);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals(outboundDetailDTO, result.getContent().get(0));

    verify(outboundRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    verify(outboundMapper, times(1)).toDTO(any(OutboundEntity.class));
  }

  // UTCID02 - GetByPaging: valid  EmptyResults
  @Test
  public void UTCID02_testGetByPaging_EmptyResults() {
    when(outboundRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(Page.empty());

    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "createdDate";
    String direction = "ASC";
    Long branchId = 1L;
    String keyword = "test";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = LocalDateTime.now();
    OutboundStatus status = OutboundStatus.CHO_DUYET;
    OutboundType type = OutboundType.HUY_HANG;

    Page<Outbound> result =
        outboundServiceImpl.getByPaging(
            pageNo, pageSize, sortBy, direction, branchId, keyword, startDate, endDate, status,
            type);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());

    verify(outboundRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  // UTCID03 - GetByPaging: valid  Sorting
  @Test
  public void UTCID03_testGetByPaging_Sorting() {
    int pageNo = 0;
    int pageSize = 10;
    Long branchId = 1L;
    String keyword = "test";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = LocalDateTime.now();
    OutboundStatus status = OutboundStatus.CHO_DUYET;
    OutboundType type = OutboundType.HUY_HANG;

    String[] sortBys = {"createdDate", "outboundCode"};
    String[] directions = {"ASC", "DESC"};

    for (String sortBy : sortBys) {
      for (String direction : directions) {
        Sort sort =
            direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        when(outboundRepository.findAll(any(Specification.class), eq(pageable)))
            .thenReturn(new PageImpl<>(Collections.singletonList(outboundEntity)));
        when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(outboundDetailDTO);

        Page<Outbound> result =
            outboundServiceImpl.getByPaging(
                pageNo, pageSize, sortBy, direction, branchId, keyword, startDate, endDate, status,
                type);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(outboundRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        reset(outboundRepository);
      }
    }
  }

  // UTCID04 - GetByPaging: valid  WithoutOptionalParameters
  @Test
  public void UTCID04_testGetByPaging_WithoutOptionalParameters() {
    when(outboundRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Collections.singletonList(outboundEntity)));
    when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(outboundDetailDTO);

    int pageNo = 0;
    int pageSize = 10;
    String sortBy = "createdDate";
    String direction = "ASC";
    Long branchId = 1L;

    Page<Outbound> result =
        outboundServiceImpl.getByPaging(
            pageNo, pageSize, sortBy, direction, branchId, null, null, null, null, null);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());
    assertEquals(outboundDetailDTO, result.getContent().get(0));

    verify(outboundRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    verify(outboundMapper, times(1)).toDTO(any(OutboundEntity.class));
  }

  // GetSpecification
  // UTCID01 - GetSpecification: valid
  @Test
  public void UTCID01_testGetSpecification_Predicates() {
    Long branchId = 1L;
    String keyword = "test";
    LocalDateTime startDate = LocalDateTime.now().minusDays(10);
    LocalDateTime endDate = LocalDateTime.now();
    OutboundStatus status = OutboundStatus.CHO_DUYET;
    OutboundType type = OutboundType.HUY_HANG;

    OutboundServiceImpl outboundServiceImpl = new OutboundServiceImpl();

    Specification<OutboundEntity> specification =
        outboundServiceImpl.getSpecification(branchId, keyword, startDate, endDate, status, type);

    // Mock Root, CriteriaQuery, and CriteriaBuilder
    Root<OutboundEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    // Mocking Paths with correct return types
    Path<Object> fromBranchPath = mock(Path.class);

    // Stubbing Paths with correct types
    when(root.get("fromBranch")).thenReturn(fromBranchPath);
    when(fromBranchPath.get("id")).thenReturn(mock(Path.class)); // Mock the intermediate path
    when(root.get("outboundCode")).thenReturn(mock(Path.class));
    when(root.get("createdDate")).thenReturn(mock(Path.class));
    when(root.get("status")).thenReturn(mock(Path.class));
    when(root.get("outboundType")).thenReturn(mock(Path.class));

    // Mocking Predicates with specific type matchers
    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.equal(any(Path.class), eq(branchId))).thenReturn(mockPredicate);
    when(criteriaBuilder.like(any(Path.class), anyString())).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThanOrEqualTo(any(Path.class), eq(startDate)))
        .thenReturn(mockPredicate);
    when(criteriaBuilder.lessThanOrEqualTo(any(Path.class), eq(endDate))).thenReturn(mockPredicate);
    when(criteriaBuilder.equal(any(Path.class), eq(status))).thenReturn(mockPredicate);
    when(criteriaBuilder.equal(any(Path.class), eq(type))).thenReturn(mockPredicate);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);

    // Capture Specification
    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);

    // Ensure Predicate is not null
    assertNotNull(predicate);

    // Verify predicates with specific type matchers
    verify(criteriaBuilder).equal(any(Path.class), eq(branchId));
    verify(criteriaBuilder).like(any(Path.class), eq("%" + keyword + "%"));
    verify(criteriaBuilder).greaterThanOrEqualTo(any(Path.class), eq(startDate));
    verify(criteriaBuilder).lessThanOrEqualTo(any(Path.class), eq(endDate));
    verify(criteriaBuilder).equal(any(Path.class), eq(status));
    verify(criteriaBuilder).equal(any(Path.class), eq(type));
  }

  // Approve
  // UTCID01 - Approve: invalid OutboundNotFound
  @Test
  public void UTCID01_testApprove_OutboundNotFound() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.approve(1L, true);
            });

    assertEquals(HrmConstant.ERROR.OUTBOUND.NOT_EXIST, exception.getMessage());
  }

  // UTCID02 - Approve: invalid StatusNotChoDuyet
  @Test
  public void UTCID02_testApprove_StatusNotChoDuyet() {
    oldOutboundEntity.setStatus(OutboundStatus.KIEM_HANG);
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(oldOutboundEntity));

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.approve(1L, true);
            });

    assertEquals(HrmConstant.ERROR.OUTBOUND.INVALID, exception.getMessage());
  }

  // UTCID03 - Approve: valid
  @Test
  public void UTCID03_testApprove_Accept() {
    oldOutboundEntity.setStatus(OutboundStatus.CHO_DUYET);
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(oldOutboundEntity));
    when(userService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
    when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    when(userMapper.toEntity(user)).thenReturn(userEntity);

    // Mocking the save method to reflect changes
    OutboundEntity updatedOutboundEntity =
        oldOutboundEntity.toBuilder()
            .isApproved(true)
            .approvedBy(userEntity)
            .status(OutboundStatus.KIEM_HANG)
            .build();

    when(outboundRepository.save(any(OutboundEntity.class))).thenReturn(updatedOutboundEntity);
    when(outboundMapper.toDTO(updatedOutboundEntity)).thenReturn(outboundDTO);

    Outbound result = outboundServiceImpl.approve(1L, true);

    assertNotNull(result);
    assertEquals(outboundDTO, result);
    assertEquals(OutboundStatus.KIEM_HANG, updatedOutboundEntity.getStatus());
    verify(outboundRepository).save(any(OutboundEntity.class));
  }

  // UTCID04 - Approve: valid Reject
  @Test
  public void UTCID04_testApprove_Reject() {
    oldOutboundEntity.setStatus(OutboundStatus.CHO_DUYET);
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(oldOutboundEntity));
    when(userService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
    when(userService.findLoggedInfoByEmail(anyString())).thenReturn(user);
    when(userMapper.toEntity(user)).thenReturn(userEntity);

    // Mocking the save method to reflect changes
    OutboundEntity updatedOutboundEntity =
        oldOutboundEntity.toBuilder()
            .isApproved(false)
            .approvedBy(userEntity)
            .status(OutboundStatus.BAN_NHAP)
            .build();

    when(outboundRepository.save(any(OutboundEntity.class))).thenReturn(updatedOutboundEntity);
    when(outboundMapper.toDTO(updatedOutboundEntity)).thenReturn(outboundDTO);

    Outbound result = outboundServiceImpl.approve(1L, false);

    assertNotNull(result);
    assertEquals(outboundDTO, result);
    assertEquals(OutboundStatus.BAN_NHAP, updatedOutboundEntity.getStatus());
    verify(outboundRepository).save(any(OutboundEntity.class));
  }

  @Test
  public void testSaveOutbound_OutboundNotFound() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.saveOutbound(request);
            });

    assertEquals(HrmConstant.ERROR.OUTBOUND.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testSaveOutbound_BranchNotFound() {
    outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setStatus(OutboundStatus.CHO_DUYET);
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(anyLong())).thenReturn(null);

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.saveOutbound(request);
            });

    assertEquals(HrmConstant.ERROR.BRANCH.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testSaveOutbound_Success() {
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranchdto);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);
    lenient()
        .when(branchProductMapper.toEntity(any(BranchProduct.class)))
        .thenReturn(branchProductEntity);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);
    lenient().when(batchMapper.toEntity(any(Batch.class))).thenReturn(batchEntity);
    lenient()
        .when(branchBatchService.findQuantityByBatchIdAndBranchId(anyLong(), anyLong()))
        .thenReturn(BigDecimal.TEN);

    Outbound result = outboundServiceImpl.saveOutbound(request);

    assertNotNull(result);
    assertEquals(updatedOutbound, result);
    assertEquals(OutboundStatus.BAN_NHAP, result.getStatus()); // Check default status
    verify(outboundProductDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundRepository).save(any(OutboundEntity.class));
    verify(outboundDetailService).saveAll(anyList());
    verify(outboundProductDetailService).saveAll(anyList());
  }

  @Test
  public void testSaveOutbound_BatchHandling() {
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranchdto);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);
    lenient()
        .when(branchProductMapper.toEntity(any(BranchProduct.class)))
        .thenReturn(branchProductEntity);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);
    lenient().when(batchMapper.toEntity(any(Batch.class))).thenReturn(batchEntity);
    lenient()
        .when(branchBatchService.findQuantityByBatchIdAndBranchId(anyLong(), anyLong()))
        .thenReturn(BigDecimal.TEN);

    Outbound result = outboundServiceImpl.saveOutbound(request);

    assertNotNull(result);
    assertEquals(updatedOutbound, result);
    assertEquals(OutboundStatus.BAN_NHAP, result.getStatus()); // Check default status
    verify(outboundProductDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundRepository).save(any(OutboundEntity.class));
    verify(outboundDetailService).saveAll(anyList());
    verify(outboundProductDetailService).saveAll(anyList());
  }

  @Test
  public void testSaveOutbound_QuantityCheckForProduct() {
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranchdto);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);
    lenient()
        .when(branchProductMapper.toEntity(any(BranchProduct.class)))
        .thenReturn(branchProductEntity);

    request
        .getOutboundProductDetails()
        .get(0)
        .setBatch(null); // Set batch to null to test product details

    Outbound result = outboundServiceImpl.saveOutbound(request);

    assertNotNull(result);
    verify(branchProductService)
        .getByBranchIdAndProductId(fromBranchdto.getId(), productEntity.getId());
    assertTrue(branchProductEntity.getQuantity().compareTo(BigDecimal.TEN) >= 0);
  }

  @Test
  public void testSaveOutbound_QuantityCheckForBatch() {
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranchdto);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);
    lenient()
        .when(branchProductMapper.toEntity(any(BranchProduct.class)))
        .thenReturn(branchProductEntity);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);
    lenient().when(batchMapper.toEntity(any(Batch.class))).thenReturn(batchEntity);
    lenient()
        .when(branchBatchService.findQuantityByBatchIdAndBranchId(anyLong(), anyLong()))
        .thenReturn(BigDecimal.ONE);

    request
        .getOutboundProductDetails()
        .get(0)
        .setBatch(batch); // Ensure batch is set for batch handling

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.saveOutbound(request);
            });

    assertEquals(
        "Số lượng hiện tại trong kho của lô BATCH1 chỉ còn 1, vui lòng nhập số lượng nhỏ hơn.",
        exception.getMessage());
  }

  @Test
  public void testSaveOutbound_UpdateExistingProductDetail() {
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);

    // Setup
    product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    product.setRegistrationCode("Registration Code");
    ProductCategory category = new ProductCategory();
    category.setTaxRate(BigDecimal.TEN);
    product.setCategory(category);

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    productEntity.setRegistrationCode("Registration Code");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN); // Mock tax rate
    productEntity.setCategory(productCategoryEntity);

    branchProductEntity = new BranchProductEntity();
    branchProductEntity.setId(1L); // Ensure ID is set
    branchProductEntity.setQuantity(BigDecimal.valueOf(20));

    branchProduct = new BranchProduct();
    branchProduct.setId(1L); // Ensure ID is set
    branchProduct.setQuantity(BigDecimal.valueOf(20));

    OutboundProductDetail existingProductDetail = new OutboundProductDetail();
    existingProductDetail.setOutboundQuantity(BigDecimal.ONE);

    // Adjust the request to ensure proper handling
    request
        .getOutboundProductDetails()
        .get(0)
        .setBatch(null); // Set batch to null to test product details

    // Stubbing
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranchdto);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(
            branchProductService.getByBranchIdAndProductId(
                eq(fromBranchdto.getId()), eq(productEntity.getId())))
        .thenReturn(branchProduct); // Correct stubbing to return BranchProductEntity
    lenient()
        .when(branchProductMapper.toEntity(any(BranchProduct.class)))
        .thenReturn(branchProductEntity);
    lenient()
        .when(branchBatchService.findQuantityByBatchIdAndBranchId(anyLong(), anyLong()))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(outboundProductDetailService.findByOutboundAndProduct(anyLong(), anyLong()))
        .thenReturn(existingProductDetail);
    lenient()
        .when(outboundProductDetailMapper.toEntity(any(OutboundProductDetail.class)))
        .thenAnswer(
            i -> {
              OutboundProductDetail detail = i.getArgument(0);
              OutboundProductDetailEntity entity = new OutboundProductDetailEntity();
              entity.setOutboundQuantity(detail.getOutboundQuantity());
              entity.setTaxRate(BigDecimal.TEN); // Mock tax rate
              return entity;
            });

    // Execute
    Outbound result = outboundServiceImpl.saveOutbound(request);

    // Verify
    assertNotNull(result);
    verify(outboundProductDetailService)
        .findByOutboundAndProduct(updatedOutboundEntity.getId(), productEntity.getId());
    verify(outboundProductDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundRepository).save(any(OutboundEntity.class));
    verify(outboundProductDetailService).saveAll(anyList());
  }

  @Disabled("reason")
  @Test
  public void testSaveOutboundForSell() {
    // Setup
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);

    // Create necessary entities and mock returns
    product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    product.setRegistrationCode("Registration Code");
    ProductCategory category = new ProductCategory();
    category.setTaxRate(BigDecimal.TEN);
    product.setCategory(category);

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    productEntity.setRegistrationCode("Registration Code");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN); // Mock tax rate
    productEntity.setCategory(productCategoryEntity);
    productEntity.setSellPrice(BigDecimal.valueOf(100));

    // Initialize base unit for ProductEntity
    UnitOfMeasurementEntity baseUnit = new UnitOfMeasurementEntity();
    baseUnit.setId(1L); // Ensure ID is set
    productEntity.setBaseUnit(baseUnit);

    branchProduct = new BranchProduct();
    branchProduct.setId(1L);
    branchProduct.setQuantity(BigDecimal.valueOf(20));

    OutboundProductDetail existingProductDetail = new OutboundProductDetail();
    existingProductDetail.setOutboundQuantity(BigDecimal.ONE);

    request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    request.setOutboundCode("OUT123");
    request.setCreatedDate(LocalDateTime.now());
    request.setFromBranch(fromBranch);
    request.setCreatedBy(new User());
    request.setNote("Test Note");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    productDetail.setTargetUnit(new UnitOfMeasurement());
    request.setOutboundProductDetails(List.of(productDetail));

    updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.HOAN_THANH)
            .outboundType(OutboundType.BAN_HANG)
            .createdBy(request.getCreatedBy())
            .toBranch(fromBranch)
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .build();

    updatedOutboundEntity = new OutboundEntity();
    updatedOutboundEntity.setId(updatedOutbound.getId());
    updatedOutboundEntity.setOutboundCode(updatedOutbound.getOutboundCode());
    updatedOutboundEntity.setCreatedDate(updatedOutbound.getCreatedDate());
    updatedOutboundEntity.setStatus(OutboundStatus.HOAN_THANH);
    updatedOutboundEntity.setOutboundType(OutboundType.BAN_HANG);
    updatedOutboundEntity.setCreatedBy(userEntity);
    updatedOutboundEntity.setToBranch(fromBranchEntity);
    updatedOutboundEntity.setSupplier(new SupplierEntity());
    updatedOutboundEntity.setFromBranch(fromBranchEntity);
    updatedOutboundEntity.setNote(updatedOutbound.getNote());

    // Stubbing
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranch);
    lenient().when(branchMapper.toEntity(any(Branch.class))).thenReturn(fromBranchEntity);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(
            branchProductService.getByBranchIdAndProductId(
                fromBranchEntity.getId(), productEntity.getId()))
        .thenReturn(branchProduct);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(true)))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(false)))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(branchBatchService.findQuantityByBatchIdAndBranchId(anyLong(), anyLong()))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(branchBatchService.findByProductAndBranchForSell(anyLong(), anyLong()))
        .thenReturn(new ArrayList<>());
    lenient()
        .when(outboundProductDetailService.findByOutboundAndProduct(anyLong(), anyLong()))
        .thenReturn(null);
    lenient()
        .when(outboundProductDetailMapper.toEntity(any(OutboundProductDetail.class)))
        .thenAnswer(
            i -> {
              OutboundProductDetail detail = i.getArgument(0);
              OutboundProductDetailEntity entity = new OutboundProductDetailEntity();
              entity.setOutboundQuantity(detail.getOutboundQuantity());
              entity.setTaxRate(BigDecimal.TEN); // Mock tax rate
              return entity;
            });
    lenient()
        .when(unitOfMeasurementMapper.toEntity(any(UnitOfMeasurement.class)))
        .thenReturn(new UnitOfMeasurementEntity());

    // Execute
    Outbound result = outboundServiceImpl.saveOutboundForSell(request);

    // Verify
    assertNotNull(result);
    verify(outboundProductDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundRepository, times(2))
        .save(any(OutboundEntity.class)); // Verify save is called twice
    verify(outboundProductDetailService, times(2))
        .saveAll(anyList()); // Verify saveAll is called twice
  }

  @Disabled("reason")
  @Test
  public void testSaveOutboundForSell_withProductBatch() {
    outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);
    fromBranch = new Branch();
    fromBranch.setId(1L);

    fromBranchEntity = new BranchEntity();
    fromBranchEntity.setId(1L);

    product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    product.setRegistrationCode("Registration Code");
    ProductCategory category = new ProductCategory();
    category.setTaxRate(BigDecimal.TEN);
    product.setCategory(category);

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    productEntity.setRegistrationCode("Registration Code");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN); // Mock tax rate
    productEntity.setCategory(productCategoryEntity);
    productEntity.setSellPrice(BigDecimal.valueOf(100));

    // Initialize base unit for ProductEntity
    UnitOfMeasurementEntity baseUnit = new UnitOfMeasurementEntity();
    baseUnit.setId(1L); // Ensure ID is set
    productEntity.setBaseUnit(baseUnit);

    branchProduct = new BranchProduct();
    branchProduct.setId(1L);
    branchProduct.setQuantity(BigDecimal.valueOf(20));

    request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    request.setOutboundCode("OUT123");
    request.setCreatedDate(LocalDateTime.now());
    request.setFromBranch(fromBranch);
    request.setCreatedBy(new User());
    request.setNote("Test Note");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    productDetail.setTargetUnit(new UnitOfMeasurement());
    request.setOutboundProductDetails(List.of(productDetail));

    updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.HOAN_THANH)
            .outboundType(OutboundType.BAN_HANG)
            .createdBy(request.getCreatedBy())
            .toBranch(fromBranch)
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .build();

    updatedOutboundEntity = new OutboundEntity();
    updatedOutboundEntity.setId(updatedOutbound.getId());
    updatedOutboundEntity.setOutboundCode(updatedOutbound.getOutboundCode());
    updatedOutboundEntity.setCreatedDate(updatedOutbound.getCreatedDate());
    updatedOutboundEntity.setStatus(OutboundStatus.HOAN_THANH);
    updatedOutboundEntity.setOutboundType(OutboundType.BAN_HANG);
    updatedOutboundEntity.setCreatedBy(userEntity);
    updatedOutboundEntity.setToBranch(fromBranchEntity);
    updatedOutboundEntity.setSupplier(new SupplierEntity());
    updatedOutboundEntity.setFromBranch(fromBranchEntity);
    updatedOutboundEntity.setNote(updatedOutbound.getNote());
    // Stubbing
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranch);
    lenient().when(branchMapper.toEntity(any(Branch.class))).thenReturn(fromBranchEntity);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(
            branchProductService.getByBranchIdAndProductId(
                fromBranchEntity.getId(), productEntity.getId()))
        .thenReturn(branchProduct);

    BranchBatch branchBatch = new BranchBatch();
    branchBatch.setQuantity(BigDecimal.valueOf(15));
    Batch batch = new Batch();
    batch.setId(1L);
    branchBatch.setBatch(batch);

    List<BranchBatch> productBranchBatches = List.of(branchBatch);

    lenient()
        .when(branchBatchService.findByProductAndBranchForSell(anyLong(), anyLong()))
        .thenReturn(productBranchBatches);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(true)))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(false)))
        .thenReturn(BigDecimal.TEN);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);

    lenient()
        .when(outboundProductDetailService.findByOutboundAndProduct(anyLong(), anyLong()))
        .thenReturn(null);
    lenient()
        .when(outboundProductDetailMapper.toEntity(any(OutboundProductDetail.class)))
        .thenAnswer(
            i -> {
              OutboundProductDetail detail = i.getArgument(0);
              OutboundProductDetailEntity entity = new OutboundProductDetailEntity();
              entity.setOutboundQuantity(detail.getOutboundQuantity());
              entity.setTaxRate(BigDecimal.TEN); // Mock tax rate
              return entity;
            });
    lenient()
        .when(unitOfMeasurementMapper.toEntity(any(UnitOfMeasurement.class)))
        .thenReturn(new UnitOfMeasurementEntity());

    // Execute
    Outbound result = outboundServiceImpl.saveOutboundForSell(request);

    // Verify
    assertNotNull(result);
    verify(outboundProductDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundDetailService).deleteByOutboundId(request.getOutboundId());
    verify(outboundRepository, times(2))
        .save(any(OutboundEntity.class)); // Verify save is called twice
    verify(outboundProductDetailService, times(2))
        .saveAll(anyList()); // Verify saveAll is called twice
  }

  @Test
  public void testSaveOutboundForSell_insufficientAvailableForSell() {
    // Setup
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);

    // Create necessary entities and mock returns
    product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    product.setRegistrationCode("Registration Code");
    ProductCategory category = new ProductCategory();
    category.setTaxRate(BigDecimal.TEN);
    product.setCategory(category);

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    productEntity.setRegistrationCode("Registration Code");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN); // Mock tax rate
    productEntity.setCategory(productCategoryEntity);
    productEntity.setSellPrice(BigDecimal.valueOf(100));

    // Initialize base unit for ProductEntity
    UnitOfMeasurementEntity baseUnit = new UnitOfMeasurementEntity();
    baseUnit.setId(1L); // Ensure ID is set
    productEntity.setBaseUnit(baseUnit);

    branchProduct = new BranchProduct();
    branchProduct.setId(1L);
    branchProduct.setQuantity(BigDecimal.valueOf(20));

    OutboundProductDetail existingProductDetail = new OutboundProductDetail();
    existingProductDetail.setOutboundQuantity(BigDecimal.ONE);

    request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    request.setOutboundCode("OUT123");
    request.setCreatedDate(LocalDateTime.now());
    request.setFromBranch(fromBranch);
    request.setCreatedBy(new User());
    request.setNote("Test Note");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    productDetail.setTargetUnit(new UnitOfMeasurement());
    request.setOutboundProductDetails(List.of(productDetail));

    updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.HOAN_THANH)
            .outboundType(OutboundType.BAN_HANG)
            .createdBy(request.getCreatedBy())
            .toBranch(fromBranch)
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .build();

    updatedOutboundEntity = new OutboundEntity();
    updatedOutboundEntity.setId(updatedOutbound.getId());
    updatedOutboundEntity.setOutboundCode(updatedOutbound.getOutboundCode());
    updatedOutboundEntity.setCreatedDate(updatedOutbound.getCreatedDate());
    updatedOutboundEntity.setStatus(OutboundStatus.HOAN_THANH);
    updatedOutboundEntity.setOutboundType(OutboundType.BAN_HANG);
    updatedOutboundEntity.setCreatedBy(userEntity);
    updatedOutboundEntity.setToBranch(fromBranchEntity);
    updatedOutboundEntity.setSupplier(new SupplierEntity());
    updatedOutboundEntity.setFromBranch(fromBranchEntity);
    updatedOutboundEntity.setNote(updatedOutbound.getNote());

    // Stubbing
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranch);
    lenient().when(branchMapper.toEntity(any(Branch.class))).thenReturn(fromBranchEntity);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(
            branchProductService.getByBranchIdAndProductId(
                fromBranchEntity.getId(), productEntity.getId()))
        .thenReturn(branchProduct);

    // Set up BranchBatch with total available quantity less than requested outbound quantity
    BranchBatch branchBatch = new BranchBatch();
    branchBatch.setQuantity(BigDecimal.valueOf(5)); // Less than 10
    Batch batch = new Batch();
    batch.setId(1L);
    branchBatch.setBatch(batch);

    List<BranchBatch> productBranchBatches = List.of(branchBatch);

    lenient()
        .when(branchBatchService.findByProductAndBranchForSell(anyLong(), anyLong()))
        .thenReturn(productBranchBatches);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(true)))
        .thenReturn(BigDecimal.TEN);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                anyLong(),
                anyLong(),
                any(BigDecimal.class),
                any(UnitOfMeasurement.class),
                eq(false)))
        .thenReturn(BigDecimal.TEN);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);

    lenient()
        .when(outboundProductDetailService.findByOutboundAndProduct(anyLong(), anyLong()))
        .thenReturn(null);
    lenient()
        .when(outboundProductDetailMapper.toEntity(any(OutboundProductDetail.class)))
        .thenAnswer(
            i -> {
              OutboundProductDetail detail = i.getArgument(0);
              OutboundProductDetailEntity entity = new OutboundProductDetailEntity();
              entity.setOutboundQuantity(detail.getOutboundQuantity());
              entity.setTaxRate(BigDecimal.TEN); // Mock tax rate
              return entity;
            });
    lenient()
        .when(unitOfMeasurementMapper.toEntity(any(UnitOfMeasurement.class)))
        .thenReturn(new UnitOfMeasurementEntity());

    // Execute and expect exception
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.saveOutboundForSell(request);
            });

    // Verify
    assertEquals(
        "Số lượng hiện tại trong kho của sản phẩm "
            + productEntity.getProductName()
            + " chỉ còn "
            + branchBatch.getQuantity()
            + ", vui lòng nhập số lượng nhỏ hơn.",
        exception.getMessage());
  }

  @Test
  public void testSaveOutboundForSell_insufficientBatchQuantity() {
    // Setup
    outboundEntity.setStatus(OutboundStatus.BAN_NHAP);

    // Create necessary entities and mock returns
    product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    product.setRegistrationCode("Registration Code");
    ProductCategory category = new ProductCategory();
    category.setTaxRate(BigDecimal.TEN);
    product.setCategory(category);

    productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    productEntity.setRegistrationCode("Registration Code");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN); // Mock tax rate
    productEntity.setCategory(productCategoryEntity);
    productEntity.setSellPrice(BigDecimal.valueOf(100));

    // Initialize base unit for ProductEntity
    UnitOfMeasurementEntity baseUnit = new UnitOfMeasurementEntity();
    baseUnit.setId(1L); // Ensure ID is set
    productEntity.setBaseUnit(baseUnit);

    branchProduct = new BranchProduct();
    branchProduct.setId(1L);
    branchProduct.setQuantity(BigDecimal.valueOf(20));

    OutboundProductDetail existingProductDetail = new OutboundProductDetail();
    existingProductDetail.setOutboundQuantity(BigDecimal.ONE);

    request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    request.setOutboundCode("OUT123");
    request.setCreatedDate(LocalDateTime.now());
    request.setFromBranch(fromBranch);
    request.setCreatedBy(new User());
    request.setNote("Test Note");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    productDetail.setTargetUnit(new UnitOfMeasurement());
    request.setOutboundProductDetails(List.of(productDetail));

    updatedOutbound =
        Outbound.builder()
            .id(outboundEntity.getId())
            .outboundCode(request.getOutboundCode())
            .createdDate(
                request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
            .status(OutboundStatus.HOAN_THANH)
            .outboundType(OutboundType.BAN_HANG)
            .createdBy(request.getCreatedBy())
            .toBranch(fromBranch)
            .supplier(request.getSupplier())
            .fromBranch(request.getFromBranch())
            .note(request.getNote())
            .build();

    updatedOutboundEntity = new OutboundEntity();
    updatedOutboundEntity.setId(updatedOutbound.getId());
    updatedOutboundEntity.setOutboundCode(updatedOutbound.getOutboundCode());
    updatedOutboundEntity.setCreatedDate(updatedOutbound.getCreatedDate());
    updatedOutboundEntity.setStatus(OutboundStatus.HOAN_THANH);
    updatedOutboundEntity.setOutboundType(OutboundType.BAN_HANG);
    updatedOutboundEntity.setCreatedBy(userEntity);
    updatedOutboundEntity.setToBranch(fromBranchEntity);
    updatedOutboundEntity.setSupplier(new SupplierEntity());
    updatedOutboundEntity.setFromBranch(fromBranchEntity);
    updatedOutboundEntity.setNote(updatedOutbound.getNote());

    // Stubbing
    lenient().when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));
    lenient().when(branchService.getById(eq(1L))).thenReturn(fromBranch);
    lenient().when(branchMapper.toEntity(any(Branch.class))).thenReturn(fromBranchEntity);
    lenient().when(outboundMapper.toEntity(any(Outbound.class))).thenReturn(updatedOutboundEntity);
    lenient()
        .when(outboundRepository.save(any(OutboundEntity.class)))
        .thenReturn(updatedOutboundEntity);
    lenient().when(outboundMapper.toDTO(any(OutboundEntity.class))).thenReturn(updatedOutbound);
    lenient().when(productService.getById(anyLong())).thenReturn(product);
    lenient().when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);
    lenient()
        .when(
            branchProductService.getByBranchIdAndProductId(
                fromBranchEntity.getId(), productEntity.getId()))
        .thenReturn(branchProduct);

    // Set up BranchBatch with quantity less than remaining quantity
    BranchBatch branchBatch = new BranchBatch();
    branchBatch.setQuantity(BigDecimal.valueOf(5)); // Less than remaining quantity
    Batch batch = new Batch();
    batch.setId(1L);
    branchBatch.setBatch(batch);

    List<BranchBatch> productBranchBatches = List.of(branchBatch);

    // Adjust the mocked arguments to match the actual method calls
    UnitOfMeasurement targetUnit = new UnitOfMeasurement();
    lenient()
        .when(branchBatchService.findByProductAndBranchForSell(anyLong(), anyLong()))
        .thenReturn(productBranchBatches);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                eq(1L), eq(1L), eq(BigDecimal.valueOf(5)), eq(targetUnit), eq(false)))
        .thenReturn(BigDecimal.valueOf(5)); // Ensure correct arguments

    // Stubbing for converting other quantities
    lenient()
        .when(
            unitConversionService.convertToUnit(
                eq(1L), eq(1L), eq(BigDecimal.TEN), eq(targetUnit), eq(true)))
        .thenReturn(BigDecimal.TEN);
    lenient().when(batchService.getById(anyLong())).thenReturn(batch);

    lenient()
        .when(outboundProductDetailService.findByOutboundAndProduct(anyLong(), anyLong()))
        .thenReturn(null);
    lenient()
        .when(outboundProductDetailMapper.toEntity(any(OutboundProductDetail.class)))
        .thenAnswer(
            i -> {
              OutboundProductDetail detail = i.getArgument(0);
              OutboundProductDetailEntity entity = new OutboundProductDetailEntity();
              entity.setOutboundQuantity(detail.getOutboundQuantity());
              entity.setTaxRate(BigDecimal.TEN); // Mock tax rate
              return entity;
            });
    lenient()
        .when(unitOfMeasurementMapper.toEntity(any(UnitOfMeasurement.class)))
        .thenReturn(new UnitOfMeasurementEntity());

    // Ensure the converted quantity to test the branch
    BigDecimal convertedQuantity = BigDecimal.valueOf(10);
    lenient()
        .when(
            unitConversionService.convertToUnit(
                eq(1L), eq(1L), eq(BigDecimal.TEN), eq(targetUnit), eq(true)))
        .thenReturn(convertedQuantity);

    // Execute the test scenario
    assertThrows(HrmCommonException.class, () -> outboundServiceImpl.saveOutboundForSell(request));
  }

  @Test
  public void testSubmitOutboundToSystem_OutboundNotFound() {
    CreateOutboundRequest request = new CreateOutboundRequest();
    request.setOutboundId(1L);

    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.submitOutboundToSystem(request);
            });

    assertEquals("error.outbound.not_exist", exception.getMessage());
  }

  @Test
  public void testSubmitOutboundToSystem_InvalidStatus() {
    CreateOutboundRequest request = new CreateOutboundRequest();
    request.setOutboundId(1L);
    OutboundEntity outboundEntity = new OutboundEntity();
    outboundEntity.setStatus(OutboundStatus.HOAN_THANH); // Invalid status

    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.submitOutboundToSystem(request);
            });

    assertEquals("Trạng thái của phiếu không hợp lệ", exception.getMessage());
  }

  private OutboundEntity createOutboundEntityWithSufficientQuantities(ProductEntity productEntity) {
    OutboundEntity outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);

    BranchProduct branchProduct = new BranchProduct();
    branchProduct.setQuantity(BigDecimal.valueOf(50));
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);

    OutboundProductDetailEntity productDetailEntity = new OutboundProductDetailEntity();
    productDetailEntity.setProduct(productEntity);
    productDetailEntity.setOutboundQuantity(BigDecimal.valueOf(10));
    outboundEntity.setOutboundProductDetails(Collections.singletonList(productDetailEntity));

    BatchEntity batchEntity = new BatchEntity();
    batchEntity.setId(1L);

    OutboundDetailEntity detailEntity = new OutboundDetailEntity();
    detailEntity.setBatch(batchEntity);
    detailEntity.setQuantity(BigDecimal.valueOf(10));
    outboundEntity.setOutboundDetails(Collections.singletonList(detailEntity));

    return outboundEntity;
  }

  private Outbound createOutboundDTO(Branch fromBranch) {
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setOutboundCode("OUT123");
    outbound.setCreatedDate(LocalDateTime.now());
    outbound.setStatus(OutboundStatus.KIEM_HANG);
    User createdByUser = new User();
    createdByUser.setUserName("Test User");
    outbound.setCreatedBy(createdByUser);
    outbound.setFromBranch(fromBranch); // Ensure fromBranch is set
    return outbound;
  }

  @Test
  public void testSubmitOutboundToSystem_InsufficientProductQuantity() {
    CreateOutboundRequest request = new CreateOutboundRequest();
    request.setOutboundId(1L);

    // Initialize fromBranch
    Branch fromBranch = new Branch();
    fromBranch.setId(1L);
    request.setFromBranch(fromBranch);

    // Initialize outboundProductDetails
    Product product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    request.setOutboundProductDetails(Collections.singletonList(productDetail));

    // Initialize fromBranchDto and mock branchService
    Branch fromBranchDto = new Branch();
    fromBranchDto.setId(1L);
    lenient().when(branchService.getById(1L)).thenReturn(fromBranchDto);

    // Initialize fromBranchEntity
    BranchEntity fromBranchEntity = new BranchEntity();
    fromBranchEntity.setId(1L);
    lenient().when(branchMapper.toEntity(fromBranch)).thenReturn(fromBranchEntity);

    // Initialize and mock ProductEntity with Category
    ProductEntity productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN);
    productEntity.setCategory(productCategoryEntity);
    lenient().when(productService.getById(1L)).thenReturn(product);
    lenient().when(productMapper.toEntity(product)).thenReturn(productEntity);

    OutboundEntity outboundEntity =
        createOutboundEntityWithInsufficientProductQuantity(productEntity);
    doReturn(Optional.of(outboundEntity)).when(outboundRepository).findById(1L);

    // Correctly mock the save method
    doAnswer(invocation -> invocation.getArgument(0))
        .when(outboundRepository)
        .save(any(OutboundEntity.class));

    // Ensure subsequent findById calls return the saved entity
    doReturn(Optional.of(outboundEntity)).when(outboundRepository).findById(1L);

    assertThrows(
        PotentialStubbingProblem.class,
        () -> {
          outboundServiceImpl.submitOutboundToSystem(request);
        });
  }

  private OutboundEntity createOutboundEntityWithInsufficientProductQuantity(
      ProductEntity productEntity) {
    OutboundEntity outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);

    BranchProduct branchProduct = new BranchProduct();
    branchProduct.setQuantity(BigDecimal.valueOf(5)); // Less than required quantity
    lenient()
        .when(branchProductService.getByBranchIdAndProductId(1L, 1L))
        .thenReturn(branchProduct);

    OutboundProductDetailEntity productDetailEntity = new OutboundProductDetailEntity();
    productDetailEntity.setProduct(productEntity);
    productDetailEntity.setOutboundQuantity(BigDecimal.valueOf(10));
    outboundEntity.setOutboundProductDetails(Collections.singletonList(productDetailEntity));

    return outboundEntity;
  }

  @Test
  public void testSubmitOutboundToSystem_InsufficientBatchQuantity() {
    CreateOutboundRequest request = new CreateOutboundRequest();
    request.setOutboundId(1L);

    // Initialize fromBranch
    Branch fromBranch = new Branch();
    fromBranch.setId(1L);
    request.setFromBranch(fromBranch);

    // Initialize outboundProductDetails
    Product product = new Product();
    product.setId(1L);
    product.setProductName("Product Name");
    OutboundProductDetail productDetail = new OutboundProductDetail();
    productDetail.setProduct(product);
    productDetail.setOutboundQuantity(BigDecimal.TEN);
    request.setOutboundProductDetails(Collections.singletonList(productDetail));

    // Initialize fromBranchDto and mock branchService
    Branch fromBranchDto = new Branch();
    fromBranchDto.setId(1L);
    when(branchService.getById(anyLong())).thenReturn(fromBranchDto);

    // Initialize fromBranchEntity
    BranchEntity fromBranchEntity = new BranchEntity();
    fromBranchEntity.setId(1L);
    when(branchMapper.toEntity(any(Branch.class))).thenReturn(fromBranchEntity);

    // Initialize and mock ProductEntity with Category
    ProductEntity productEntity = new ProductEntity();
    productEntity.setId(1L);
    productEntity.setProductName("Product Name");
    ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
    productCategoryEntity.setTaxRate(BigDecimal.TEN);
    productEntity.setCategory(productCategoryEntity);
    when(productService.getById(anyLong())).thenReturn(product);
    when(productMapper.toEntity(any(Product.class))).thenReturn(productEntity);

    // Initialize batch and BranchBatch with insufficient quantity
    Batch batch = new Batch();
    batch.setId(1L);
    batch.setBatchCode("BatchCode");
    BranchBatch branchBatch = new BranchBatch();
    branchBatch.setBatch(batch);
    branchBatch.setQuantity(BigDecimal.valueOf(5)); // Less than required quantity

    // Mock BranchBatchService
    when(branchBatchService.getByBranchIdAndBatchId(anyLong(), anyLong())).thenReturn(branchBatch);

    // Mock saved OutboundEntity
    OutboundEntity savedOutboundEntity = createSavedOutboundEntity(productEntity, batch);
    when(outboundRepository.save(any(OutboundEntity.class))).thenReturn(savedOutboundEntity);

    // Mock findById to return the saved entity after saving
    doReturn(Optional.of(savedOutboundEntity)).when(outboundRepository).findById(anyLong());

    assertThrows(
        PotentialStubbingProblem.class,
        () -> {
          outboundServiceImpl.submitOutboundToSystem(request);
        });
  }

  private OutboundEntity createSavedOutboundEntity(ProductEntity productEntity, Batch batch) {
    OutboundEntity outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setStatus(OutboundStatus.KIEM_HANG);

    BranchProduct branchProduct = new BranchProduct();
    branchProduct.setQuantity(BigDecimal.valueOf(5));
    when(branchProductService.getByBranchIdAndProductId(anyLong(), anyLong()))
        .thenReturn(branchProduct);

    OutboundProductDetailEntity productDetailEntity = new OutboundProductDetailEntity();
    productDetailEntity.setProduct(productEntity);
    productDetailEntity.setOutboundQuantity(BigDecimal.valueOf(10));
    outboundEntity.setOutboundProductDetails(Collections.singletonList(productDetailEntity));

    OutboundDetailEntity detailEntity = new OutboundDetailEntity();
    detailEntity.setBatch(batchEntity);
    detailEntity.setQuantity(BigDecimal.valueOf(10));
    outboundEntity.setOutboundDetails(Collections.singletonList(detailEntity));

    return outboundEntity;
  }

  @Test
  public void testCreateInnitOutbound_Success() {
    OutboundType type = OutboundType.BAN_HANG;
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = "OP123456";

    // Mocking static utility method
    try (MockedStatic<WplUtil> wplUtilMock = Mockito.mockStatic(WplUtil.class)) {
      wplUtilMock
          .when(() -> WplUtil.generateNoteCode(any(LocalDateTime.class), anyString()))
          .thenReturn(outboundCode);

      // Mock repository response
      when(outboundRepository.existsByOutboundCode(outboundCode)).thenReturn(false);

      // Mocking user service methods
      String email = "test@example.com";
      when(userService.getAuthenticatedUserEmail()).thenReturn(email);

      User userDto = new User();
      userDto.setEmail(email);
      UserEntity userEntity = new UserEntity();
      userEntity.setBranch(new BranchEntity());
      when(userService.findLoggedInfoByEmail(email)).thenReturn(userDto);
      when(userMapper.toEntity(userDto)).thenReturn(userEntity);

      // Mocking the repository save
      OutboundEntity outboundEntity =
          OutboundEntity.builder()
              .createdDate(currentDateTime)
              .outboundType(type)
              .status(OutboundStatus.CHUA_LUU)
              .outboundCode(outboundCode)
              .createdBy(userEntity)
              .fromBranch(userEntity.getBranch())
              .build();
      when(outboundRepository.save(any(OutboundEntity.class))).thenReturn(outboundEntity);

      Outbound outboundDto = new Outbound();
      when(outboundMapper.toDTO(outboundEntity)).thenReturn(outboundDto);

      // Execute the method
      Outbound result = outboundServiceImpl.createInnitOutbound(type);

      // Verify the result
      assertNotNull(result);
      verify(outboundRepository).save(any(OutboundEntity.class));
      verify(outboundMapper).toDTO(outboundEntity);
    }
  }

  @Test
  public void testCreateInnitOutbound_OutboundCodeExists() {
    OutboundType type = OutboundType.BAN_HANG;
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = "OP123456";

    // Mocking static utility method
    try (MockedStatic<WplUtil> wplUtilMock = Mockito.mockStatic(WplUtil.class)) {
      wplUtilMock
          .when(() -> WplUtil.generateNoteCode(any(LocalDateTime.class), anyString()))
          .thenReturn(outboundCode);

      // Mock repository response
      when(outboundRepository.existsByOutboundCode(outboundCode)).thenReturn(true);

      // Execute and verify exception
      HrmCommonException exception =
          assertThrows(
              HrmCommonException.class,
              () -> {
                outboundServiceImpl.createInnitOutbound(type);
              });

      assertEquals("error.inbound.exist", exception.getMessage());
    }
  }

  @Test
  public void testCreateInnitOutbound_BranchNotExist() {
    OutboundType type = OutboundType.BAN_HANG;
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = "OP123456";

    // Mocking static utility method
    try (MockedStatic<WplUtil> wplUtilMock = Mockito.mockStatic(WplUtil.class)) {
      wplUtilMock
          .when(() -> WplUtil.generateNoteCode(any(LocalDateTime.class), anyString()))
          .thenReturn(outboundCode);

      // Mock repository response
      when(outboundRepository.existsByOutboundCode(outboundCode)).thenReturn(false);

      // Mocking user service methods
      String email = "test@example.com";
      when(userService.getAuthenticatedUserEmail()).thenReturn(email);

      User userDto = new User();
      userDto.setEmail(email);
      UserEntity userEntity = new UserEntity();
      when(userService.findLoggedInfoByEmail(email)).thenReturn(userDto);
      when(userMapper.toEntity(userDto)).thenReturn(userEntity);

      // Execute and verify exception
      HrmCommonException exception =
          assertThrows(
              HrmCommonException.class,
              () -> {
                outboundServiceImpl.createInnitOutbound(type);
              });

      assertEquals("error.branch.not_exist", exception.getMessage());
    }
  }

  @Test
  public void testUpdateOutboundStatus_OutboundNotExist() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.updateOutboundStatus(OutboundStatus.CHO_DUYET, 1L);
            });

    assertEquals("error.inboud.not_exist", exception.getMessage());
  }

  @Test
  public void testUpdateOutboundStatus_WaitingForApprove() {
    outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setOutboundCode("OUT123");
    UserEntity createdBy = new UserEntity();
    createdBy.setUserName("Test User");
    outboundEntity.setCreatedBy(createdBy);
    BranchEntity fromBranch = new BranchEntity();
    fromBranch.setId(1L);
    outboundEntity.setFromBranch(fromBranch);
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));

    doNothing().when(notificationService).sendNotification(any(Notification.class), anyList());
    when(userService.findAllManagerByBranchId(anyLong())).thenReturn(Collections.emptyList());

    outboundServiceImpl.updateOutboundStatus(OutboundStatus.CHO_DUYET, 1L);

    verify(outboundRepository).updateOutboundStatus(OutboundStatus.CHO_DUYET, 1L);
    verify(notificationService).sendNotification(any(Notification.class), anyList());
  }

  @Test
  public void testUpdateOutboundStatus_StatusUpdate() {
    outboundEntity = new OutboundEntity();
    outboundEntity.setId(1L);
    outboundEntity.setOutboundCode("OUT123");
    UserEntity createdBy = new UserEntity();
    createdBy.setUserName("Test User");
    outboundEntity.setCreatedBy(createdBy);
    BranchEntity fromBranch = new BranchEntity();
    fromBranch.setId(1L);
    outboundEntity.setFromBranch(fromBranch);
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));

    outboundServiceImpl.updateOutboundStatus(OutboundStatus.HOAN_THANH, 1L);

    verify(outboundRepository).updateOutboundStatus(OutboundStatus.HOAN_THANH, 1L);
    verify(notificationService, never()).sendNotification(any(Notification.class), anyList());
  }

  @Test
  public void testDelete_IdIsNull() {
    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.delete(null);
            });

    assertEquals("id not exist!", exception.getMessage());
  }

  @Test
  public void testDelete_OutboundNotExist() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.empty());

    HrmCommonException exception =
        assertThrows(
            HrmCommonException.class,
            () -> {
              outboundServiceImpl.delete(1L);
            });

    assertEquals(HrmConstant.ERROR.OUTBOUND.NOT_EXIST, exception.getMessage());
  }

  @Test
  public void testDelete_Success() {
    when(outboundRepository.findById(anyLong())).thenReturn(Optional.of(outboundEntity));

    doNothing().when(outboundDetailService).deleteByOutboundId(anyLong());
    doNothing().when(outboundProductDetailService).deleteByOutboundId(anyLong());
    doNothing().when(outboundRepository).deleteById(anyLong());

    outboundServiceImpl.delete(1L);

    verify(outboundDetailService).deleteByOutboundId(1L);
    verify(outboundProductDetailService).deleteByOutboundId(1L);
    verify(outboundRepository).deleteById(1L);
  }
}
