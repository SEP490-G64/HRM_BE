package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface BatchService {
  // Retrieve a batch by its ID.
  Batch getById(Long id);

  // Get a paginated list of batch based on provided filters.
  Page<Batch> getByPaging(int pageNo, int pageSize, String sortBy, String keyword);

  Batch findOrCreateBatchByBatchCodeAndProduct(
      String batchCode, Product product, BigDecimal inboundPrice, LocalDateTime expireDate);

  // Create a new Batch.
  Batch create(Batch batch);

  // Update an existing Batch.
  Batch update(Batch batch);

  // Delete a Batch by its ID.
  void delete(Long id);

  // Method to get a list of batches by productId through the intermediary table (BatchInbound)
  List<Batch> getBatchesByProductThroughInbound(Long productId);
}
