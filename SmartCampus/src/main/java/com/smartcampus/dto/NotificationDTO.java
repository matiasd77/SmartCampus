package com.smartcampus.dto;

import com.smartcampus.entity.NotificationPriority;
import com.smartcampus.entity.NotificationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    // User details for response
    private String userName;
    private String userEmail;
    private String userRole;

    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String message;

    private Boolean isRead;

    private NotificationType type;

    private NotificationPriority priority;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 500, message = "Action URL must not exceed 500 characters")
    private String actionUrl;

    @Size(max = 100, message = "Action text must not exceed 100 characters")
    private String actionText;

    private LocalDateTime expiryDate;

    private LocalDateTime scheduledDate;

    private Boolean isDismissible;

    private Boolean isUrgent;

    private Boolean isSilent;

    @Size(max = 100, message = "Icon must not exceed 100 characters")
    private String icon;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    private LocalDateTime readAt;

    @Size(max = 100, message = "Read by must not exceed 100 characters")
    private String readBy;

    private Boolean isArchived;

    private LocalDateTime archiveDate;

    @Size(max = 100, message = "Archived by must not exceed 100 characters")
    private String archivedBy;

    @Size(max = 500, message = "Archive reason must not exceed 500 characters")
    private String archiveReason;

    private Boolean isSystem;

    @Size(max = 100, message = "Source must not exceed 100 characters")
    private String source;

    @Size(max = 200, message = "Source ID must not exceed 200 characters")
    private String sourceId;

    private Boolean isBroadcast;

    private String broadcastTarget;

    private Boolean isRecurring;

    @Size(max = 100, message = "Recurrence pattern must not exceed 100 characters")
    private String recurrencePattern;

    private LocalDateTime recurrenceEndDate;

    private Boolean isTemplate;

    @Size(max = 100, message = "Template name must not exceed 100 characters")
    private String templateName;

    private String templateVariables;

    private Boolean isScheduled;

    private LocalDateTime scheduledSendDate;

    private Boolean isSent;

    private LocalDateTime sentAt;

    @Size(max = 100, message = "Sent by must not exceed 100 characters")
    private String sentBy;

    private String deliveryMethod;

    private String deliveryStatus;

    @Size(max = 500, message = "Delivery error must not exceed 500 characters")
    private String deliveryError;

    private Integer retryCount;

    private Integer maxRetries;

    private LocalDateTime nextRetryDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if notification is read
    public boolean isRead() {
        return Boolean.TRUE.equals(isRead);
    }

    // Helper method to check if notification is unread
    public boolean isUnread() {
        return !Boolean.TRUE.equals(isRead);
    }

    // Helper method to check if notification is expired
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    // Helper method to check if notification is scheduled
    public boolean isScheduled() {
        return Boolean.TRUE.equals(isScheduled);
    }

    // Helper method to check if notification is ready to be sent
    public boolean isReadyToSend() {
        if (!Boolean.TRUE.equals(isScheduled)) {
            return true;
        }
        return scheduledSendDate != null && LocalDateTime.now().isAfter(scheduledSendDate);
    }

    // Helper method to check if notification is urgent
    public boolean isUrgent() {
        return Boolean.TRUE.equals(isUrgent) || NotificationPriority.URGENT.equals(priority);
    }

    // Helper method to check if notification is dismissible
    public boolean isDismissible() {
        return Boolean.TRUE.equals(isDismissible);
    }

    // Helper method to check if notification is silent
    public boolean isSilent() {
        return Boolean.TRUE.equals(isSilent);
    }

    // Helper method to check if notification is archived
    public boolean isArchived() {
        return Boolean.TRUE.equals(isArchived);
    }

    // Helper method to check if notification is system notification
    public boolean isSystem() {
        return Boolean.TRUE.equals(isSystem);
    }

    // Helper method to check if notification is broadcast
    public boolean isBroadcast() {
        return Boolean.TRUE.equals(isBroadcast);
    }

    // Helper method to check if notification is recurring
    public boolean isRecurring() {
        return Boolean.TRUE.equals(isRecurring);
    }

    // Helper method to check if notification is template
    public boolean isTemplate() {
        return Boolean.TRUE.equals(isTemplate);
    }

    // Helper method to check if notification is sent
    public boolean isSent() {
        return Boolean.TRUE.equals(isSent);
    }

    // Helper method to check if notification can be retried
    public boolean canRetry() {
        return retryCount != null && maxRetries != null && retryCount < maxRetries;
    }

    // Helper method to check if notification should be retried
    public boolean shouldRetry() {
        return canRetry() && nextRetryDate != null && LocalDateTime.now().isAfter(nextRetryDate);
    }

    // Helper method to get user info
    public String getUserInfo() {
        if (userName != null && userEmail != null) {
            return userName + " (" + userEmail + ")";
        }
        return null;
    }

    // Helper method to get notification age in minutes
    public Long getAgeInMinutes() {
        if (createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    // Helper method to get notification age in hours
    public Long getAgeInHours() {
        if (createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }

    // Helper method to get notification age in days
    public Long getAgeInDays() {
        if (createdAt == null) {
            return null;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }

    // Helper method to get time since read in minutes
    public Long getTimeSinceReadInMinutes() {
        if (readAt == null) {
            return null;
        }
        return java.time.Duration.between(readAt, LocalDateTime.now()).toMinutes();
    }

    // Helper method to get time since read in hours
    public Long getTimeSinceReadInHours() {
        if (readAt == null) {
            return null;
        }
        return java.time.Duration.between(readAt, LocalDateTime.now()).toHours();
    }

    // Helper method to get time since read in days
    public Long getTimeSinceReadInDays() {
        if (readAt == null) {
            return null;
        }
        return java.time.Duration.between(readAt, LocalDateTime.now()).toDays();
    }

    // Helper method to get formatted age
    public String getFormattedAge() {
        Long ageInMinutes = getAgeInMinutes();
        if (ageInMinutes == null) {
            return "Unknown";
        }
        
        if (ageInMinutes < 60) {
            return ageInMinutes + " minute" + (ageInMinutes == 1 ? "" : "s") + " ago";
        }
        
        Long ageInHours = getAgeInHours();
        if (ageInHours < 24) {
            return ageInHours + " hour" + (ageInHours == 1 ? "" : "s") + " ago";
        }
        
        Long ageInDays = getAgeInDays();
        return ageInDays + " day" + (ageInDays == 1 ? "" : "s") + " ago";
    }

    // Helper method to get formatted time since read
    public String getFormattedTimeSinceRead() {
        Long timeInMinutes = getTimeSinceReadInMinutes();
        if (timeInMinutes == null) {
            return "Not read";
        }
        
        if (timeInMinutes < 60) {
            return timeInMinutes + " minute" + (timeInMinutes == 1 ? "" : "s") + " ago";
        }
        
        Long timeInHours = getTimeSinceReadInHours();
        if (timeInHours < 24) {
            return timeInHours + " hour" + (timeInHours == 1 ? "" : "s") + " ago";
        }
        
        Long timeInDays = getTimeSinceReadInDays();
        return timeInDays + " day" + (timeInDays == 1 ? "" : "s") + " ago";
    }
} 