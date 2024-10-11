package com.example.hrm_be.controllers.supplier;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.SupplierService;
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
@RequestMapping("/api/v1/staff/supplier")
@Tag(name = "Admin-Users API")
@SecurityRequirement(name = "Authorization")
public class StaffSupplierController {
  private final SupplierService supplierService;

  // GET: /api/v1/staff/supplier
  // Get List of Suppliers
  // Allows paging, sorting, and searching by name or location
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Supplier>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "") String name) {

    // Retrieve paginated list of suppliers from the service
    Page<Supplier> supplierPage = supplierService.getByPaging(page, size, sortBy, name);

    // Construct response object with supplier data and pagination details
    BaseOutput<List<Supplier>> response =
        BaseOutput.<List<Supplier>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(supplierPage.getTotalPages()) // Total number of pages available
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the current page
            .total(supplierPage.getTotalElements()) // Total number of suppliers
            .data(supplierPage.getContent()) // List of suppliers in the current page
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/supplier/{id}
  // Get Supplier by Id
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Supplier>> getById(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Retrieve supplier by ID
    Supplier supplier = supplierService.getById(id);

    // Construct response object with supplier data
    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(supplier) // Attach supplier data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/supplier
  // Create new Supplier
  @PostMapping()
  protected ResponseEntity<BaseOutput<Supplier>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Supplier supplier) {

    // Check if the supplier object is null
    if (supplier == null) {
      // Create response indicating invalid request body
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED) // Set response status to FAILED
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to create a new supplier
    Supplier createdSupplier = supplierService.create(supplier);

    // Construct response object with created supplier data
    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(createdSupplier) // Attach newly created supplier data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/supplier/{id}
  // Update Supplier
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Supplier>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Supplier supplier) {

    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    supplier.setId(id); // Set the ID of the supplier to update
    // Call service to update the supplier
    Supplier updatedSupplier = supplierService.update(supplier);

    // Construct response object with updated supplier data
    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(updatedSupplier) // Attach updated supplier data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/supplier/{id}
  // Delete Supplier
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to delete the supplier by ID
    supplierService.delete(id);

    // Construct response object indicating successful deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString()) // Response data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build());
  }
}
