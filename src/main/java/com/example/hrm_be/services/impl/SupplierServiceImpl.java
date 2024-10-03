package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.SupplierMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Supplier;
import com.example.hrm_be.models.entities.SupplierEntity;
import com.example.hrm_be.repositories.SupplierRepository;
import com.example.hrm_be.services.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {
  @Autowired private SupplierRepository supplierRepository;

  @Autowired private SupplierMapper supplierMapper;

  @Override
  public Supplier getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> supplierRepository.findById(e).map(b -> supplierMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Supplier> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Tìm kiếm theo tên
    return supplierRepository
        .findBySupplierNameContainsIgnoreCase(name, pageable)
        .map(dao -> supplierMapper.toDTO(dao));
  }

  @Override
  public Supplier create(Supplier supplier) {
    if (supplier == null
        || supplierRepository.existsBySupplierNameAndAddress(
            supplier.getSupplierName(), supplier.getAddress())) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.EXIST);
    }
    return Optional.ofNullable(supplier)
        .map(e -> supplierMapper.toEntity(e))
        .map(e -> supplierRepository.save(e))
        .map(e -> supplierMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Supplier update(Supplier supplier) {
    SupplierEntity oldSupplierEntity = supplierRepository.findById(supplier.getId()).orElse(null);
    if (oldSupplierEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.ROLE.NOT_EXIST);
    }
    return Optional.ofNullable(oldSupplierEntity)
        .map(
            op ->
                op.toBuilder()
                    .supplierName(supplier.getSupplierName())
                    .phoneNumber(supplier.getPhoneNumber())
                    .email(supplier.getEmail())
                    .address(supplier.getAddress())
                    .taxCode(supplier.getTaxCode())
                    .faxNumber(supplier.getFaxNumber())
                    .status(supplier.getStatus())
                    .build())
        .map(supplierRepository::save)
        .map(supplierMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      return;
    }
    supplierRepository.deleteById(id);
  }
}
