package com.example.hrm_be.controllers.inboundBatchDetail;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.InboundBatchDetail;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.InboundBatchDetailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/inbound-batch-detail")
@Tag(name = "Staff-Inbound-Batch-Detail API")
@SecurityRequirement(name = "Authorization")
public class StaffInboundBatchDetailController {
  @Autowired InboundBatchDetailService inboundBatchDetailService;

  @PostMapping()
  protected ResponseEntity<BaseOutput<InboundBatchDetail>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InboundBatchDetail inboundBatchDetail) {
    // Validate the request body
    if (inboundBatchDetail == null) {
      BaseOutput<InboundBatchDetail> response =
          BaseOutput.<InboundBatchDetail>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the branch batch
    InboundBatchDetail createdInboundBatchDetail =
        inboundBatchDetailService.create(inboundBatchDetail);

    // Build the response with the created branch data
    BaseOutput<InboundBatchDetail> response =
        BaseOutput.<InboundBatchDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInboundBatchDetail)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Updates an existing Branch
  @PutMapping("")
  protected ResponseEntity<BaseOutput<InboundBatchDetail>> update(
      @RequestBody @NotNull(message = "error.request.body.invalid")
          InboundBatchDetail inboundBatchDetail) {
    // Validate the input
    if (inboundBatchDetail == null) {
      BaseOutput<InboundBatchDetail> response =
          BaseOutput.<InboundBatchDetail>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the branch
    InboundBatchDetail updateInboundBatchDetail =
        inboundBatchDetailService.update(inboundBatchDetail);

    // Build the response with the updated branch data
    BaseOutput<InboundBatchDetail> response =
        BaseOutput.<InboundBatchDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInboundBatchDetail)
            .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // Deletes a Branch by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the branch by ID
    inboundBatchDetailService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }
}
