package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.BranchType;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.BranchService;
import io.micrometer.common.lang.Nullable;
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
    // Validation: Check if the ID is blank, this never happen
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    return Optional.ofNullable(id)
        .flatMap(
            e ->
                branchRepository
                    .findById(e)
                    .map(
                        b ->
                            b.getIsDeleted() != null && b.getIsDeleted()
                                ? null
                                : branchMapper.toDTO(b))) // Kiểm tra isDeleted trong map
        .orElse(null);
  }

  // Retrieves a paginated list of Branch entities, allowing sorting and searching by name or
  // location and type
  @Override
  public Page<Branch> getByPaging(
      int pageNo,
      int pageSize,
      String sortBy,
      String keyword,
      BranchType branchType,
      @Nullable Boolean status) {

    if (pageNo < 0 || pageSize < 1) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (sortBy == null) {
      sortBy = "id";
    }
    if (!Objects.equals(sortBy, "id")
        && !Objects.equals(sortBy, "branchName")
        && !Objects.equals(sortBy, "location")
        && !Objects.equals(sortBy, "branchType")
        && !Objects.equals(sortBy, "contactPerson")
        && !Objects.equals(sortBy, "phoneNumber")
        && !Objects.equals(sortBy, "capacity")
        && !Objects.equals(sortBy, "activeStatus")) {
      throw new HrmCommonException(HrmConstant.ERROR.PAGE.INVALID);
    }

    if (keyword == null) {
      keyword = "";
    }

    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return branchRepository
        .findByBranchNameOrLocationAndBranchType(keyword, branchType, status, pageable)
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

    if (branch.getId() == null) {
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
    // Validation: Check if the ID is blank, this never happen
    if (id == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.INVALID);
    }

    // Retrieve the existing branch entity by ID
    BranchEntity oldBranchEntity = branchRepository.findById(id).orElse(null);
    if (oldBranchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }

    oldBranchEntity.setIsDeleted(true);
    // Delete the branch by ID
    branchRepository.save(oldBranchEntity);
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
        || branch.getBranchName().trim().isEmpty()
        || branch.getBranchName().length() > 100) {
      return false;
    }
    if (branch.getLocation() == null
        || branch.getLocation().trim().isEmpty()
        || branch.getLocation().length() > 255) {
      return false;
    }
    if (branch.getContactPerson() != null
        && !branch.getContactPerson().trim().isEmpty()
        && branch.getContactPerson().length() > 100) {
      return false;
    }
    if (branch.getPhoneNumber() == null
        || branch.getPhoneNumber().trim().isEmpty()
        || !branch.getPhoneNumber().matches(HrmConstant.REGEX.PHONE_NUMBER)) {
      return false;
    }
    if (branch.getCapacity() != null
        && (branch.getCapacity() < 1
            || branch.getCapacity()
                > 100000)) { // Use 100000 instead of 100.000 for decimal format correction
      return false;
    }

    if (branch.getBranchType() == null) {
      return false;
    }

    if (branch.getActiveStatus() == null) {
      return false;
    }

    return true;
  }
}
