package com.example.hrm_be.controllers.manufacturer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ManufacturerService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StaffManufacturerControllerTest {
  private ManufacturerService manufacturerService;
  private StaffManufacturerController manufacturerController;

  @BeforeEach
  void setUp() {
    manufacturerService = mock(ManufacturerService.class);
    manufacturerController = new StaffManufacturerController(manufacturerService);
  }

  @Test
  void getByPaging_ShouldReturnListOfManufacturers() {
    // Arrange
    Manufacturer manufacturer =
        new Manufacturer()
            .setId(1L)
            .setManufacturerName("Manufacturer1")
            .setAddress("Address1")
            .setPhoneNumber("123456789");

    Page<Manufacturer> page = new PageImpl<>(Collections.singletonList(manufacturer));
    when(manufacturerService.getByPaging(0, 10, "id", "")).thenReturn(page);

    // Act
    ResponseEntity<BaseOutput<List<Manufacturer>>> response =
        manufacturerController.getByPaging(0, 10, "id", "");

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
    assertEquals(1, response.getBody().getData().size());
  }

  @Test
  void getById_ShouldReturnManufacturer() {
    // Arrange
    Manufacturer manufacturer =
        new Manufacturer()
            .setId(1L)
            .setManufacturerName("Manufacturer1")
            .setAddress("Address1")
            .setPhoneNumber("123456789");

    when(manufacturerService.getById(1L)).thenReturn(manufacturer);

    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response = manufacturerController.getById(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(manufacturer, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void create_ShouldReturnCreatedManufacturer() {
    // Arrange
    Manufacturer manufacturer =
        new Manufacturer()
            .setManufacturerName("Manufacturer1")
            .setAddress("Address1")
            .setPhoneNumber("123456789");

    Manufacturer createdManufacturer =
        new Manufacturer()
            .setId(1L)
            .setManufacturerName("Manufacturer1")
            .setAddress("Address1")
            .setPhoneNumber("123456789");

    when(manufacturerService.create(manufacturer)).thenReturn(createdManufacturer);

    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response = manufacturerController.create(manufacturer);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(createdManufacturer, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void update_ShouldReturnUpdatedManufacturer() {
    // Arrange
    Manufacturer manufacturer =
        new Manufacturer()
            .setId(1L)
            .setManufacturerName("Updated Manufacturer")
            .setAddress("Updated Address")
            .setPhoneNumber("987654321");

    when(manufacturerService.update(manufacturer)).thenReturn(manufacturer);

    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response =
        manufacturerController.update(1L, manufacturer);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(manufacturer, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void delete_ShouldReturnOk() {
    // Arrange
    doNothing().when(manufacturerService).delete(anyLong());

    // Act
    ResponseEntity<BaseOutput<String>> response = manufacturerController.delete(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void getById_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response = manufacturerController.getById(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void create_ShouldReturnBadRequest_WhenManufacturerIsNull() {
    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response = manufacturerController.create(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(HrmConstant.ERROR.REQUEST.INVALID_BODY, response.getBody().getErrors().get(0));
  }

  @Test
  void update_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<Manufacturer>> response =
        manufacturerController.update(-1L, new Manufacturer());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void delete_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<String>> response = manufacturerController.delete(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }
}
