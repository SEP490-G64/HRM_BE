package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.BranchType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
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

  @Column(name = "branch_name", length = 100)
  String branchName;

  @Enumerated(EnumType.STRING)
  @Column(name = "branch_type")
  BranchType branchType;

  @Column(name = "location", length = 256)
  String location;

  @Column(name = "contact_person", length = 100)
  String contactPerson;

  @Column(name = "phone_number", length = 11)
  String phoneNumber;

  @Column(name = "capacity")
  Integer capacity;

  @Column(name = "active_status")
  Boolean activeStatus;

  @Column(name = "is_deleted")
  Boolean isDeleted;

  @ToString.Exclude
  @OneToMany(mappedBy = "branch")
  List<BranchBatchEntity> branchBatches; // 1-N with BranchBatch

  @ToString.Exclude
  @OneToMany(mappedBy = "toBranch")
  List<InboundEntity> fromBranchInbound;

  @ToString.Exclude
  @OneToMany(mappedBy = "fromBranch")
  List<InboundEntity> toBranchInbound; // 1-N with Inbound

  @ToString.Exclude
  @OneToMany(mappedBy = "branch")
  List<BranchProductEntity> branchProducts; // 1-N with BranchProduct

  @ToString.Exclude
  @OneToMany(mappedBy = "toBranch")
  List<OutboundEntity> toBranchOutbound;

  @ToString.Exclude
  @OneToMany(mappedBy = "fromBranch")
  List<OutboundEntity> fromBranchOutbound;

  @ToString.Exclude
  @OneToMany(mappedBy = "branch")
  List<InventoryCheckEntity> inventoryChecks; // 1-N with InventoryCheck

  @ToString.Exclude
  @OneToMany(mappedBy = "branch")
  List<UserEntity> users;
}
