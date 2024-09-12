package com.example.hrm_be.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken {
  String accessToken;
}
