package com.example.hrm_be.services.impl;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.components.InboundMapper;
import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.configs.exceptions.HrmCommonException;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.entities.InboundEntity;
import com.example.hrm_be.models.entities.ProductEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.responses.InnitInbound;
import com.example.hrm_be.repositories.InboundRepository;
import com.example.hrm_be.repositories.ProductRepository;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  @Autowired private ProductRepository productRepository;

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
  @Transactional
  public Inbound submitInbound(InnitInbound innitInbound) {
    InboundEntity inbound = new InboundEntity();
    inbound.setInboundCode(innitInbound.getInboundCode());
    inbound.setInboundDate(innitInbound.getDate());

    InboundEntity savedInboundEntity = inboundRepository.save(inbound);
    addOrUpdateProductInbound();
    return null;
  }

  public void addOrUpdateProductInbound(ProductInbound newProductInbound) {
    // Check if a product with the same registrationCode exists in the database
    boolean productExists = productRepository.existsByRegistrationCode(newProductInbound.getRegistrationCode());

    if (productExists) {
      // Fetch the existing product and update its quantity
      ProductInbound existingProduct =
          productRepository.find(newProductInbound.getRegistrationCode());
      existingProduct.setQuantity(existingProduct.getQuantity() + newProductInbound.getQuantity());
      productInboundRepository.save(existingProduct); // Save updated product
    } else {
      // If product does not exist, insert new product
      productInboundRepository.save(newProductInbound);
    }
  }
}
