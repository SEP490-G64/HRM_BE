package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.OutboundDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundProductDetailEntity;
import com.example.hrm_be.repositories.OutboundDetailRepository;
import com.example.hrm_be.services.OutboundDetailService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OutboundDetailServiceImpl implements OutboundDetailService {

  @Autowired private OutboundDetailRepository outboundDetailRepository;

  @Autowired private OutboundDetailMapper outboundDetailMapper;

  @Override
  public OutboundDetail getById(Long id) {
    // Fetch outbound detail by ID, map the entity to DTO, and return it
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                outboundDetailRepository
                    .findById(e)
                    .map(b -> outboundDetailMapper.toDTOWithOutBoundDetails(b)))
        .orElse(null);
  }

  @Override
  public Page<OutboundDetail> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Pagination and sorting for outbound details
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return outboundDetailRepository.findAll(pageable).map(dao -> outboundDetailMapper.toDTO(dao));
  }

  // Method to create a new outbound detail
  @Override
  public OutboundDetail create(OutboundDetail outboundDetail) {
    if (outboundDetail == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.OUTBOUND_DETAILS.EXIST); // Error handling for null outbound detail
    }

    // Convert DTO to entity, save it in the repository, and convert it back to DTO
    return Optional.ofNullable(outboundDetail)
        .map(outboundDetailMapper::toEntity)
        .map(e -> outboundDetailRepository.save(e))
        .map(e -> outboundDetailMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing outbound detail
  @Override
  public OutboundDetail update(OutboundDetail outboundDetail) {
    // Fetch the existing outbound detail entity by ID
    OutboundDetailEntity oldOutboundDetailEntity =
        outboundDetailRepository.findById(outboundDetail.getId()).orElse(null);
    if (oldOutboundDetailEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.OUTBOUND_DETAILS.NOT_EXIST); // Error if the entity is not found
    }

    // Update the existing entity's properties using the builder pattern
    return Optional.ofNullable(oldOutboundDetailEntity)
        .map(op -> op.toBuilder().quantity(outboundDetail.getQuantity()).build())
        .map(outboundDetailRepository::save)
        .map(outboundDetailMapper::toDTO)
        .orElse(null);
  }

  // Method to delete an outbound detail by ID
  @Override
  public void delete(Long id) {
    // Validate the ID
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Fetch the entity by ID
    OutboundDetailEntity oldOutboundDetailEntity =
        outboundDetailRepository.findById(id).orElse(null);
    if (oldOutboundDetailEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.OUTBOUND_DETAILS.NOT_EXIST); // Error if entity is not found
    }

    // Delete the entity from the repository
    outboundDetailRepository.deleteById(id);
  }

  @Override
  public void deleteByOutboundId(Long outboundId) {
    outboundDetailRepository.deleteByOutboundId(outboundId);
  }

  @Override
  public OutboundDetailEntity findByOutboundAndBatch(Long outboundId, Long batchId) {
    return outboundDetailRepository.findByOutboundIdAndBatchId(outboundId, batchId).orElse(null);
  }

  @Override
  public List<OutboundDetailEntity> saveAll(List<OutboundDetailEntity> outboundDetailEntities) {
    return outboundDetailRepository.saveAll(outboundDetailEntities);
  }

  @Override
  public List<OutboundDetailEntity> findByOutbound(Long outboundId) {
    return outboundDetailRepository.findAllWithBatchAndProductAndCategoryByOutboundId(outboundId);
  }
}
