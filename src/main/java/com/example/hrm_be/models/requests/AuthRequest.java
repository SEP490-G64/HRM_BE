package com.example.hrm_be.models.requests;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
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
public class AuthRequest implements Serializable {
  @Serial private static final long serialVersionUID = -8075627584991337938L;

  @NotNull(message = HrmConstant.ERROR.REQUEST.INVALID_BODY_EMAIL)
  @Email(message = HrmConstant.ERROR.REQUEST.INVALID_BODY_EMAIL)
  String email;

  @NotBlank(message = HrmConstant.ERROR.REQUEST.INVALID_BODY_PASSWORD)
  String password;

  Boolean isNotSendingEmail;

  String deviceToken;
}
