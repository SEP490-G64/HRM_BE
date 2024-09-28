package com.example.hrm_be.controllers.ingredient;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.ProductIngredient;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.ProductIngredientService;
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
@RequestMapping("/api/v1/staff/ingredient")
public class StaffProductIngredientController {
  private final ProductIngredientService productIngredientService;

  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<ProductIngredient>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy) {
    Page<ProductIngredient> ingredientPage =
        productIngredientService.getByPaging(page, size, sortBy);

    BaseOutput<List<ProductIngredient>> response =
        BaseOutput.<List<ProductIngredient>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(ingredientPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(ingredientPage.getTotalElements())
            .data(ingredientPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductIngredient>> getById(@PathVariable("id") Long id) {
    if (id <= 0 || id == null) {
      BaseOutput<ProductIngredient> response =
          BaseOutput.<ProductIngredient>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    ProductIngredient ingredient = productIngredientService.getById(id);

    BaseOutput<ProductIngredient> response =
        BaseOutput.<ProductIngredient>builder()
            .message(HttpStatus.OK.toString())
            .data(ingredient)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping()
  protected ResponseEntity<BaseOutput<ProductIngredient>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") ProductIngredient ingredient) {
    if (ingredient == null) {
      BaseOutput<ProductIngredient> response =
          BaseOutput.<ProductIngredient>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    ProductIngredient createdIngredient = productIngredientService.create(ingredient);
    BaseOutput<ProductIngredient> response =
        BaseOutput.<ProductIngredient>builder()
            .message(HttpStatus.OK.toString())
            .data(createdIngredient)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<ProductIngredient>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") ProductIngredient ingredient) {
    if (id <= 0 || id == null) {
      BaseOutput<ProductIngredient> response =
          BaseOutput.<ProductIngredient>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    ingredient.setId(id);
    ProductIngredient updateIngredient = productIngredientService.update(ingredient);
    BaseOutput<ProductIngredient> response =
        BaseOutput.<ProductIngredient>builder()
            .message(HttpStatus.OK.toString())
            .data(updateIngredient)
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
    productIngredientService.delete(id);
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
