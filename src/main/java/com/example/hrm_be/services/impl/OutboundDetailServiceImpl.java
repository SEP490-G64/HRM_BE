package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.OutboundDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.repositories.OutboundDetailRepository;
import com.example.hrm_be.services.OutboundDetailService;
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
public class OutboundDetailServiceImpl implements OutboundDetailService {
  @Autowired private OutboundDetailRepository outboundDetailRepository;

  @Autowired private OutboundDetailMapper outboundDetailMapper;

  @Autowired private EntityManager entityManager;

  @Override
  public OutboundDetail getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> outboundDetailRepository.findById(e).map(b -> outboundDetailMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<OutboundDetail> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return outboundDetailRepository.findAll(pageable).map(dao -> outboundDetailMapper.toDTO(dao));
  }

  @Override
  public OutboundDetail create(OutboundDetail outboundDetail) {
    if (outboundDetail == null) {
      throw new HrmCommonException(HrmConstant.ERROR.OUTBOUND_DETAILS.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(outboundDetail)
        .map(outboundDetailMapper::toEntity)
        .map(e -> outboundDetailRepository.save(e))
        .map(e -> outboundDetailMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public OutboundDetail update(OutboundDetail outboundDetail) {
    OutboundDetailEntity oldoutboundDetailEntity =
        outboundDetailRepository.findById(outboundDetail.getId()).orElse(null);
    if (oldoutboundDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.OUTBOUND_DETAILS.NOT_EXIST);
    }

    return Optional.ofNullable(oldoutboundDetailEntity)
        .map(op -> op.toBuilder().quantity(outboundDetail.getQuantity()).build())
        .map(outboundDetailRepository::save)
        .map(outboundDetailMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    OutboundDetailEntity oldoutboundDetailEntity =
        outboundDetailRepository.findById(id).orElse(null);
    if (oldoutboundDetailEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.OUTBOUND_DETAILS.NOT_EXIST);
    }

    outboundDetailRepository.deleteById(id);
  }
}
