package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.OutboundDetail;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OutboundDetailService {
  OutboundDetail getById(Long id);

  Page<OutboundDetail> getByPaging(int pageNo, int pageSize, String sortBy);

  OutboundDetail create(OutboundDetail outboundDetail);

  OutboundDetail update(OutboundDetail outboundDetail);

  void delete(Long id);
}
