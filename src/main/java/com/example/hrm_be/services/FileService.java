package com.example.hrm_be.services;

import com.example.hrm_be.models.dtos.File;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
  String saveFile(MultipartFile multipartFile) throws IOException;

  boolean deleteFile(Long id) throws Exception;

  List<File> getFiles();

  String getFileById(Long id);
}
