package com.example.hrm_be.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.hrm_be.components.ImageMapper;
import com.example.hrm_be.models.dtos.Image;
import com.example.hrm_be.models.entities.ImageEntity;
import com.example.hrm_be.repositories.ImageRepository;
import com.example.hrm_be.services.FileService;
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
public class FileServiceImpl implements FileService {
  @Autowired ImageRepository imageRepo;

  @Autowired AmazonS3 s3Client;

  @Autowired ImageMapper imageMapper;

  @Value("${hrm-config.s3.bucket:default}")
  private String doSpaceBucket;

  @Override
  public byte[] getFileById(Long id) {
    ImageEntity foundImage = imageRepo.findById(id).orElse(null);
    if (foundImage == null) {
      return null;
    }

    String folder = getFolderFromFileType(foundImage.getExt());
    String key = folder + foundImage.getName() + "." + foundImage.getExt();
    return getImageFromS3(key);
  }

  @Override
  public long saveFile(MultipartFile multipartFile) {
    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    String imgName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
    String key = getFolderFromFileType(extension) + imgName + "." + extension;
    PutObjectResult result = saveImageToS3(multipartFile, key);
    if (result == null) {
      return -1L; // save fail
    }
    ImageEntity image = new ImageEntity();
    image.setName(imgName);
    image.setExt(extension);
    image.setCreatedTime(new Timestamp(new Date().getTime()));
    // Map Image DTO to entity and save it to the repository
    ImageEntity saveImage =
        Optional.ofNullable(image)
            .map(e -> imageRepo.save(e)) // Save entity to the repository
            .orElse(null); // Return null if the Manufacturer creation fails
    return saveImage != null ? image.getId() : -1L;
  }

  @Override
  public boolean deleteFile(Long fileId) throws Exception {
    Optional<ImageEntity> imageOpt = imageRepo.findById(fileId);
    if (imageOpt.get() == null) {
      return false;
    }
    ImageEntity image = imageOpt.get();
    String key = getFolderFromFileType(image.getExt()) + image.getName() + "." + image.getExt();
    try {
      s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
      imageRepo.delete(image);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public List<Image> getFiles() {
    List<Image> images = new ArrayList<>();
    List<ImageEntity> existImages = imageRepo.findAll();
    for (ImageEntity existImage : existImages) {
      images.add(imageMapper.toDTO(existImage));
    }
    return images;
  }

  private PutObjectResult saveImageToS3(MultipartFile multipartFile, String key) {
    try {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(multipartFile.getInputStream().available());
      if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
        metadata.setContentType(multipartFile.getContentType());
      }
      return s3Client.putObject(
          new PutObjectRequest(doSpaceBucket, key, multipartFile.getInputStream(), metadata)
              .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      return null;
    }
  }

  private byte[] getImageFromS3(String key) {
    try {
      S3Object image = s3Client.getObject(new GetObjectRequest(doSpaceBucket, key));
      return image.getObjectContent().readAllBytes();
    } catch (IOException e) {
      return null;
    }
  }

  private String getFolderFromFileType(String extension) {
    return switch (extension) {
      case "jpg", "png", "jpeg" -> "images/";
      case "xlsx", "docx", "pdf" -> "documents/";
      default -> "defaults/";
    };
  }
}
