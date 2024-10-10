package com.example.hrm_be.controllers.image;

import com.example.hrm_be.models.dtos.Image;
import com.example.hrm_be.services.ImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/image")
@Tag(name = "Admin-Users API")
@SecurityRequirement(name = "Authorization")
public class StaffImageController {
  @Autowired ImageService imageService;

  @GetMapping("")
  public List<Image> getImages() {
    return imageService.getImages();
  }

  @PutMapping("/save")
  public void saveImage(@RequestParam(value = "image", required = true) MultipartFile image)
      throws IOException {
    imageService.saveFile(image);
  }

  @DeleteMapping("/delete/{fileId}")
  public void deleteById(@PathVariable("fileId") Long fileId) throws Exception {
    imageService.deleteFile(fileId);
  }
}
