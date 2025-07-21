package com.smartcampus.repository;

import com.smartcampus.entity.Announcement;
import com.smartcampus.entity.AnnouncementPriority;
import com.smartcampus.entity.AnnouncementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    List<Announcement> findByCourseId(Long courseId);
    
    List<Announcement> findByPostedBy_Id(Long postedById);
    
    // Convenience methods with simplified naming
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById")
    List<Announcement> findByPostedById(@Param("postedById") Long postedById);
    
    List<Announcement> findByStatus(AnnouncementStatus status);
    
    List<Announcement> findByPriority(AnnouncementPriority priority);
    
    List<Announcement> findByIsPinned(Boolean isPinned);
    
    List<Announcement> findByIsPublic(Boolean isPublic);
    
    List<Announcement> findByIsUrgent(Boolean isUrgent);
    
    List<Announcement> findByIsArchived(Boolean isArchived);
    
    List<Announcement> findByIsScheduled(Boolean isScheduled);
    
    List<Announcement> findByIsRecurring(Boolean isRecurring);
    
    List<Announcement> findByRequiresAcknowledgment(Boolean requiresAcknowledgment);
    
    List<Announcement> findByCourseIdAndStatus(Long courseId, AnnouncementStatus status);
    
    List<Announcement> findByPostedBy_IdAndStatus(Long postedById, AnnouncementStatus status);
    
    // Convenience method with simplified naming
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById AND a.status = :status")
    List<Announcement> findByPostedByIdAndStatus(@Param("postedById") Long postedById, @Param("status") AnnouncementStatus status);
    
    List<Announcement> findByCourseIdAndIsPinned(Long courseId, Boolean isPinned);
    
    List<Announcement> findByCourseIdAndIsUrgent(Long courseId, Boolean isUrgent);
    
    List<Announcement> findByCourseIdAndIsPublic(Long courseId, Boolean isPublic);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.isActive = true")
    List<Announcement> findActiveByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById AND a.isActive = true")
    List<Announcement> findActiveByPostedById(@Param("postedById") Long postedById);
    
    @Query("SELECT a FROM Announcement a WHERE a.isActive = true")
    List<Announcement> findAllActive();
    
    @Query("SELECT a FROM Announcement a WHERE a.isActive = true AND a.isPublic = true")
    List<Announcement> findAllActivePublic();
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.isActive = true AND a.isPublic = true")
    List<Announcement> findActivePublicByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.expiryDate IS NULL OR a.expiryDate > :now")
    List<Announcement> findNonExpired(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND (a.expiryDate IS NULL OR a.expiryDate > :now)")
    List<Announcement> findNonExpiredByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.publishDate IS NULL OR a.publishDate <= :now")
    List<Announcement> findPublished(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND (a.publishDate IS NULL OR a.publishDate <= :now)")
    List<Announcement> findPublishedByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Announcement> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Announcement> findByCourseIdAndCreatedAtBetween(@Param("courseId") Long courseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Announcement> findByPostedBy_IdAndCreatedAtBetween(@Param("postedById") Long postedById, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Convenience method with simplified naming
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Announcement> findByPostedByIdAndCreatedAtBetween(@Param("postedById") Long postedById, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Announcement a WHERE a.title LIKE %:keyword% OR a.content LIKE %:keyword% OR a.summary LIKE %:keyword%")
    List<Announcement> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword% OR a.summary LIKE %:keyword%)")
    List<Announcement> findByCourseIdAndKeyword(@Param("courseId") Long courseId, @Param("keyword") String keyword);
    
    @Query("SELECT a FROM Announcement a WHERE a.tags LIKE %:tag%")
    List<Announcement> findByTag(@Param("tag") String tag);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.tags LIKE %:tag%")
    List<Announcement> findByCourseIdAndTag(@Param("courseId") Long courseId, @Param("tag") String tag);
    
    @Query("SELECT a FROM Announcement a WHERE a.category = :category")
    List<Announcement> findByCategory(@Param("category") String category);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.category = :category")
    List<Announcement> findByCourseIdAndCategory(@Param("courseId") Long courseId, @Param("category") String category);
    
    @Query("SELECT a FROM Announcement a WHERE a.acknowledgmentDeadline IS NOT NULL AND a.acknowledgmentDeadline < :now")
    List<Announcement> findWithExpiredAcknowledgmentDeadline(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.acknowledgmentDeadline IS NOT NULL AND a.acknowledgmentDeadline < :now")
    List<Announcement> findWithExpiredAcknowledgmentDeadlineByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.scheduledDate IS NOT NULL AND a.scheduledDate <= :now AND a.isScheduled = true")
    List<Announcement> findScheduledForPublishing(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.recurrenceEndDate IS NOT NULL AND a.recurrenceEndDate < :now AND a.isRecurring = true")
    List<Announcement> findExpiredRecurring(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.recurrenceEndDate IS NOT NULL AND a.recurrenceEndDate < :now AND a.isRecurring = true")
    List<Announcement> findExpiredRecurringByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Announcement a WHERE a.attachmentUrl IS NOT NULL AND a.attachmentUrl != ''")
    List<Announcement> findWithAttachments();
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.attachmentUrl IS NOT NULL AND a.attachmentUrl != ''")
    List<Announcement> findWithAttachmentsByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.viewCount IS NOT NULL ORDER BY a.viewCount DESC")
    List<Announcement> findMostViewed();
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.viewCount IS NOT NULL ORDER BY a.viewCount DESC")
    List<Announcement> findMostViewedByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Announcement a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Announcement> findRecent(@Param("since") LocalDateTime since);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.id = :courseId AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Announcement> findRecentByCourseId(@Param("courseId") Long courseId, @Param("since") LocalDateTime since);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.semester = :semester AND a.course.academicYear = :academicYear")
    List<Announcement> findBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.professor.id = :professorId")
    List<Announcement> findByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.professor.id = :professorId AND a.isActive = true")
    List<Announcement> findActiveByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.professor.department = :department")
    List<Announcement> findByDepartment(@Param("department") String department);
    
    @Query("SELECT a FROM Announcement a WHERE a.course.professor.department = :department AND a.isActive = true")
    List<Announcement> findActiveByDepartment(@Param("department") String department);
    
    // Pagination support
    Page<Announcement> findByCourseId(Long courseId, Pageable pageable);
    
    Page<Announcement> findByPostedBy_Id(Long postedById, Pageable pageable);
    
    // Convenience method with simplified naming
    @Query("SELECT a FROM Announcement a WHERE a.postedBy.id = :postedById")
    Page<Announcement> findByPostedById(@Param("postedById") Long postedById, Pageable pageable);
    
    Page<Announcement> findByStatus(AnnouncementStatus status, Pageable pageable);
    
    Page<Announcement> findByPriority(AnnouncementPriority priority, Pageable pageable);
    
    Page<Announcement> findByIsPinned(Boolean isPinned, Pageable pageable);
    
    Page<Announcement> findByIsPublic(Boolean isPublic, Pageable pageable);
    
    Page<Announcement> findByIsUrgent(Boolean isUrgent, Pageable pageable);
    
    Page<Announcement> findByIsArchived(Boolean isArchived, Pageable pageable);
    
    // Count queries
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.postedBy.id = :postedById")
    Long countByPostedById(@Param("postedById") Long postedById);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.professor.id = :professorId")
    Long countByProfessorId(@Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.status = :status")
    Long countByStatus(@Param("status") AnnouncementStatus status);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.priority = :priority")
    Long countByPriority(@Param("priority") AnnouncementPriority priority);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.isActive = true")
    Long countActive();
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId AND a.isActive = true")
    Long countActiveByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.isActive = true AND a.isPublic = true")
    Long countActivePublic();
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId AND a.isActive = true AND a.isPublic = true")
    Long countActivePublicByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.isUrgent = true")
    Long countUrgent();
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId AND a.isUrgent = true")
    Long countUrgentByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.isPinned = true")
    Long countPinned();
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId AND a.isPinned = true")
    Long countPinnedByCourseId(@Param("courseId") Long courseId);
} 