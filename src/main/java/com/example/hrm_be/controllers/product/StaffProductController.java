package com.example.hrm_be.controllers.product;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.BranchProduct;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.dtos.ProductBaseDTO;
import com.example.hrm_be.models.entities.AllowedProductEntity;
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

  @GetMapping("/allow-products")
  protected ResponseEntity<BaseOutput<List<AllowedProductEntity>>> getAllowProducts(
      @RequestParam(defaultValue = "") String keyword) {
    List<AllowedProductEntity> products = productService.getAllowProducts(keyword);

    BaseOutput<List<AllowedProductEntity>> response =
        BaseOutput.<List<AllowedProductEntity>>builder()
            .message(HttpStatus.OK.toString())
            .total((long) products.size())
            .data(products)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("")
  public ResponseEntity<BaseOutput<List<ProductBaseDTO>>> searchProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
      @RequestParam(required = false) Optional<String> keyword,
      @RequestParam(required = false) Optional<Long> categoryId,
      @RequestParam(required = false) Optional<Long> typeId,
      @RequestParam(required = false) Optional<Long> manufacturerId,
      @RequestParam(required = false) Optional<String> status) {

    Page<ProductBaseDTO> products =
        productService.searchProducts(
            page,
            size,
            sortBy,
            sortDirection,
            keyword,
            manufacturerId,
            categoryId,
            typeId,
            status);

    BaseOutput<List<ProductBaseDTO>> response =
        BaseOutput.<List<ProductBaseDTO>>builder()
            .totalPages(products.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(products.getTotalElements())
            .data(products.getContent())
            .message(HttpStatus.OK.toString())
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

  @GetMapping("/products-by-supplier/{supplierId}")
  protected ResponseEntity<BaseOutput<List<Product>>> getAllProductsWithSupplier(
      @PathVariable("supplierId") Long supplierId,
      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {

    List<Product> products = productService.getAllProductsBySupplier(supplierId, keyword);

    BaseOutput<List<Product>> response =
        BaseOutput.<List<Product>>builder()
            .message(HttpStatus.OK.toString())
            .total((long) products.size())
            .data(products)
            .status(ResponseStatus.SUCCESS)
            .build();

    return ResponseEntity.ok(response);
  }
}
