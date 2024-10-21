package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.responses.InnitInbound;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundService {
  Inbound getById(Long id);

  Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Inbound create(Inbound inbound);

  Inbound update(Inbound inbound);

  Inbound approve(Long id, boolean accept);

  void delete(Long id);

  Inbound submitInbound(InnitInbound innitInbound);
}
