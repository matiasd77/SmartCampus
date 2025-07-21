package com.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private NotificationPriority priority;

    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description")
    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(name = "category")
    private String category;

    @Size(max = 500, message = "Action URL must not exceed 500 characters")
    @Column(name = "action_url")
    private String actionUrl;

    @Size(max = 100, message = "Action text must not exceed 100 characters")
    @Column(name = "action_text")
    private String actionText;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "is_dismissible")
    @Builder.Default
    private Boolean isDismissible = true;

    @Column(name = "is_urgent")
    @Builder.Default
    private Boolean isUrgent = false;

    @Column(name = "is_silent")
    @Builder.Default
    private Boolean isSilent = false;

    @Size(max = 100, message = "Icon must not exceed 100 characters")
    @Column(name = "icon")
    private String icon;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    @Column(name = "color")
    private String color;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Size(max = 100, message = "Read by must not exceed 100 characters")
    @Column(name = "read_by")
    private String readBy;

    @Column(name = "is_archived")
    @Builder.Default
    private Boolean isArchived = false;

    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    @Size(max = 100, message = "Archived by must not exceed 100 characters")
    @Column(name = "archived_by")
    private String archivedBy;

    @Size(max = 500, message = "Archive reason must not exceed 500 characters")
    @Column(name = "archive_reason")
    private String archiveReason;

    @Column(name = "is_system")
    @Builder.Default
    private Boolean isSystem = false;

    @Size(max = 100, message = "Source must not exceed 100 characters")
    @Column(name = "source")
    private String source;

    @Size(max = 200, message = "Source ID must not exceed 200 characters")
    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "is_broadcast")
    @Builder.Default
    private Boolean isBroadcast = false;

    @Column(name = "broadcast_target")
    private String broadcastTarget; // ALL, STUDENTS, PROFESSORS, ADMINS, COURSE_123, etc.

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Size(max = 100, message = "Recurrence pattern must not exceed 100 characters")
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    @Column(name = "is_template")
    @Builder.Default
    private Boolean isTemplate = false;

    @Size(max = 100, message = "Template name must not exceed 100 characters")
    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_variables")
    private String templateVariables; // JSON string for template variables

    @Column(name = "is_scheduled")
    @Builder.Default
    private Boolean isScheduled = false;

    @Column(name = "scheduled_send_date")
    private LocalDateTime scheduledSendDate;

    @Column(name = "is_sent")
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Size(max = 100, message = "Sent by must not exceed 100 characters")
    @Column(name = "sent_by")
    private String sentBy;

    @Column(name = "delivery_method")
    private String deliveryMethod; // IN_APP, EMAIL, SMS, PUSH, etc.

    @Column(name = "delivery_status")
    private String deliveryStatus; // PENDING, SENT, DELIVERED, FAILED, etc.

    @Size(max = 500, message = "Delivery error must not exceed 500 characters")
    @Column(name = "delivery_error")
    private String deliveryError;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "next_retry_date")
    private LocalDateTime nextRetryDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
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
        return retryCount < maxRetries;
    }

    // Helper method to check if notification should be retried
    public boolean shouldRetry() {
        return canRetry() && nextRetryDate != null && LocalDateTime.now().isAfter(nextRetryDate);
    }

    // Helper method to get user info
    public String getUserInfo() {
        if (user != null) {
            return user.getName() + " (" + user.getEmail() + ")";
        }
        return null;
    }

    // Helper method to get user role
    public String getUserRole() {
        if (user != null && user.getRole() != null) {
            return user.getRole().name();
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

    // Helper method to mark as read
    public void markAsRead(String readBy) {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
        this.readBy = readBy;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to mark as unread
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
        this.readBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to archive notification
    public void archive(String archivedBy, String archiveReason) {
        this.isArchived = true;
        this.archiveDate = LocalDateTime.now();
        this.archivedBy = archivedBy;
        this.archiveReason = archiveReason;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to unarchive notification
    public void unarchive() {
        this.isArchived = false;
        this.archiveDate = null;
        this.archivedBy = null;
        this.archiveReason = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to mark as sent
    public void markAsSent(String sentBy) {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
        this.sentBy = sentBy;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to increment retry count
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to set next retry date
    public void setNextRetryDate(LocalDateTime nextRetryDate) {
        this.nextRetryDate = nextRetryDate;
        this.updatedAt = LocalDateTime.now();
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