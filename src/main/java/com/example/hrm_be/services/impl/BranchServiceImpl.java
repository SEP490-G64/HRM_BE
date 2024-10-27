package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.BranchService;
import io.micrometer.common.util.StringUtils;
import java.util.Objects;
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
public class BranchServiceImpl implements BranchService {
  // Injects the repository to interact with branch data in the database
  @Autowired private BranchRepository branchRepository;

  // Injects the mapper to convert between DTO and Entity objects for branches
  @Autowired private BranchMapper branchMapper;

  // Retrieves a Branch by its ID
  @Override
  public Branch getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> branchRepository.findById(e).map(b -> branchMapper.toDTO(b)))
        .orElse(null);
  }

  // Retrieves a paginated list of Branch entities, allowing sorting and searching by name or
  // location and type
  @Override
  public Page<Branch> getByPaging(
      int pageNo, int pageSize, String sortBy, String keyword, BranchType branchType) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return branchRepository
        .findByBranchNameOrLocationAndBranchType(keyword, branchType, pageable)
        .map(dao -> branchMapper.toDTO(dao));
  }

  // Creates a new Branch
  @Override
  public Branch create(Branch branch) {
    // Validation: Ensure the branch is not null and does not have any invalid field
    if (branch == null || !commonValidate(branch)) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    // Validation: Ensure the branch does not already exist at the same location
    if (branchRepository.existsByLocation(branch.getLocation())) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    // Convert DTO to entity, save it, and convert back to DTO
    return Optional.ofNullable(branch)
        .map(e -> branchMapper.toEntity(e))
        .map(e -> branchRepository.save(e))
        .map(e -> branchMapper.toDTO(e))
        .orElse(null);
  }

  // Updates an existing Branch
  @Override
  public Branch update(Branch branch) {
    if (branch == null || !commonValidate(branch)) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }
    // Retrieve the existing branch entity by ID
    BranchEntity oldBranchEntity = branchRepository.findById(branch.getId()).orElse(null);
    if (oldBranchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    // Check if branch location exist except current branch
    if (branchRepository.existsByLocation(branch.getLocation())
        && !Objects.equals(branch.getLocation(), oldBranchEntity.getLocation())) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }

    // Update the fields of the existing branch entity with new values
    return Optional.ofNullable(oldBranchEntity)
        .map(
            op ->
                op.toBuilder()
                    .branchName(branch.getBranchName())
                    .branchType(branch.getBranchType())
                    .capacity(branch.getCapacity())
                    .contactPerson(branch.getContactPerson())
                    .phoneNumber(branch.getPhoneNumber())
                    .location(branch.getLocation())
                    .activeStatus(branch.getActiveStatus())
                    .build())
        .map(branchRepository::save)
        .map(branchMapper::toDTO)
        .orElse(null);
  }

  // Deletes a Branch by ID
  @Override
  public void delete(Long id) {
    // Validation: Check if the ID is blank
    if (StringUtils.isBlank(id.toString())) {
      return;
    }

    // Retrieve the existing branch entity by ID
    BranchEntity oldBranchEntity = branchRepository.findById(id).orElse(null);
    if (oldBranchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    // Delete the branch by ID
    branchRepository.deleteById(id);
  }

  @Override
  public Branch getByLocationContains(String location) {
    return branchRepository
        .findByLocationContainsIgnoreCase(location)
        .map(branchMapper::toDTO)
        .orElse(null);
  }

  // This method will validate branch name, simple location, contact person, phone number, capacity
  private boolean commonValidate(Branch branch) {
    if (branch.getBranchName() == null
        || branch.getBranchName().isEmpty()
        || branchRepository.existsByBranchName(branch.getBranchName())) {
      return false;
    }
    if (branch.getLocation() == null || branch.getLocation().isEmpty()) {
      return false;
    }
    if (branch.getContactPerson().length() > 256) {
      return false;
    }
    if (branch.getPhoneNumber() == null
        || branch.getPhoneNumber().isEmpty()
        || branch.getPhoneNumber().length() > 11
        || !branch.getPhoneNumber().matches(HrmConstant.REGEX.PHONE_NUMBER)) {
      return false;
    }
    if (branch.getCapacity() < 0
        || branch.getCapacity()
            > 100000) { // Use 100000 instead of 100.000 for decimal format correction
      return false;
    }
    return branch.getBranchType() != null;
  }
}
