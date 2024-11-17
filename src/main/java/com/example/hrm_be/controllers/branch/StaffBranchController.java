package com.example.hrm_be.controllers.branch;

import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Branch;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.BranchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/branch")
@Tag(name = "Staff-Branches API")
@SecurityRequirement(name = "Authorization")
public class StaffBranchController {
  // Injected service for handling branch operations
  private final BranchService branchService;

  // Retrieves a paginated list of Branch entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Branch>>> getByPaging() {
    Page<Branch> branchPage = branchService.getByPaging(0, Integer.MAX_VALUE, "id", "", null, null);

    // Build the response with pagination details
    BaseOutput<List<Branch>> response =
        BaseOutput.<List<Branch>>builder()
            .message(HttpStatus.OK.toString())
            .data(branchPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }
}
