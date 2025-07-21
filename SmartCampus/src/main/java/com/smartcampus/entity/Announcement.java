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
import com.smartcampus.entity.Role;

@Entity
@Table(name = "announcements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    @NotNull(message = "Posted by is required")
    private User postedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private AnnouncementPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AnnouncementStatus status;

    @Column(name = "is_pinned")
    private Boolean isPinned;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Size(max = 500, message = "Summary must not exceed 500 characters")
    @Column(name = "summary")
    private String summary;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(name = "category")
    private String category;

    @Size(max = 200, message = "Tags must not exceed 200 characters")
    @Column(name = "tags")
    private String tags;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "is_urgent")
    private Boolean isUrgent;

    @Column(name = "requires_acknowledgment")
    private Boolean requiresAcknowledgment;

    @Column(name = "acknowledgment_deadline")
    private LocalDateTime acknowledgmentDeadline;

    @Size(max = 100, message = "Attachment name must not exceed 100 characters")
    @Column(name = "attachment_name")
    private String attachmentName;

    @Size(max = 500, message = "Attachment URL must not exceed 500 characters")
    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Size(max = 50, message = "Attachment type must not exceed 50 characters")
    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "is_scheduled")
    private Boolean isScheduled;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "is_recurring")
    private Boolean isRecurring;

    @Size(max = 100, message = "Recurrence pattern must not exceed 100 characters")
    @Column(name = "recurrence_pattern")
    private String recurrencePattern;

    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    @Column(name = "is_archived")
    private Boolean isArchived;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    @Size(max = 100, message = "Archived by must not exceed 100 characters")
    @Column(name = "archived_by")
    private String archivedBy;

    @Size(max = 500, message = "Archive reason must not exceed 500 characters")
    @Column(name = "archive_reason")
    private String archiveReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
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
        if (course != null) {
            return course.getCode() + " - " + course.getName();
        }
        return null;
    }

    // Helper method to get posted by info
    public String getPostedByInfo() {
        if (postedBy != null) {
            return postedBy.getName();
        }
        return null;
    }

    // Helper method to get professor info if posted by professor
    public String getProfessorInfo() {
        if (postedBy != null && postedBy.getRole() == Role.PROFESSOR) {
            return postedBy.getName();
        }
        return null;
    }

    // Helper method to get admin info if posted by admin
    public String getAdminInfo() {
        if (postedBy != null && postedBy.getRole() == Role.ADMIN) {
            return postedBy.getName();
        }
        return null;
    }

    // Helper method to get department info
    public String getDepartmentInfo() {
        if (course != null && course.getProfessor() != null) {
            return course.getProfessor().getDepartment();
        }
        return null;
    }

    // Helper method to get semester info
    public String getSemesterInfo() {
        if (course != null) {
            String semester = course.getSemester();
            Integer year = course.getAcademicYear();
            if (semester != null && year != null) {
                return semester + " " + year;
            }
        }
        return null;
    }

    // Helper method to increment view count
    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 0;
        }
        viewCount++;
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