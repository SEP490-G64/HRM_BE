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

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
  @Autowired private SupplierRepository supplierRepository;

  @Autowired private SupplierMapper supplierMapper;

  @Override
  public Supplier getById(Long id) {
    // Use Optional to handle potential null values for the supplier ID
    return Optional.ofNullable(id)
        // Attempt to find the supplier by ID and map to DTO
        .flatMap(e -> supplierRepository.findById(e).map(b -> supplierMapper.toDTO(b)))
        // Return null if ID is null or supplier not found
        .orElse(null);
  }

  @Override
  public Page<Supplier> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    // Create pageable object to handle pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Search for suppliers by name or address (case-insensitive)
    return supplierRepository
        .findBySupplierNameContainsIgnoreCaseOrAddressContainsIgnoreCase(name, name, pageable)
        .map(dao -> supplierMapper.toDTO(dao)); // Map found entities to DTOs
  }

  @Override
  public Supplier create(Supplier supplier) {
    // Validate that supplier is not null and does not already exist
    if (supplier == null
        || supplierRepository.existsBySupplierNameAndAddress(
            supplier.getSupplierName(), supplier.getAddress())) {
      // Throw exception if supplier already exists
      throw new HrmCommonException(HrmConstant.ERROR.SUPPLIER.EXIST);
    }

    // Check if supplier tax code exist except for the current supplier (by comparing with old
    // entity data)
    if (supplier.getTaxCode() != null && !supplier.getTaxCode().trim().isEmpty()) {
      if (supplierRepository.existsByTaxCode(supplier.getTaxCode())) {
        throw new HrmCommonException(HrmConstant.ERROR.SUPPLIER.TAXCODE_NOT_EXIST);
      }
    }

    // Map supplier DTO to entity and save it to the repository
    return Optional.ofNullable(supplier)
        .map(e -> supplierMapper.toEntity(e)) // Convert DTO to entity
        .map(e -> supplierRepository.save(e)) // Save entity to the repository
        .map(e -> supplierMapper.toDTO(e)) // Map saved entity back to DTO
        .orElse(null); // Return null if the supplier creation fails
  }

  @Override
  public Supplier update(Supplier supplier) {
    // Retrieve existing supplier entity by ID
    SupplierEntity oldSupplierEntity = supplierRepository.findById(supplier.getId()).orElse(null);
    // Check if the supplier to be updated exists
    if (oldSupplierEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.SUPPLIER.NOT_EXIST); // Throw exception if supplier does not exist
    }

    // Check if supplier name and address exist except current supplier
    if (supplierRepository.existsBySupplierNameAndAddress(
            supplier.getSupplierName(), supplier.getAddress())
        && (!Objects.equals(supplier.getSupplierName(), oldSupplierEntity.getSupplierName())
            && !Objects.equals(supplier.getAddress(), oldSupplierEntity.getAddress()))) {
      throw new HrmCommonException(HrmConstant.ERROR.SUPPLIER.EXIST);
    }

    // Check if supplier tax code exist except for the current supplier (by comparing with old
    // entity data)
    if (supplier.getTaxCode() != null && !supplier.getTaxCode().trim().isEmpty()) {
      if (!supplier.getTaxCode().equals(oldSupplierEntity.getTaxCode())
          && supplierRepository.existsByTaxCode(supplier.getTaxCode())) {
        throw new HrmCommonException(HrmConstant.ERROR.SUPPLIER.TAXCODE_NOT_EXIST);
      }
    }

    // Use Optional to map the existing supplier entity to a new one with updated fields
    return Optional.ofNullable(oldSupplierEntity)
        .map(
            op ->
                op.toBuilder() // Use builder pattern for immutability
                    .supplierName(supplier.getSupplierName())
                    .phoneNumber(supplier.getPhoneNumber())
                    .email(supplier.getEmail())
                    .address(supplier.getAddress())
                    .taxCode(supplier.getTaxCode())
                    .faxNumber(supplier.getFaxNumber())
                    .status(supplier.getStatus())
                    .build()) // Build updated supplier entity
        .map(supplierRepository::save) // Save the updated supplier entity
        .map(supplierMapper::toDTO) // Convert saved entity back to DTO
        .orElse(null); // Return null if the update fails
  }

  @Override
  public void delete(Long id) {
    // Check if the supplier ID is null; if so, do nothing
    if (id == null) {
      return;
    }

    // Retrieve existing supplier entity by ID
    SupplierEntity oldSupplierEntity = supplierRepository.findById(id).orElse(null);
    // Check if the supplier to be updated exists
    if (oldSupplierEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.SUPPLIER.NOT_EXIST); // Throw exception if supplier does not exist
    }

    // Delete the supplier by ID
    supplierRepository.deleteById(id);
  }
}
