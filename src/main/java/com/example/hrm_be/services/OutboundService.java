package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.models.dtos.Outbound;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OutboundService {
  Outbound getById(Long id);

  Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Outbound create(Outbound outbound);

  Outbound update(Outbound outbound);

  Outbound approve(Long id, boolean accept);

  Outbound createInnitOutbound(OutboundType type);

  void delete(Long id);
}
