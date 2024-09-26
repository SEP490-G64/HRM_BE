package com.example.hrm_be.services;


import com.example.hrm_be.models.entities.PasswordResetTokenEntity;

public interface PasswordTokenService {
    boolean create(PasswordResetTokenEntity passwordResetTokenEntity);

    PasswordResetTokenEntity findByEmail(String email);

    void delete(Long id);
}
