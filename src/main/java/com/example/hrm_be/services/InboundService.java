package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Inbound;
import com.itextpdf.text.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public interface InboundService {
  Inbound getById(Long id);

  Page<Inbound> getByPaging(int pageNo, int pageSize, String sortBy);

  Inbound create(Inbound inbound);

  Inbound update(Inbound inbound);

  Inbound approve(Long id, boolean accept);

  void delete(Long id);
  ByteArrayOutputStream generateInboundPdf(Long inboundId) throws DocumentException, IOException;
}
