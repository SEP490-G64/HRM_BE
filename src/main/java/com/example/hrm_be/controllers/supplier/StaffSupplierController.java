package com.example.hrm_be.controllers.supplier;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.SupplierService;
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
public class StaffSupplierController {
  private final SupplierService supplierService;

  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Supplier>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "") String name) {
    Page<Supplier> supplierPage = supplierService.getByPaging(page, size, sortBy, name);

    BaseOutput<List<Supplier>> response =
        BaseOutput.<List<Supplier>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(supplierPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(supplierPage.getTotalElements())
            .data(supplierPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Supplier>> getById(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Supplier supplier = supplierService.getById(id);

    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(supplier)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping()
  protected ResponseEntity<BaseOutput<Supplier>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Supplier supplier) {
    if (supplier == null) {
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Supplier createdsupplier = supplierService.create(supplier);
    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(createdsupplier)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Supplier>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Supplier supplier) {
    if (id <= 0 || id == null) {
      BaseOutput<Supplier> response =
          BaseOutput.<Supplier>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    supplier.setId(id);
    Supplier updatesupplier = supplierService.update(supplier);
    BaseOutput<Supplier> response =
        BaseOutput.<Supplier>builder()
            .message(HttpStatus.OK.toString())
            .data(updatesupplier)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    supplierService.delete(id);
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
