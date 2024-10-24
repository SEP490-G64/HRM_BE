package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundService {
  InboundDetail getById(Long id);

  Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Inbound create(Inbound inbound);

  Inbound update(Inbound inbound);

  Inbound approve(Long id, boolean accept);

  void delete(Long id);

  Inbound submitDraftInbound(CreateInboundRequest innitInbound);

  Inbound createInnitInbound(InboundType type);
}
