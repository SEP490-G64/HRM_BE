package com.example.hrm_be.models.responses;

import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.dtos.Supplier;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.springframework.cglib.core.Local;

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class InnitInbound {
  String inboundCode;
  LocalDateTime date;
}
