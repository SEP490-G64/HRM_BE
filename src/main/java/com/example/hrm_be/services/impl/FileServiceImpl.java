package com.example.hrm_be.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.components.FileMapper;
import com.example.hrm_be.models.dtos.File;
import com.example.hrm_be.models.entities.FileEntity;
import com.example.hrm_be.repositories.FileRepository;
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
  @Autowired FileRepository fileRepo;

  @Autowired AmazonS3 s3Client;

  @Autowired FileMapper fileMapper;

  @Value("${hrm-config.s3.bucket:default}")
  private String doSpaceBucket;

  @Override
  public String getFileById(Long id) {
    FileEntity foundImage = fileRepo.findById(id).orElse(null);
    if (foundImage == null) {
      return null;
    }

    return foundImage.getLink();
  }

  @Override
  public long saveFile(MultipartFile multipartFile) {
    String key, extension;
    String imgName = FilenameUtils.removeExtension(multipartFile.getOriginalFilename());
    try {
      extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
      key = getFolderFromFileType(extension) + "/" + imgName + "." + extension;
    } catch (Exception e) {
      extension = "";
      key = multipartFile.getOriginalFilename();
    }
    PutObjectResult result = saveImageToS3(multipartFile, key);
    if (result == null) {
      return -1L; // save fail
    }
    FileEntity image = new FileEntity();
    image.setName(imgName);
    image.setExt(extension);
    image.setCreatedTime(new Timestamp(new Date().getTime()));
    image.setLink(HrmConstant.S3LINK + "/" + key);
    // Map Image DTO to entity and save it to the repository
    FileEntity saveImage =
        Optional.ofNullable(image)
            .map(e -> fileRepo.save(e)) // Save entity to the repository
            .orElse(null); // Return null if the Manufacturer creation fails
    return saveImage != null ? image.getId() : -1L;
  }

  @Override
  public boolean deleteFile(Long fileId) throws Exception {
    Optional<FileEntity> imageOpt = fileRepo.findById(fileId);
    if (imageOpt.get() == null) {
      return false;
    }
    FileEntity image = imageOpt.get();
    String key =
        getFolderFromFileType(image.getExt()) + "/" + image.getName() + "." + image.getExt();
    try {
      s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
      fileRepo.delete(image);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public List<File> getFiles() {
    List<File> files = new ArrayList<>();
    List<FileEntity> existImages = fileRepo.findAll();
    for (FileEntity existImage : existImages) {
      files.add(fileMapper.toDTO(existImage));
    }
    return files;
  }

  private PutObjectResult saveImageToS3(MultipartFile multipartFile, String key) {
    try {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(multipartFile.getInputStream().available());
      if (multipartFile.getContentType() != null && !"".equals(multipartFile.getContentType())) {
        metadata.setContentType(multipartFile.getContentType());
      }
      PutObjectResult test =
          s3Client.putObject(
              new PutObjectRequest(doSpaceBucket, key, multipartFile.getInputStream(), metadata)
                  .withCannedAcl(CannedAccessControlList.PublicRead));
      return test;
    } catch (IOException e) {
      return null;
    }
  }

  private String getFolderFromFileType(String extension) {
    return switch (extension) {
      case "jpg", "png", "jpeg" -> "images";
      case "xlsx", "docx", "pdf" -> "documents";
      default -> "defaults/";
    };
  }
}
