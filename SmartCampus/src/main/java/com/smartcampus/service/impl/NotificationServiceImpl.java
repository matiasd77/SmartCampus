package com.smartcampus.service.impl;

import com.smartcampus.dto.NotificationDTO;
import com.smartcampus.entity.Notification;
import com.smartcampus.entity.NotificationPriority;
import com.smartcampus.entity.NotificationType;
import com.smartcampus.entity.User;
import com.smartcampus.exception.NotificationNotFoundException;
import com.smartcampus.exception.UserNotFoundException;
import com.smartcampus.mapper.NotificationMapper;
import com.smartcampus.repository.NotificationRepository;
import com.smartcampus.repository.UserRepository;
import com.smartcampus.service.NotificationService;
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
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getAllNotificationsPaginated(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> NotificationNotFoundException.withId(id));
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        // Validate user exists
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(notificationDTO.getUserId()));

        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification.setUser(user);
        
        // Set default values
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        if (notification.getType() == null) {
            notification.setType(NotificationType.GENERAL);
        }
        if (notification.getPriority() == null) {
            notification.setPriority(NotificationPriority.NORMAL);
        }
        if (notification.getIsDismissible() == null) {
            notification.setIsDismissible(true);
        }
        if (notification.getIsUrgent() == null) {
            notification.setIsUrgent(false);
        }
        if (notification.getIsSilent() == null) {
            notification.setIsSilent(false);
        }
        if (notification.getIsArchived() == null) {
            notification.setIsArchived(false);
        }
        if (notification.getIsSystem() == null) {
            notification.setIsSystem(false);
        }
        if (notification.getIsBroadcast() == null) {
            notification.setIsBroadcast(false);
        }
        if (notification.getIsRecurring() == null) {
            notification.setIsRecurring(false);
        }
        if (notification.getIsTemplate() == null) {
            notification.setIsTemplate(false);
        }
        if (notification.getIsScheduled() == null) {
            notification.setIsScheduled(false);
        }
        if (notification.getIsSent() == null) {
            notification.setIsSent(false);
        }
        if (notification.getRetryCount() == null) {
            notification.setRetryCount(0);
        }
        if (notification.getMaxRetries() == null) {
            notification.setMaxRetries(3);
        }
        
        // Set audit fields
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(savedNotification);
    }

    @Override
    public NotificationDTO updateNotification(Long id, NotificationDTO notificationDTO) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> NotificationNotFoundException.withId(id));

        // Update fields
        if (notificationDTO.getMessage() != null) {
            existingNotification.setMessage(notificationDTO.getMessage());
        }
        if (notificationDTO.getType() != null) {
            existingNotification.setType(notificationDTO.getType());
        }
        if (notificationDTO.getPriority() != null) {
            existingNotification.setPriority(notificationDTO.getPriority());
        }
        if (notificationDTO.getTitle() != null) {
            existingNotification.setTitle(notificationDTO.getTitle());
        }
        if (notificationDTO.getDescription() != null) {
            existingNotification.setDescription(notificationDTO.getDescription());
        }
        if (notificationDTO.getCategory() != null) {
            existingNotification.setCategory(notificationDTO.getCategory());
        }
        if (notificationDTO.getActionUrl() != null) {
            existingNotification.setActionUrl(notificationDTO.getActionUrl());
        }
        if (notificationDTO.getActionText() != null) {
            existingNotification.setActionText(notificationDTO.getActionText());
        }
        if (notificationDTO.getExpiryDate() != null) {
            existingNotification.setExpiryDate(notificationDTO.getExpiryDate());
        }
        if (notificationDTO.getScheduledDate() != null) {
            existingNotification.setScheduledDate(notificationDTO.getScheduledDate());
        }
        if (notificationDTO.getIsDismissible() != null) {
            existingNotification.setIsDismissible(notificationDTO.getIsDismissible());
        }
        if (notificationDTO.getIsUrgent() != null) {
            existingNotification.setIsUrgent(notificationDTO.getIsUrgent());
        }
        if (notificationDTO.getIsSilent() != null) {
            existingNotification.setIsSilent(notificationDTO.getIsSilent());
        }
        if (notificationDTO.getIcon() != null) {
            existingNotification.setIcon(notificationDTO.getIcon());
        }
        if (notificationDTO.getColor() != null) {
            existingNotification.setColor(notificationDTO.getColor());
        }
        if (notificationDTO.getIsSystem() != null) {
            existingNotification.setIsSystem(notificationDTO.getIsSystem());
        }
        if (notificationDTO.getSource() != null) {
            existingNotification.setSource(notificationDTO.getSource());
        }
        if (notificationDTO.getSourceId() != null) {
            existingNotification.setSourceId(notificationDTO.getSourceId());
        }
        if (notificationDTO.getIsBroadcast() != null) {
            existingNotification.setIsBroadcast(notificationDTO.getIsBroadcast());
        }
        if (notificationDTO.getBroadcastTarget() != null) {
            existingNotification.setBroadcastTarget(notificationDTO.getBroadcastTarget());
        }
        if (notificationDTO.getIsRecurring() != null) {
            existingNotification.setIsRecurring(notificationDTO.getIsRecurring());
        }
        if (notificationDTO.getRecurrencePattern() != null) {
            existingNotification.setRecurrencePattern(notificationDTO.getRecurrencePattern());
        }
        if (notificationDTO.getRecurrenceEndDate() != null) {
            existingNotification.setRecurrenceEndDate(notificationDTO.getRecurrenceEndDate());
        }
        if (notificationDTO.getIsTemplate() != null) {
            existingNotification.setIsTemplate(notificationDTO.getIsTemplate());
        }
        if (notificationDTO.getTemplateName() != null) {
            existingNotification.setTemplateName(notificationDTO.getTemplateName());
        }
        if (notificationDTO.getTemplateVariables() != null) {
            existingNotification.setTemplateVariables(notificationDTO.getTemplateVariables());
        }
        if (notificationDTO.getIsScheduled() != null) {
            existingNotification.setIsScheduled(notificationDTO.getIsScheduled());
        }
        if (notificationDTO.getScheduledSendDate() != null) {
            existingNotification.setScheduledSendDate(notificationDTO.getScheduledSendDate());
        }
        if (notificationDTO.getDeliveryMethod() != null) {
            existingNotification.setDeliveryMethod(notificationDTO.getDeliveryMethod());
        }
        if (notificationDTO.getDeliveryStatus() != null) {
            existingNotification.setDeliveryStatus(notificationDTO.getDeliveryStatus());
        }
        if (notificationDTO.getDeliveryError() != null) {
            existingNotification.setDeliveryError(notificationDTO.getDeliveryError());
        }
        if (notificationDTO.getMaxRetries() != null) {
            existingNotification.setMaxRetries(notificationDTO.getMaxRetries());
        }
        
        existingNotification.setUpdatedAt(LocalDateTime.now());

        Notification updatedNotification = notificationRepository.save(existingNotification);
        return notificationMapper.toDto(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> NotificationNotFoundException.withId(id));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserIdPaginated(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsRead(Long userId, Boolean isRead) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, isRead);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotificationsByUserIdAndIsReadPaginated(Long userId, Boolean isRead, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, isRead, pageable);
        return notifications.map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndType(Long userId, NotificationType type) {
        List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, type);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndPriority(Long userId, NotificationPriority priority) {
        List<Notification> notifications = notificationRepository.findByUserIdAndPriority(userId, priority);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsUrgent(Long userId, Boolean isUrgent) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsUrgent(userId, isUrgent);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsArchived(Long userId, Boolean isArchived) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsArchived(userId, isArchived);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsSystem(Long userId, Boolean isSystem) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsSystem(userId, isSystem);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsBroadcast(Long userId, Boolean isBroadcast) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsBroadcast(userId, isBroadcast);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsScheduled(Long userId, Boolean isScheduled) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsScheduled(userId, isScheduled);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndIsSent(Long userId, Boolean isSent) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsSent(userId, isSent);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndCategory(Long userId, String category) {
        List<Notification> notifications = notificationRepository.findByUserIdAndCategory(userId, category);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndSource(Long userId, String source) {
        List<Notification> notifications = notificationRepository.findByUserIdAndSource(userId, source);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndCreatedAtSince(Long userId, LocalDateTime since) {
        List<Notification> notifications = notificationRepository.findByUserIdAndCreatedAtSince(userId, since);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Notification> notifications = notificationRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndReadAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadAtBetween(userId, startDate, endDate);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndKeyword(Long userId, String keyword) {
        List<Notification> notifications = notificationRepository.findByUserIdAndKeyword(userId, keyword);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getExpiredNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findExpiredByUserId(userId, LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNonExpiredNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findNonExpiredByUserId(userId, LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getScheduledForSendingNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findScheduledForSendingByUserId(userId, LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getExpiredRecurringNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findExpiredRecurringByUserId(userId, LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndDeliveryStatus(Long userId, String deliveryStatus) {
        List<Notification> notifications = notificationRepository.findByUserIdAndDeliveryStatus(userId, deliveryStatus);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndDeliveryMethod(Long userId, String deliveryMethod) {
        List<Notification> notifications = notificationRepository.findByUserIdAndDeliveryMethod(userId, deliveryMethod);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getReadyForRetryNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findReadyForRetryByUserId(userId, LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    // System-wide queries
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsRead(Boolean isRead) {
        List<Notification> notifications = notificationRepository.findByIsRead(isRead);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByType(NotificationType type) {
        List<Notification> notifications = notificationRepository.findByType(type);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByPriority(NotificationPriority priority) {
        List<Notification> notifications = notificationRepository.findByPriority(priority);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsUrgent(Boolean isUrgent) {
        List<Notification> notifications = notificationRepository.findByIsUrgent(isUrgent);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsArchived(Boolean isArchived) {
        List<Notification> notifications = notificationRepository.findByIsArchived(isArchived);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsSystem(Boolean isSystem) {
        List<Notification> notifications = notificationRepository.findByIsSystem(isSystem);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsBroadcast(Boolean isBroadcast) {
        List<Notification> notifications = notificationRepository.findByIsBroadcast(isBroadcast);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsScheduled(Boolean isScheduled) {
        List<Notification> notifications = notificationRepository.findByIsScheduled(isScheduled);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByIsSent(Boolean isSent) {
        List<Notification> notifications = notificationRepository.findByIsSent(isSent);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByCategory(String category) {
        List<Notification> notifications = notificationRepository.findByCategory(category);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsBySource(String source) {
        List<Notification> notifications = notificationRepository.findBySource(source);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByCreatedAtSince(LocalDateTime since) {
        List<Notification> notifications = notificationRepository.findByCreatedAtSince(since);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Notification> notifications = notificationRepository.findByCreatedAtBetween(startDate, endDate);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByReadAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Notification> notifications = notificationRepository.findByReadAtBetween(startDate, endDate);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByKeyword(String keyword) {
        List<Notification> notifications = notificationRepository.findByKeyword(keyword);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getExpiredNotifications() {
        List<Notification> notifications = notificationRepository.findExpired(LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNonExpiredNotifications() {
        List<Notification> notifications = notificationRepository.findNonExpired(LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getScheduledForSendingNotifications() {
        List<Notification> notifications = notificationRepository.findScheduledForSending(LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getExpiredRecurringNotifications() {
        List<Notification> notifications = notificationRepository.findExpiredRecurring(LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByDeliveryStatus(String deliveryStatus) {
        List<Notification> notifications = notificationRepository.findByDeliveryStatus(deliveryStatus);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByDeliveryMethod(String deliveryMethod) {
        List<Notification> notifications = notificationRepository.findByDeliveryMethod(deliveryMethod);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getReadyForRetryNotifications() {
        List<Notification> notifications = notificationRepository.findReadyForRetry(LocalDateTime.now());
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByBroadcastTarget(String target) {
        List<Notification> notifications = notificationRepository.findByBroadcastTarget(target);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByBroadcastTargetContaining(String target) {
        List<Notification> notifications = notificationRepository.findByBroadcastTargetContaining(target);
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllTemplates() {
        List<Notification> notifications = notificationRepository.findAllTemplates();
        return notificationMapper.toDtoList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByTemplateName(String templateName) {
        List<Notification> notifications = notificationRepository.findByTemplateName(templateName);
        return notificationMapper.toDtoList(notifications);
    }

    // Count queries
    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserId(Long userId) {
        return notificationRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndIsRead(Long userId, Boolean isRead) {
        return notificationRepository.countByUserIdAndIsRead(userId, isRead);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndType(Long userId, NotificationType type) {
        return notificationRepository.countByUserIdAndType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndPriority(Long userId, NotificationPriority priority) {
        return notificationRepository.countByUserIdAndPriority(userId, priority);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndIsUrgent(Long userId, Boolean isUrgent) {
        return notificationRepository.countByUserIdAndIsUrgent(userId, isUrgent);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndIsArchived(Long userId, Boolean isArchived) {
        return notificationRepository.countByUserIdAndIsArchived(userId, isArchived);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndIsSystem(Long userId, Boolean isSystem) {
        return notificationRepository.countByUserIdAndIsSystem(userId, isSystem);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndCategory(Long userId, String category) {
        return notificationRepository.countByUserIdAndCategory(userId, category);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByUserIdAndCreatedAtSince(Long userId, LocalDateTime since) {
        return notificationRepository.countByUserIdAndCreatedAtSince(userId, since);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNonExpiredNotificationCountByUserId(Long userId) {
        return notificationRepository.countNonExpiredByUserId(userId, LocalDateTime.now());
    }

    // System-wide count queries
    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsRead(Boolean isRead) {
        return notificationRepository.countByIsRead(isRead);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByType(NotificationType type) {
        return notificationRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByPriority(NotificationPriority priority) {
        return notificationRepository.countByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsUrgent(Boolean isUrgent) {
        return notificationRepository.countByIsUrgent(isUrgent);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsArchived(Boolean isArchived) {
        return notificationRepository.countByIsArchived(isArchived);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsSystem(Boolean isSystem) {
        return notificationRepository.countByIsSystem(isSystem);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsBroadcast(Boolean isBroadcast) {
        return notificationRepository.countByIsBroadcast(isBroadcast);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsScheduled(Boolean isScheduled) {
        return notificationRepository.countByIsScheduled(isScheduled);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByIsSent(Boolean isSent) {
        return notificationRepository.countByIsSent(isSent);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByCategory(String category) {
        return notificationRepository.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountBySource(String source) {
        return notificationRepository.countBySource(source);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByCreatedAtSince(LocalDateTime since) {
        return notificationRepository.countByCreatedAtSince(since);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNonExpiredNotificationCount() {
        return notificationRepository.countNonExpired(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByDeliveryStatus(String deliveryStatus) {
        return notificationRepository.countByDeliveryStatus(deliveryStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByDeliveryMethod(String deliveryMethod) {
        return notificationRepository.countByDeliveryMethod(deliveryMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getReadyForRetryNotificationCount() {
        return notificationRepository.countReadyForRetry(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByBroadcastTarget(String target) {
        return notificationRepository.countByBroadcastTarget(target);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTemplateCount() {
        return notificationRepository.countTemplates();
    }

    // Action methods
    @Override
    public void markAsRead(Long notificationId, String readBy) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.markAsRead(readBy);
        notificationRepository.save(notification);
    }

    @Override
    public void markAsUnread(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.markAsUnread();
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsReadByUserId(Long userId, String readBy) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, false);
        for (Notification notification : notifications) {
            notification.markAsRead(readBy);
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAllAsUnreadByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, true);
        for (Notification notification : notifications) {
            notification.markAsUnread();
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void archiveNotification(Long notificationId, String archivedBy, String archiveReason) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.archive(archivedBy, archiveReason);
        notificationRepository.save(notification);
    }

    @Override
    public void unarchiveNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.unarchive();
        notificationRepository.save(notification);
    }

    @Override
    public void archiveAllByUserId(Long userId, String archivedBy, String archiveReason) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsArchived(userId, false);
        for (Notification notification : notifications) {
            notification.archive(archivedBy, archiveReason);
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void unarchiveAllByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsArchived(userId, true);
        for (Notification notification : notifications) {
            notification.unarchive();
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAsSent(Long notificationId, String sentBy) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.markAsSent(sentBy);
        notificationRepository.save(notification);
    }

    @Override
    public void incrementRetryCount(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.incrementRetryCount();
        notificationRepository.save(notification);
    }

    @Override
    public void setNextRetryDate(Long notificationId, LocalDateTime nextRetryDate) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.setNextRetryDate(nextRetryDate);
        notificationRepository.save(notification);
    }

    // Notification sending methods
    @Override
    public void sendNotificationToUser(Long userId, NotificationDTO notificationDTO) {
        notificationDTO.setUserId(userId);
        createNotification(notificationDTO);
    }

    @Override
    public void sendNotificationToUsers(List<Long> userIds, NotificationDTO notificationDTO) {
        for (Long userId : userIds) {
            sendNotificationToUser(userId, notificationDTO);
        }
    }

    @Override
    public void sendBroadcastNotification(NotificationDTO notificationDTO, String target) {
        notificationDTO.setIsBroadcast(true);
        notificationDTO.setBroadcastTarget(target);
        // This would typically involve finding all users matching the target
        // For now, we'll create a broadcast notification without specific users
        createNotification(notificationDTO);
    }

    @Override
    public void sendSystemNotification(Long userId, String message, NotificationType type, NotificationPriority priority) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .message(message)
                .type(type)
                .priority(priority)
                .isSystem(true)
                .source("SYSTEM")
                .build();
        sendNotificationToUser(userId, notificationDTO);
    }

    @Override
    public void sendSystemNotificationToUsers(List<Long> userIds, String message, NotificationType type, NotificationPriority priority) {
        for (Long userId : userIds) {
            sendSystemNotification(userId, message, type, priority);
        }
    }

    @Override
    public void sendSystemBroadcastNotification(String message, NotificationType type, NotificationPriority priority, String target) {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .message(message)
                .type(type)
                .priority(priority)
                .isSystem(true)
                .isBroadcast(true)
                .source("SYSTEM")
                .build();
        sendBroadcastNotification(notificationDTO, target);
    }

    @Override
    public void scheduleNotification(Long userId, NotificationDTO notificationDTO, LocalDateTime scheduledDate) {
        notificationDTO.setUserId(userId);
        notificationDTO.setIsScheduled(true);
        notificationDTO.setScheduledSendDate(scheduledDate);
        createNotification(notificationDTO);
    }

    @Override
    public void scheduleBroadcastNotification(NotificationDTO notificationDTO, String target, LocalDateTime scheduledDate) {
        notificationDTO.setIsBroadcast(true);
        notificationDTO.setBroadcastTarget(target);
        notificationDTO.setIsScheduled(true);
        notificationDTO.setScheduledSendDate(scheduledDate);
        createNotification(notificationDTO);
    }

    @Override
    public void cancelScheduledNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        notification.setIsScheduled(false);
        notification.setScheduledSendDate(null);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    // Processing methods
    @Override
    public void processScheduledNotifications() {
        List<Notification> scheduledNotifications = notificationRepository.findScheduledForSending(LocalDateTime.now());
        for (Notification notification : scheduledNotifications) {
            notification.setIsScheduled(false);
            notification.setIsSent(true);
            notification.setSentAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(scheduledNotifications);
    }

    @Override
    public void processRecurringNotifications() {
        List<Notification> expiredRecurringNotifications = notificationRepository.findExpiredRecurring(LocalDateTime.now());
        for (Notification notification : expiredRecurringNotifications) {
            notification.setIsRecurring(false);
            notification.setUpdatedAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(expiredRecurringNotifications);
    }

    @Override
    public void processRetryNotifications() {
        List<Notification> retryNotifications = notificationRepository.findReadyForRetry(LocalDateTime.now());
        for (Notification notification : retryNotifications) {
            notification.incrementRetryCount();
            // Set next retry date (exponential backoff)
            LocalDateTime nextRetry = LocalDateTime.now().plusMinutes((long) Math.pow(2, notification.getRetryCount()));
            notification.setNextRetryDate(nextRetry);
        }
        notificationRepository.saveAll(retryNotifications);
    }

    @Override
    public void cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpired(LocalDateTime.now());
        notificationRepository.deleteAll(expiredNotifications);
    }

    @Override
    public void cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Notification> oldNotifications = notificationRepository.findByCreatedAtSince(cutoffDate);
        notificationRepository.deleteAll(oldNotifications);
    }
} 