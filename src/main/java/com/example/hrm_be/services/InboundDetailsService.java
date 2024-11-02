package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.InboundDetails;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundDetailsService {
  InboundDetails getById(Long id);

  List<InboundDetails> getByInboundId(Long id);

  Page<InboundDetails> getByPaging(int pageNo, int pageSize, String sortBy);

  InboundDetails create(InboundDetails inboundDetails);

  InboundDetails update(InboundDetails inboundDetails);

  void delete(Long id);
}
