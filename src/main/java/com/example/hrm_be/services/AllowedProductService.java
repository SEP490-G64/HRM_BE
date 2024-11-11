package com.example.hrm_be.services;

import com.example.hrm_be.models.entities.AllowedProductEntity;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface AllowedProductService {
  List<AllowedProductEntity> getAllAllowedProducts();

  List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList);

  AllowedProductEntity getAllowedProductByCode(String registerCode);
}
