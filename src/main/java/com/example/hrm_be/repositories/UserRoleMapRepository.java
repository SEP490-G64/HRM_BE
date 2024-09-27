package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRoleMapRepository extends JpaRepository<UserRoleMapEntity, Long> {

  @Query("FROM UserRoleMapEntity urm WHERE urm.user = :user")
  List<UserRoleMapEntity> findByUser(UserEntity user);

  @Query(
      "SELECT COUNT(ur)>0 FROM UserRoleMapEntity ur WHERE ur.user.email = :userEmail AND"
          + " ur.role.type = :roleType ")
  Boolean existsByEmailAndRole(String userEmail, RoleType roleType);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserRoleMapEntity urm WHERE urm.user = :user")
  void deleteByUser(UserEntity user);
}
