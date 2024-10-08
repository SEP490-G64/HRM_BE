package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.ManufacturerMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Manufacturer;
import com.example.hrm_be.models.entities.ManufacturerEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.repositories.ManufacturerRepository;
import com.example.hrm_be.services.ManufacturerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  @Autowired private ProductMapper productMapper;

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
  public Page<Manufacturer> getByPaging(int pageNo, int pageSize, String sortBy, String name) {
    // Create pageable object to handle pagination and sorting
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());

    // Search for Manufacturers by name or address (case-insensitive)
    return manufacturerRepository
        .findByManufacturerNameContainsIgnoreCaseOrAddressContainsIgnoreCase(name, name, pageable)
        .map(dao -> manufacturerMapper.toDTO(dao)); // Map found entities to DTOs
  }

  @Override
  public Manufacturer create(Manufacturer Manufacturer) {
    // Validate that Manufacturer is not null and does not already exist
    if (Manufacturer == null
        || manufacturerRepository.existsByManufacturerNameAndAddress(
            Manufacturer.getManufacturerName(), Manufacturer.getAddress())) {
      // Throw exception if Manufacturer already exists
      throw new HrmCommonException(HrmConstant.ERROR.MANUFACTURER.EXIST);
    }

    // Map Manufacturer DTO to entity and save it to the repository
    return Optional.ofNullable(Manufacturer)
        .map(e -> manufacturerMapper.toEntity(e)) // Convert DTO to entity
        .map(e -> manufacturerRepository.save(e)) // Save entity to the repository
        .map(e -> manufacturerMapper.toDTO(e)) // Map saved entity back to DTO
        .orElse(null); // Return null if the Manufacturer creation fails
  }

  @Override
  public Manufacturer update(Manufacturer Manufacturer) {
    // Retrieve existing Manufacturer entity by ID
    ManufacturerEntity oldManufacturerEntity =
        manufacturerRepository.findById(Manufacturer.getId()).orElse(null);
    // Check if the Manufacturer to be updated exists
    if (oldManufacturerEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR
              .MANUFACTURER
              .NOT_EXIST); // Throw exception if Manufacturer does not exist
    }

    List<ProductEntity> productEntityList;
    if (Manufacturer.getProducts() != null) {
      productEntityList = new ArrayList<>();
      for (int i = 0; i < Manufacturer.getProducts().size(); i++) {
        productEntityList.add(productMapper.toEntity(Manufacturer.getProducts().get(i)));
      }
    } else {
      productEntityList = null;
    }

    // Use Optional to map the existing Manufacturer entity to a new one with updated fields
    return Optional.ofNullable(oldManufacturerEntity)
        .map(
            op ->
                op.toBuilder() // Use builder pattern for immutability
                    .manufacturerName(Manufacturer.getManufacturerName())
                    .phoneNumber(Manufacturer.getPhoneNumber())
                    .email(Manufacturer.getEmail())
                    .address(Manufacturer.getAddress())
                    .taxCode(Manufacturer.getTaxCode())
                    .status(Manufacturer.getStatus())
                    .origin(Manufacturer.getOrigin())
                    .products(productEntityList)
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
    // Delete the Manufacturer by ID
    manufacturerRepository.deleteById(id);
  }
}
