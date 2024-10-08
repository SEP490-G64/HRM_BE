package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "unit_of_measurement")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UnitOfMeasurementEntity extends CommonEntity {

  @Column(name = "unit_name", length = 100)
  String unitName;

  @ToString.Exclude
  @OneToMany(mappedBy = "baseUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<ProductEntity> products;

  @ToString.Exclude
  @OneToMany(mappedBy = "largerUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<UnitConversionEntity> largerUnitConversions; // 1-N with UnitConversion as larger unit

  @ToString.Exclude
  @OneToMany(mappedBy = "smallerUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<UnitConversionEntity> smallerUnitConversions;
}
