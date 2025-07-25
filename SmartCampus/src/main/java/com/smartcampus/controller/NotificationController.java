package com.smartcampus.controller;

import com.smartcampus.dto.ApiResponse;
import com.smartcampus.dto.NotificationDTO;
import com.smartcampus.entity.NotificationPriority;
import com.smartcampus.entity.NotificationType;
import com.smartcampus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final com.smartcampus.service.NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get Current User's Notifications",
        description = "Retrieve all notifications for the currently authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserNotifications(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<List<NotificationDTO>>error("Authentication required"));
        }
        
        try {
            Long userId = userService.getUserByEmail(email).getId();
            List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("User notifications retrieved successfully", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<NotificationDTO>>error("Failed to retrieve notifications"));
        }
    }

    @GetMapping("/paginated")
    @Operation(
        summary = "Get Current User's Notifications Paginated",
        description = "Retrieve all notifications for the currently authenticated user with pagination"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getCurrentUserNotificationsPaginated(
            @PageableDefault(size = 20) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<NotificationDTO> notifications = notificationService.getNotificationsByUserIdPaginated(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread")
    @Operation(
        summary = "Get Current User's Unread Notifications",
        description = "Retrieve all unread notifications for the currently authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Unread notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserUnreadNotifications() {
        Long userId = getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndIsRead(userId, false);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get Current User's Unread Notification Count",
        description = "Get the count of unread notifications for the currently authenticated user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Unread count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Count Response",
                    value = "{\"success\": true, \"message\": \"Unread count retrieved successfully\", \"data\": 5}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<Long>> getCurrentUserUnreadCount(@AuthenticationPrincipal String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Long>error("Authentication required"));
        }
        
        try {
            Long userId = userService.getUserByEmail(email).getId();
            Long unreadCount = notificationService.getNotificationCountByUserIdAndIsRead(userId, false);
            return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Long>error("Failed to retrieve unread count"));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get Notification by ID",
        description = "Retrieve a specific notification by its ID (only if it belongs to the current user)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notification retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Notification not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<NotificationDTO>> getNotificationById(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        // Check if the notification belongs to the current user
        Long userId = getCurrentUserId();
        if (!userId.equals(notification.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<NotificationDTO>error("Access denied", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved successfully", notification));
    }

    @PutMapping("/{id}/read")
    @Operation(
        summary = "Mark Notification as Read",
        description = "Mark a specific notification as read (only if it belongs to the current user)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notification marked as read successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Notification marked as read successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Notification not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        // Check if the notification belongs to the current user
        NotificationDTO notification = notificationService.getNotificationById(id);
        Long userId = getCurrentUserId();
        if (!userId.equals(notification.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied", "You can only mark your own notifications as read"));
        }
        
        String currentUserName = getCurrentUserName();
        notificationService.markAsRead(id, currentUserName);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read successfully", null));
    }

    @PutMapping("/{id}/unread")
    @Operation(
        summary = "Mark Notification as Unread",
        description = "Mark a specific notification as unread (only if it belongs to the current user)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notification marked as unread successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"Notification marked as unread successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Notification not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<String>> markAsUnread(@PathVariable Long id) {
        // Check if the notification belongs to the current user
        NotificationDTO notification = notificationService.getNotificationById(id);
        Long userId = getCurrentUserId();
        if (!userId.equals(notification.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied", "You can only mark your own notifications as unread"));
        }
        
        notificationService.markAsUnread(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as unread successfully", null));
    }

    @PutMapping("/read-all")
    @Operation(
        summary = "Mark All Notifications as Read",
        description = "Mark all notifications for the current user as read"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "All notifications marked as read successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"All notifications marked as read successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        Long userId = getCurrentUserId();
        String currentUserName = getCurrentUserName();
        notificationService.markAllAsReadByUserId(userId, currentUserName);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read successfully", null));
    }

    @PutMapping("/unread-all")
    @Operation(
        summary = "Mark All Notifications as Unread",
        description = "Mark all notifications for the current user as unread"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "All notifications marked as unread successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\": true, \"message\": \"All notifications marked as unread successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<String>> markAllAsUnread() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsUnreadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as unread successfully", null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send Notification to User",
        description = "Send a notification to a specific user (Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notification details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class),
                examples = @ExampleObject(
                    name = "Notification Creation",
                    value = "{\"userId\": 1, \"message\": \"Welcome to SmartCampus!\", \"type\": \"WELCOME\", \"priority\": \"NORMAL\", \"title\": \"Welcome Message\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Notification sent successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = "{\"success\": false, \"message\": \"Message is required\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<NotificationDTO>> sendNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent successfully", createdNotification));
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send Broadcast Notification",
        description = "Send a broadcast notification to multiple users (Admin only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Broadcast notification details",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class),
                examples = @ExampleObject(
                    name = "Broadcast Notification",
                    value = "{\"message\": \"System maintenance scheduled\", \"type\": \"SYSTEM_MAINTENANCE\", \"priority\": \"HIGH\", \"title\": \"Maintenance Notice\", \"broadcastTarget\": \"ALL\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Broadcast notification sent successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<NotificationDTO>> sendBroadcastNotification(
            @Valid @RequestBody NotificationDTO notificationDTO,
            @RequestParam String target) {
        notificationDTO.setIsBroadcast(true);
        notificationDTO.setBroadcastTarget(target);
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Broadcast notification sent successfully", createdNotification));
    }

    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send System Notification",
        description = "Send a system notification to a user (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "System notification sent successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> sendSystemNotification(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL") NotificationType type,
            @RequestParam(defaultValue = "NORMAL") NotificationPriority priority) {
        notificationService.sendSystemNotification(userId, message, type, priority);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("System notification sent successfully", null));
    }

    @PostMapping("/system/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Send System Broadcast Notification",
        description = "Send a system broadcast notification (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "System broadcast notification sent successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> sendSystemBroadcastNotification(
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL") NotificationType type,
            @RequestParam(defaultValue = "NORMAL") NotificationPriority priority,
            @RequestParam String target) {
        notificationService.sendSystemBroadcastNotification(message, type, priority, target);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("System broadcast notification sent successfully", null));
    }

    @PostMapping("/debug/create-test-notifications")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Create Test Notifications (Debug)",
        description = "Create sample notifications for the current user for testing purposes"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Test notifications created successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<String>> createTestNotifications() {
        try {
            Long userId = getCurrentUserId();
            String userName = getCurrentUserName();
            
            // Create sample notifications
            notificationService.sendSystemNotification(userId, "Welcome to SmartCampus! This is your first notification.", 
                com.smartcampus.entity.NotificationType.WELCOME, com.smartcampus.entity.NotificationPriority.NORMAL);
            
            notificationService.sendSystemNotification(userId, "Your course registration has been confirmed.", 
                com.smartcampus.entity.NotificationType.COURSE_ENROLLMENT, com.smartcampus.entity.NotificationPriority.HIGH);
            
            notificationService.sendSystemNotification(userId, "New assignment posted in Computer Science 101.", 
                com.smartcampus.entity.NotificationType.ASSIGNMENT_DUE, com.smartcampus.entity.NotificationPriority.NORMAL);
            
            notificationService.sendSystemNotification(userId, "System maintenance scheduled for tomorrow at 2 AM.", 
                com.smartcampus.entity.NotificationType.SYSTEM_MAINTENANCE, com.smartcampus.entity.NotificationPriority.URGENT);
            
            notificationService.sendSystemNotification(userId, "Your profile has been updated successfully.", 
                com.smartcampus.entity.NotificationType.ACCOUNT_UPDATE, com.smartcampus.entity.NotificationPriority.LOW);
            
            return ResponseEntity.ok(ApiResponse.success("Test notifications created successfully for user: " + userName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create test notifications: " + e.getMessage()));
        }
    }

    @DeleteMapping("/debug/cleanup-test-notifications")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Cleanup Test Notifications (Debug)",
        description = "Delete all test notifications from the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Test notifications cleaned up successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> cleanupTestNotifications() {
        try {
            // Delete all notifications that match test patterns
            notificationService.deleteTestNotifications();
            return ResponseEntity.ok(ApiResponse.success("Test notifications cleaned up successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to cleanup test notifications: " + e.getMessage()));
        }
    }

    // Additional user-specific endpoints
    @GetMapping("/type/{type}")
    @Operation(
        summary = "Get Current User's Notifications by Type",
        description = "Retrieve notifications of a specific type for the current user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notifications by type retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserNotificationsByType(@PathVariable NotificationType type) {
        Long userId = getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndType(userId, type);
        return ResponseEntity.ok(ApiResponse.success("Notifications by type retrieved successfully", notifications));
    }

    @GetMapping("/priority/{priority}")
    @Operation(
        summary = "Get Current User's Notifications by Priority",
        description = "Retrieve notifications of a specific priority for the current user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notifications by priority retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserNotificationsByPriority(@PathVariable NotificationPriority priority) {
        Long userId = getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndPriority(userId, priority);
        return ResponseEntity.ok(ApiResponse.success("Notifications by priority retrieved successfully", notifications));
    }

    @GetMapping("/urgent")
    @Operation(
        summary = "Get Current User's Urgent Notifications",
        description = "Retrieve urgent notifications for the current user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Urgent notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserUrgentNotifications() {
        Long userId = getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndIsUrgent(userId, true);
        return ResponseEntity.ok(ApiResponse.success("Urgent notifications retrieved successfully", notifications));
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Get Current User's Recent Notifications",
        description = "Retrieve recent notifications for the current user (last 7 days)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recent notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserRecentNotifications() {
        Long userId = getCurrentUserId();
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndCreatedAtSince(userId, since);
        return ResponseEntity.ok(ApiResponse.success("Recent notifications retrieved successfully", notifications));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search Current User's Notifications",
        description = "Search notifications for the current user by keyword"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Search results retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> searchCurrentUserNotifications(@RequestParam String keyword) {
        Long userId = getCurrentUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserIdAndKeyword(userId, keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", notifications));
    }

    // Admin-only endpoints for system management
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get All Notifications (Admin)",
        description = "Retrieve all notifications in the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "All notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDTO.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(ApiResponse.success("All notifications retrieved successfully", notifications));
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get Notification Statistics (Admin)",
        description = "Get comprehensive notification statistics (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Statistics Response",
                    value = "{\"success\": true, \"message\": \"Statistics retrieved successfully\", \"data\": {\"total\": 100, \"unread\": 25, \"urgent\": 5, \"system\": 10}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<Object>> getNotificationStatistics() {
        Long totalCount = notificationService.getNotificationCountByIsRead(null);
        Long unreadCount = notificationService.getNotificationCountByIsRead(false);
        Long urgentCount = notificationService.getNotificationCountByIsUrgent(true);
        Long systemCount = notificationService.getNotificationCountByIsSystem(true);
        
        var stats = new Object() {
            public final Long total = totalCount;
            public final Long unread = unreadCount;
            public final Long urgent = urgentCount;
            public final Long system = systemCount;
        };
        
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete Notification (Admin)",
        description = "Delete a notification from the system (Admin only)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Notification deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Notification not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin access required"
        )
    })
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully", null));
    }

    // Helper methods
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String email = (String) authentication.getPrincipal();
            try {
                return userService.findByEmail(email).getId();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get user ID for email: " + email, e);
            }
        }
        throw new RuntimeException("User not authenticated");
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String email = (String) authentication.getPrincipal();
            try {
                return userService.findByEmail(email).getName();
            } catch (Exception e) {
                return email; // Fallback to email if name retrieval fails
            }
        }
        return "System";
    }
} 