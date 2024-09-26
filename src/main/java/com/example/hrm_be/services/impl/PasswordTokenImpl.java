package com.example.hrm_be.services.impl;

import com.example.hrm_be.models.entities.PasswordResetTokenEntity;
import com.example.hrm_be.repositories.PasswordTokenRepository;
import com.example.hrm_be.services.PasswordTokenService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PasswordTokenImpl implements PasswordTokenService {
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Override
    public boolean create(PasswordResetTokenEntity passwordResetTokenEntity) {
        if (findByEmail(passwordResetTokenEntity.getUserEmail()) != null) {
            delete(passwordResetTokenEntity.getId());
        }
        PasswordResetTokenEntity entity = Optional.of(passwordResetTokenEntity)
                .map(passwordTokenRepository::save)
                .orElse(null);
        return entity != null;
    }

    @Override
    public PasswordResetTokenEntity findByEmail(String email) {
        return passwordTokenRepository.findByEmailAddress(email).orElse(null);
    }

    @Override
    public void delete(Long id) {
        if (StringUtils.isBlank(id.toString())) {
            return;
        }
        passwordTokenRepository.deleteById(id);
    }
}
