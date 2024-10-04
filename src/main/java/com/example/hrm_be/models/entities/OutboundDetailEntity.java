package com.example.hrm_be.models.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@Table(name = "outbound_details")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OutboundDetailEntity extends CommonEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "outbound_id",
      nullable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  OutboundEntity outbound;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "batch_id",
      nullable = false,
      foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  BatchEntity batch;

  @Column(name = "quantity", nullable = false)
  Integer quantity;
}