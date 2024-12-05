package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.FirebaseTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FirebaseTokenRepository extends JpaRepository<FirebaseTokenEntity, Long> {

  @Query("SELECT bp FROM FirebaseTokenEntity bp WHERE bp.user.id = :aLong")
  Optional<FirebaseTokenEntity> findByUserId(@Param("aLong") Long aLong);

  void deleteByDeviceToken(String deviceToken);
}
