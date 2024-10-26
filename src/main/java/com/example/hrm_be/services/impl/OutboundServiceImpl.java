package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BATCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.BRANCH;
import com.example.hrm_be.commons.constants.HrmConstant.ERROR.INBOUND;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.components.BatchMapper;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.OutboundMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Batch;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.dtos.ProductOutbound;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchBatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import com.example.hrm_be.repositories.BatchRepository;
import com.example.hrm_be.repositories.BranchBatchRepository;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.repositories.OutboundDetailRepository;
import com.example.hrm_be.repositories.OutboundRepository;
import com.example.hrm_be.services.OutboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OutboundServiceImpl implements OutboundService {

  @Autowired private OutboundRepository outboundRepository;
  @Autowired private BranchBatchRepository branchBatchRepository;
  @Autowired private BranchRepository branchRepository;
  @Autowired private OutboundDetailRepository outboundDetailRepository;
  @Autowired private BatchRepository batchRepository;
  @Autowired private OutboundMapper outboundMapper;
  @Autowired private BranchMapper branchMapper;
  @Autowired private BatchMapper batchMapper;
  @Autowired private WplUtil wplUtil;
  @Autowired private UserService userService;
  @Autowired private UserMapper userMapper;

  @Override
  public Outbound getById(Long id) {
    OutboundEntity outboundEntity =
        outboundRepository
            .findById(id)
            .orElseThrow(() -> new HrmCommonException("Outbound with ID " + id + " not found"));

    // Convert the entity to the DTO
    Outbound outbound = outboundMapper.toDTO(outboundEntity);

    // Iterate through the outbound details to calculate the remainQuantity
    for (OutboundDetail detail : outbound.getOutboundDetails()) {
      Batch batch = detail.getBatch();
      Branch fromBranch = outbound.getFromBranch();

      // Find the corresponding BranchBatchEntity
      BranchBatchEntity branchBatch =
          branchBatchRepository
              .findByBranchAndBatch(branchMapper.toEntity(fromBranch), batchMapper.toEntity(batch))
              .orElseThrow(
                  () ->
                      new HrmCommonException(
                          "BranchBatch not found for the given branch and batch"));

      // Calculate remainQuantity
      detail.setRemainQuantity(branchBatch.getQuantity() - detail.getQuantity() );
    }

    return outbound;
  }

  @Override
  public Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return outboundRepository.findAll(pageable).map(dao -> outboundMapper.toDTO(dao));
  }

  // Method to create a new Outbound record
  @Override
  public Outbound create(Outbound outbound) {
    if (outbound == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.EXIST); // Error handling for null outbound object
    }

    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity =
        userMapper.toEntity(userService.findLoggedInfoByEmail(email)); // Get user entity

    return Optional.ofNullable(outbound)
        .map(outboundMapper::toEntity)
        .map(
            e -> {
              e.setCreatedBy(userEntity);
              e.setCreatedDate(LocalDateTime.now());
              e.setStatus(OutboundStatus.CHO_DUYET);
              e.setIsApproved(false);
              return outboundRepository.save(e);
            })
        .map(e -> outboundMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing Outbound record
  @Override
  public Outbound update(Outbound outbound) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(outbound.getId()).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    return Optional.ofNullable(oldoutboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(outbound.getNote())
                    .outboundType(outbound.getOutboundType())
                    .status(outbound.getStatus())
                    .taxable(outbound.getTaxable())
                    .totalPrice(outbound.getTotalPrice())
                    .build())
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  // Method to approve an outbound record
  @Override
  public Outbound approve(Long id, boolean accept) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldoutboundEntity)
        .map(op -> op.toBuilder().isApproved(accept).approvedBy(userEntity).build())
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  @Override
  public Outbound saveOutbound(CreateOutboundRequest request) {
    Optional<OutboundEntity> unsavedOutbound = outboundRepository.findById(request.getOutboundId());
    BranchEntity fromBranch =
        branchRepository.findById(request.getFromBranch().getId()).orElse(null);
    if (fromBranch == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }
    OutboundEntity outboundEntity = unsavedOutbound.get();

    Outbound updatedOutbound =
        Outbound.builder()
            .id(
                outboundEntity
                    .getId()) // Retain the existing ID (and other immutable properties, if any)
            .outBoundCode(request.getOutboundCode())
            .createdDate(request.getCreatedDate() != null ? request.getCreatedDate() : null)
            .status(OutboundStatus.BAN_NHAP)
            .outboundType(request.getOutboundType())
            .createdBy(request.getCreatedBy())
            .toBranch(branchMapper.convertToDTOBasicInfo(outboundEntity.getToBranch()))
            .supplier(request.getSupplier() != null ? request.getSupplier() : null)
            .fromBranch(request.getFromBranch() != null ? request.getFromBranch() : null)
            .note(request.getNote())
            .build();

    // Save the updated entity back to the repository
    OutboundEntity updatedOutboundEntity =
        outboundRepository.save(outboundMapper.toEntity(updatedOutbound));

    List<OutboundDetailEntity> outboundDetailEntities = new ArrayList<>();
    for (ProductOutbound productOutbound : request.getProductOutbounds()) {
      BatchEntity batchEntity =
          batchRepository.findById(productOutbound.getBatch().getId()).orElse(null);
      if (batchEntity == null) {
        throw new HrmCommonException(BATCH.NOT_EXIST);
      }
      if (branchBatchRepository.findQuantityByBatchIdAndBranchId(
              batchEntity.getId(), fromBranch.getId())
          < productOutbound.getOutboundQuantity()) {
        throw new HrmCommonException(
            "Số lượng nhiều hơn trong kho, không thể xuất, " + batchEntity.getBatchCode());
      }

      // Check if OutboundDetailEntity already exists for this outbound and batch
      Optional<OutboundDetailEntity> existingDetail =
          outboundDetailRepository.findByOutboundAndBatch(updatedOutboundEntity, batchEntity);

      OutboundDetailEntity outboundDetail;
      if (existingDetail.isPresent()) {
        // Update the existing detail with the new quantity
        outboundDetail = existingDetail.get();
        outboundDetail.setQuantity(productOutbound.getOutboundQuantity());
      } else {
        // Create a new outbound detail if it doesn't exist
        outboundDetail =
            OutboundDetailEntity.builder()
                .outbound(updatedOutboundEntity)
                .quantity(productOutbound.getOutboundQuantity())
                .batch(batchMapper.toEntity(productOutbound.getBatch()))
                .build();
      }
      outboundDetailEntities.add(outboundDetail);
    }
    // Save all updated or new outbound details back to the repository
    outboundDetailRepository.saveAll(outboundDetailEntities);
    return null;
  }

  @Override
  public Outbound submitOutboundToSystem(Long outboundId) {
    return null;
  }

  @Override
  public Outbound createInnitOutbound(OutboundType type) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    String outboundCode = WplUtil.generateNoteCode(currentDateTime, "OP");
    if (outboundRepository.existsByOutBoundCode(outboundCode)) {
      throw new HrmCommonException(INBOUND.EXIST);
    }
    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));
    BranchEntity branchEntity = userEntity.getBranch();
    if (branchEntity == null) {
      throw new HrmCommonException(BRANCH.NOT_EXIST);
    }
    OutboundEntity outbound =
        OutboundEntity.builder()
            .createdDate(currentDateTime)
            .outboundType(type)
            .status(OutboundStatus.CHUA_LUU)
            .outBoundCode(outboundCode)
            .createdBy(userEntity)
            .toBranch(branchEntity)
            .build();
    return Optional.ofNullable(outbound)
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  // Method to delete an outbound record
  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.BRANCH.NOT_EXIST); // Error if outbound entity is not found
    }

    outboundRepository.deleteById(id); // Delete the outbound entity by ID
  }
}
