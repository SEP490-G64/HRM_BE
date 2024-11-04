package com.example.hrm_be.services;

import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.InboundDetail;
import com.itextpdf.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public interface InboundService {
  InboundDetail getById(Long id);

  Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Inbound create(Inbound inbound);

  Inbound update(Inbound inbound);

  Inbound approve(Long id, boolean accept);

  void delete(Long id);

  Inbound saveInbound(CreateInboundRequest innitInbound);

  Inbound submitInboundToSystem(Long inboundId);

  Inbound createInnitInbound(InboundType type);

  Inbound updateInboundStatus(InboundStatus status, Long id);

  ByteArrayOutputStream generateInboundPdf(Long inboundId) throws DocumentException, IOException;
}
