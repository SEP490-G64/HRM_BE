package com.example.hrm_be.controllers.storageLocation;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.StorageLocation;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.StorageLocationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/storage-location")
@Tag(name = "Admin-Storage-Locations API")
@SecurityRequirement(name = "Authorization")
public class StaffStorageLocationController {
  private final StorageLocationService storageLocationService;

  // GET: /api/v1/staff/storageLocation
  // Get List of storageLocations
  // Allows paging, sorting, and searching by name or location
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<StorageLocation>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "") String name) {

    // Retrieve paginated list of storageLocations from the service
    Page<StorageLocation> storageLocationPage =
        storageLocationService.getByPaging(page, size, sortBy, name);

    // Construct response object with storageLocation data and pagination details
    BaseOutput<List<StorageLocation>> response =
        BaseOutput.<List<StorageLocation>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(storageLocationPage.getTotalPages()) // Total number of pages available
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the current page
            .total(storageLocationPage.getTotalElements()) // Total number of storageLocations
            .data(storageLocationPage.getContent()) // List of storageLocations in the current page
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/storageLocation/{id}
  // Get storageLocation by Id
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<StorageLocation>> getById(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<StorageLocation> response =
          BaseOutput.<StorageLocation>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Retrieve storageLocation by ID
    StorageLocation storageLocation = storageLocationService.getById(id);

    // Construct response object with storageLocation data
    BaseOutput<StorageLocation> response =
        BaseOutput.<StorageLocation>builder()
            .message(HttpStatus.OK.toString())
            .data(storageLocation) // Attach storageLocation data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/storageLocation
  // Create new storageLocation
  @PostMapping()
  protected ResponseEntity<BaseOutput<StorageLocation>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          StorageLocation storageLocation) {

    // Check if the storageLocation object is null
    if (storageLocation == null) {
      // Create response indicating invalid request body
      BaseOutput<StorageLocation> response =
          BaseOutput.<StorageLocation>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED) // Set response status to FAILED
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to create a new storageLocation
    StorageLocation createdstorageLocation = storageLocationService.create(storageLocation);

    // Construct response object with created storageLocation data
    BaseOutput<StorageLocation> response =
        BaseOutput.<StorageLocation>builder()
            .message(HttpStatus.OK.toString())
            .data(createdstorageLocation) // Attach newly created storageLocation data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/storageLocation/{id}
  // Update storageLocation
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<StorageLocation>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          StorageLocation storageLocation) {

    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<StorageLocation> response =
          BaseOutput.<StorageLocation>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    storageLocation.setId(id); // Set the ID of the storageLocation to update
    // Call service to update the storageLocation
    StorageLocation updatedstorageLocation = storageLocationService.update(storageLocation);

    // Construct response object with updated storageLocation data
    BaseOutput<StorageLocation> response =
        BaseOutput.<StorageLocation>builder()
            .message(HttpStatus.OK.toString())
            .data(updatedstorageLocation) // Attach updated storageLocation data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/storageLocation/{id}
  // Delete storageLocation
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

    // Call service to delete the storageLocation by ID
    storageLocationService.delete(id);

    // Construct response object indicating successful deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString()) // Response data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build());
  }
}
