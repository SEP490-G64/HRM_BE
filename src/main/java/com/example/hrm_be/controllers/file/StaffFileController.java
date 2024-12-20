package com.example.hrm_be.controllers.file;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.models.dtos.File;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.FileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/file")
@Tag(name = "Staff-File API")
@SecurityRequirement(name = "Authorization")
public class StaffFileController {
  @Autowired FileService fileService;

  // This method will return all the row in the DB
  @GetMapping("")
  public ResponseEntity<BaseOutput<List<File>>> getFiles() {
    List<File> files = fileService.getFiles();

    BaseOutput<List<File>> response =
        BaseOutput.<List<File>>builder()
            .message(HttpStatus.OK.toString())
            .data(files)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // This method will get file from DO Space
  @GetMapping("/{fileId}")
  public ResponseEntity<BaseOutput<String>> getFile(@PathVariable long fileId) {
    String linkFile = fileService.getFileById(fileId);

    BaseOutput<String> response =
        BaseOutput.<String>builder()
            .message(HttpStatus.OK.toString())
            .data(linkFile)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // This method will save file to DO Space
  @PostMapping("/save")
  public ResponseEntity<BaseOutput<String>> saveFile(
      @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
    String result = fileService.saveFile(file);
    BaseOutput<String> response;
    if (result == null) {
      response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    response =
        BaseOutput.<String>builder()
            .message(HttpStatus.OK.toString())
            .data(result)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // This method will delete file from DO Space
  @DeleteMapping("/{fileId}")
  public ResponseEntity<BaseOutput<Boolean>> deleteById(@PathVariable("fileId") Long fileId)
      throws Exception {
    boolean result = fileService.deleteFile(fileId);
    BaseOutput<Boolean> response;
    if (result) {
      response =
          BaseOutput.<Boolean>builder()
              .message(HttpStatus.OK.toString())
              .data(true)
              .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
              .build();
      return ResponseEntity.ok(response);
    } else {
      response =
          BaseOutput.<Boolean>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
