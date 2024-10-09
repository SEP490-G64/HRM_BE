package com.example.hrm_be.controllers.unitOfMeasurement;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.UnitOfMeasurement;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UnitOfMeasurementService;
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
@RequestMapping("/api/v1/staff/unit-of-measurement")
@Tag(name = "Admin-Units API")
@SecurityRequirement(name = "Authorization")
public class StaffUnitOfMeasurementController {
  private final UnitOfMeasurementService unitOfMeasurementService;

  // GET: /api/v1/staff/unitOfMeasurement
  // Get List of UnitOfMeasurements
  // Allows paging, sorting, and searching by name or location
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<UnitOfMeasurement>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "") String name) {

    // Retrieve paginated list of unitOfMeasurements from the service
    Page<UnitOfMeasurement> unitOfMeasurementPage =
        unitOfMeasurementService.getByPaging(page, size, sortBy, name);

    // Construct response object with unitOfMeasurement data and pagination details
    BaseOutput<List<UnitOfMeasurement>> response =
        BaseOutput.<List<UnitOfMeasurement>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(unitOfMeasurementPage.getTotalPages()) // Total number of pages available
            .currentPage(page) // Current page number
            .pageSize(size) // Size of the current page
            .total(unitOfMeasurementPage.getTotalElements()) // Total number of unitOfMeasurements
            .data(
                unitOfMeasurementPage
                    .getContent()) // List of unitOfMeasurements in the current page
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/unitOfMeasurement/{id}
  // Get UnitOfMeasurement by Id
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<UnitOfMeasurement>> getById(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<UnitOfMeasurement> response =
          BaseOutput.<UnitOfMeasurement>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Retrieve unitOfMeasurement by ID
    UnitOfMeasurement unitOfMeasurement = unitOfMeasurementService.getById(id);

    // Construct response object with unitOfMeasurement data
    BaseOutput<UnitOfMeasurement> response =
        BaseOutput.<UnitOfMeasurement>builder()
            .message(HttpStatus.OK.toString())
            .data(unitOfMeasurement) // Attach unitOfMeasurement data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/unitOfMeasurement
  // Create new UnitOfMeasurement
  @PostMapping()
  protected ResponseEntity<BaseOutput<UnitOfMeasurement>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          UnitOfMeasurement unitOfMeasurement) {

    // Check if the unitOfMeasurement object is null
    if (unitOfMeasurement == null) {
      // Create response indicating invalid request body
      BaseOutput<UnitOfMeasurement> response =
          BaseOutput.<UnitOfMeasurement>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED) // Set response status to FAILED
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to create a new unitOfMeasurement
    UnitOfMeasurement createdUnitOfMeasurement = unitOfMeasurementService.create(unitOfMeasurement);

    // Construct response object with created unitOfMeasurement data
    BaseOutput<UnitOfMeasurement> response =
        BaseOutput.<UnitOfMeasurement>builder()
            .message(HttpStatus.OK.toString())
            .data(createdUnitOfMeasurement) // Attach newly created unitOfMeasurement data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/unitOfMeasurement/{id}
  // Update UnitOfMeasurement
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<UnitOfMeasurement>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          UnitOfMeasurement unitOfMeasurement) {

    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<UnitOfMeasurement> response =
          BaseOutput.<UnitOfMeasurement>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    unitOfMeasurement.setId(id); // Set the ID of the unitOfMeasurement to update
    // Call service to update the unitOfMeasurement
    UnitOfMeasurement updatedUnitOfMeasurement = unitOfMeasurementService.update(unitOfMeasurement);

    // Construct response object with updated unitOfMeasurement data
    BaseOutput<UnitOfMeasurement> response =
        BaseOutput.<UnitOfMeasurement>builder()
            .message(HttpStatus.OK.toString())
            .data(updatedUnitOfMeasurement) // Attach updated unitOfMeasurement data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/unitOfMeasurement/{id}
  // Delete UnitOfMeasurement
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

    // Call service to delete the unitOfMeasurement by ID
    unitOfMeasurementService.delete(id);

    // Construct response object indicating successful deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString()) // Response data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build());
  }
}
