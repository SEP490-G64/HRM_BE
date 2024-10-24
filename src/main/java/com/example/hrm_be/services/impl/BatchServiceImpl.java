package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.services.BatchService;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BatchServiceImpl implements BatchService {
  // Injects the repository to interact with batch data in the database
  @Autowired private BatchRepository batchRepository;

  // Injects the mapper to convert between DTO and Entity objects for batches
  @Autowired private BatchMapper batchMapper;

  // Retrieves a Batch by its ID
  @Override
  public Batch getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> batchRepository.findById(e).map(b -> batchMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of Batch entities, allowing sorting and searching by name
  @Override
  public Page<Batch> getByPaging(int pageNo, int pageSize, String sortBy, String keyword) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return batchRepository
        .findByBatchCodeContainingIgnoreCase(keyword, pageable)
        .map(dao -> batchMapper.toDTO(dao));
  }

  // Creates a new Batch
  @Override
  public Batch create(Batch batch) {
    // Validation: Ensure the Batch is not null and does not already exist at the same batch code
    if (batch == null || batchRepository.existsByBatchCode(batch.getBatchCode())) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(batch)
        .map(e -> batchMapper.toEntity(e))
        .map(e -> batchRepository.save(e))
        .map(e -> batchMapper.toDTO(e))
        .orElse(null);
  }

  // Updates an existing Batch
  @Override
  public Batch update(Batch batch) {
    // Retrieve the existing Batch entity by ID
    BatchEntity oldBatchEntity = batchRepository.findById(batch.getId()).orElse(null);
    if (oldBatchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BATCH.NOT_EXIST);
    }

    // Update the fields of the existing Batch entity with new values
    return Optional.ofNullable(oldBatchEntity)
        .map(
            op ->
                op.toBuilder()
                    .batchCode(batch.getBatchCode())
                    .expireDate(batch.getExpireDate())
                    .produceDate(batch.getProduceDate())
                    .inboundPrice(batch.getInboundPrice())
                    .build())
        .map(batchRepository::save)
        .map(batchMapper::toDTO)
        .orElse(null);
  }

  // Deletes a Batch by ID
  @Override
  public void delete(Long id) {
    // Validation: Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Delete the batch by ID
    batchRepository.deleteById(id);
  }

  @Override
  public List<Batch> getBatchesByProductThroughInbound(Long productId) {
    return batchRepository.findAllByProductIdThroughInbound(productId).stream().map(
        batchMapper::toDTO
    ).collect(Collectors.toList());
  }
}
