package com.example.hrm_be.controllers.allowedProduct;

import com.example.hrm_be.models.entities.AllowedProductEntity;
import com.example.hrm_be.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/allowed-product")
@Tag(name = "Admin-allowed API")
@SecurityRequirement(name = "Authorization")
public class AdminAllowedProductController {
  @Autowired
  private ProductService productService;

  @PostMapping("/add")
  public ResponseEntity<List<AllowedProductEntity>> addProduct(@RequestBody List<Map<String, Object>> productJson) {
    List<AllowedProductEntity> product = productService.addProductFromJson(productJson);
    return ResponseEntity.ok(product);
  }
}
