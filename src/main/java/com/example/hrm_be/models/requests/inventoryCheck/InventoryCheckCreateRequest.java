package com.example.hrm_be.models.requests.inventoryCheck;

import com.example.hrm_be.commons.enums.InventoryCheckStatus;
import com.example.hrm_be.models.entities.BranchEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryCheckCreateRequest {
    Long branchId;

    String note;
}
