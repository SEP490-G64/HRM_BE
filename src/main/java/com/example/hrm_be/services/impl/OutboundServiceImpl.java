package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.components.OutboundMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.repositories.OutboundRepository;
import com.example.hrm_be.services.OutboundService;
import com.example.hrm_be.services.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OutboundServiceImpl implements OutboundService {
  @Autowired private OutboundRepository outboundRepository;

  @Autowired private OutboundMapper outboundMapper;

  @Autowired private EntityManager entityManager;

  @Autowired private UserService userService;
  @Autowired private UserMapper userMapper;

  @Override
  public Outbound getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> outboundRepository.findById(e).map(b -> outboundMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return outboundRepository.findAll(pageable).map(dao -> outboundMapper.toDTO(dao));
  }

  @Override
  public Outbound create(Outbound outbound) {
    if (outbound == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    // Convert DTO to entity, save it, and convert back to DTO
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

  @Override
  public Outbound update(Outbound outbound) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(outbound.getId()).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
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

  @Override
  public Outbound approve(Long id) {
    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldoutboundEntity)
        .map(op -> op.toBuilder().isApproved(true).approvedBy(userEntity).build())
        .map(outboundRepository::save)
        .map(outboundMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    OutboundEntity oldoutboundEntity = outboundRepository.findById(id).orElse(null);
    if (oldoutboundEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    outboundRepository.deleteById(id);
  }
}
