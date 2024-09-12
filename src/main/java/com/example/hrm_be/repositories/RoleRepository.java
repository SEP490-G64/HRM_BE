package com.example.hrm_be.repositories;

import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.models.entities.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findByType(RoleType type);

  boolean existsByType(RoleType type);
}
