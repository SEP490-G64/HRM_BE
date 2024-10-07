package com.example.hrm_be.controllers.productCategory;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.ProductCategory;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ProductCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/category")
@Tag(name = "Staff-ProductCategories API")
@SecurityRequirement(name = "Authorization")
public class StaffProductCategoryController {
  // Injecting ProductCategoryService to handle business logic for ProductCategory
  private final ProductCategoryService productCategoryService;

  // Handles GET requests for paginated list of ProductCategory entities
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<ProductCategory>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<ProductCategory> categoryPage =
        productCategoryService.getByPaging(page, size, sortBy, keyword);

    // Building response object with the retrieved data and pagination details
    BaseOutput<List<ProductCategory>> response =
        BaseOutput.<List<ProductCategory>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(categoryPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(categoryPage.getTotalElements())
            .data(categoryPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles GET requests to retrieve a single ProductCategory by ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductCategory>> getById(@PathVariable("id") Long id) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<ProductCategory> response =
          BaseOutput.<ProductCategory>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve ProductCategory by ID
    ProductCategory productCategory = productCategoryService.getById(id);

    // Building success response
    BaseOutput<ProductCategory> response =
        BaseOutput.<ProductCategory>builder()
            .message(HttpStatus.OK.toString())
            .data(productCategory)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles POST requests to create a new ProductCategory
  @PostMapping()
  protected ResponseEntity<BaseOutput<ProductCategory>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          ProductCategory productCategory) {
    // Validation: If the request body is null, return a bad request response
    if (productCategory == null) {
      BaseOutput<ProductCategory> response =
          BaseOutput.<ProductCategory>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the new ProductCategory via service
    ProductCategory createdCategory = productCategoryService.create(productCategory);

    // Building success response
    BaseOutput<ProductCategory> response =
        BaseOutput.<ProductCategory>builder()
            .message(HttpStatus.OK.toString())
            .data(createdCategory)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles PUT requests to update an existing ProductCategory by ID
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductCategory>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          ProductCategory productCategory) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<ProductCategory> response =
          BaseOutput.<ProductCategory>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID to the existing product category before updating
    productCategory.setId(id);
    ProductCategory updateCategory = productCategoryService.update(productCategory);

    // Building success response
    BaseOutput<ProductCategory> response =
        BaseOutput.<ProductCategory>builder()
            .message(HttpStatus.OK.toString())
            .data(updateCategory)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles DELETE requests to remove a ProductCategory by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the ProductCategory via service
    productCategoryService.delete(id);

    // Building success response after deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
