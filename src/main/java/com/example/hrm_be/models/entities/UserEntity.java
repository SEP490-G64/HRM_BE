package com.example.hrm_be.models.entities;

import com.example.hrm_be.commons.enums.UserStatusType;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = "userRoleMap")
@Entity
@Table(name = "[user]")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserEntity extends CommonEntity {

  @Column(name = "username")
  String userName;

  @Column(name = "email", unique = true)
  String email;

  @Column(name = "password")
  String password;

  @Column(name = "phone")
  String phone;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  UserStatusType status;

  @ToString.Exclude
  @OneToMany(mappedBy = "user")
  List<UserRoleMapEntity> userRoleMap;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  BranchEntity branch;

  @OneToMany(mappedBy = "user")
  List<NotificationUserEntity> notifications;

  @OneToOne(mappedBy = "user")
  FirebaseTokenEntity firebaseToken;
}
