package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.Image;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {
  long saveFile(MultipartFile multipartFile) throws IOException;

  void deleteFile(Long id) throws Exception;

  List<Image> getImages();
}
