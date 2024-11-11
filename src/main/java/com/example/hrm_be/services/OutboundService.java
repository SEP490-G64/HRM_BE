package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.OutboundStatus;
import com.example.hrm_be.commons.enums.OutboundType;
import com.example.hrm_be.models.dtos.Outbound;
import com.example.hrm_be.models.requests.CreateOutboundRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OutboundService {
  Outbound getById(Long id);

  Page<Outbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Outbound approve(Long id, boolean accept);

  Outbound saveOutbound(CreateOutboundRequest initOutbound);

  Outbound saveOutboundForSell(CreateOutboundRequest initOutbound);

  Outbound submitOutboundToSystem(Long outboundId);

  Outbound createInnitOutbound(OutboundType type);

  void updateOutboundStatus(OutboundStatus status, Long id);

  void delete(Long id);
}
