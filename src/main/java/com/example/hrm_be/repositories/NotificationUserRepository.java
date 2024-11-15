package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.NotificationUserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUserEntity,
    Long> {
  List<NotificationUserEntity> findByUser_Id(Long userId);

  List<NotificationUserEntity> findByUser_IdAndIsReadIsFalse(Long userId);

  Optional<NotificationUserEntity> findById(Long id);

  Optional<NotificationUserEntity> findByNotification_IdAndUser_Id(Long notifId, Long userId);

  Integer countByUser_IdAndIsReadFalse(Long userId);

}
