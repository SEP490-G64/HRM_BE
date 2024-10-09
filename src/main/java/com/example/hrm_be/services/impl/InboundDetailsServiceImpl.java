package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.repositories.InboundDetailsRepository;
import com.example.hrm_be.services.InboundDetailsService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
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

  @Autowired private EntityManager entityManager;

  @Override
  public InboundDetails getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> inboundDetailsRepository.findById(e).map(b -> inboundDetailsMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return inboundDetailsRepository.findAll(pageable).map(dao -> inboundDetailsMapper.toDTO(dao));
  }

  @Override
  public InboundDetails create(InboundDetails inboundDetails) {
    if (inboundDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_DETAILS.EXIST);
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
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(inboundDetails.getId()).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST);
    }

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
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(id).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.INBOUND_DETAILS.NOT_EXIST);
    }

    inboundDetailsRepository.deleteById(id);
  }
}
