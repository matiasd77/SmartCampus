package com.smartcampus.service.impl;

import com.smartcampus.dto.AnnouncementDTO;
import com.smartcampus.entity.Announcement;
import com.smartcampus.entity.AnnouncementPriority;
import com.smartcampus.entity.AnnouncementStatus;
import com.smartcampus.entity.Course;
import com.smartcampus.entity.User;
import com.smartcampus.exception.AnnouncementNotFoundException;
import com.smartcampus.exception.CourseNotFoundException;
import com.smartcampus.exception.UserNotFoundException;
import com.smartcampus.mapper.AnnouncementMapper;
import com.smartcampus.repository.AnnouncementRepository;
import com.smartcampus.repository.CourseRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AnnouncementMapper announcementMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAllAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAll();
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAllAnnouncementsPaginated(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findAll(pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementDTO getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(id));
        return announcementMapper.toDto(announcement);
    }

    @Override
    public AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO) {
        // Validate course exists if provided
        Course course = null;
        if (announcementDTO.getCourseId() != null) {
            course = courseRepository.findById(announcementDTO.getCourseId())
                    .orElseThrow(() -> CourseNotFoundException.withId(announcementDTO.getCourseId()));
        }

        // Validate posted by user exists
        User postedBy = userRepository.findById(announcementDTO.getPostedById())
                .orElseThrow(() -> new UserNotFoundException(announcementDTO.getPostedById()));

        Announcement announcement = announcementMapper.toEntity(announcementDTO);
        announcement.setCourse(course);
        announcement.setPostedBy(postedBy);
        
        // Set default values
        if (announcement.getStatus() == null) {
            announcement.setStatus(AnnouncementStatus.ACTIVE);
        }
        if (announcement.getPriority() == null) {
            announcement.setPriority(AnnouncementPriority.NORMAL);
        }
        if (announcement.getIsPinned() == null) {
            announcement.setIsPinned(false);
        }
        if (announcement.getIsPublic() == null) {
            announcement.setIsPublic(true);
        }
        if (announcement.getIsUrgent() == null) {
            announcement.setIsUrgent(false);
        }
        if (announcement.getIsArchived() == null) {
            announcement.setIsArchived(false);
        }
        if (announcement.getIsScheduled() == null) {
            announcement.setIsScheduled(false);
        }
        if (announcement.getIsRecurring() == null) {
            announcement.setIsRecurring(false);
        }
        if (announcement.getRequiresAcknowledgment() == null) {
            announcement.setRequiresAcknowledgment(false);
        }
        if (announcement.getViewCount() == null) {
            announcement.setViewCount(0);
        }
        
        // Set audit fields
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return announcementMapper.toDto(savedAnnouncement);
    }

    @Override
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO announcementDTO) {
        Announcement existingAnnouncement = announcementRepository.findById(id)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(id));

        // Update fields
        if (announcementDTO.getTitle() != null) {
            existingAnnouncement.setTitle(announcementDTO.getTitle());
        }
        if (announcementDTO.getContent() != null) {
            existingAnnouncement.setContent(announcementDTO.getContent());
        }
        if (announcementDTO.getPriority() != null) {
            existingAnnouncement.setPriority(announcementDTO.getPriority());
        }
        if (announcementDTO.getStatus() != null) {
            existingAnnouncement.setStatus(announcementDTO.getStatus());
        }
        if (announcementDTO.getIsPinned() != null) {
            existingAnnouncement.setIsPinned(announcementDTO.getIsPinned());
        }
        if (announcementDTO.getIsPublic() != null) {
            existingAnnouncement.setIsPublic(announcementDTO.getIsPublic());
        }
        if (announcementDTO.getExpiryDate() != null) {
            existingAnnouncement.setExpiryDate(announcementDTO.getExpiryDate());
        }
        if (announcementDTO.getPublishDate() != null) {
            existingAnnouncement.setPublishDate(announcementDTO.getPublishDate());
        }
        if (announcementDTO.getSummary() != null) {
            existingAnnouncement.setSummary(announcementDTO.getSummary());
        }
        if (announcementDTO.getCategory() != null) {
            existingAnnouncement.setCategory(announcementDTO.getCategory());
        }
        if (announcementDTO.getTags() != null) {
            existingAnnouncement.setTags(announcementDTO.getTags());
        }
        if (announcementDTO.getIsUrgent() != null) {
            existingAnnouncement.setIsUrgent(announcementDTO.getIsUrgent());
        }
        if (announcementDTO.getRequiresAcknowledgment() != null) {
            existingAnnouncement.setRequiresAcknowledgment(announcementDTO.getRequiresAcknowledgment());
        }
        if (announcementDTO.getAcknowledgmentDeadline() != null) {
            existingAnnouncement.setAcknowledgmentDeadline(announcementDTO.getAcknowledgmentDeadline());
        }
        if (announcementDTO.getAttachmentName() != null) {
            existingAnnouncement.setAttachmentName(announcementDTO.getAttachmentName());
        }
        if (announcementDTO.getAttachmentUrl() != null) {
            existingAnnouncement.setAttachmentUrl(announcementDTO.getAttachmentUrl());
        }
        if (announcementDTO.getAttachmentSize() != null) {
            existingAnnouncement.setAttachmentSize(announcementDTO.getAttachmentSize());
        }
        if (announcementDTO.getAttachmentType() != null) {
            existingAnnouncement.setAttachmentType(announcementDTO.getAttachmentType());
        }
        if (announcementDTO.getIsScheduled() != null) {
            existingAnnouncement.setIsScheduled(announcementDTO.getIsScheduled());
        }
        if (announcementDTO.getScheduledDate() != null) {
            existingAnnouncement.setScheduledDate(announcementDTO.getScheduledDate());
        }
        if (announcementDTO.getIsRecurring() != null) {
            existingAnnouncement.setIsRecurring(announcementDTO.getIsRecurring());
        }
        if (announcementDTO.getRecurrencePattern() != null) {
            existingAnnouncement.setRecurrencePattern(announcementDTO.getRecurrencePattern());
        }
        if (announcementDTO.getRecurrenceEndDate() != null) {
            existingAnnouncement.setRecurrenceEndDate(announcementDTO.getRecurrenceEndDate());
        }
        
        existingAnnouncement.setUpdatedAt(LocalDateTime.now());

        Announcement updatedAnnouncement = announcementRepository.save(existingAnnouncement);
        return announcementMapper.toDto(updatedAnnouncement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(id));
        announcementRepository.delete(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findByCourseId(courseId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByCourseIdPaginated(Long courseId, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByCourseId(courseId, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByPostedById(Long postedById) {
        List<Announcement> announcements = announcementRepository.findByPostedById(postedById);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByPostedByIdPaginated(Long postedById, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByPostedById(postedById, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByStatus(AnnouncementStatus status) {
        List<Announcement> announcements = announcementRepository.findByStatus(status);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByStatusPaginated(AnnouncementStatus status, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByStatus(status, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByPriority(AnnouncementPriority priority) {
        List<Announcement> announcements = announcementRepository.findByPriority(priority);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByPriorityPaginated(AnnouncementPriority priority, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByPriority(priority, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsPinned(Boolean isPinned) {
        List<Announcement> announcements = announcementRepository.findByIsPinned(isPinned);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByIsPinnedPaginated(Boolean isPinned, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByIsPinned(isPinned, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsPublic(Boolean isPublic) {
        List<Announcement> announcements = announcementRepository.findByIsPublic(isPublic);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByIsPublicPaginated(Boolean isPublic, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByIsPublic(isPublic, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsUrgent(Boolean isUrgent) {
        List<Announcement> announcements = announcementRepository.findByIsUrgent(isUrgent);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByIsUrgentPaginated(Boolean isUrgent, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByIsUrgent(isUrgent, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsArchived(Boolean isArchived) {
        List<Announcement> announcements = announcementRepository.findByIsArchived(isArchived);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAnnouncementsByIsArchivedPaginated(Boolean isArchived, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByIsArchived(isArchived, pageable);
        return announcements.map(announcementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsScheduled(Boolean isScheduled) {
        List<Announcement> announcements = announcementRepository.findByIsScheduled(isScheduled);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByIsRecurring(Boolean isRecurring) {
        List<Announcement> announcements = announcementRepository.findByIsRecurring(isRecurring);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByRequiresAcknowledgment(Boolean requiresAcknowledgment) {
        List<Announcement> announcements = announcementRepository.findByRequiresAcknowledgment(requiresAcknowledgment);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndStatus(Long courseId, AnnouncementStatus status) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndStatus(courseId, status);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByPostedByIdAndStatus(Long postedById, AnnouncementStatus status) {
        List<Announcement> announcements = announcementRepository.findByPostedByIdAndStatus(postedById, status);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndIsPinned(Long courseId, Boolean isPinned) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndIsPinned(courseId, isPinned);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndIsUrgent(Long courseId, Boolean isUrgent) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndIsUrgent(courseId, isUrgent);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndIsPublic(Long courseId, Boolean isPublic) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndIsPublic(courseId, isPublic);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAllActive();
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findActiveByCourseId(courseId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActivePublicAnnouncements() {
        List<Announcement> announcements = announcementRepository.findAllActivePublic();
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActivePublicAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findActivePublicByCourseId(courseId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getNonExpiredAnnouncements() {
        List<Announcement> announcements = announcementRepository.findNonExpired(LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getNonExpiredAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findNonExpiredByCourseId(courseId, LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementRepository.findPublished(LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getPublishedAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findPublishedByCourseId(courseId, LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Announcement> announcements = announcementRepository.findByCreatedAtBetween(startDate, endDate);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndCreatedAtBetween(Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndCreatedAtBetween(courseId, startDate, endDate);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByPostedByIdAndCreatedAtBetween(Long postedById, LocalDateTime startDate, LocalDateTime endDate) {
        List<Announcement> announcements = announcementRepository.findByPostedByIdAndCreatedAtBetween(postedById, startDate, endDate);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByKeyword(String keyword) {
        List<Announcement> announcements = announcementRepository.findByKeyword(keyword);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndKeyword(Long courseId, String keyword) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndKeyword(courseId, keyword);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByTag(String tag) {
        List<Announcement> announcements = announcementRepository.findByTag(tag);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndTag(Long courseId, String tag) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndTag(courseId, tag);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCategory(String category) {
        List<Announcement> announcements = announcementRepository.findByCategory(category);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByCourseIdAndCategory(Long courseId, String category) {
        List<Announcement> announcements = announcementRepository.findByCourseIdAndCategory(courseId, category);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsWithExpiredAcknowledgmentDeadline() {
        List<Announcement> announcements = announcementRepository.findWithExpiredAcknowledgmentDeadline(LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsWithExpiredAcknowledgmentDeadlineByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findWithExpiredAcknowledgmentDeadlineByCourseId(courseId, LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsScheduledForPublishing() {
        List<Announcement> announcements = announcementRepository.findScheduledForPublishing(LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getExpiredRecurringAnnouncements() {
        List<Announcement> announcements = announcementRepository.findExpiredRecurring(LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getExpiredRecurringAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findExpiredRecurringByCourseId(courseId, LocalDateTime.now());
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsWithAttachments() {
        List<Announcement> announcements = announcementRepository.findWithAttachments();
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsWithAttachmentsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findWithAttachmentsByCourseId(courseId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getMostViewedAnnouncements() {
        List<Announcement> announcements = announcementRepository.findMostViewed();
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getMostViewedAnnouncementsByCourseId(Long courseId) {
        List<Announcement> announcements = announcementRepository.findMostViewedByCourseId(courseId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getRecentAnnouncements(LocalDateTime since) {
        List<Announcement> announcements = announcementRepository.findRecent(since);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getRecentAnnouncementsByCourseId(Long courseId, LocalDateTime since) {
        List<Announcement> announcements = announcementRepository.findRecentByCourseId(courseId, since);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsBySemesterAndAcademicYear(String semester, Integer academicYear) {
        List<Announcement> announcements = announcementRepository.findBySemesterAndAcademicYear(semester, academicYear);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByProfessorId(Long professorId) {
        List<Announcement> announcements = announcementRepository.findByProfessorId(professorId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncementsByProfessorId(Long professorId) {
        List<Announcement> announcements = announcementRepository.findActiveByProfessorId(professorId);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByDepartment(String department) {
        List<Announcement> announcements = announcementRepository.findByDepartment(department);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncementsByDepartment(String department) {
        List<Announcement> announcements = announcementRepository.findActiveByDepartment(department);
        return announcementMapper.toDtoList(announcements);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementCountByCourseId(Long courseId) {
        return announcementRepository.countByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementCountByPostedById(Long postedById) {
        return announcementRepository.countByPostedById(postedById);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementCountByProfessorId(Long professorId) {
        return announcementRepository.countByProfessorId(professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementCountByStatus(AnnouncementStatus status) {
        return announcementRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementCountByPriority(AnnouncementPriority priority) {
        return announcementRepository.countByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveAnnouncementCount() {
        return announcementRepository.countActive();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveAnnouncementCountByCourseId(Long courseId) {
        return announcementRepository.countActiveByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActivePublicAnnouncementCount() {
        return announcementRepository.countActivePublic();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActivePublicAnnouncementCountByCourseId(Long courseId) {
        return announcementRepository.countActivePublicByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUrgentAnnouncementCount() {
        return announcementRepository.countUrgent();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUrgentAnnouncementCountByCourseId(Long courseId) {
        return announcementRepository.countUrgentByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPinnedAnnouncementCount() {
        return announcementRepository.countPinned();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPinnedAnnouncementCountByCourseId(Long courseId) {
        return announcementRepository.countPinnedByCourseId(courseId);
    }

    @Override
    public void incrementViewCount(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.incrementViewCount();
        announcementRepository.save(announcement);
    }

    @Override
    public void archiveAnnouncement(Long announcementId, String archivedBy, String archiveReason) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsArchived(true);
        announcement.setArchiveDate(LocalDateTime.now());
        announcement.setArchivedBy(archivedBy);
        announcement.setArchiveReason(archiveReason);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void unarchiveAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsArchived(false);
        announcement.setArchiveDate(null);
        announcement.setArchivedBy(null);
        announcement.setArchiveReason(null);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void pinAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsPinned(true);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void unpinAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsPinned(false);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void markAsUrgent(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsUrgent(true);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void unmarkAsUrgent(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsUrgent(false);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void publishAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setStatus(AnnouncementStatus.ACTIVE);
        announcement.setPublishDate(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void unpublishAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setStatus(AnnouncementStatus.INACTIVE);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void scheduleAnnouncement(Long announcementId, LocalDateTime scheduledDate) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsScheduled(true);
        announcement.setScheduledDate(scheduledDate);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Override
    public void unscheduleAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> AnnouncementNotFoundException.withId(announcementId));
        announcement.setIsScheduled(false);
        announcement.setScheduledDate(null);
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementRepository.save(announcement);
    }
} 