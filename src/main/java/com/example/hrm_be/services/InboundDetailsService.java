package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.entities.InboundEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundDetailsService {
  InboundDetails getById(Long id);

  Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  void delete(Long id);

  List<InboundDetails> findByInboundId(Long inboundId);

  void deleteAll(List<InboundDetails> inboundDetailsEntities);

  void saveAll(List<InboundDetails> inboundDetailsEntities);

  InboundEntity updateAverageInboundPricesForProductsAndInboundTotalPrice(InboundEntity inbound);
}
