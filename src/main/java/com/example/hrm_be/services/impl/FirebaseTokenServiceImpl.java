package com.example.hrm_be.services.impl;

import com.example.hrm_be.components.UserMapper;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.FirebaseTokenEntity;
import com.example.hrm_be.repositories.FirebaseTokenRepository;
import com.example.hrm_be.services.FirebaseTokenService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class FirebaseTokenServiceImpl implements FirebaseTokenService {
  private final FirebaseTokenRepository repository;
  private final UserMapper userMapper;



  public FirebaseTokenServiceImpl(FirebaseTokenRepository repository, UserMapper userMapper) {
    this.repository = repository;
    this.userMapper = userMapper;
  }

  public void saveOrUpdateToken(User user, String deviceToken) {
    Optional<FirebaseTokenEntity> existingToken = repository.findByUserId(user.getId());
    if (existingToken.isPresent()) {
      existingToken.get().setDeviceToken(deviceToken);
      repository.save(existingToken.get());
    } else {
      FirebaseTokenEntity newToken = new FirebaseTokenEntity();
      newToken.setUser(userMapper.toEntity(user));
      newToken.setDeviceToken(deviceToken);
      repository.save(newToken);
    }
  }
}
