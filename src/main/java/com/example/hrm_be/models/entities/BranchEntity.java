package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.BranchType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "branch")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BranchEntity extends CommonEntity {

  @Column(name = "branch_name", length = 255)
  String branchName;

  @Enumerated(EnumType.STRING)
  @Column(name = "branch_type")
  BranchType branchType;

  @Column(name = "location", length = 255)
  String location;

  @Column(name = "contact_person", length = 255)
  String contactPerson;

  @Column(name = "phone_number", length = 50)
  String phoneNumber;

  @Column(name = "capacity")
  Integer capacity;

  @Column(name = "active_status")
  Boolean activeStatus;

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<BranchBatchEntity> branchBatches; // 1-N with BranchBatch

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InboundEntity> inbounds; // 1-N with Inbound

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<BranchProductEntity> branchProducts; // 1-N with BranchProduct

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<OutboundEntity> outbounds; // 1-N with Outbound

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<InventoryCheckEntity> inventoryChecks; // 1-N with InventoryCheck

  @ToString.Exclude
  @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<UserEntity> users;
}
