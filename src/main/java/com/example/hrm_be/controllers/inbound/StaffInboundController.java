package com.example.hrm_be.controllers.inbound;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.InboundStatus;
import com.example.hrm_be.commons.enums.InboundType;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Inbound;
import com.example.hrm_be.models.dtos.ProductInbound;
import com.example.hrm_be.models.requests.CreateInboundRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.models.responses.InboundDetail;
import com.example.hrm_be.services.FileService;
import com.example.hrm_be.services.InboundService;
import com.example.hrm_be.services.UserService;
import com.example.hrm_be.utils.WplUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staff/inbound")
@Tag(name = "Staff-Inbounds API")
@SecurityRequirement(name = "Authorization")
public class StaffInboundController {
  private final InboundService inboundService;

  private final WplUtil wplUtil;
  private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
  private final UserService userService;
  private final RedisTemplate<String, Object> redisTemplate;
  private final FileService fileService;

  // GET: /api/v1/staff/inbound
  // Retrieves a paginated list of Inbound entities
  // with optional sorting and searching by name or location and filter type
  @GetMapping("")
  protected ResponseEntity<BaseOutput<List<Inbound>>> getByPaging(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "") String keyword) {
    Page<Inbound> InboundPage = inboundService.getByPaging(page, size, sortBy);

    // Build the response with pagination details
    BaseOutput<List<Inbound>> response =
        BaseOutput.<List<Inbound>>builder()
            .message(HttpStatus.OK.toString())
            .totalPages(InboundPage.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .total(InboundPage.getTotalElements())
            .data(InboundPage.getContent())
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // GET: /api/v1/staff/inbound/{id}
  // Retrieves a Inbound by its ID
  @GetMapping("/{id}")
  protected ResponseEntity<BaseOutput<InboundDetail>> getById(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<InboundDetail> response =
          BaseOutput.<InboundDetail>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fetch Inbound by ID
    InboundDetail Inbound = inboundService.getById(id);

    // Build the response with the found Inbound data
    BaseOutput<InboundDetail> response =
        BaseOutput.<InboundDetail>builder()
            .message(HttpStatus.OK.toString())
            .data(Inbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // POST: /api/v1/staff/inbound
  // Creates a new Inbound
  @PostMapping()
  protected ResponseEntity<BaseOutput<Inbound>> create(
      @RequestBody @NotNull(message = "error.request.body.invalid") Inbound Inbound) {
    // Validate the request body
    if (Inbound == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
              .status(ResponseStatus.FAILED)
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Create the Inbound
    Inbound createdInbound = inboundService.create(Inbound);

    // Build the response with the created Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(createdInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/inbound/{id}
  // Updates an existing Inbound
  @PutMapping("/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> update(
      @PathVariable("id") Long id,
      @RequestBody @NotNull(message = "error.request.body.invalid") Inbound Inbound) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Set the ID for the Inbound to update
    Inbound.setId(id);

    // Update the Inbound
    Inbound updateInbound = inboundService.update(Inbound);

    // Build the response with the updated Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // PUT: /api/v1/staff/inbound/approve/{id}
  // Approve an Inbound by Manager
  @PutMapping("/approve/{id}")
  protected ResponseEntity<BaseOutput<Inbound>> approve(
      @PathVariable("id") Long id, @RequestParam boolean accept) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<Inbound> response =
          BaseOutput.<Inbound>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Update the Inbound
    Inbound updateInbound = inboundService.approve(id, accept);

    // Build the response with the updated Inbound data
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(updateInbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  // DELETE: /api/v1/staff/inbound/{id}
  // Deletes a Inbound by ID
  @DeleteMapping("/{id}")
  protected ResponseEntity<BaseOutput<String>> delete(@PathVariable("id") Long id) {
    // Validate the path variable ID
    if (id <= 0 || id == null) {
      BaseOutput<String> response =
          BaseOutput.<String>builder()
              .status(ResponseStatus.FAILED)
              .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
              .build();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Delete the Inbound by ID
    inboundService.delete(id);

    // Build the response indicating success
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }

  @PostMapping("/create-init-inbound")
  public ResponseEntity<BaseOutput<Inbound>> createInnitInbound(@RequestParam String type) {
    // Check if the type is valid using the exists method
    if (!InboundType.exists(type)) {
      // Return a bad request if the type is invalid
      return ResponseEntity.badRequest()
          .body(
              BaseOutput.<Inbound>builder()
                  .status(ResponseStatus.FAILED)
                  .message("Invalid Inbound Type")
                  .build());
    }
    Inbound inbound = inboundService.createInnitInbound(InboundType.parse(type));
    return ResponseEntity.ok(
        BaseOutput.<Inbound>builder().data(inbound).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/submit-draft")
  public ResponseEntity<BaseOutput<Inbound>> submitDraft(
      @RequestBody CreateInboundRequest request) {
    Inbound inbound = inboundService.saveInbound(request);
    return ResponseEntity.ok(
        BaseOutput.<Inbound>builder().data(inbound).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/add-product")
  public ResponseEntity<BaseOutput<String>> addProductToOrder(
      HttpSession session, @RequestBody ProductInbound product) {

    String sessionName = userService.getAuthenticatedUserEmail();
    List<ProductInbound> products = (List<ProductInbound>) session.getAttribute(sessionName);
    if (products == null) {
      products = new ArrayList<>();
    }
    products.add(product);
    session.setAttribute(sessionName, products);
    return ResponseEntity.ok(
        BaseOutput.<String>builder()
            .data(HttpStatus.OK.toString())
            .status(ResponseStatus.SUCCESS)
            .build());
  }

  @GetMapping("/get-products")
  public List<ProductInbound> getProductsFromOrder(HttpSession session) {
    String sessionName = userService.getAuthenticatedUserEmail();
    List<ProductInbound> products = (List<ProductInbound>) session.getAttribute(sessionName);
    return products != null ? products : new ArrayList<>();
  }


  @GetMapping("/getSessionId")
  protected ResponseEntity<BaseOutput<String>> getSessionId(HttpSession session) {
    String sessionId = session.getId();

    // Return the InnitInbound object (either new or from session)
    return ResponseEntity.ok(
        BaseOutput.<String>builder().data(sessionId).status(ResponseStatus.SUCCESS).build());
  }

  @PostMapping("/clear-session")
  public void clearAllSessions() {
    // Assuming session keys are prefixed with "spring:session:"
    Set<String> sessionKeys = redisTemplate.keys("spring:session:*");

    if (sessionKeys != null) {
      redisTemplate.delete(sessionKeys);
    }
  }

  @PostMapping("/download")
  public ResponseEntity<InputStreamResource> encodeAndDownload(@RequestBody List<Object> userList)
      throws IOException {
    // Call the service to encode the list of User objects
    String encodedJson = fileService.encodeJsonToFile(userList);

    // Create a byte array from the encoded string
    ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(encodedJson.getBytes(StandardCharsets.UTF_8));

    // Set headers to indicate file download
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=encodedData.txt");

    // Return the file as a downloadable response
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(encodedJson.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new InputStreamResource(byteArrayInputStream));
  }

  @PutMapping("/{id}/update-status")
  public ResponseEntity<BaseOutput<String>> updateStatus(
      @RequestParam String type, @PathVariable(name = "id") Long id) {
    inboundService.updateInboundStatus(InboundStatus.valueOf(type), id);
    return ResponseEntity.ok(BaseOutput.<String>builder().status(ResponseStatus.SUCCESS).build());
  }

  @PutMapping("/{id}/submit")
  public ResponseEntity<BaseOutput<Inbound>> submitToSystem(@PathVariable(name = "id") Long id) {
    Inbound inbound = inboundService.submitInboundToSystem(id);
    BaseOutput<Inbound> response =
        BaseOutput.<Inbound>builder()
            .message(HttpStatus.OK.toString())
            .data(inbound)
            .status(ResponseStatus.SUCCESS)
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/decode-file")
  public ResponseEntity<List<ProductInbound>> decodeFile(
      @RequestParam(value = "file") MultipartFile file) {
    try {

      String encodedJson = new String(file.getBytes(), StandardCharsets.UTF_8);

      // Decode the Base64 encoded content to a list of ProductInbound objects
      List<ProductInbound> decodedProducts = fileService.decodeJsonList(encodedJson);

      // Return the decoded list of products
      return ResponseEntity.ok(decodedProducts);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(null);
    }
  }
  //
  //  @PostMapping("submit-inbound")
  //  public  ResponseEntity<List<ProductInbound>> submit( )
  //  {
  //
  //  }

}
