package com.smartcampus.mapper.impl;

import com.smartcampus.dto.AnnouncementDTO;
import com.smartcampus.entity.Announcement;
import com.smartcampus.mapper.AnnouncementMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnnouncementMapperImpl implements AnnouncementMapper {

    @Override
    public AnnouncementDTO toDto(Announcement announcement) {
        if (announcement == null) {
            return null;
        }

        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .courseId(announcement.getCourse() != null ? announcement.getCourse().getId() : null)
                .courseName(announcement.getCourse() != null ? announcement.getCourse().getName() : null)
                .courseCode(announcement.getCourse() != null ? announcement.getCourse().getCode() : null)
                .departmentName(announcement.getCourse() != null && announcement.getCourse().getProfessor() != null ? 
                        announcement.getCourse().getProfessor().getDepartment() : null)
                .postedById(announcement.getPostedBy() != null ? announcement.getPostedBy().getId() : null)
                .postedByName(announcement.getPostedBy() != null ? announcement.getPostedBy().getName() : null)
                .postedByEmail(announcement.getPostedBy() != null ? announcement.getPostedBy().getEmail() : null)
                .postedByRole(announcement.getPostedBy() != null ? announcement.getPostedBy().getRole().name() : null)
                .priority(announcement.getPriority())
                .status(announcement.getStatus())
                .isPinned(announcement.getIsPinned())
                .isPublic(announcement.getIsPublic())
                .expiryDate(announcement.getExpiryDate())
                .publishDate(announcement.getPublishDate())
                .summary(announcement.getSummary())
                .category(announcement.getCategory())
                .tags(announcement.getTags())
                .viewCount(announcement.getViewCount())
                .isUrgent(announcement.getIsUrgent())
                .requiresAcknowledgment(announcement.getRequiresAcknowledgment())
                .acknowledgmentDeadline(announcement.getAcknowledgmentDeadline())
                .attachmentName(announcement.getAttachmentName())
                .attachmentUrl(announcement.getAttachmentUrl())
                .attachmentSize(announcement.getAttachmentSize())
                .attachmentType(announcement.getAttachmentType())
                .isScheduled(announcement.getIsScheduled())
                .scheduledDate(announcement.getScheduledDate())
                .isRecurring(announcement.getIsRecurring())
                .recurrencePattern(announcement.getRecurrencePattern())
                .recurrenceEndDate(announcement.getRecurrenceEndDate())
                .isArchived(announcement.getIsArchived())
                .archiveDate(announcement.getArchiveDate())
                .archivedBy(announcement.getArchivedBy())
                .archiveReason(announcement.getArchiveReason())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }

    @Override
    public Announcement toEntity(AnnouncementDTO announcementDTO) {
        if (announcementDTO == null) {
            return null;
        }

        return Announcement.builder()
                .id(announcementDTO.getId())
                .title(announcementDTO.getTitle())
                .content(announcementDTO.getContent())
                .priority(announcementDTO.getPriority())
                .status(announcementDTO.getStatus())
                .isPinned(announcementDTO.getIsPinned())
                .isPublic(announcementDTO.getIsPublic())
                .expiryDate(announcementDTO.getExpiryDate())
                .publishDate(announcementDTO.getPublishDate())
                .summary(announcementDTO.getSummary())
                .category(announcementDTO.getCategory())
                .tags(announcementDTO.getTags())
                .viewCount(announcementDTO.getViewCount())
                .isUrgent(announcementDTO.getIsUrgent())
                .requiresAcknowledgment(announcementDTO.getRequiresAcknowledgment())
                .acknowledgmentDeadline(announcementDTO.getAcknowledgmentDeadline())
                .attachmentName(announcementDTO.getAttachmentName())
                .attachmentUrl(announcementDTO.getAttachmentUrl())
                .attachmentSize(announcementDTO.getAttachmentSize())
                .attachmentType(announcementDTO.getAttachmentType())
                .isScheduled(announcementDTO.getIsScheduled())
                .scheduledDate(announcementDTO.getScheduledDate())
                .isRecurring(announcementDTO.getIsRecurring())
                .recurrencePattern(announcementDTO.getRecurrencePattern())
                .recurrenceEndDate(announcementDTO.getRecurrenceEndDate())
                .isArchived(announcementDTO.getIsArchived())
                .archiveDate(announcementDTO.getArchiveDate())
                .archivedBy(announcementDTO.getArchivedBy())
                .archiveReason(announcementDTO.getArchiveReason())
                .build();
    }

    @Override
    public List<AnnouncementDTO> toDtoList(List<Announcement> announcements) {
        if (announcements == null) {
            return null;
        }

        return announcements.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 