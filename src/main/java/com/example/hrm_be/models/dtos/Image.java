package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.CommonEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.Timestamp;
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
public class Image extends CommonEntity {
  Long id;
  private String name;
  private String ext;
  private Timestamp createdTime;
}
