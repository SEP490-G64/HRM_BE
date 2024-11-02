package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.BranchBatchMapper;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.services.BranchBatchService;
import java.math.BigDecimal;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BranchBatchServiceImpl implements BranchBatchService {
  @Autowired private BranchBatchRepository branchBatchRepository;

  @Autowired private BranchBatchMapper branchBatchMapper;
  @Autowired private BatchMapper batchMapper;
  @Autowired private BranchMapper branchMapper;

  @Override
  public BranchBatch create(BranchBatch branchBatch) {
    if (branchBatch == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCHBATCH.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(branchBatch)
        .map(e -> branchBatchMapper.toEntity(e))
        .map(e -> branchBatchRepository.save(e))
        .map(e -> branchBatchMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public BranchBatch update(BranchBatch branchBatch) {
    // Retrieve the existing branch entity by ID
    BranchBatchEntity oldBranchBatch =
        branchBatchRepository.findById(branchBatch.getId()).orElse(null);
    if (oldBranchBatch == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    // Update the fields of the existing branch entity with new values
    return Optional.ofNullable(oldBranchBatch)
        .map(op -> op.toBuilder().quantity(branchBatch.getQuantity()).build())
        .map(branchBatchRepository::save)
        .map(branchBatchMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null || StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    BranchBatchEntity oldBranchBatch = branchBatchRepository.findById(id).orElse(null);
    if (oldBranchBatch == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCHBATCH.NOT_EXIST); // Error if inbound entity is not found
    }

    branchBatchRepository.deleteById(id); // Delete the inbound entity by ID
  }

  @Override
  public BranchBatch updateQuantityOrCreateBranchBatch(Branch branch, Batch batch, Integer quantity) {
    // Retrieve or create a new BranchBatch entity
    BranchBatchEntity branchBatch = branchBatchRepository
        .findByBranch_IdAndBatch_Id(branch.getId(), batch.getId())
        .orElse(new BranchBatchEntity());

    // If it exists, update the quantity
    if (branchBatch.getId() != null) {
      branchBatch.setQuantity(
          branchBatch.getQuantity() != null
              ? branchBatch.getQuantity().add(BigDecimal.valueOf(quantity))
              : BigDecimal.valueOf(quantity)
      );
    } else {
      // Otherwise, set the details for a new entity
      branchBatch.setBatch(batchMapper.toEntity(batch));
      branchBatch.setBranch(branchMapper.toEntity(branch));
      branchBatch.setQuantity(BigDecimal.valueOf(quantity));
    }

    // Save and return the updated or new entity
    return branchBatchMapper.toDTO(branchBatchRepository.save(branchBatch));
  }
}
