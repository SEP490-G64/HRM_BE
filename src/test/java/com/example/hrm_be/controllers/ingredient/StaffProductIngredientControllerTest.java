package com.example.hrm_be.controllers.ingredient;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.responses.BaseOutput;
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

class StaffProductIngredientControllerTest {

  private ProductIngredientService productIngredientService;
  private StaffProductIngredientController productIngredientController;

  @BeforeEach
  void setUp() {
    productIngredientService = mock(ProductIngredientService.class);
    productIngredientController = new StaffProductIngredientController(productIngredientService);
  }

  @Test
  void getByPaging_ShouldReturnListOfProductIngredients() {
    // Arrange
    ProductIngredient ingredient = new ProductIngredient();
    ingredient.setId(1L);
    ingredient.setName("Ingredient1");

    Page<ProductIngredient> page = new PageImpl<>(Collections.singletonList(ingredient));
    when(productIngredientService.getByPaging(0, 20, "id")).thenReturn(page);

    // Act
    ResponseEntity<BaseOutput<List<ProductIngredient>>> response =
        productIngredientController.getByPaging(0, 20, "id");

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
    assertEquals(1, response.getBody().getData().size());
  }

  @Test
  void getById_ShouldReturnProductIngredient() {
    // Arrange
    ProductIngredient ingredient = new ProductIngredient();
    ingredient.setId(1L);
    ingredient.setName("Ingredient1");

    when(productIngredientService.getById(1L)).thenReturn(ingredient);

    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.getById(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ingredient, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void create_ShouldReturnCreatedProductIngredient() {
    // Arrange
    ProductIngredient ingredient = new ProductIngredient();
    ingredient.setName("Ingredient1");

    ProductIngredient createdIngredient = new ProductIngredient();
    createdIngredient.setId(1L);
    createdIngredient.setName("Ingredient1");

    when(productIngredientService.create(ingredient)).thenReturn(createdIngredient);

    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.create(ingredient);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(createdIngredient, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void update_ShouldReturnUpdatedProductIngredient() {
    // Arrange
    ProductIngredient ingredient = new ProductIngredient();
    ingredient.setId(1L);
    ingredient.setName("Updated Ingredient");

    when(productIngredientService.update(ingredient)).thenReturn(ingredient);

    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.update(1L, ingredient);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ingredient, response.getBody().getData());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void delete_ShouldReturnOk() {
    // Arrange
    doNothing().when(productIngredientService).delete(anyLong());

    // Act
    ResponseEntity<BaseOutput<String>> response = productIngredientController.delete(1L);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ResponseStatus.SUCCESS, response.getBody().getStatus());
  }

  @Test
  void getById_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.getById(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void create_ShouldReturnBadRequest_WhenIngredientIsNull() {
    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.create(null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(HrmConstant.ERROR.REQUEST.INVALID_BODY, response.getBody().getErrors().get(0));
  }

  @Test
  void update_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<ProductIngredient>> response =
        productIngredientController.update(-1L, new ProductIngredient());

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }

  @Test
  void delete_ShouldReturnBadRequest_WhenIdIsInvalid() {
    // Act
    ResponseEntity<BaseOutput<String>> response = productIngredientController.delete(-1L);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(ResponseStatus.FAILED, response.getBody().getStatus());
    assertEquals(
        HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE, response.getBody().getErrors().get(0));
  }
}
