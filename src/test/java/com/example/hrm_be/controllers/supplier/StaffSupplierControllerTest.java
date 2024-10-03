package com.example.hrm_be.controllers.supplier;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class StaffSupplierControllerTest {

  private SupplierService supplierService;
  private StaffSupplierController supplierController;

  @BeforeEach
  void setUp() {
    supplierService = mock(SupplierService.class);
    supplierController = new StaffSupplierController(supplierService);
  }

  @Test
  void getByPaging_ShouldReturnListOfSuppliers() {
    // Arrange
    Supplier supplier =
        new Supplier()
            .setId(1L)
            .setSupplierName("Supplier1")
            .setAddress("Address1")
            .setContactPerson("Contact1")
            .setPhoneNumber("123456789");

    Page<Supplier> page = new PageImpl<>(Collections.singletonList(supplier));
    when(supplierService.getByPaging(0, 10, "id")).thenReturn(page);

    // Act
    ResponseEntity<BaseOutput<List<Supplier>>> response =
        supplierController.getByPaging(0, 10, "id");

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
    assertEquals(1, response.getBody().getData().size());
  }

  @Test
  void getById_ShouldReturnSupplier() {
    // Arrange
    Supplier supplier =
        new Supplier()
            .setId(1L)
            .setSupplierName("Supplier1")
            .setAddress("Address1")
            .setContactPerson("Contact1")
            .setPhoneNumber("123456789");

    when(supplierService.getById(1L)).thenReturn(supplier);

    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.getById(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(supplier, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void create_ShouldReturnCreatedSupplier() {
    // Arrange
    Supplier supplier =
        new Supplier()
            .setSupplierName("Supplier1")
            .setAddress("Address1")
            .setContactPerson("Contact1")
            .setPhoneNumber("123456789");

    Supplier createdSupplier =
        new Supplier()
            .setId(1L)
            .setSupplierName("Supplier1")
            .setAddress("Address1")
            .setContactPerson("Contact1")
            .setPhoneNumber("123456789");

    when(supplierService.create(supplier)).thenReturn(createdSupplier);

    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.create(supplier);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(createdSupplier, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void update_ShouldReturnUpdatedSupplier() {
    // Arrange
    Supplier supplier =
        new Supplier()
            .setId(1L)
            .setSupplierName("Updated Supplier")
            .setAddress("Updated Address")
            .setContactPerson("Updated Contact")
            .setPhoneNumber("987654321");

    when(supplierService.update(supplier)).thenReturn(supplier);

    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.update(1L, supplier);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(supplier, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void delete_ShouldReturnOk() {
    // Arrange
    doNothing().when(supplierService).delete(anyLong());

    // Act
    ResponseEntity<BaseOutput<String>> response = supplierController.delete(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void getById_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.getById(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void create_ShouldReturnBadRequest_WhenSupplierIsNull() {
    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.create(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(HrmConstant.ERROR.REQUEST.INVALID_BODY, response.getBody().getErrors().get(0));
  }

  @Test
  void update_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<Supplier>> response = supplierController.update(-1L, new Supplier());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void delete_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<String>> response = supplierController.delete(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }
}
