package com.example.hrm_be.controllers.batch;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.BatchDto;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BatchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/batch")
@Tag(name = "Staff-Batches API")
@SecurityRequirement(name = "Authorization")
public class StaffBatchController {
  // Injecting BatchService to handle business logic for ProductCategory
  private final BatchService batchService;

  // Handles GET requests for paginated list of Batch entities
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<BatchDto>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = true) Long productId,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false) LocalDateTime produceStartDate,
      @RequestParam(required = false) LocalDateTime produceEndDate,
      @RequestParam(required = false) LocalDateTime expireStartDate,
      @RequestParam(required = false) LocalDateTime expireEndDate) {
    Page<BatchDto> batchPage =
        batchService.getByPaging(
            page,
            size,
            sortBy,
            productId,
            keyword,
            produceStartDate,
            produceEndDate,
            expireStartDate,
            expireEndDate);

    // Building response object with the retrieved data and pagination details
    BaseOutput<List<BatchDto>> response =
        BaseOutput.<List<BatchDto>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(batchPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(batchPage.getTotalElements())
            .data(batchPage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles GET requests to retrieve a single Batch by ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Batch>> getById(@PathVariable("id") Long id) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<Batch> response =
          BaseOutput.<Batch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Retrieve Batch by ID
    Batch batch = batchService.getById(id);

    // Building success response
    BaseOutput<Batch> response =
        BaseOutput.<Batch>builder()
            .message(HttpStatus.OK.toString())
            .data(batch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles GET requests to retrieve a single Batch by ID
  @GetMapping("/expired-batch-days")
  protected ResponseEntity<BaseOutput<List<Batch>>> getByExpiredBatchInDays(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") String sortDirection,
      @RequestParam(defaultValue = "0") Long days) {

    LocalDateTime now = LocalDateTime.now();
    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

    // Retrieve Batch by ID
    Page<Batch> list = batchService.getExpiredBatchesInDays(now, days, pageable);

    // Building success response
    BaseOutput<List<Batch>> response =
        BaseOutput.<List<Batch>>builder()
            .totalPages(list.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(list.getTotalElements())
            .data(list.getContent())
            .message(HttpStatus.OK.toString())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles GET requests to retrieve a single Batch by ID
  @GetMapping("/expired-batch")
  protected ResponseEntity<BaseOutput<List<Batch>>> getByExpiredBatch(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") String sortDirection) {

    LocalDateTime now = LocalDateTime.now();
    Pageable pageable =
        PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    // Retrieve Batch by ID
    Page<Batch> batches = batchService.getExpiredBatches(now, pageable);
    BaseOutput<List<Batch>> response =
        BaseOutput.<List<Batch>>builder()
            .totalPages(batches.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(batches.getTotalElements())
            .data(batches.getContent())
            .message(HttpStatus.OK.toString())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles POST requests to create a new Batch
  @PostMapping()
  protected ResponseEntity<BaseOutput<Batch>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Batch batch) {
    // Validation: If the request body is null, return a bad request response
    if (batch == null) {
      BaseOutput<Batch> response =
          BaseOutput.<Batch>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the new Batch via service
    Batch createdBatch = batchService.create(batch);

    // Building success response
    BaseOutput<Batch> response =
        BaseOutput.<Batch>builder()
            .message(HttpStatus.OK.toString())
            .data(createdBatch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles PUT requests to update an existing Batch by ID
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Batch>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Batch batch) {
    // Validation: If the provided ID is invalid, return a bad request response
    if (id <= 0 || id == null) {
      BaseOutput<Batch> response =
          BaseOutput.<Batch>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID to the existing Batch before updating
    batch.setId(id);
    Batch updateBatch = batchService.update(batch);

    // Building success response
    BaseOutput<Batch> response =
        BaseOutput.<Batch>builder()
            .message(HttpStatus.OK.toString())
            .data(updateBatch)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Handles DELETE requests to remove a Batch by ID
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

    // Delete the Batch via service
    batchService.delete(id);

    // Building success response after deletion
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
