package com.example.hrm_be.controllers.manufacturer;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ManufacturerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/manufacturer")
@Tag(name = "Admin-Manufacturers API")
@SecurityRequirement(name = "Authorization")
public class StaffManufacturerController {
  private final ManufacturerService manufacturerService;

  // GET: /api/v1/staff/get-all
  // Get List of Manufacturer
  // GET all Manufacturer
  @GetMapping("/get-all")
  protected ResponseEntity<BaseOutput<List<Manufacturer>>> getAll() {

    // Retrieve paginated list of unitConversions from the service
    List<Manufacturer> manufacturers = manufacturerService.getAll();

    // Construct response object with unitConversion data and pagination details
    BaseOutput<List<Manufacturer>> response =
        BaseOutput.<List<Manufacturer>>builder()
            .message(HttpStatus.OK.toString())
            .data(manufacturers)
            .status(ResponseStatus.SUCCESS) // Set response tatus to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/Manufacturer
  // Get List of Manufacturers
  // Allows paging, sorting, and searching by name or location
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Manufacturer>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "") boolean status) {

    // Retrieve paginated list of Manufacturers from the service
    Page<Manufacturer> ManufacturerPage = manufacturerService.getByPaging(page, size, sortBy, keyword, status);

    // Construct response object with Manufacturer data and pagination details
    BaseOutput<List<Manufacturer>> response =
        BaseOutput.<List<Manufacturer>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(ManufacturerPage.getTotalPages()) // Total number of pages available
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the current page
            .total(ManufacturerPage.getTotalElements()) // Total number of Manufacturers
            .data(ManufacturerPage.getContent()) // List of Manufacturers in the current page
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/Manufacturer/{id}
  // Get Manufacturer by ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Manufacturer>> getById(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<Manufacturer> response =
          BaseOutput.<Manufacturer>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Retrieve Manufacturer by ID
    Manufacturer Manufacturer = manufacturerService.getById(id);

    // Construct response object with Manufacturer data
    BaseOutput<Manufacturer> response =
        BaseOutput.<Manufacturer>builder()
            .message(HttpStatus.OK.toString())
            .data(Manufacturer) // Attach Manufacturer data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/Manufacturer
  // Create new Manufacturer
  @PostMapping()
  protected ResponseEntity<BaseOutput<Manufacturer>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Manufacturer manufacturer) {

    // Check if the Manufacturer object is null
    if (manufacturer == null) {
      // Create response indicating invalid request body
      BaseOutput<Manufacturer> response =
          BaseOutput.<Manufacturer>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED) // Set response status to FAILED
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to create a new Manufacturer
    Manufacturer createdManufacturer = manufacturerService.create(manufacturer);

    // Construct response object with created Manufacturer data
    BaseOutput<Manufacturer> response =
        BaseOutput.<Manufacturer>builder()
            .message(HttpStatus.OK.toString())
            .data(createdManufacturer) // Attach newly created Manufacturer data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/Manufacturer/{id}
  // Update Manufacturer
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Manufacturer>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Manufacturer manufacturer) {

    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<Manufacturer> response =
          BaseOutput.<Manufacturer>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    manufacturer.setId(id); // Set the ID of the Manufacturer to update
    // Call service to update the Manufacturer
    Manufacturer updatedManufacturer = manufacturerService.update(manufacturer);

    // Construct response object with updated Manufacturer data
    BaseOutput<Manufacturer> response =
        BaseOutput.<Manufacturer>builder()
            .message(HttpStatus.OK.toString())
            .data(updatedManufacturer) // Attach updated Manufacturer data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/Manufacturer/{id}
  // Delete Manufacturer
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

    // Call service to delete the Manufacturer by ID
    manufacturerService.delete(id);

    // Construct response object indicating successful deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString()) // Response data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build());
  }
}
