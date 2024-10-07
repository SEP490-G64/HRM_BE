package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.InventoryCheckDetailsMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.InventoryCheckDetails;
import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.InventoryCheckDetailsEntity;
import com.example.hrm_be.models.entities.InventoryCheckEntity;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsCreateRequest;
import com.example.hrm_be.models.requests.inventoryCheckDetails.InventoryCheckDetailsUpdateRequest;
import com.example.hrm_be.repositories.InventoryCheckDetailsRepository;
import com.example.hrm_be.services.InventoryCheckDetailsService;
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
public class InventoryCheckDetailsServiceImpl implements InventoryCheckDetailsService {
    @Autowired
    private InventoryCheckDetailsRepository inventoryCheckDetailsRepository;

    @Autowired private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;

    @Autowired private EntityManager entityManager;

    @Override
    public InventoryCheckDetails getById(Long id) {
        return Optional.ofNullable(id)
                .flatMap(e -> inventoryCheckDetailsRepository.findById(e).map(b -> inventoryCheckDetailsMapper.toDTO(b)))
                .orElse(null);
    }

    @Override
    public Page<InventoryCheckDetails> getByPaging(int pageNo, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        return inventoryCheckDetailsRepository
                .findAll(pageable)
                .map(dao -> inventoryCheckDetailsMapper.toDTO(dao));
    }

    @Override
    public InventoryCheckDetails create(InventoryCheckDetailsCreateRequest InventoryCheckDetails) {
        if (InventoryCheckDetails == null) {
            throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
        }

        BatchEntity batch;
        if (InventoryCheckDetails.getBatchId() != null) {
            batch = entityManager.getReference(BatchEntity.class, InventoryCheckDetails.getBatchId());
            if (batch == null) {
                throw new HrmCommonException("Batch not found with id: " + InventoryCheckDetails.getBatchId());
            }
        } else {
            batch = null;
        }

        InventoryCheckEntity inventoryCheck;
        if (InventoryCheckDetails.getInventoryCheckId() != null) {
            inventoryCheck = entityManager.getReference(InventoryCheckEntity.class, InventoryCheckDetails.getInventoryCheckId());
            if (inventoryCheck == null) {
                throw new HrmCommonException("Inventory Check not found with id: " + InventoryCheckDetails.getInventoryCheckId());
            }
        } else {
            inventoryCheck = null;
        }

        // Convert DTO to entity, save it, and convert back to DTO
        return Optional.ofNullable(InventoryCheckDetails)
                .map(e -> inventoryCheckDetailsMapper.toEntity(e, inventoryCheck, batch))
                .map(e -> inventoryCheckDetailsRepository.save(e))
                .map(e -> inventoryCheckDetailsMapper.toDTO(e))
                .orElse(null);
    }

    @Override
    public InventoryCheckDetails update(InventoryCheckDetailsUpdateRequest InventoryCheckDetails) {
        InventoryCheckDetailsEntity oldInventoryCheckDetailsEntity = inventoryCheckDetailsRepository.findById(InventoryCheckDetails.getId()).orElse(null);
        if (oldInventoryCheckDetailsEntity == null) {
            throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
        }

        return Optional.ofNullable(oldInventoryCheckDetailsEntity)
                .map(
                        op ->
                                op.toBuilder()
                                        .systemQuantity(InventoryCheckDetails.getSystemQuantity())
                                        .countedQuantity(InventoryCheckDetails.getCountedQuantity())
                                        .difference(InventoryCheckDetails.getDifference())
                                        .reason(InventoryCheckDetails.getReason())
                                        .build())
                .map(inventoryCheckDetailsRepository::save)
                .map(inventoryCheckDetailsMapper::toDTO)
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        if (StringUtils.isBlank(id.toString())) {
            return;
        }

        inventoryCheckDetailsRepository.deleteById(id);
    }
}
