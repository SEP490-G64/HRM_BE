package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.dtos.InboundDetails;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface InboundService {
  InboundDetail getDetail(Long id);

  Inbound getById(Long Id);

  Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Inbound create(Inbound inbound);

  Inbound update(Inbound inbound);

  Inbound approve(Long id, boolean accept);

  void delete(Long id);

  Inbound saveInbound(CreateInboundRequest innitInbound);

  Inbound submitInboundToSystem(Long inboundId);

  Inbound createInnitInbound(InboundType type);

  Inbound updateInboundStatus(InboundStatus status, Long id);

  void saveUpdatedEntities(List<InboundDetails> inboundDetailsList,
      List<InboundBatchDetail> inboundBatchDetailsList);
  void deleteUnmatchedEntities(List<InboundDetails> requestInboundDetails,
      List<InboundBatchDetail> requestInboundBatchDetails);
}
