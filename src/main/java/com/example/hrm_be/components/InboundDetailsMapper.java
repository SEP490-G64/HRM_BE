package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.*;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsCreateRequest;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InboundDetailsMapper {

  @Autowired @Lazy private InboundMapper inboundMapper;
  @Autowired @Lazy private ProductMapper productMapper;

  // Convert InboundDetailsEntity to InboundDetailsDTO
  public InboundDetails toDTO(InboundDetailsEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert InboundDetailsDTO to InboundDetailsEntity
  public InboundDetailsEntity toEntity(InboundDetails dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                InboundDetailsEntity.builder()
                    .inbound(d.getInbound() != null ? inboundMapper.toEntity(d.getInbound()) : null)
                    .product(d.getProduct() != null ? productMapper.toEntity(d.getProduct()) : null)
                    .requestQuantity(d.getRequestQuantity())
                    .receiveQuantity(d.getReceiveQuantity())
                    .build())
        .orElse(null);
  }

  // Helper method to convert InboundDetailsEntity to InboundDetailsDTO
  private InboundDetails convertToDTO(InboundDetailsEntity entity) {
    return InboundDetails.builder()
        .inbound(entity.getInbound() != null ? inboundMapper.toDTO(entity.getInbound()) : null)
        .product(entity.getProduct() != null ? productMapper.toDTO(entity.getProduct()) : null)
        .requestQuantity(entity.getRequestQuantity())
        .receiveQuantity(entity.getReceiveQuantity())
        .build();
  }

  // Convert InboundDetailsCreateRequest to InboundDetailsEntity
  public InboundDetailsEntity toEntity(
      InboundDetailsCreateRequest dto, InboundEntity inbound, ProductEntity product) {
    return Optional.ofNullable(dto)
        .map(
            request -> {
              // Create InboundEntity from InboundDetailsCreateRequest
              return InboundDetailsEntity.builder()
                  .inbound(inbound)
                  .product(product)
                  .requestQuantity(dto.getRequestQuantity())
                  .receiveQuantity(dto.getReceiveQuantity())
                  .build();
            })
        .orElse(null);
  }

  // Convert InboundUpdateRequest to InboundDetailsEntity
  public InboundDetailsEntity toEntity(InboundDetailsUpdateRequest dto) {
    return Optional.ofNullable(dto)
        .map(
            request -> {
              // Create InboundDetailsEntity from InboundDetailsUpdateRequest
              return InboundDetailsEntity.builder()
                  .requestQuantity(dto.getRequestQuantity())
                  .receiveQuantity(dto.getReceiveQuantity())
                  .build();
            })
        .orElse(null);
  }
}
