package com.example.hrm_be.controllers.purchase;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Purchase;
import com.example.hrm_be.models.requests.purchase.PurchaseCreateRequest;
import com.example.hrm_be.models.requests.purchase.PurchaseUpdateRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.PurchaseService;
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
@RequestMapping("/api/v1/staff/purchase")
@Tag(name = "Staff-Purchases API")
@SecurityRequirement(name = "Authorization")
public class StaffPurchaseController {
  private final PurchaseService purchaseService;

  // Retrieves a paginated list of Purchase entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Purchase>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<Purchase> PurchasePage = purchaseService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<Purchase>> response =
        BaseOutput.<List<Purchase>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(PurchasePage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(PurchasePage.getTotalElements())
            .data(PurchasePage.getContent())
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Retrieves a Purchase by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Purchase>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Purchase> response =
          BaseOutput.<Purchase>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch Purchase by ID
    Purchase Purchase = purchaseService.getById(id);

    // Build the response with the found Purchase data
    BaseOutput<Purchase> response =
        BaseOutput.<Purchase>builder()
            .message(HttpStatus.OK.toString())
            .data(Purchase)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Creates a new Purchase
  @PostMapping()
  protected ResponseEntity<BaseOutput<Purchase>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          PurchaseCreateRequest Purchase) {
    // Validate the request body
    if (Purchase == null) {
      BaseOutput<Purchase> response =
          BaseOutput.<Purchase>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the Purchase
    Purchase createdPurchase = purchaseService.create(Purchase);

    // Build the response with the created Purchase data
    BaseOutput<Purchase> response =
        BaseOutput.<Purchase>builder()
            .message(HttpStatus.OK.toString())
            .data(createdPurchase)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Purchase
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Purchase>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid")
          PurchaseUpdateRequest Purchase) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Purchase> response =
          BaseOutput.<Purchase>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the Purchase to update
    Purchase.setId(id);

    // Update the Purchase
    Purchase updatePurchase = purchaseService.update(Purchase);

    // Build the response with the updated Purchase data
    BaseOutput<Purchase> response =
        BaseOutput.<Purchase>builder()
            .message(HttpStatus.OK.toString())
            .data(updatePurchase)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a Purchase by ID
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

    // Delete the Purchase by ID
    purchaseService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
