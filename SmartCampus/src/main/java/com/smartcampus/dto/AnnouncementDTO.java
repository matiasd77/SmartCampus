package com.smartcampus.dto;

import com.smartcampus.entity.AnnouncementPriority;
import com.smartcampus.entity.AnnouncementStatus;
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
public class AnnouncementDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;

    private Long courseId;

    // Course details for response
    private String courseName;
    private String courseCode;
    private String departmentName;

    @NotNull(message = "Posted by is required")
    private Long postedById;

    // Posted by details for response
    private String postedByName;
    private String postedByEmail;
    private String postedByRole;

    private AnnouncementPriority priority;

    private AnnouncementStatus status;

    private Boolean isPinned;

    private Boolean isPublic;

    private LocalDateTime expiryDate;

    private LocalDateTime publishDate;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 200, message = "Tags must not exceed 200 characters")
    private String tags;

    private Integer viewCount;

    private Boolean isUrgent;

    private Boolean requiresAcknowledgment;

    private LocalDateTime acknowledgmentDeadline;

    @Size(max = 100, message = "Attachment name must not exceed 100 characters")
    private String attachmentName;

    @Size(max = 500, message = "Attachment URL must not exceed 500 characters")
    private String attachmentUrl;

    private Long attachmentSize;

    @Size(max = 50, message = "Attachment type must not exceed 50 characters")
    private String attachmentType;

    private Boolean isScheduled;

    private LocalDateTime scheduledDate;

    private Boolean isRecurring;

    @Size(max = 100, message = "Recurrence pattern must not exceed 100 characters")
    private String recurrencePattern;

    private LocalDateTime recurrenceEndDate;

    private Boolean isArchived;

    private LocalDateTime archiveDate;

    @Size(max = 100, message = "Archived by must not exceed 100 characters")
    private String archivedBy;

    @Size(max = 500, message = "Archive reason must not exceed 500 characters")
    private String archiveReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Helper method to check if announcement is active
    public boolean isActive() {
        if (status != null && status == AnnouncementStatus.INACTIVE) {
            return false;
        }
        if (expiryDate != null && LocalDateTime.now().isAfter(expiryDate)) {
            return false;
        }
        if (publishDate != null && LocalDateTime.now().isBefore(publishDate)) {
            return false;
        }
        return !Boolean.TRUE.equals(isArchived);
    }

    // Helper method to check if announcement is expired
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    // Helper method to check if announcement is published
    public boolean isPublished() {
        return publishDate == null || LocalDateTime.now().isAfter(publishDate);
    }

    // Helper method to check if announcement is urgent
    public boolean isUrgent() {
        return Boolean.TRUE.equals(isUrgent) || AnnouncementPriority.URGENT.equals(priority);
    }

    // Helper method to check if announcement is pinned
    public boolean isPinned() {
        return Boolean.TRUE.equals(isPinned);
    }

    // Helper method to check if announcement is public
    public boolean isPublic() {
        return Boolean.TRUE.equals(isPublic);
    }

    // Helper method to check if announcement requires acknowledgment
    public boolean requiresAcknowledgment() {
        return Boolean.TRUE.equals(requiresAcknowledgment);
    }

    // Helper method to check if acknowledgment deadline has passed
    public boolean isAcknowledgmentDeadlinePassed() {
        return acknowledgmentDeadline != null && LocalDateTime.now().isAfter(acknowledgmentDeadline);
    }

    // Helper method to check if announcement is scheduled
    public boolean isScheduled() {
        return Boolean.TRUE.equals(isScheduled);
    }

    // Helper method to check if announcement is recurring
    public boolean isRecurring() {
        return Boolean.TRUE.equals(isRecurring);
    }

    // Helper method to check if announcement is archived
    public boolean isArchived() {
        return Boolean.TRUE.equals(isArchived);
    }

    // Helper method to get course info
    public String getCourseInfo() {
        if (courseCode != null && courseName != null) {
            return courseCode + " - " + courseName;
        }
        return null;
    }

    // Helper method to get truncated content for preview
    public String getContentPreview(int maxLength) {
        if (content == null) {
            return null;
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    // Helper method to get tags as array
    public String[] getTagsArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    // Helper method to check if announcement has attachment
    public boolean hasAttachment() {
        return attachmentUrl != null && !attachmentUrl.trim().isEmpty();
    }

    // Helper method to get file size in human readable format
    public String getAttachmentSizeFormatted() {
        if (attachmentSize == null) {
            return null;
        }
        
        long bytes = attachmentSize;
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
} 