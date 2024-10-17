package com.example.hrm_be.controllers.product;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/api/v1/staff/product")
@Tag(name = "Staff-Product API")
@SecurityRequirement(name = "Authorization")
public class StaffProductController {
  private final ProductService productService;

  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Product>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
      @RequestParam(required = false, defaultValue = "name") String searchType,
      @RequestParam(defaultValue = "") String searchValue) {
    Page<Product> productPage =
        productService.getByPaging(page, size, sortBy, sortDirection, searchType, searchValue);

    BaseOutput<List<Product>> response =
        BaseOutput.<List<Product>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(productPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(productPage.getTotalElements())
            .data(productPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search")
  public ResponseEntity<BaseOutput<List<Product>>> searchProducts(
      @RequestParam Optional<String> keyword,
      @RequestParam Optional<Long> manufacturerId,
      @RequestParam Optional<Long> categoryId,
      @RequestParam Optional<Long> typeId,
      @RequestParam Optional<String> status) {

    List<Product> products = productService.searchProducts(
        keyword, manufacturerId, categoryId, typeId, status);

    BaseOutput<List<Product>> response =
        BaseOutput.<List<Product>>builder()
            .message(HttpStatus.OK.toString())
            .data(products)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("category/{id}")
  protected ResponseEntity<BaseOutput<List<Product>>> getByPagingAndCateId(
      @PathVariable("id") Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy) {
    Page<Product> productPage = productService.getByPagingAndCateId(page, size, sortBy, id);

    BaseOutput<List<Product>> response =
        BaseOutput.<List<Product>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(productPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(productPage.getTotalElements())
            .data(productPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("type/{id}")
  protected ResponseEntity<BaseOutput<List<Product>>> getByPagingAndTypeId(
      @PathVariable("id") Long id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy) {
    Page<Product> productPage = productService.getByPagingAndTypeId(page, size, sortBy, id);

    BaseOutput<List<Product>> response =
        BaseOutput.<List<Product>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(productPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(productPage.getTotalElements())
            .data(productPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<Product>> getById(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<Product> response =
          BaseOutput.<Product>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Product product = productService.getById(id);

    BaseOutput<Product> response =
        BaseOutput.<Product>builder()
            .message(HttpStatus.OK.toString())
            .data(product)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping()
  protected ResponseEntity<BaseOutput<Product>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Product product) {
    if (product == null) {
      BaseOutput<Product> response =
          BaseOutput.<Product>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Product createdproduct = productService.create(product);
    BaseOutput<Product> response =
        BaseOutput.<Product>builder()
            .message(HttpStatus.OK.toString())
            .data(createdproduct)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Product>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Product product) {
    if (id <= 0 || id == null) {
      BaseOutput<Product> response =
          BaseOutput.<Product>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    product.setId(id);
    Product updateproduct = productService.update(product);
    BaseOutput<Product> response =
        BaseOutput.<Product>builder()
            .message(HttpStatus.OK.toString())
            .data(updateproduct)
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
    productService.delete(id);
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
