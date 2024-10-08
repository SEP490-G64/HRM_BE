package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.Image;
import com.example.hrm_be.models.entities.ImageEntity;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {
  // Convert ImageEntity to ImageDTO
  public Image toDTO(ImageEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert ImageDTO to ImageEntity
  public ImageEntity toEntity(Image dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                ImageEntity.builder()
                    .name(d.getName())
                    .ext(d.getExt())
                    .createdTime(d.getCreatedTime())
                    .build())
        .orElse(null);
  }

  // Helper method to convert ImageEntity to ImageDTO
  private Image convertToDTO(ImageEntity entity) {
    return Image.builder()
        .name(entity.getName())
        .ext(entity.getExt())
        .createdTime(entity.getCreatedTime())
        .build();
  }
}
