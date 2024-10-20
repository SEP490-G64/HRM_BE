package com.example.hrm_be.components;

import com.example.hrm_be.models.dtos.File;
import com.example.hrm_be.models.entities.FileEntity;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {
  // Convert ImageEntity to ImageDTO
  public File toDTO(FileEntity entity) {
    return Optional.ofNullable(entity).map(this::convertToDTO).orElse(null);
  }

  // Convert ImageDTO to ImageEntity
  public FileEntity toEntity(File dto) {
    return Optional.ofNullable(dto)
        .map(
            d ->
                FileEntity.builder()
                    .name(d.getName())
                    .ext(d.getExt())
                    .createdTime(d.getCreatedTime())
                    .link(d.getLink())
                    .build())
        .orElse(null);
  }

  // Helper method to convert ImageEntity to ImageDTO
  private File convertToDTO(FileEntity entity) {
    return File.builder()
        .name(entity.getName())
        .ext(entity.getExt())
        .createdTime(entity.getCreatedTime())
        .link(entity.getLink())
        .build();
  }
}
