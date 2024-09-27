package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.services.BranchService;
import io.micrometer.common.util.StringUtils;
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
public class BranchSeviceImpl implements BranchService {

  @Autowired private BranchRepository branchRepository;
  @Autowired private BranchMapper branchMapper;

  @Override
  public Branch getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> branchRepository.findById(e).map(b -> branchMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Branch> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return branchRepository.findAll(pageable).map(dao -> branchMapper.toDTO(dao));
  }

  @Override
  public Branch create(Branch branch) {
    if (branch == null || branchRepository.existsByLocation(branch.getLocation())) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.EXIST);
    }
    return Optional.ofNullable(branch)
        .map(e -> branchMapper.toEntity(e))
        .map(e -> branchRepository.save(e))
        .map(e -> branchMapper.toDTO(e))
        .orElse(null);
  }

  @Override
  public Branch update(Branch branch) {
    BranchEntity oldBranchEntity = branchRepository.findById(branch.getId()).orElse(null);
    if (oldBranchEntity == null) {
      throw new HrmCommonException(HrmConstant.ERROR.BRANCH.NOT_EXIST);
    }
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
                    .build())
        .map(branchRepository::save)
        .map(branchMapper::toDTO)
        .orElse(null);
  }

  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return;
    }
    branchRepository.deleteById(id);
  }
}
