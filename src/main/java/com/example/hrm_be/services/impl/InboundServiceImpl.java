package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.components.BranchMapper;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.ProductMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.*;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.repositories.BranchRepository;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import com.example.hrm_be.utils.PDFUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InboundServiceImpl implements InboundService {
  @Autowired private InboundRepository inboundRepository;
  @Autowired private InboundMapper inboundMapper;
  @Autowired private UserService userService;
  @Autowired private UserMapper userMapper;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private ProductMapper productMapper;
  @Autowired
  private BranchRepository branchRepository;
  @Autowired
  private BranchMapper branchMapper;

  @Override
  public Inbound getById(Long id) {
    return Optional.ofNullable(id)
        .flatMap(e -> inboundRepository.findById(e).map(b -> inboundMapper.toDTO(b)))
        .orElse(null);
  }

  @Override
  public Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy) {
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
    return inboundRepository.findAll(pageable).map(dao -> inboundMapper.toDTO(dao));
  }

  // Method to create a new Inbound record
  @Override
  public Inbound create(Inbound inbound) {
    if (inbound == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.EXIST); // Error handling for null inbound object
    }

    String email = userService.getAuthenticatedUserEmail(); // Retrieve the logged-in user's email
    UserEntity userEntity =
        userMapper.toEntity(userService.findLoggedInfoByEmail(email)); // Get user entity

    return Optional.ofNullable(inbound)
        .map(inboundMapper::toEntity)
        .map(
            e -> {
              e.setCreatedBy(userEntity);
              e.setCreatedDate(LocalDateTime.now());
              e.setStatus(InboundStatus.CHO_DUYET);
              e.setIsApproved(false);
              return inboundRepository.save(e);
            })
        .map(e -> inboundMapper.toDTO(e))
        .orElse(null);
  }

  // Method to update an existing Inbound record
  @Override
  public Inbound update(Inbound inbound) {
    InboundEntity oldinboundEntity = inboundRepository.findById(inbound.getId()).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    return Optional.ofNullable(oldinboundEntity)
        .map(
            op ->
                op.toBuilder()
                    .note(inbound.getNote())
                    .inboundType(inbound.getInboundType())
                    .status(inbound.getStatus())
                    .taxable(inbound.getTaxable())
                    .totalPrice(inbound.getTotalPrice())
                    .inboundDate(inbound.getInboundDate())
                    .isApproved(inbound.getIsApproved())
                    .approvedBy(oldinboundEntity.getApprovedBy())
                    .build())
        .map(inboundRepository::save)
        .map(inboundMapper::toDTO)
        .orElse(null);
  }

  // Method to approve an inbound record
  @Override
  public Inbound approve(Long id, boolean accept) {
    InboundEntity oldinboundEntity = inboundRepository.findById(id).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    String email = userService.getAuthenticatedUserEmail();
    UserEntity userEntity = userMapper.toEntity(userService.findLoggedInfoByEmail(email));

    return Optional.ofNullable(oldinboundEntity)
        .map(op -> op.toBuilder().isApproved(accept).approvedBy(userEntity).build())
        .map(inboundRepository::save)
        .map(inboundMapper::toDTO)
        .orElse(null);
  }

  // Method to delete an inbound record
  @Override
  public void delete(Long id) {
    if (StringUtils.isBlank(id.toString())) {
      return; // Return if the ID is invalid
    }

    InboundEntity oldinboundEntity = inboundRepository.findById(id).orElse(null);
    if (oldinboundEntity == null) {
      throw new HrmCommonException(
          HrmConstant.ERROR.INBOUND.NOT_EXIST); // Error if inbound entity is not found
    }

    inboundRepository.deleteById(id); // Delete the inbound entity by ID
  }

  @Override
  public ByteArrayOutputStream generateInboundPdf(Long inboundId) throws DocumentException, IOException {
    // Fetch Inbound and associated details
//    Inbound inbound = inboundRepository.findById(inboundId).map(inboundMapper::toDTO).orElse(null);
//    if (inbound == null) {
//      throw new EntityNotFoundException("Inbound record not found with ID: " + inboundId);
//    }
//    ByteArrayOutputStream out = PDFUtil.createReceiptPdf(inbound);

    //Dữ liệu để demo--------------------------------
    Inbound demo = new Inbound();
    demo.setInboundDate(LocalDateTime.now());
    Branch branch = branchRepository.findById(1l).map(branchMapper::toDTO).orElse(null);
    demo.setToBranch(branch);
    //demo.setTotalPrice(BigDecimal.valueOf(190000));
    Supplier supplier = new Supplier();
    supplier.setSupplierName("Tên nhà cung cấp");
    demo.setSupplier(supplier);
    demo.setToBranch(branch); // Gán chi nhánh cho inbound
    demo.setInboundDetails(new ArrayList<>());
    // Ví dụ thêm một chi tiết nhập kho
    InboundDetails detail = new InboundDetails();
    Optional<Product> product = productRepository.findById(1l).map(productMapper::toDTO);
    product.get().setInboundPrice(BigDecimal.valueOf(19));
    detail.setProduct(product.get());
    detail.setRequestQuantity(10);
    detail.setReceiveQuantity(9);
    demo.getInboundDetails().add(detail);

    Optional<Product> product1 = productRepository.findById(2l).map(productMapper::toDTO);
    product1.get().setInboundPrice(BigDecimal.valueOf(19));
    detail.setProduct(product1.get());
    detail.setRequestQuantity(10);
    detail.setReceiveQuantity(9);
    demo.getInboundDetails().add(detail);
    ByteArrayOutputStream out = PDFUtil.createReceiptPdf(demo);
    //---------------------------------------

    return out;
  }


}
