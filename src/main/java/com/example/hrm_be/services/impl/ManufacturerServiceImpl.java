package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ManufacturerMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import com.example.hrm_be.repositories.ManufacturerRepository;
import com.example.hrm_be.services.ManufacturerService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ManufacturerServiceImpl implements ManufacturerService {
  @Autowired private ManufacturerRepository manufacturerRepository;

  @Autowired private ManufacturerMapper manufacturerMapper;

  @Override
  public List<Manufacturer> getAll() {
    List<ManufacturerEntity> manufacturerEntities = manufacturerRepository.findAll();
    return manufacturerEntities.stream()
        .map(dao -> manufacturerMapper.toDTO(dao))
        .collect(Collectors.toList());
  }

  @Override
  public Manufacturer getById(Long id) {
    // Use Optional to handle potential null values for the Manufacturer ID
    return Optional.ofNullable(id)
        // Attempt to find the Manufacturer by ID and map to DTO
        .flatMap(e -> manufacturerRepository.findById(e).map(b -> manufacturerMapper.toDTO(b)))
        // Return null if ID is null or Manufacturer not found
        .orElse(null);
  }

  @Override
  public Page<Manufacturer> getByPaging(
      int pageNo, int pageSize, String sortBy, String name, Boolean status) {
    // Create pageable object to handle pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Search for Manufacturers by name or address (case-insensitive)
    return manufacturerRepository
        .searchManufacturers(name, status, pageable)
        .map(dao -> manufacturerMapper.toDTO(dao)); // Map found entities to DTOs
  }

  @Override
  public Manufacturer create(Manufacturer manufacturer) {
    // Validate that Manufacturer is not null and does not already exist
    if (manufacturer == null
        || manufacturerRepository.existsByManufacturerNameAndAddress(
            manufacturer.getManufacturerName(), manufacturer.getAddress())) {
      // Throw exception if Manufacturer already exists
      throw new HrmCommonException(HrmConstant.ERROR.MANUFACTURER.EXIST);
    }

    // Check if manufacturer tax code exist
    if (manufacturer.getTaxCode() != null && !manufacturer.getTaxCode().trim().isEmpty()) {
      if (manufacturerRepository.existsByTaxCode(manufacturer.getTaxCode())) {
        throw new HrmCommonException(HrmConstant.ERROR.MANUFACTURER.TAXCODE_NOT_EXIST);
      }
    }

    // Map Manufacturer DTO to entity and save it to the repository
    return Optional.ofNullable(manufacturer)
        .map(e -> manufacturerMapper.toEntity(e)) // Convert DTO to entity
        .map(e -> manufacturerRepository.save(e)) // Save entity to the repository
        .map(e -> manufacturerMapper.toDTO(e)) // Map saved entity back to DTO
        .orElse(null); // Return null if the Manufacturer creation fails
  }

  @Override
  public Manufacturer update(Manufacturer manufacturer) {
    // Retrieve existing Manufacturer entity by ID
    ManufacturerEntity oldManufacturerEntity =
        manufacturerRepository.findById(manufacturer.getId()).orElse(null);
    // Check if the Manufacturer to be updated exists
    if (oldManufacturerEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR
              .MANUFACTURER
              .NOT_EXIST); // Throw exception if Manufacturer does not exist
    }

    // Check if manufacturer name and address exist except for the current manufacturer (by
    // comparing with old entity data)
    if (manufacturerRepository.existsByManufacturerNameAndAddress(
            manufacturer.getManufacturerName(), manufacturer.getAddress())
        && (!Objects.equals(
                manufacturer.getManufacturerName(), oldManufacturerEntity.getManufacturerName())
            || !Objects.equals(manufacturer.getAddress(), oldManufacturerEntity.getAddress()))) {
      throw new HrmCommonException(HrmConstant.ERROR.MANUFACTURER.EXIST);
    }

    // Check if manufacturer tax code exist except for the current manufacturer (by comparing with
    // old entity data)
    if (manufacturer.getTaxCode() != null && !manufacturer.getTaxCode().trim().isEmpty()) {
      if (!manufacturer.getTaxCode().equals(oldManufacturerEntity.getTaxCode())
          && manufacturerRepository.existsByTaxCode(manufacturer.getTaxCode())) {
        throw new HrmCommonException(HrmConstant.ERROR.MANUFACTURER.TAXCODE_NOT_EXIST);
      }
    }

    // Use Optional to map the existing Manufacturer entity to a new one with updated fields
    return Optional.ofNullable(oldManufacturerEntity)
        .map(
            op ->
                op.toBuilder() // Use builder pattern for immutability
                    .manufacturerName(manufacturer.getManufacturerName())
                    .phoneNumber(manufacturer.getPhoneNumber())
                    .email(manufacturer.getEmail())
                    .address(manufacturer.getAddress())
                    .taxCode(manufacturer.getTaxCode())
                    .status(manufacturer.getStatus())
                    .origin(manufacturer.getOrigin())
                    .build()) // Build updated Manufacturer entity
        .map(manufacturerRepository::save) // Save the updated Manufacturer entity
        .map(manufacturerMapper::toDTO) // Convert saved entity back to DTO
        .orElse(null); // Return null if the update fails
  }

  @Override
  public void delete(Long id) {
    // Check if the Manufacturer ID is null; if so, do nothing
    if (id == null) {
      return;
    }

    // Retrieve existing Manufacturer entity by ID
    ManufacturerEntity oldManufacturerEntity = manufacturerRepository.findById(id).orElse(null);
    // Check if the Manufacturer to be deleted exists
    if (oldManufacturerEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR
              .MANUFACTURER
              .NOT_EXIST); // Throw exception if Manufacturer does not exist
    }

    // Delete the Manufacturer by ID
    manufacturerRepository.deleteById(id);
  }

  @Override
  public Manufacturer getByName(String name) {
    return Optional.ofNullable(name)
        .flatMap(
            e ->
                manufacturerRepository
                    .findByManufacturerName(e)
                    .map(m -> manufacturerMapper.toDTO(m)))
        .orElse(null);
  }
}
