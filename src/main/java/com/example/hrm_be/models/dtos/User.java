package com.example.hrm_be.models.dtos;

import com.example.hrm_be.commons.enums.UserStatusType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
  @Serial private static final long serialVersionUID = 587129066277401871L;

  Long id;

  String userName;

  String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  String password;

  String phone;

  String firstName;

  String lastName;

  UserStatusType status;

  transient List<Role> roles;

  Branch branch;

  FirebaseToken firebaseToken;
}
