package com.example.hrm_be.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.hrm_be.components.ImageMapper;
import com.example.hrm_be.models.dtos.Image;
import com.example.hrm_be.models.entities.ImageEntity;
import com.example.hrm_be.repositories.ImageRepository;
import com.example.hrm_be.services.ImageService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
  @Autowired ImageRepository imageRepo;

  @Autowired AmazonS3 s3Client;

  @Autowired ImageMapper imageMapper;

  @Value("${hrm-config.s3.bucket:default}")
  private String doSpaceBucket;

  String FOLDER = "files/";

  @Override
  public long saveFile(MultipartFile multipartFile) throws IOException {
    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    String imgName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
    String key = FOLDER + imgName + "." + extension;
    saveImageToServer(multipartFile, key);
    Image image = new Image();
    image.setName(imgName);
    image.setExt(extension);
    image.setCreatedTime(new Timestamp(new Date().getTime()));
    // Map Image DTO to entity and save it to the repository
    Optional.ofNullable(image)
        .map(e -> imageMapper.toEntity(e)) // Convert DTO to entity
        .map(e -> imageRepo.save(e)) // Save entity to the repository
        .map(e -> imageMapper.toDTO(e)) // Map saved entity back to DTO
        .orElse(null); // Return null if the Manufacturer creation fails
    return image.getId();
  }

  @Override
  public void deleteFile(Long fileId) throws Exception {
    Optional<ImageEntity> imageOpt = imageRepo.findById(fileId);
    if (imageOpt.get() != null) {
      ImageEntity image = imageOpt.get();
      String key = FOLDER + image.getName() + "." + image.getExt();
      s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
      imageRepo.delete(image);
    }
  }

  private void saveImageToServer(MultipartFile multipartFile, String key) throws IOException {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getInputStream().available());
    if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
      metadata.setContentType(multipartFile.getContentType());
    }
    s3Client.putObject(
        new PutObjectRequest(doSpaceBucket, key, multipartFile.getInputStream(), metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));
  }

  @Override
  public List<Image> getImages() {
    List<Image> images = new ArrayList<>();
    List<ImageEntity> existImages = imageRepo.findAll();
    for (int i = 0; i < existImages.size(); i++) {
      images.add(imageMapper.toDTO(existImages.get(i)));
    }
    return images;
  }
}
