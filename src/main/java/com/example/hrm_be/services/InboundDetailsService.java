package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsCreateRequest;
import com.example.hrm_be.models.requests.inboundDetails.InboundDetailsUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundDetailsService {
  InboundDetails getById(Long id);

  Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  InboundDetails create(InboundDetailsCreateRequest inboundDetails);

  InboundDetails update(InboundDetailsUpdateRequest inboundDetails);

  void delete(Long id);
}
