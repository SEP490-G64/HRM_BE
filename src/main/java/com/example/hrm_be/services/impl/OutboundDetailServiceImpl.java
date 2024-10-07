package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.OutboundDetailMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.OutboundDetail;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.OutboundDetailEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.example.hrm_be.models.requests.outboundDetails.OutboundDetailsCreateRequest;
import com.example.hrm_be.models.requests.outboundDetails.OutboundDetailsUpdateRequest;
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
    @Autowired
    private OutboundDetailRepository outboundDetailRepository;

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
        return outboundDetailRepository
                .findAll(pageable)
                .map(dao -> outboundDetailMapper.toDTO(dao));
    }

    @Override
    public OutboundDetail create(OutboundDetailsCreateRequest outboundDetail) {
        if (outboundDetail == null) {
            throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
        }

        OutboundEntity outbound;
        if (outboundDetail.getOutboundId() != null) {
            outbound = entityManager.getReference(OutboundEntity.class, outboundDetail.getOutboundId());
            if (outbound == null) {
                throw new HrmCommonException("Outbound not found with id: " + outboundDetail.getOutboundId());
            }
        } else {
            outbound = null;
        }

        BatchEntity batch;
        if (outboundDetail.getBatchId() != null) {
            batch = entityManager.getReference(BatchEntity.class, outboundDetail.getBatchId());
            if (batch == null) {
                throw new HrmCommonException("Batch not found with id: " + outboundDetail.getBatchId());
            }
        } else {
            batch = null;
        }

        // Convert DTO to entity, save it, and convert back to DTO
        return Optional.ofNullable(outboundDetail)
                .map(e -> outboundDetailMapper.toEntity(e, outbound, batch))
                .map(e -> outboundDetailRepository.save(e))
                .map(e -> outboundDetailMapper.toDTO(e))
                .orElse(null);
    }

    @Override
    public OutboundDetail update(OutboundDetailsUpdateRequest outboundDetail) {
        OutboundDetailEntity oldoutboundDetailEntity = outboundDetailRepository.findById(outboundDetail.getId()).orElse(null);
        if (oldoutboundDetailEntity == null) {
            throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
        }

        return Optional.ofNullable(oldoutboundDetailEntity)
                .map(
                        op ->
                                op.toBuilder()
                                        .quantity(outboundDetail.getQuantity())
                                        .build())
                .map(outboundDetailRepository::save)
                .map(outboundDetailMapper::toDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        if (StringUtils.isBlank(id.toString())) {
            return;
        }

        outboundDetailRepository.deleteById(id);
    }
}
