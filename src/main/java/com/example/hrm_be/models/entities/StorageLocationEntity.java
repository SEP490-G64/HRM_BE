package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

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
@Table(name = "storage_location")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StorageLocationEntity extends CommonEntity {
  @Column(name = "shelf_name", length = 50)
  String shelfName;

  @Column(name = "aisle")
  Integer aisle;

  @Column(name = "row_number")
  Integer rowNumber;

  @Column(name = "shelf_level")
  Integer shelfLevel;

  @Column(name = "zone", length = 20)
  String zone;

  @Enumerated(EnumType.STRING)
  @Column(name = "location_type")
  LocationType locationType;

  @Column(name = "special_condition", length = 255)
  String specialCondition;

  @Column(name = "active")
  Boolean active;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  BranchEntity branch;

  @ToString.Exclude
  @OneToMany(mappedBy = "storageLocation")
  List<BranchProductEntity> branchProducts;
}
