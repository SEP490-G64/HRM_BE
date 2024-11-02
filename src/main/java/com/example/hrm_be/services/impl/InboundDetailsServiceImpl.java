package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.services.InboundDetailsService;
import io.micrometer.common.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InboundDetailsServiceImpl implements InboundDetailsService {

  @Autowired private InboundDetailsRepository inboundDetailsRepository;

  @Autowired private InboundDetailsMapper inboundDetailsMapper;

  @Override
  public InboundDetails getById(Long id) {
    // Retrieve inbound details by ID and convert to DTO
    return Optional.ofNullable(id)
        .flatMap(
            e ->
                inboundDetailsRepository
                    .findById(e)
                    .map(b -> inboundDetailsMapper.toDTOWithInBoundDetails(b)))
        .orElse(null);
  }

  @Override
  public List<InboundDetails> getByInboundId(Long id) {
    // Retrieve inbound details by ID and convert to DTO
    return Optional.ofNullable(id)
        .map(
            e ->
                inboundDetailsRepository.findByInbound_Id(e).stream()
                    .map(b -> inboundDetailsMapper.toDTOWithInBoundDetails(b))
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  @Override
  public Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    // Create pageable request for pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    // Retrieve paginated inbound details and map to DTOs
    return inboundDetailsRepository.findAll(pageable).map(dao -> inboundDetailsMapper.toDTO(dao));
  }

  @Override
  public InboundDetails create(InboundDetails inboundDetails) {
    // Validate that inboundDetails is not null
    if (inboundDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_DETAILS.EXIST); // Throw error if null
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inboundDetails)
        .map(inboundDetailsMapper::toEntity)
        .map(e -> inboundDetailsRepository.save(e))
        .map(e -> inboundDetailsMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InboundDetails update(InboundDetails inboundDetails) {
    // Find existing inbound details by ID
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(inboundDetails.getId()).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Update fields of the existing entity
    return Optional.ofNullable(oldInboundDetailsEntity)
        .map(
            op ->
                op.toBuilder()
                    .receiveQuantity(inboundDetails.getReceiveQuantity())
                    .requestQuantity(inboundDetails.getRequestQuantity())
                    .build())
        .map(inboundDetailsRepository::save)
        .map(inboundDetailsMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    // Validate the ID
    if (StringUtils.isBlank(id.toString())) {
      return; // Exit if invalid
    }

    // Find existing inbound details by ID
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(id).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST); // Throw error if not found
    }

    // Delete the inbound details
    inboundDetailsRepository.deleteById(id);
  }
}
