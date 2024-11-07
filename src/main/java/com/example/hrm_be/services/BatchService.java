package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Batch;

import com.example.hrm_be.models.dtos.Product;
import java.time.LocalDateTime;
import java.util.List;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface BatchService {
  // Retrieve a batch by its ID.
  Batch getById(Long id);

  // Get a paginated list of batch based on provided filters.
  Page<Batch> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      Long productId,
      String keyword,
      LocalDateTime produceStartDate,
      LocalDateTime produceEndDate,
      LocalDateTime expireStartDate,
      LocalDateTime expireEndDate);

  // Create a new Batch.
  Batch create(Batch batch);

  // Update an existing Batch.
  Batch update(Batch batch);

  // Delete a Batch by its ID.
  void delete(Long id);

  Batch addBatchInInbound(Batch batch, Product product);

  List<BatchEntity> findAllByProductId(Long inboundId);
}
