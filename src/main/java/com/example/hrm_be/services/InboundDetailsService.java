package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.InboundDetailsEntity;
import com.example.hrm_be.models.entities.InboundEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InboundDetailsService {
  InboundDetails getById(Long id);

  Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  InboundDetails create(InboundDetails inboundDetails);

  InboundDetails update(InboundDetails inboundDetails);

  void delete(Long id);

  List<InboundDetailsEntity> findByInboundId(Long inboundId);

  void deleteAll(List<InboundDetailsEntity> inboundDetailsEntities);

  void saveAll(List<InboundDetailsEntity> inboundDetailsEntities);

  InboundEntity updateAverageInboundPricesForProductsAndInboundTotalPrice(InboundEntity inbound);
}
