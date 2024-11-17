package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.BatchStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchDto {
    Long id;
    String batchCode;

    LocalDateTime produceDate;

    LocalDateTime expireDate;

    BatchStatus batchStatus;

    BigDecimal inboundPrice;

    BigDecimal quantity;

    String baseUnit;

    List<UnitConversion> unitConversions;
}
