package com.example.hrm_be.models.requests.outboundDetails;

import com.example.hrm_be.models.entities.BatchEntity;
import com.example.hrm_be.models.entities.OutboundEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutboundDetailsCreateRequest {
    Long outboundId;

    Long batchId;

    Integer quantity;
}
