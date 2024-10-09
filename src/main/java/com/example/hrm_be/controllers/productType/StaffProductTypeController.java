package com.example.hrm_be.controllers.productType;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.ProductType;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ProductTypeService;
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
@RequestMapping("/api/v1/staff/type")
@Tag(name = "Staff-Types API")
@SecurityRequirement(name = "Authorization")
public class StaffProductTypeController {
  // Injecting ProductTypeService to handle business logic for ProductType
  private final ProductTypeService productTypeService;

  // Handles GET requests for paginated list of ProductType entities
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<ProductType>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<ProductType> categoryPage = productTypeService.getByPaging(page, size, sortBy, keyword);

    // Building response object with the retrieved data and pagination details
    BaseOutput<List<ProductType>> response =
        BaseOutput.<List<ProductType>>builder()
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

  // Handles GET requests to retrieve a single ProductType by ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductType>> getById(@PathVariable("id") Long id) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<ProductType> response =
          BaseOutput.<ProductType>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve ProductType by ID
    ProductType productType = productTypeService.getById(id);

    // Building success response
    BaseOutput<ProductType> response =
        BaseOutput.<ProductType>builder()
            .message(HttpStatus.OK.toString())
            .data(productType)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles POST requests to create a new ProductType
  @PostMapping()
  protected ResponseEntity<BaseOutput<ProductType>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") ProductType productType) {
    // Validation: If the request body is null, return a bad request response
    if (productType == null) {
      BaseOutput<ProductType> response =
          BaseOutput.<ProductType>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the new ProductType via service
    ProductType createdType = productTypeService.create(productType);

    // Building success response
    BaseOutput<ProductType> response =
        BaseOutput.<ProductType>builder()
            .message(HttpStatus.OK.toString())
            .data(createdType)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles PUT requests to update an existing ProductType by ID
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductType>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") ProductType productType) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<ProductType> response =
          BaseOutput.<ProductType>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID to the existing product type before updating
    productType.setId(id);
    ProductType updateType = productTypeService.update(productType);

    // Building success response
    BaseOutput<ProductType> response =
        BaseOutput.<ProductType>builder()
            .message(HttpStatus.OK.toString())
            .data(updateType)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles DELETE requests to remove a ProductType by ID
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

    // Delete the ProductType via service
    productTypeService.delete(id);

    // Building success response after deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
