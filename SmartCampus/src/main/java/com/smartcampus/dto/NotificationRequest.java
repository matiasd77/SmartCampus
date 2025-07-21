package com.smartcampus.dto;

import com.smartcampus.entity.NotificationPriority;
import com.smartcampus.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Recipient IDs are required")
    private List<Long> recipientIds;

    private Long senderId;

    private NotificationType type;

    private NotificationPriority priority;

    private String relatedEntityType;

    private Long relatedEntityId;

    private String actionUrl;

    private String icon;

    private LocalDateTime expiryDate;
} 