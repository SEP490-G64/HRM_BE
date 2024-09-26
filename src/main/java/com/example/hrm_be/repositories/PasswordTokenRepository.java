package com.example.hrm_be.repositories;

import com.example.hrm_be.models.entities.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    @Query("select p from PasswordResetTokenEntity p where p.userEmail = ?1")
    Optional<PasswordResetTokenEntity> findByEmailAddress(String emailAddress);
}
