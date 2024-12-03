package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.FirebaseToken;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.FirebaseTokenEntity;
import com.example.hrm_be.models.entities.UserEntity;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class FirebaseTokenMapper {
  @Autowired @Lazy private UserMapper userMapper;

  public FirebaseToken toDTO(FirebaseTokenEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDto).orElse(null);
  }

  public FirebaseTokenEntity toEntity(FirebaseToken dto) {
    return Optional.ofNullable(dto)
        .map(
            e -> FirebaseTokenEntity.builder()
                .id(e.getId())
                .deviceToken(e.getDeviceToken())
                .user(userMapper.toEntity(e.getUser()))
                .build())
        .orElse(null);
  }

  private FirebaseToken convertToDto(FirebaseTokenEntity entity) {
    return Optional.ofNullable(entity)
        .map(
            e ->
                FirebaseToken.builder()
                    .id(e.getId())
                    .deviceToken(e.getDeviceToken())
                    .build())
        .orElse(null);
  }
}
