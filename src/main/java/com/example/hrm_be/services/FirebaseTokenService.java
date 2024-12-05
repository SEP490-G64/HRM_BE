package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.User;
import org.springframework.stereotype.Service;

@Service
public interface FirebaseTokenService {
  void saveOrUpdateToken(User userId, String deviceToken);
}
