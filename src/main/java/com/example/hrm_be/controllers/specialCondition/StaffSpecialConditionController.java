package com.example.hrm_be.controllers.specialCondition;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.SpecialCondition;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.SpecialConditionService;
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
@RequestMapping("/api/v1/staff/special-condition")
@Tag(name = "Staff-Special-Conditions API")
@SecurityRequirement(name = "Authorization")
public class StaffSpecialConditionController {
  private final SpecialConditionService specialConditionService;

  // GET: /api/v1/staff/special-condition
  // Retrieves a paginated list of SpecialCondition entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<SpecialCondition>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<SpecialCondition> SpecialConditionPage =
        specialConditionService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<SpecialCondition>> response =
        BaseOutput.<List<SpecialCondition>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(SpecialConditionPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(SpecialConditionPage.getTotalElements())
            .data(SpecialConditionPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/special-condition/{id}
  // Retrieves a SpecialCondition by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<SpecialCondition>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<SpecialCondition> response =
          BaseOutput.<SpecialCondition>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch SpecialCondition by ID
    SpecialCondition specialCondition = specialConditionService.getById(id);
    if (specialCondition == null) {
      BaseOutput<SpecialCondition> response =
          BaseOutput.<SpecialCondition>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.RESPONSE.NOT_FOUND))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Build the response with the found SpecialCondition data
    BaseOutput<SpecialCondition> response =
        BaseOutput.<SpecialCondition>builder()
            .message(HttpStatus.OK.toString())
            .data(specialCondition)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/special-condition
  // Creates a new SpecialCondition
  @PostMapping()
  protected ResponseEntity<BaseOutput<SpecialCondition>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          SpecialCondition specialCondition) {
    // Validate the request body
    if (specialCondition == null) {
      BaseOutput<SpecialCondition> response =
          BaseOutput.<SpecialCondition>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the SpecialCondition
    SpecialCondition createdSpecialCondition = specialConditionService.create(specialCondition);

    // Build the response with the created SpecialCondition data
    BaseOutput<SpecialCondition> response =
        BaseOutput.<SpecialCondition>builder()
            .message(HttpStatus.OK.toString())
            .data(createdSpecialCondition)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/special-condition/{id}
  // Updates an existing SpecialCondition
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<SpecialCondition>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          SpecialCondition SpecialCondition) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<SpecialCondition> response =
          BaseOutput.<SpecialCondition>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the SpecialCondition to update
    SpecialCondition.setId(id);

    // Update the SpecialCondition
    SpecialCondition updateSpecialCondition = specialConditionService.update(SpecialCondition);

    // Build the response with the updated SpecialCondition data
    BaseOutput<SpecialCondition> response =
        BaseOutput.<SpecialCondition>builder()
            .message(HttpStatus.OK.toString())
            .data(updateSpecialCondition)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/special-condition/{id}
  // Deletes a SpecialCondition by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the SpecialCondition by ID
    try {
      specialConditionService.delete(id);
    } catch (Exception e) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(e.getMessage()))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
