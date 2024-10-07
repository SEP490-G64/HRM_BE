package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.requests.outbound.OutboundCreateRequest;
import com.example.hrm_be.models.requests.outbound.OutboundUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OutboundService {
  Outbound getById(Long id);

  Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Outbound create(OutboundCreateRequest outbound);

  Outbound update(OutboundUpdateRequest outbound);

  Outbound approve(Long id);

  void delete(Long id);
}
