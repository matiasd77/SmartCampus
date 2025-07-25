package com.smartcampus.repository;

import com.smartcampus.entity.Notification;
import com.smartcampus.entity.NotificationPriority;
import com.smartcampus.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser_Id(Long userId);
    
    // Alternative method names for convenience
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    Page<Notification> findByUser_Id(Long userId, Pageable pageable);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
    Page<Notification> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    List<Notification> findByUser_IdAndIsRead(Long userId, Boolean isRead);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead")
    List<Notification> findByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
    
    Page<Notification> findByUser_IdAndIsRead(Long userId, Boolean isRead, Pageable pageable);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead")
    Page<Notification> findByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead, Pageable pageable);
    
    List<Notification> findByUser_IdAndType(Long userId, NotificationType type);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);
    
    List<Notification> findByUser_IdAndPriority(Long userId, NotificationPriority priority);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.priority = :priority")
    List<Notification> findByUserIdAndPriority(@Param("userId") Long userId, @Param("priority") NotificationPriority priority);
    
    List<Notification> findByUser_IdAndIsUrgent(Long userId, Boolean isUrgent);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isUrgent = :isUrgent")
    List<Notification> findByUserIdAndIsUrgent(@Param("userId") Long userId, @Param("isUrgent") Boolean isUrgent);
    
    List<Notification> findByUser_IdAndIsArchived(Long userId, Boolean isArchived);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isArchived = :isArchived")
    List<Notification> findByUserIdAndIsArchived(@Param("userId") Long userId, @Param("isArchived") Boolean isArchived);
    
    List<Notification> findByUser_IdAndIsSystem(Long userId, Boolean isSystem);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isSystem = :isSystem")
    List<Notification> findByUserIdAndIsSystem(@Param("userId") Long userId, @Param("isSystem") Boolean isSystem);
    
    List<Notification> findByUser_IdAndIsBroadcast(Long userId, Boolean isBroadcast);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isBroadcast = :isBroadcast")
    List<Notification> findByUserIdAndIsBroadcast(@Param("userId") Long userId, @Param("isBroadcast") Boolean isBroadcast);
    
    List<Notification> findByUser_IdAndIsScheduled(Long userId, Boolean isScheduled);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isScheduled = :isScheduled")
    List<Notification> findByUserIdAndIsScheduled(@Param("userId") Long userId, @Param("isScheduled") Boolean isScheduled);
    
    List<Notification> findByUser_IdAndIsSent(Long userId, Boolean isSent);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isSent = :isSent")
    List<Notification> findByUserIdAndIsSent(@Param("userId") Long userId, @Param("isSent") Boolean isSent);
    
    List<Notification> findByUser_IdAndCategory(Long userId, String category);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.category = :category")
    List<Notification> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    
    List<Notification> findByUser_IdAndSource(Long userId, String source);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.source = :source")
    List<Notification> findByUserIdAndSource(@Param("userId") Long userId, @Param("source") String source);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since")
    List<Notification> findByUser_IdAndCreatedAtSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since")
    List<Notification> findByUserIdAndCreatedAtSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUser_IdAndCreatedAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.readAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUser_IdAndReadAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.readAt BETWEEN :startDate AND :endDate")
    List<Notification> findByUserIdAndReadAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.title LIKE %:keyword% OR n.message LIKE %:keyword% OR n.description LIKE %:keyword%)")
    List<Notification> findByUser_IdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.title LIKE %:keyword% OR n.message LIKE %:keyword% OR n.description LIKE %:keyword%)")
    List<Notification> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.expiryDate IS NOT NULL AND n.expiryDate < :now")
    List<Notification> findExpiredByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.expiryDate IS NOT NULL AND n.expiryDate < :now")
    List<Notification> findExpiredByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.expiryDate IS NULL OR n.expiryDate > :now")
    List<Notification> findNonExpiredByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND (n.expiryDate IS NULL OR n.expiryDate > :now)")
    List<Notification> findNonExpiredByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.scheduledSendDate IS NOT NULL AND n.scheduledSendDate <= :now AND n.isScheduled = true")
    List<Notification> findScheduledForSendingByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.scheduledSendDate IS NOT NULL AND n.scheduledSendDate <= :now AND n.isScheduled = true")
    List<Notification> findScheduledForSendingByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.recurrenceEndDate IS NOT NULL AND n.recurrenceEndDate < :now AND n.isRecurring = true")
    List<Notification> findExpiredRecurringByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.recurrenceEndDate IS NOT NULL AND n.recurrenceEndDate < :now AND n.isRecurring = true")
    List<Notification> findExpiredRecurringByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.deliveryStatus = :deliveryStatus")
    List<Notification> findByUser_IdAndDeliveryStatus(@Param("userId") Long userId, @Param("deliveryStatus") String deliveryStatus);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.deliveryStatus = :deliveryStatus")
    List<Notification> findByUserIdAndDeliveryStatus(@Param("userId") Long userId, @Param("deliveryStatus") String deliveryStatus);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.deliveryMethod = :deliveryMethod")
    List<Notification> findByUser_IdAndDeliveryMethod(@Param("userId") Long userId, @Param("deliveryMethod") String deliveryMethod);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.deliveryMethod = :deliveryMethod")
    List<Notification> findByUserIdAndDeliveryMethod(@Param("userId") Long userId, @Param("deliveryMethod") String deliveryMethod);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.retryCount < n.maxRetries AND n.nextRetryDate IS NOT NULL AND n.nextRetryDate <= :now")
    List<Notification> findReadyForRetryByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.retryCount < n.maxRetries AND n.nextRetryDate IS NOT NULL AND n.nextRetryDate <= :now")
    List<Notification> findReadyForRetryByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // System-wide queries (for admins)
    List<Notification> findByIsRead(Boolean isRead);
    
    List<Notification> findByType(NotificationType type);
    
    List<Notification> findByPriority(NotificationPriority priority);
    
    List<Notification> findByIsUrgent(Boolean isUrgent);
    
    List<Notification> findByIsArchived(Boolean isArchived);
    
    List<Notification> findByIsSystem(Boolean isSystem);
    
    List<Notification> findByIsBroadcast(Boolean isBroadcast);
    
    List<Notification> findByIsScheduled(Boolean isScheduled);
    
    List<Notification> findByIsSent(Boolean isSent);
    
    List<Notification> findByCategory(String category);
    
    List<Notification> findBySource(String source);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since")
    List<Notification> findByCreatedAtSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.readAt BETWEEN :startDate AND :endDate")
    List<Notification> findByReadAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n FROM Notification n WHERE n.title LIKE %:keyword% OR n.message LIKE %:keyword% OR n.description LIKE %:keyword%")
    List<Notification> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT n FROM Notification n WHERE n.expiryDate IS NOT NULL AND n.expiryDate < :now")
    List<Notification> findExpired(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.expiryDate IS NULL OR n.expiryDate > :now")
    List<Notification> findNonExpired(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.scheduledSendDate IS NOT NULL AND n.scheduledSendDate <= :now AND n.isScheduled = true")
    List<Notification> findScheduledForSending(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.recurrenceEndDate IS NOT NULL AND n.recurrenceEndDate < :now AND n.isRecurring = true")
    List<Notification> findExpiredRecurring(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.deliveryStatus = :deliveryStatus")
    List<Notification> findByDeliveryStatus(@Param("deliveryStatus") String deliveryStatus);
    
    @Query("SELECT n FROM Notification n WHERE n.deliveryMethod = :deliveryMethod")
    List<Notification> findByDeliveryMethod(@Param("deliveryMethod") String deliveryMethod);
    
    @Query("SELECT n FROM Notification n WHERE n.retryCount < n.maxRetries AND n.nextRetryDate IS NOT NULL AND n.nextRetryDate <= :now")
    List<Notification> findReadyForRetry(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.broadcastTarget = :target")
    List<Notification> findByBroadcastTarget(@Param("target") String target);
    
    @Query("SELECT n FROM Notification n WHERE n.broadcastTarget LIKE %:target%")
    List<Notification> findByBroadcastTargetContaining(@Param("target") String target);
    
    @Query("SELECT n FROM Notification n WHERE n.isTemplate = true")
    List<Notification> findAllTemplates();
    
    @Query("SELECT n FROM Notification n WHERE n.isTemplate = true AND n.templateName = :templateName")
    List<Notification> findByTemplateName(@Param("templateName") String templateName);
    
    // Count queries
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId")
    Long countByUser_Id(@Param("userId") Long userId);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead")
    Long countByUser_IdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead")
    Long countByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    Long countByUser_IdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    Long countByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.priority = :priority")
    Long countByUser_IdAndPriority(@Param("userId") Long userId, @Param("priority") NotificationPriority priority);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.priority = :priority")
    Long countByUserIdAndPriority(@Param("userId") Long userId, @Param("priority") NotificationPriority priority);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isUrgent = :isUrgent")
    Long countByUser_IdAndIsUrgent(@Param("userId") Long userId, @Param("isUrgent") Boolean isUrgent);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isUrgent = :isUrgent")
    Long countByUserIdAndIsUrgent(@Param("userId") Long userId, @Param("isUrgent") Boolean isUrgent);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isArchived = :isArchived")
    Long countByUser_IdAndIsArchived(@Param("userId") Long userId, @Param("isArchived") Boolean isArchived);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isArchived = :isArchived")
    Long countByUserIdAndIsArchived(@Param("userId") Long userId, @Param("isArchived") Boolean isArchived);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isSystem = :isSystem")
    Long countByUser_IdAndIsSystem(@Param("userId") Long userId, @Param("isSystem") Boolean isSystem);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isSystem = :isSystem")
    Long countByUserIdAndIsSystem(@Param("userId") Long userId, @Param("isSystem") Boolean isSystem);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.category = :category")
    Long countByUser_IdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.category = :category")
    Long countByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since")
    Long countByUser_IdAndCreatedAtSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since")
    Long countByUserIdAndCreatedAtSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.expiryDate IS NULL OR n.expiryDate > :now")
    Long countNonExpiredByUser_Id(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Convenience method with simplified naming
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND (n.expiryDate IS NULL OR n.expiryDate > :now)")
    Long countNonExpiredByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // System-wide count queries
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = :isRead")
    Long countByIsRead(@Param("isRead") Boolean isRead);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.type = :type")
    Long countByType(@Param("type") NotificationType type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.priority = :priority")
    Long countByPriority(@Param("priority") NotificationPriority priority);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isUrgent = :isUrgent")
    Long countByIsUrgent(@Param("isUrgent") Boolean isUrgent);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isArchived = :isArchived")
    Long countByIsArchived(@Param("isArchived") Boolean isArchived);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isSystem = :isSystem")
    Long countByIsSystem(@Param("isSystem") Boolean isSystem);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isBroadcast = :isBroadcast")
    Long countByIsBroadcast(@Param("isBroadcast") Boolean isBroadcast);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isScheduled = :isScheduled")
    Long countByIsScheduled(@Param("isScheduled") Boolean isScheduled);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isSent = :isSent")
    Long countByIsSent(@Param("isSent") Boolean isSent);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.category = :category")
    Long countByCategory(@Param("category") String category);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.source = :source")
    Long countBySource(@Param("source") String source);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt >= :since")
    Long countByCreatedAtSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.expiryDate IS NULL OR n.expiryDate > :now")
    Long countNonExpired(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.deliveryStatus = :deliveryStatus")
    Long countByDeliveryStatus(@Param("deliveryStatus") String deliveryStatus);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.deliveryMethod = :deliveryMethod")
    Long countByDeliveryMethod(@Param("deliveryMethod") String deliveryMethod);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.retryCount < n.maxRetries AND n.nextRetryDate IS NOT NULL AND n.nextRetryDate <= :now")
    Long countReadyForRetry(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.broadcastTarget = :target")
    Long countByBroadcastTarget(@Param("target") String target);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isTemplate = true")
    Long countTemplates();

    void deleteByCreatedAtBefore(LocalDateTime date);
    void deleteByMessageContaining(String message);
} 