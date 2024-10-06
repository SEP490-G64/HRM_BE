package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.UserStatusType;
import com.example.hrm_be.models.entities.RoleEntity;
import com.example.hrm_be.models.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUserName(String username);

  @Query(
      "SELECT u FROM UserEntity u "
          + "WHERE (LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
          + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
          + "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchKeyword, '%')) "
          + "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchKeyword, '%'))) "
          + "AND (u.status <> :status)")
  Page<UserEntity> searchUsers(String searchKeyword, UserStatusType status, Pageable pageable);

  Page<UserEntity> findByStatus(UserStatusType status, Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE FROM UserEntity u WHERE u.id IN :ids")
  void deleteByIds(@Param("ids") List<Long> ids);

  @Query(
      "SELECT role FROM UserEntity user "
          + "JOIN user.userRoleMap urm "
          + "JOIN urm.role role "
          + "WHERE user.email = :email")
  List<RoleEntity> findRolesByEmail(String email);
}
