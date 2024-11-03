package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchBatchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.BranchBatch;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.services.BranchBatchService;

import java.util.List;
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
  public void updateBranchBatchInInbound(BranchEntity toBranch, BatchEntity batch, Integer quantity) {
    // Check if BranchBatchEntity already exists
    BranchBatchEntity branchBatch =
            branchBatchRepository
                    .findByBranchAndBatch(toBranch, batch)
                    .orElse(new BranchBatchEntity());

    // If it exists, update the quantity, otherwise create a new one
    if (branchBatch.getId() != null) {
      branchBatch.setQuantity(
              branchBatch.getQuantity() != null
                      ? branchBatch.getQuantity() + quantity
                      : quantity); // Update existing
      // quantity
    } else {
      branchBatch.setBatch(batch);
      branchBatch.setBranch(toBranch);
      branchBatch.setQuantity(quantity);
    }

    // Save the BranchBatchEntity
    branchBatchRepository.save(branchBatch);
  }

  @Override
  public List<BranchBatchEntity> findByBatchId(Long id) {
    return branchBatchRepository.findByBatchId(id);
  }
}
