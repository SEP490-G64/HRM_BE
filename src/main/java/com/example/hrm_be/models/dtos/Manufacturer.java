package com.example.hrm_be.models.dtos;

import com.example.hrm_be.models.entities.ProductEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class Manufacturer implements Serializable {

  @Serial private static final long serialVersionUID = 7885590599725037539L;
  Long id;

  String manufacturerName;

  String contactPerson;

  String phoneNumber;

  String email;

  String address;

  String origin;

  List<Product> products;
}
