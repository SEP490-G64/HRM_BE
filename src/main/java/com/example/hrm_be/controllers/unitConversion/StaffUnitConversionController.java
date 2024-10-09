package com.example.hrm_be.controllers.unitConversion;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.UnitConversion;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.UnitConversionService;
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
@RequestMapping("/api/v1/staff/unit-conversion")
@Tag(name = "Admin-Users API")
@SecurityRequirement(name = "Authorization")
public class StaffUnitConversionController {
  private final UnitConversionService unitConversionService;

  // GET: /api/v1/staff/unitConversion
  // Get List of UnitConversions
  // GET all unit conversion
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<UnitConversion>>> getAll(
      @RequestParam(required = false, defaultValue = "id") String sortBy) {

    // Retrieve paginated list of unitConversions from the service
    List<UnitConversion> unitConversionPage = unitConversionService.getAll();

    // Construct response object with unitConversion data and pagination details
    BaseOutput<List<UnitConversion>> response =
        BaseOutput.<List<UnitConversion>>builder()
            .message(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS) // Set response tatus to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/unitConversion/{id}
  // Get UnitConversion by Id
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<UnitConversion>> getById(@PathVariable("id") Long id) {
    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<UnitConversion> response =
          BaseOutput.<UnitConversion>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Retrieve unitConversion by ID
    UnitConversion unitConversion = unitConversionService.getById(id);

    // Construct response object with unitConversion data
    BaseOutput<UnitConversion> response =
        BaseOutput.<UnitConversion>builder()
            .message(HttpStatus.OK.toString())
            .data(unitConversion) // Attach unitConversion data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/unitConversion
  // Create new UnitConversion
  @PostMapping()
  protected ResponseEntity<BaseOutput<UnitConversion>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") UnitConversion unitConversion) {

    // Check if the unitConversion object is null
    if (unitConversion == null) {
      // Create response indicating invalid request body
      BaseOutput<UnitConversion> response =
          BaseOutput.<UnitConversion>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED) // Set response status to FAILED
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    // Call service to create a new unitConversion
    UnitConversion createdUnitConversion = unitConversionService.create(unitConversion);

    // Construct response object with created unitConversion data
    BaseOutput<UnitConversion> response =
        BaseOutput.<UnitConversion>builder()
            .message(HttpStatus.OK.toString())
            .data(createdUnitConversion) // Attach newly created unitConversion data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/unitConversion/{id}
  // Update UnitConversion
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<UnitConversion>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") UnitConversion unitConversion) {

    // Check if the provided ID is less than or equal to zero
    if (id <= 0 || id == null) {
      // Create response indicating invalid path variable
      BaseOutput<UnitConversion> response =
          BaseOutput.<UnitConversion>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(response); // Return BAD_REQUEST response
    }

    unitConversion.setId(id); // Set the ID of the unitConversion to update
    // Call service to update the unitConversion
    UnitConversion updatedUnitConversion = unitConversionService.update(unitConversion);

    // Construct response object with updated unitConversion data
    BaseOutput<UnitConversion> response =
        BaseOutput.<UnitConversion>builder()
            .message(HttpStatus.OK.toString())
            .data(updatedUnitConversion) // Attach updated unitConversion data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build();

    // Return response with status OK
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/unitConversion/{id}
  // Delete UnitConversion
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

    // Call service to delete the unitConversion by ID
    unitConversionService.delete(id);

    // Construct response object indicating successful deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString()) // Response data
            .status(ResponseStatus.SUCCESS) // Set response status to SUCCESS
            .build());
  }
}
