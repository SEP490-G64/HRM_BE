package com.example.hrm_be.controllers.allowedProduct;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.AllowedProductService;
import com.example.hrm_be.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/allowed-product")
@Tag(name = "Admin-allowed API")
@SecurityRequirement(name = "Authorization")
public class AdminAllowedProductController {
  @Autowired private AllowedProductService allowedProductService;

  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<AllowedProductEntity>>> getByPaging(){

    // Build the response with pagination details
    BaseOutput<List<AllowedProductEntity>> response =
            BaseOutput.<List<AllowedProductEntity>>builder()
                    .message(HttpStatus.OK.toString())
                    .data(allowedProductService.getAllAllowedProducts())
                    .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                    .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("")
  public ResponseEntity<List<AllowedProductEntity>> addProduct(
      @RequestBody List<Map<String, Object>> productJson) {
    List<AllowedProductEntity> product = allowedProductService.addProductFromJson(productJson);
    return ResponseEntity.ok(product);
  }


}
