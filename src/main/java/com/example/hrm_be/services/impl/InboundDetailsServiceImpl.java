package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InboundDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsCreateRequest;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsUpdateRequest;
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
  public InboundDetails create(InboundDetailsCreateRequest inboundDetails) {
    if (inboundDetails == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    InboundEntity inbound;
    if (inboundDetails.getInboundId() != null) {
      inbound = entityManager.getReference(InboundEntity.class, inboundDetails.getInboundId());
      if (inbound == null) {
        throw new HrmCommonException("Inbound not found with id: " + inboundDetails.getInboundId());
      }
    } else {
      inbound = null;
    }

    ProductEntity product;
    if (inboundDetails.getProductId() != null) {
      product = entityManager.getReference(ProductEntity.class, inboundDetails.getProductId());
      if (product == null) {
        throw new HrmCommonException(
            "Product Check not found with id: " + inboundDetails.getProductId());
      }
    } else {
      product = null;
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(inboundDetails)
        .map(e -> inboundDetailsMapper.toEntity(e, inbound, product))
        .map(e -> inboundDetailsRepository.save(e))
        .map(e -> inboundDetailsMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public InboundDetails update(InboundDetailsUpdateRequest inboundDetails) {
    InboundDetailsEntity oldInboundDetailsEntity =
        inboundDetailsRepository.findById(inboundDetails.getId()).orElse(null);
    if (oldInboundDetailsEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
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

    inboundDetailsRepository.deleteById(id);
  }
}
