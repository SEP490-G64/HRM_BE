package com.example.hrm_be.services;

import com.example.hrm_be.models.entities.AllowedProductEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface AllowedProductService {
  List<AllowedProductEntity> getAllAllowedProducts();

  List<AllowedProductEntity> addProductFromJson(List<Map<String, Object>> productJsonList);

  AllowedProductEntity getAllowedProductByCode(String registerCode);
}
