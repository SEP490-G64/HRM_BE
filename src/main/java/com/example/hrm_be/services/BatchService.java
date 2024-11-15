package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Product;
import com.example.hrm_be.models.entities.BatchEntity;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
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

  // Method to get a list of batches by productId through the intermediary table (BatchInbound)
  List<Batch> getBatchesByProductThroughInbound(Long productId);

  List<Batch> getExpiredBatches(LocalDateTime now);

  Batch addBatchInInbound(Batch batch, Product product);

  List<BatchEntity> findAllByProductId(Long inboundId);

  List<Batch> getExpiredBatchesInDays(LocalDateTime now, Long days);
}
