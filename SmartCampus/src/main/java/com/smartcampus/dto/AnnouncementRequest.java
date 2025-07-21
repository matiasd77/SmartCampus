package com.smartcampus.dto;

import com.smartcampus.entity.AnnouncementPriority;
import com.smartcampus.entity.AnnouncementStatus;
import com.smartcampus.entity.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Long courseId;

    private AnnouncementType type;

    private AnnouncementPriority priority;

    private AnnouncementStatus status;

    private String targetAudience;

    private LocalDateTime expiryDate;

    private LocalDateTime publishDate;

    private String semester;

    private String academicYear;

    private String tags;

    private String attachmentUrl;
} 