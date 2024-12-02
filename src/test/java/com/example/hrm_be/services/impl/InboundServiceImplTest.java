package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.components.*;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.services.*;
import com.example.hrm_be.utils.PDFUtil;
import com.itextpdf.text.DocumentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.relational.core.sql.In;
import org.testcontainers.shaded.org.apache.commons.lang3.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboundServiceImplTest {

    @Mock
    private InboundRepository inboundRepository;

    @Mock
    private InboundMapper inboundMapper;

    @Mock
    private UnitOfMeasurementMapper unitOfMeasurementMapper;

    @InjectMocks
    private InboundServiceImpl inboundService;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<InboundEntity> criteriaQuery;

    @Mock
    private Root<InboundEntity> root;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private InboundBatchDetailService inboundBatchDetailService;

    @Mock
    private InboundDetailsService inboundDetailsService;

    @Mock
    private ProductService productService;

    @Mock
    private BatchService batchService;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private NotificationService notificationService;

    @Mock private PDFUtil pdfUtil;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BranchProductService branchProductService;

    @Mock
    private ProductSupplierService productSupplierService;

    @Mock
    private EntityManager entityManager;  // Mocked EntityManager

    @Mock
    private BatchMapper batchMapper;

    @Mock
    private BranchBatchService branchBatchService;

    @Mock private
    InventoryCheckService inventoryCheckService; // Mock the InventoryCheckService

    private Long inboundId;
    private InboundEntity inboundEntity;
    private ProductEntity productEntity;
    private InboundDetailsEntity inboundDetailsEntity;
    private CreateInboundRequest request;
    private Inbound inbound;
    private ProductInbound productInbound;
    private Batch batch;
    private BranchEntity branchEntity;
    private UserEntity userEntity;
    private InboundType inboundType;
    private InboundStatus status;
    private ByteArrayOutputStream byteArrayOutputStream;
    private InboundDetail inboundDetail;
    private InboundEntity updatedInboundEntity;
    private SupplierEntity supplierEntity;
    private ProductSuppliers productSuppliers;
    private ProductSuppliersEntity productSuppliersEntity;
    private BatchEntity batchEntity;
    private Product product;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        request = new CreateInboundRequest();
        request.setInboundId(1L);

        inboundEntity = new InboundEntity();
        inboundEntity.setId(1L);
        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        inbound = new Inbound();
        inbound.setId(1L);

        productInbound = new ProductInbound();
        productInbound.setProductName("product 1");
        productInbound.setRequestQuantity(100);
        productInbound.setDiscount(10.0);
        productInbound.setTaxRate(BigDecimal.valueOf(5.0));
        productInbound.setReceiveQuantity(90);
        productInbound.setPrice(12.0);

        batch = new Batch();
        batch.setBatchCode("BATCH001");
        batch.setInboundBatchQuantity(100);
        batch.setInboundPrice(BigDecimal.valueOf(10.0));

        productInbound.setBatches(List.of(batch));

        request.setProductInbounds(List.of(productInbound));
        inboundId = 1L;
        inboundEntity = new InboundEntity();
        inboundEntity.setId(inboundId);

        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setProductName("Paracetamol");
        productEntity.setRegistrationCode("GC-215-13");

        product = new Product();
        product.setId(1L);
        product.setProductName("Paracetamol");
        product.setRegistrationCode("GC-215-13");

        inboundDetailsEntity = new InboundDetailsEntity();
        inboundDetailsEntity.setProduct(productEntity);
        inboundDetailsEntity.setReceiveQuantity(10);
        inboundDetailsEntity.setInboundPrice(BigDecimal.valueOf(100));
        userEntity = new UserEntity();
        userEntity.setUserName("test");
        branchEntity = new BranchEntity();
        inboundType = InboundType.NHAP_TU_NHA_CUNG_CAP;
        status = mock(InboundStatus.class);
        byteArrayOutputStream = new ByteArrayOutputStream();
        inboundDetail = new InboundDetail();

        branchEntity = new BranchEntity();
        branchEntity.setId(1L);
        supplierEntity = new SupplierEntity();
        supplierEntity.setId(1L);

        inboundEntity = new InboundEntity();
        inboundEntity.setId(1L);
        inboundEntity.setStatus(InboundStatus.KIEM_HANG);
        inboundEntity.setToBranch(branchEntity);
        inboundEntity.setSupplier(supplierEntity);
        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Ensure InboundType is set
        inboundEntity.setCreatedBy(new UserEntity());

        updatedInboundEntity = new InboundEntity();
        updatedInboundEntity.setId(1L);
        updatedInboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP);
        updatedInboundEntity.setIsApproved(true);
        updatedInboundEntity.setStatus(InboundStatus.KIEM_HANG);
        updatedInboundEntity.setToBranch(branchEntity);
        updatedInboundEntity.setSupplier(supplierEntity);
        updatedInboundEntity.setCreatedBy(new UserEntity());

        productEntity = new ProductEntity();
        productSuppliersEntity = new ProductSuppliersEntity();
        productSuppliersEntity.setProduct(productEntity);
        productSuppliersEntity.setSupplier(supplierEntity);

        productSuppliers = new ProductSuppliers();
        productSuppliers.setId(1L);

        batchEntity = new BatchEntity();
        batchEntity.setId(1L);
        batchEntity.setProduct(productEntity);


        inbound = new Inbound();

        inboundDetail = new InboundDetail();
        inboundDetail.setProductBatchDetails(Collections.emptyList());
    }

    // UTCID01: GetById - Valid Inbound
    @Test
    void UTCID01_testGetById_Success() {
        inboundEntity.setInboundDetails(List.of(inboundDetailsEntity));

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inboundEntity));

        InboundDetail inboundDTO = new InboundDetail();
        inboundDTO.setProductBatchDetails(Collections.emptyList());
        when(inboundMapper.convertToInboundDetail(inboundEntity)).thenReturn(inboundDTO);

        InboundDetail result = inboundService.getById(inboundId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(inboundDTO, result);
    }

    // UTCID02: GetById - Invalid inboundId is null
    @Test
    void UTCID02_testGetById_IdNull() {
        Assertions.assertThrows(HrmCommonException.class, () -> inboundService.getById(null));
    }

    // UTCID03: GetById - Inbound Not Found
    @Test
    void UTCID03_testGetById_InboundNotFound() {
        when(inboundRepository.findById(inboundId)).thenReturn(Optional.empty());
        Assertions.assertThrows(HrmCommonException.class, () -> inboundService.getById(inboundId));
    }

    // UTCID04: GetById - Product is Null
    @Test
    void UTCID04_testGetById_ProductNull() {
        inboundDetailsEntity.setProduct(null);
        inboundEntity.setInboundDetails(List.of(inboundDetailsEntity));

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertToInboundDetail(inboundEntity)).thenReturn(new InboundDetail());

        InboundDetail result = inboundService.getById(inboundId);

        Assertions.assertNotNull(result);
    }

    // UTCID05: GetById - Batch is Null
    @Test
    void UTCID05_testGetById_BatchNullInProduct() {
        productEntity.setBatches(null); // Batches are null
        inboundDetailsEntity.setProduct(productEntity);
        inboundEntity.setInboundDetails(List.of(inboundDetailsEntity));

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertToInboundDetail(inboundEntity)).thenReturn(new InboundDetail());

        InboundDetail result = inboundService.getById(inboundId);

        Assertions.assertNotNull(result);
    }

    // UTCID06: GetById - Valid Id And Batches
    @Test
    void UTCID06_testGetById_ValidIdAndBatches() {
        BatchEntity mockBatch = new BatchEntity();
        mockBatch.setId(1L);
        InboundBatchDetailEntity inboundBatchDetail = new InboundBatchDetailEntity();
        inboundBatchDetail.setInbound(inboundEntity);
        inboundBatchDetail.setQuantity(10);
        mockBatch.setInboundBatchDetail(List.of(inboundBatchDetail));
        productEntity.setBatches(List.of(mockBatch));
        inboundDetailsEntity.setProduct(productEntity);
        inboundEntity.setInboundDetails(List.of(inboundDetailsEntity));

        when(inboundRepository.findById(inboundId)).thenReturn(Optional.of(inboundEntity));

        InboundDetail inboundDTO = new InboundDetail();
        when(inboundMapper.convertToInboundDetail(inboundEntity)).thenReturn(inboundDTO);
        when(unitOfMeasurementMapper.toDTO(productEntity.getBaseUnit())).thenReturn(new UnitOfMeasurement());

        InboundDetail result = inboundService.getById(inboundId);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getProductBatchDetails().isEmpty());

        InboundProductDetailDTO productDetail = result.getProductBatchDetails().get(0);
        Assertions.assertEquals(productEntity.getId(), productDetail.getProductId());
        Assertions.assertFalse(productDetail.getBatches().isEmpty());

        Batch batch = productDetail.getBatches().get(0);
        Assertions.assertEquals(mockBatch.getId(), batch.getId());
        Assertions.assertEquals(10, batch.getInboundBatchQuantity());
    }

    // UTCID07: GetById - Valid Parameters
    @Test
    void UTCID07_testGetByPaging_withValidParameters() {
        // Arrange
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "createdDate";
        String direction = "ASC";
        Long branchId = 1L;
        String keyword = "test";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        InboundStatus status = InboundStatus.BAN_NHAP;
        InboundType type = InboundType.NHAP_TU_NHA_CUNG_CAP;

        // Mocked data
        InboundEntity mockEntity = new InboundEntity();
        mockEntity.setId(1L);

        Inbound mockDTO = new Inbound();
        mockDTO.setId(1L);

        List<InboundEntity> mockEntityList = List.of(mockEntity);
        Page<InboundEntity> mockPage = new PageImpl<>(mockEntityList);

        when(inboundRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        when(inboundMapper.toDTO(mockEntity)).thenReturn(mockDTO);

        // Act
        Page<Inbound> result = inboundService.getByPaging(
                pageNo, pageSize, sortBy, direction, branchId, keyword, startDate, endDate, status, type);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getContent().size());
        Assertions.assertEquals(1L, result.getContent().get(0).getId());
    }

    // UTCID08: GetById - Valid Descending Sort
    @Test
    void UTCID08_testGetByPaging_withDescendingSort() {
        // Arrange
        int pageNo = 0;
        int pageSize = 5;
        String sortBy = "updatedDate";
        String direction = "DESC";

        // Mocked data
        List<InboundEntity> mockEntityList = List.of();
        Page<InboundEntity> mockPage = new PageImpl<>(mockEntityList);

        when(inboundRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<Inbound> result = inboundService.getByPaging(
                pageNo, pageSize, sortBy, direction, null, null, null, null, null, null);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());

    }

    // UTCID09: GetById - inValid Null Parameters
    @Test
    void UTCID09_testGetByPaging_withNullParameters() {
        // Arrange
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "id";
        String direction = null; // Should default to DESC

        List<InboundEntity> mockEntityList = List.of();
        Page<InboundEntity> mockPage = new PageImpl<>(mockEntityList);

        when(inboundRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        Page<Inbound> result = inboundService.getByPaging(
                pageNo, pageSize, sortBy, direction, null, null, null, null, null, null);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
    }

    // UTCID10: GetById - Valid Filters
    @Test
    void testGetByPaging_withFilters() {
        // Arrange
        Long branchId = 1L;
        String keyword = "keyword";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        InboundStatus status = InboundStatus.BAN_NHAP;
        InboundType type = InboundType.NHAP_TU_NHA_CUNG_CAP;

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());
        Specification<InboundEntity> specification = inboundService.getSpecification(
                branchId, keyword, startDate, endDate, status, type);

        // Mock results
        List<InboundEntity> mockEntityList = List.of(new InboundEntity());
        Page<InboundEntity> mockPage = new PageImpl<>(mockEntityList, pageable, 1);

        when(inboundRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        when(inboundMapper.toDTO(any(InboundEntity.class)))
                .thenReturn(new Inbound());

        // Act
        Page<Inbound> result = inboundService.getByPaging(
                0, 5, "id", "DESC", branchId, keyword, startDate, endDate, status, type);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    // UTCID11: GetById - Valid AllFilters
    @Test
    void UTCID11_testGetSpecification_withAllFilters() {
        // Arrange: Tạo tham số đầu vào
        Long branchId = 1L;
        String keyword = "TEST";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        InboundStatus status = InboundStatus.BAN_NHAP;
        InboundType type = InboundType.NHAP_TU_NHA_CUNG_CAP;

        // Giả lập hành vi của root.get() và criteriaBuilder
        Path<Object> branchPath = Mockito.mock(Path.class);
        Mockito.when(root.get("toBranch")).thenReturn(branchPath);
        Mockito.when(branchPath.get("id")).thenReturn(Mockito.mock(Path.class));

        Path<Object> inboundCodePath = Mockito.mock(Path.class);
        Mockito.when(root.get("inboundCode")).thenReturn(inboundCodePath);

        Path<Object> createdDatePath = Mockito.mock(Path.class);
        Mockito.when(root.get("createdDate")).thenReturn(createdDatePath);

        Path<Object> statusPath = Mockito.mock(Path.class);
        Mockito.when(root.get("status")).thenReturn(statusPath);

        Path<Object> inboundTypePath = Mockito.mock(Path.class);
        Mockito.when(root.get("inboundType")).thenReturn(inboundTypePath);

        // Act: Tạo Specification và lấy Predicate
        Specification<InboundEntity> specification = new InboundServiceImpl()
                .getSpecification(branchId, keyword, startDate, endDate, status, type);

        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        // Assert:
        assertNull(predicate);
    }

    //Approve Inbound
    // UTCID01: Approve Inbound - Valid
    @Test
    void UTCID01_testApproveInbound_success() {
        // Arrange
        Long id = 1L;
        boolean accept = true;

        inboundEntity.setStatus(InboundStatus.CHO_DUYET);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        InboundEntity updatedInbound = inboundEntity.toBuilder()
                .isApproved(accept)
                .approvedBy(userEntity)
                .status(InboundStatus.CHO_HANG)
                .build();

        Inbound inboundDTO = new Inbound(); // Mocked DTO to return
        inboundDTO.setId(id);

        when(inboundRepository.findById(id)).thenReturn(Optional.of(inboundEntity));
        when(userService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
        when(userService.findLoggedInfoByEmail("test@example.com")).thenReturn(new User());
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(updatedInbound);
        when(inboundMapper.toDTO(any(InboundEntity.class))).thenReturn(inboundDTO);

        // Act
        Inbound result = inboundService.approve(id, accept);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    // UTCID02: Approve Inbound - InValid inbound Null
    @Test
    void UTCID02_testApproveInbound_Invalid_inboundNull() {
        // Arrange
        Long id = 1L;
        boolean accept = true;

        inboundEntity = null;

        when(inboundRepository.findById(id)).thenReturn(Optional.ofNullable(inboundEntity));

        Assertions.assertThrows(HrmCommonException.class, () -> inboundService.approve(id, accept));
    }

    // UTCID03: Approve Inbound - InValid inbound Status Not 'ChoDuyet'
    @Test
    void UTCID03_testApproveInbound_Invalid_inboundStatusNotChoDuyet() {
        // Arrange
        Long id = 1L;
        boolean accept = true;

        inboundEntity.setStatus(InboundStatus.CHUA_LUU);

        when(inboundRepository.findById(id)).thenReturn(Optional.of(inboundEntity));

        Assertions.assertThrows(HrmCommonException.class, () -> inboundService.approve(id, accept));
    }

    // Delete
    // UTCID01: Delete - Valid
    @Test
    void UTCID01_testDelete_withValidId() {
        // Arrange
        Long id = 1L;
        InboundEntity oldInbound = InboundEntity.builder()
                .id(id)
                .build();

        when(inboundRepository.findById(id)).thenReturn(Optional.of(oldInbound));

        // Act
        inboundService.delete(id);

        // Assert
        verify(inboundRepository, times(1)).findById(id);
        verify(inboundDetailsService, times(1)).deleteAllByInboundId(id);
        verify(inboundBatchDetailService, times(1)).deleteAllByInboundId(id);
        verify(inboundRepository, times(1)).deleteById(id);
    }

    // UTCID01: Delete - Valid
    @Test
    void testDelete_withNonExistentId() {
        // Arrange
        Long id = 1L;
        when(inboundRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        HrmCommonException exception = assertThrows(HrmCommonException.class, () -> {
            inboundService.delete(id);
        });

        assertEquals(HrmConstant.ERROR.INBOUND.NOT_EXIST, exception.getMessage());
        verify(inboundRepository, times(1)).findById(id);
        verifyNoInteractions(inboundDetailsService, inboundBatchDetailService);
    }

    // UTCID02: Delete - InValid Null Id
    @Test
    void UTCID02_testDelete_withNullId() {
        Assertions.assertThrows(HrmCommonException.class, () -> inboundService.delete(null));
        // Assert
        verifyNoInteractions(inboundRepository, inboundDetailsService, inboundBatchDetailService);
    }

    //SaveInbound
    // UTCID01: SaveInbound - Valid
    @Test
    void UTCID01_testSaveInbound() {
        // Prepare mock input
        request.setProductInbounds(List.of(
                new ProductInbound( /* initialize ProductInbound */ )
        ));
        request.setSupplier(new Supplier());
        request.setToBranch(new Branch());

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>());
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        Product mockProduct = new Product(); // Mocked product
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch(); // Mocked batch
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID02: SaveInbound - Valid Inbound With Batches
    @Test
    void UTCID02_testSaveInboundWithBatches() {

        request.setSupplier(new Supplier());

        Batch batch1 = new Batch();
        batch1.setBatchCode("BATCH001");
        batch1.setInboundBatchQuantity(100);
        batch1.setInboundPrice(BigDecimal.valueOf(10.0));

        Batch batch2 = new Batch();
        batch2.setBatchCode("BATCH002");
        batch2.setInboundBatchQuantity(200);
        batch2.setInboundPrice(BigDecimal.valueOf(20.0));

        productInbound.setBatches(List.of(batch1, batch2));
        productInbound.setProductName("product 1");

        request.setProductInbounds(List.of(productInbound));


        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>());
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        Product mockProduct = new Product();
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");
        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID03: SaveInbound - Valid Inbound With Product Details
    @Test
    void UTCID03_testSaveInboundWithProductDetails() {
        // Prepare mock input
        request.setSupplier(new Supplier());

        Batch batch1 = new Batch();
        batch1.setBatchCode("BATCH001");
        batch1.setInboundBatchQuantity(100);
        batch1.setInboundPrice(BigDecimal.valueOf(10.0));

        request.setProductInbounds(List.of(productInbound));

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>());
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        Product mockProduct = new Product();
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID04: SaveInbound - Valid Inbound With Inbound Details
    @Test
    void UTCID04_testSaveInboundWithInboundDetails() {
        // Prepare mock input
        request.setSupplier(new Supplier());
        request.setProductInbounds(List.of(productInbound));

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        InboundDetails existingDetail = new InboundDetails();
        existingDetail.setProduct(new Product());
        existingDetail.getProduct().setId(1L);

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        when(inboundDetailsService.findByInboundId(1L)).thenReturn(List.of(existingDetail));
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        Product mockProduct = new Product();
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID05: SaveInbound - Valid Inbound With Batches And Details
    @Test
    void UTCID05_testSaveInboundWithBatchesAndDetails() {
        // Prepare mock input
        request.setSupplier(new Supplier());
        request.setProductInbounds(List.of(productInbound));

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        batch.setId(1L);

        InboundDetails existingDetail = new InboundDetails();
        existingDetail.setProduct(new Product());
        existingDetail.getProduct().setId(1L);

        InboundBatchDetail existingBatchDetail = new InboundBatchDetail();
        existingBatchDetail.setBatch(batch);
        existingBatchDetail.setQuantity(50);
        existingBatchDetail.setInboundPrice(BigDecimal.valueOf(8.0));

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        // Use mutable lists for mocks
        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingDetail)));
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingBatchDetail)));

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID06: SaveInbound - Valid Inbound With Existing Inbound Batch Detail
    @Test
    void UTCID06_testSaveInboundWithExistingInboundBatchDetail() {
        // Prepare mock input
        request.setSupplier(new Supplier());
        request.setProductInbounds(List.of(productInbound));

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        batch.setId(1L);

        InboundDetails existingDetail = new InboundDetails();
        existingDetail.setProduct(new Product());
        existingDetail.getProduct().setId(1L);

        InboundBatchDetail existingBatchDetail = new InboundBatchDetail();
        existingBatchDetail.setBatch(batch);
        existingBatchDetail.setQuantity(50);
        existingBatchDetail.setInboundPrice(BigDecimal.valueOf(8.0));

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        // Use mutable lists for mocks
        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingDetail)));
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingBatchDetail)));

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        mockBatch.setId(1L);  // Ensure mock batch ID is set
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID07: SaveInbound - inValid Throws Exception When Supplier Missing
    @Test
    void UTCID07_testSaveInboundThrowsExceptionWhenSupplierMissing() {

        request.setSupplier(null); // No supplier provided

        inboundEntity.setId(1L);
        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));

        // Execute and expect exception
        HrmCommonException thrown =
                assertThrows(
                        HrmCommonException.class,
                        () -> inboundService.saveInbound(request),
                        "Expected saveInbound to throw, but it didn't"
                );

        assertEquals("error.supplier.not_exist", thrown.getMessage());
        verify(inboundRepository).findById(1L);
    }

    // UTCID08: SaveInbound - inValid Throws Exception When Branch Missing
    @Test
    void UTCID08_testSaveInboundThrowsExceptionWhenBranchMissing() {
        // Prepare mock input
        request.setFromBranch(null); // No branch provided

        inboundEntity.setInboundType(InboundType.CHUYEN_KHO_NOI_BO); // Mock branch type

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));

        // Execute and expect exception
        HrmCommonException thrown =
                assertThrows(
                        HrmCommonException.class,
                        () -> inboundService.saveInbound(request),
                        "Expected saveInbound to throw, but it didn't"
                );

        assertEquals("error.branch.not_exist", thrown.getMessage());
        verify(inboundRepository).findById(1L);
    }

    // UTCID09: SaveInbound - Valid Inbound With Batch Quantity Zero
    @Test
    void UTCID09_testSaveInboundWithBatchQuantityZero() {
        // Prepare mock input
        request.setSupplier(new Supplier());

        batch.setId(1L);  // Ensure batch ID is set

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);


        InboundDetails existingDetail = new InboundDetails();
        existingDetail.setProduct(new Product());
        existingDetail.getProduct().setId(1L);

        InboundBatchDetail existingBatchDetail = new InboundBatchDetail();
        existingBatchDetail.setBatch(batch);
        existingBatchDetail.setQuantity(50);
        existingBatchDetail.setInboundPrice(BigDecimal.valueOf(8.0));

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        // Use mutable lists for mocks
        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingDetail)));
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingBatchDetail)));

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        mockBatch.setId(1L);  // Ensure mock batch ID is set
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID10: SaveInbound - Valid Inbound Empty Batch Code Null
    @Test
    void UTCID10testSaveInboundWithEmptyBatchCodeNull() {

        batch.setBatchCode("");
        request.setSupplier(new Supplier());
        // Prepare mock input
        request.setSupplier(new Supplier());

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);
        // Mock repository behavior
        // Mock repository behavior
        when(inboundRepository.findById(request.getInboundId())).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        // Mock services
        Product product = new Product();
        product.setId(1L);
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(product);

        // Use mutable lists for mocks
        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>());
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        // Execute method
        Inbound result = inboundService.saveInbound(request);

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID11: SaveInbound - Valid Inbound With Empty Batch Code
    @Test
    void UTCID11_testSaveInboundWithEmptyBatchCode() {
        // Prepare mock input
        request.setSupplier(new Supplier());

        inboundEntity.setInboundType(InboundType.NHAP_TU_NHA_CUNG_CAP); // Mock supplier type
        inboundEntity.setIsApproved(true);
        inboundEntity.setStatus(InboundStatus.CHO_HANG);

        InboundDetails existingDetail = new InboundDetails();
        existingDetail.setProduct(new Product());
        existingDetail.getProduct().setId(1L);

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toEntity(any(Inbound.class))).thenReturn(inboundEntity);

        // Use mutable lists for mocks
        when(inboundDetailsService.findByInboundId(1L)).thenReturn(new ArrayList<>(List.of(existingDetail)));
        when(inboundBatchDetailService.findByInboundId(1L)).thenReturn(new ArrayList<>());

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        when(productService.addProductInInbound(any(ProductInbound.class))).thenReturn(mockProduct);

        Batch mockBatch = new Batch();
        mockBatch.setId(1L);
        lenient().when(batchService.addBatchInInbound(any(Batch.class), eq(mockProduct))).thenReturn(mockBatch);

        // Execute method with additional debug statements
        System.out.println("Before calling saveInbound");
        Inbound result = inboundService.saveInbound(request);
        System.out.println("After calling saveInbound");

        // Assertions
        assertNull(result, "The result should not be null");

        // Verify interactions
        verify(inboundRepository).findById(1L);
        verify(inboundRepository).save(any(InboundEntity.class));
        verify(inboundDetailsService).saveAll(anyList());
        verify(inboundBatchDetailService).saveAll(anyList());
    }

    // UTCID12: SaveInbound - inValid Throws Exception When Inbound Missing
    @Test
    void UTCID12_testSaveInboundThrowsExceptionWhenInboundMissing() {
        // Prepare mock input
        CreateInboundRequest request = new CreateInboundRequest();
        request.setInboundId(1L);
        request.setSupplier(new Supplier()); // Supplier provided

        // Mocking repository and service calls
        when(inboundRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                        HrmCommonException.class,
                        () -> inboundService.saveInbound(request),
                        "Expected saveInbound to throw, but it didn't"
                );

        verify(inboundRepository).findById(1L);
    }

    //create Innit Inbound
    // UTCID01: create Innit - Valid
    @Test
    void UTCID01_testCreateInnitInbound_Success() {
        // Arrange
        String email = "user@example.com";
        branchEntity.setBranchType(BranchType.MAIN);
        userEntity.setBranch(branchEntity);

        when(userService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userMapper.toEntity((User) any())).thenReturn(userEntity);
        when(inboundRepository.existsByInboundCode(anyString())).thenReturn(false);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(inboundEntity);
        when(inboundMapper.toDTO(any(InboundEntity.class))).thenReturn(inbound);

        // Act
        Inbound result = inboundService.createInnitInbound(inboundType);

        // Assert
        assertNotNull(result);
        verify(inboundRepository, times(1)).save(any(InboundEntity.class));
        verify(inboundRepository, times(1)).existsByInboundCode(anyString());
    }

    // UTCID02: create Innit - in Valid ThrowsException_NonMainBranchFromSupplier
    @Test
    void UTCID02_testCreateInnitInbound_ThrowsException_NonMainBranchFromSupplier() {
        // Arrange
        String email = "user@example.com";
        BranchType branchType = mock(BranchType.class);
        when(branchType.isMain()).thenReturn(false);
        branchEntity.setBranchType(branchType);
        userEntity.setBranch(branchEntity);

        when(userService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userMapper.toEntity((User) any())).thenReturn(userEntity);

        // Act & Assert
        Exception exception = assertThrows(HrmCommonException.class, () -> {
            inboundService.createInnitInbound(inboundType);
        });

        String expectedMessage = "Chỉ có Kho chính mới được phép nhập hàng từ nhà cung cấp";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // UTCID03: create Innit - inValid ThrowsException_InboundCodeExists
    @Test
    void UTCID03_testCreateInnitInbound_ThrowsException_InboundCodeExists() {
        // Arrange
        String email = "user@example.com";
        branchEntity.setBranchType(BranchType.MAIN);
        userEntity.setBranch(branchEntity);

        when(userService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userMapper.toEntity((User) any())).thenReturn(userEntity);
        when(inboundRepository.existsByInboundCode(anyString())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(HrmCommonException.class, () -> {
            inboundService.createInnitInbound(inboundType);
        });

        String expectedMessage = "inbound.exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // UTCID04: create Innit - inValid ThrowsException_BranchNotExist
    @Test
    void UTCID04_testCreateInnitInbound_ThrowsException_BranchNotExist() {
        // Arrange
        String email = "user@example.com";
        userEntity.setBranch(null);

        when(userService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userService.findLoggedInfoByEmail(email)).thenReturn(new User());
        when(userMapper.toEntity((User) any())).thenReturn(userEntity);

        // Act & Assert
        Exception exception = assertThrows(HrmCommonException.class, () -> {
            inboundService.createInnitInbound(inboundType);
        });

        String expectedMessage = "branch.not_exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    //Submit Inbound To System
    // UTCID01: Submit Inbound To System - Valid
    @Test
    void UTCID01_testSubmitInboundToSystem_Success() {
        // Arrange
        request.setSupplier(new Supplier());
        when(inboundRepository.findById(request.getInboundId())).thenReturn(Optional.of(inboundEntity));
        when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        when(inboundMapper.toEntity(inbound)).thenReturn(updatedInboundEntity);
        when(inboundRepository.save(any(InboundEntity.class))).thenReturn(updatedInboundEntity);
        lenient().when(productService.getById(anyLong())).thenReturn(new Product());
        lenient().doNothing().when(branchProductService).updateBranchProductInInbound(any(BranchEntity.class), any(ProductEntity.class), any(BigDecimal.class));
        doNothing().when(entityManager).clear();
        doNothing().when(inventoryCheckService).broadcastToInventoryChecksInBranch(anyLong(), anySet(), anySet());  // Mock the inventoryCheckService method

        // Act
        Inbound result = inboundService.submitInboundToSystem(request);

        // Assert
        assertNull(result);
    }

    // UTCID02: Submit Inbound To System - Valid With Product And Batch Updates
    @Test
    void UTCID02_testSubmitInboundToSystem_WithProductAndBatchUpdates() {
        // Arrange
        request.setSupplier(new Supplier());

        InboundDetailsEntity inboundDetailsEntity = new InboundDetailsEntity();
        inboundDetailsEntity.setProduct(productEntity);
        inboundDetailsEntity.setReceiveQuantity(5);

        InboundBatchDetailEntity inboundBatchDetailsEntity = new InboundBatchDetailEntity();
        inboundBatchDetailsEntity.setBatch(batchEntity);
        inboundBatchDetailsEntity.setQuantity(10);

        inboundEntity.setInboundDetails(List.of(inboundDetailsEntity));
        inboundEntity.setInboundBatchDetails(List.of(inboundBatchDetailsEntity));

        lenient().when(inboundRepository.findById(request.getInboundId())).thenReturn(Optional.of(inboundEntity));
        lenient().when(inboundMapper.convertFromCreateRequest(request)).thenReturn(inbound);
        lenient().when(inboundMapper.toEntity(inbound)).thenReturn(updatedInboundEntity);
        lenient().when(inboundRepository.save(any(InboundEntity.class))).thenReturn(updatedInboundEntity);

        // Handle potential null values
        lenient().when(productMapper.toEntity(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product != null ? productEntity : null;
        });

        lenient().when(batchMapper.toEntity(any(Batch.class))).thenAnswer(invocation -> {
            Batch batch = invocation.getArgument(0);
            return batch != null ? batchEntity : null;
        });

        lenient().when(batchService.getById(anyLong())).thenReturn(batch);
        lenient().doNothing().when(branchProductService).updateBranchProductInInbound(any(BranchEntity.class), any(ProductEntity.class), any(BigDecimal.class));
        lenient().doNothing().when(branchBatchService).updateBranchBatchInInbound(any(BranchEntity.class), any(BatchEntity.class), any(BigDecimal.class));
        lenient().doReturn(null).when(productSupplierService).findByProductAndSupplier(any(ProductEntity.class), any(SupplierEntity.class));
        lenient().when(productSupplierService.save(any(ProductSuppliersEntity.class))).thenReturn(productSuppliers);
        lenient().doNothing().when(entityManager).clear();
        doNothing().when(inventoryCheckService).broadcastToInventoryChecksInBranch(anyLong(), anySet(), anySet());  // Mock the inventoryCheckService method

        // Act
        Inbound result = inboundService.submitInboundToSystem(request);
        System.out.println("Result: " + result);

        // Assert
        assertNull(result);
    }

    // UTCID03: Submit Inbound To System - inValid InboundNotFound
    @Test
    void UTCID03_testSubmitInboundToSystem_InboundNotFound() {
        // Arrange
        when(inboundRepository.findById(request.getInboundId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HrmCommonException.class, () -> {
            inboundService.submitInboundToSystem(request);
        });
    }

    // UTCID04: Submit Inbound To System - Invalid Status
    @Test
    void UTCID04_testSubmitInboundToSystem_InvalidStatus() {
        // Arrange
        inboundEntity.setStatus(InboundStatus.HOAN_THANH);
        when(inboundRepository.findById(request.getInboundId())).thenReturn(Optional.of(inboundEntity));

        // Act & Assert
         assertThrows(HrmCommonException.class, () -> {
            inboundService.submitInboundToSystem(request);
        });
    }

    //Update Inbound Status
    // UTCID01: Inbound Status - Valid
    @Test
    void UTCID01_testUpdateInboundStatus_Success() {
        // Arrange
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        doNothing().when(inboundRepository).updateInboundStatus(any(InboundStatus.class), anyLong());

        // Act
        inboundService.updateInboundStatus(status, 1L);

        // Assert
        verify(inboundRepository, times(1)).findById(1L);
        verify(inboundRepository, times(1)).updateInboundStatus(any(InboundStatus.class), anyLong());
    }

    // UTCID02: Inbound Status - inValid  InboundNotExist
    @Test
    void UTCID02_testUpdateInboundStatus_InboundNotExist() {
        // Arrange
        when(inboundRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(HrmCommonException.class, () -> {
            inboundService.updateInboundStatus(status, 1L);
        });

    }

    // UTCID03: Inbound Status - Valid  WaitingForApprove
    @Test
    void UTCID03_testUpdateInboundStatus_WaitingForApprove() {
        inboundEntity.setCreatedBy(userEntity);
        branchEntity.setId(1L);
        inboundEntity.setToBranch(branchEntity);
        // Arrang
        when(inboundRepository.findById(1L)).thenReturn(Optional.of(inboundEntity));
        doNothing().when(inboundRepository).updateInboundStatus(any(InboundStatus.class), anyLong());
        when(status.isWaitingForApprove()).thenReturn(true);
        doNothing().when(notificationService).sendNotification(any(Notification.class), anyList());
        when(userService.findAllManagerByBranchId(anyLong())).thenReturn(Collections.emptyList());

        // Act
        inboundService.updateInboundStatus(status, 1L);

        // Assert
        verify(notificationService, times(1)).sendNotification(any(Notification.class), anyList());
    }


//    @Test
//    void testGenerateInboundPdf_Success() throws DocumentException, IOException {
//
//        Branch toBranch = new Branch();
//        toBranch.setBranchName("Main Branch");
//        toBranch.setLocation("Main Location");
//        Branch fromBranch = new Branch();
//        fromBranch.setBranchName("Sub Branch");
//        fromBranch.setLocation("Sub Location");
//
//        inboundDetail.setToBranch(toBranch);
//        inboundDetail.setFromBranch(fromBranch);
//        inboundDetail.setProductBatchDetails(Collections.emptyList());
//
//        // Create a spy of InboundServiceImpl
//        InboundServiceImpl inboundServiceSpy = spy(inboundService);
//
//        // Stub the getById method
//        doReturn(inboundDetail).when(inboundServiceSpy).getById(inboundId);
//
//        // Mock pdfUtil.createReceiptPdf method
//        when(pdfUtil.createReceiptPdf(any(InboundDetail.class))).thenReturn(byteArrayOutputStream);
//
//        // Act
//        ByteArrayOutputStream result = inboundServiceSpy.generateInboundPdf(inboundId);
//
//        // Assert
//        assertNotNull(result);
//        verify(inboundServiceSpy, times(1)).getById(inboundId);
//        verify(pdfUtil, times(1)).createReceiptPdf(inboundDetail);
//    }

    //Generate Inbound Pdf
    // UTCID03: Generate Inbound Pdf - inValid  InboundNotFound
    @Test
    void testGenerateInboundPdf_InboundNotFound() throws DocumentException, IOException {
        // Create a spy of InboundServiceImpl
        InboundServiceImpl inboundServiceSpy = spy(inboundService);

        // Stub the getById method to return null
        doReturn(null).when(inboundServiceSpy).getById(inboundId);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            inboundServiceSpy.generateInboundPdf(inboundId);
        });

    }
}
