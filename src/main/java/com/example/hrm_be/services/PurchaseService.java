package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface PurchaseService {
  Purchase getById(Long id);

  Page<Purchase> getByPaging(int pageNo, int pageSize, String sortBy);

  Purchase create(Purchase purchase);

  Purchase update(Purchase purchase);

  void delete(Long id);
}
