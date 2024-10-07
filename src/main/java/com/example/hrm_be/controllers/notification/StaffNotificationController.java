package com.example.hrm_be.controllers.notification;

import com.example.hrm_be.commons.constants.HrmConstant;
import com.example.hrm_be.commons.enums.ResponseStatus;
import com.example.hrm_be.models.dtos.Notification;
import com.example.hrm_be.models.requests.notification.NotificationCreateRequest;
import com.example.hrm_be.models.requests.notification.NotificationUpdateRequest;
import com.example.hrm_be.models.responses.BaseOutput;
import com.example.hrm_be.services.NotificationService;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/v1/staff/notification")
public class StaffNotificationController {
    private final NotificationService notificationService;

    // Retrieves a paginated list of notification entities
    // with optional sorting and searching by name or location and filter type
    @GetMapping("")
    protected ResponseEntity<BaseOutput<List<Notification>>> getByPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "") String keyword) {
        Page<Notification> notificationPage = notificationService.getByPaging(page, size, sortBy);

        // Build the response with pagination details
        BaseOutput<List<Notification>> response =
                BaseOutput.<List<Notification>>builder()
                        .message(HttpStatus.OK.toString())
                        .totalPages(notificationPage.getTotalPages())
                        .currentPage(page)
                        .pageSize(size)
                        .total(notificationPage.getTotalElements())
                        .data(notificationPage.getContent())
                        .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                        .build();
        return ResponseEntity.ok(response);
    }

    // Retrieves a notification by its ID
    @GetMapping("/{id}")
    protected ResponseEntity<BaseOutput<Notification>> getById(@PathVariable("id") Long id) {
        // Validate the path variable ID
        if (id <= 0 || id == null) {
            BaseOutput<Notification> response =
                    BaseOutput.<Notification>builder()
                            .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
                            .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
                            .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Fetch notification by ID
        Notification notification = notificationService.getById(id);

        // Build the response with the found notification data
        BaseOutput<Notification> response =
                BaseOutput.<Notification>builder()
                        .message(HttpStatus.OK.toString())
                        .data(notification)
                        .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                        .build();
        return ResponseEntity.ok(response);
    }

    // Creates a new notification
    @PostMapping()
    protected ResponseEntity<BaseOutput<Notification>> create(
            @RequestBody @NotNull(message = "error.request.body.invalid") NotificationCreateRequest notification) {
        // Validate the request body
        if (notification == null) {
            BaseOutput<Notification> response =
                    BaseOutput.<Notification>builder()
                            .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_BODY))
                            .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
                            .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Create the notification
        Notification creatednotification = notificationService.create(notification);

        // Build the response with the created notification data
        BaseOutput<Notification> response =
                BaseOutput.<Notification>builder()
                        .message(HttpStatus.OK.toString())
                        .data(creatednotification)
                        .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                        .build();
        return ResponseEntity.ok(response);
    }

    // Updates an existing notification
    @PutMapping("/{id}")
    protected ResponseEntity<BaseOutput<Notification>> update(
            @PathVariable("id") Long id,
            @RequestBody @NotNull(message = "error.request.body.invalid") NotificationUpdateRequest notification) {
        // Validate the path variable ID
        if (id <= 0 || id == null) {
            BaseOutput<Notification> response =
                    BaseOutput.<Notification>builder()
                            .status(com.example.hrm_be.commons.enums.ResponseStatus.FAILED)
                            .errors(List.of(HrmConstant.ERROR.REQUEST.INVALID_PATH_VARIABLE))
                            .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Set the ID for the notification to update
        notification.setId(id);

        // Update the notification
        Notification updatenotification = notificationService.update(notification);

        // Build the response with the updated notification data
        BaseOutput<Notification> response =
                BaseOutput.<Notification>builder()
                        .message(HttpStatus.OK.toString())
                        .data(updatenotification)
                        .status(com.example.hrm_be.commons.enums.ResponseStatus.SUCCESS)
                        .build();
        return ResponseEntity.ok(response);
    }

    // Deletes a notification by ID
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

        // Delete the notification by ID
        notificationService.delete(id);

        // Build the response indicating success
        return ResponseEntity.ok(
                BaseOutput.<String>builder()
                        .data(HttpStatus.OK.toString())
                        .status(ResponseStatus.SUCCESS)
                        .build());
    }
}
