package com.smartcampus.mapper.impl;

import com.smartcampus.dto.NotificationDTO;
import com.smartcampus.entity.Notification;
import com.smartcampus.mapper.NotificationMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDTO toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .userName(notification.getUser() != null ? notification.getUser().getName() : null)
                .userEmail(notification.getUser() != null ? notification.getUser().getEmail() : null)
                .userRole(notification.getUser() != null && notification.getUser().getRole() != null ? 
                        notification.getUser().getRole().name() : null)
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .type(notification.getType())
                .priority(notification.getPriority())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .category(notification.getCategory())
                .actionUrl(notification.getActionUrl())
                .actionText(notification.getActionText())
                .expiryDate(notification.getExpiryDate())
                .scheduledDate(notification.getScheduledDate())
                .isDismissible(notification.getIsDismissible())
                .isUrgent(notification.getIsUrgent())
                .isSilent(notification.getIsSilent())
                .icon(notification.getIcon())
                .color(notification.getColor())
                .readAt(notification.getReadAt())
                .readBy(notification.getReadBy())
                .isArchived(notification.getIsArchived())
                .archiveDate(notification.getArchiveDate())
                .archivedBy(notification.getArchivedBy())
                .archiveReason(notification.getArchiveReason())
                .isSystem(notification.getIsSystem())
                .source(notification.getSource())
                .sourceId(notification.getSourceId())
                .isBroadcast(notification.getIsBroadcast())
                .broadcastTarget(notification.getBroadcastTarget())
                .isRecurring(notification.getIsRecurring())
                .recurrencePattern(notification.getRecurrencePattern())
                .recurrenceEndDate(notification.getRecurrenceEndDate())
                .isTemplate(notification.getIsTemplate())
                .templateName(notification.getTemplateName())
                .templateVariables(notification.getTemplateVariables())
                .isScheduled(notification.getIsScheduled())
                .scheduledSendDate(notification.getScheduledSendDate())
                .isSent(notification.getIsSent())
                .sentAt(notification.getSentAt())
                .sentBy(notification.getSentBy())
                .deliveryMethod(notification.getDeliveryMethod())
                .deliveryStatus(notification.getDeliveryStatus())
                .deliveryError(notification.getDeliveryError())
                .retryCount(notification.getRetryCount())
                .maxRetries(notification.getMaxRetries())
                .nextRetryDate(notification.getNextRetryDate())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    @Override
    public Notification toEntity(NotificationDTO notificationDTO) {
        if (notificationDTO == null) {
            return null;
        }

        return Notification.builder()
                .id(notificationDTO.getId())
                .message(notificationDTO.getMessage())
                .isRead(notificationDTO.getIsRead())
                .type(notificationDTO.getType())
                .priority(notificationDTO.getPriority())
                .title(notificationDTO.getTitle())
                .description(notificationDTO.getDescription())
                .category(notificationDTO.getCategory())
                .actionUrl(notificationDTO.getActionUrl())
                .actionText(notificationDTO.getActionText())
                .expiryDate(notificationDTO.getExpiryDate())
                .scheduledDate(notificationDTO.getScheduledDate())
                .isDismissible(notificationDTO.getIsDismissible())
                .isUrgent(notificationDTO.getIsUrgent())
                .isSilent(notificationDTO.getIsSilent())
                .icon(notificationDTO.getIcon())
                .color(notificationDTO.getColor())
                .readAt(notificationDTO.getReadAt())
                .readBy(notificationDTO.getReadBy())
                .isArchived(notificationDTO.getIsArchived())
                .archiveDate(notificationDTO.getArchiveDate())
                .archivedBy(notificationDTO.getArchivedBy())
                .archiveReason(notificationDTO.getArchiveReason())
                .isSystem(notificationDTO.getIsSystem())
                .source(notificationDTO.getSource())
                .sourceId(notificationDTO.getSourceId())
                .isBroadcast(notificationDTO.getIsBroadcast())
                .broadcastTarget(notificationDTO.getBroadcastTarget())
                .isRecurring(notificationDTO.getIsRecurring())
                .recurrencePattern(notificationDTO.getRecurrencePattern())
                .recurrenceEndDate(notificationDTO.getRecurrenceEndDate())
                .isTemplate(notificationDTO.getIsTemplate())
                .templateName(notificationDTO.getTemplateName())
                .templateVariables(notificationDTO.getTemplateVariables())
                .isScheduled(notificationDTO.getIsScheduled())
                .scheduledSendDate(notificationDTO.getScheduledSendDate())
                .isSent(notificationDTO.getIsSent())
                .sentAt(notificationDTO.getSentAt())
                .sentBy(notificationDTO.getSentBy())
                .deliveryMethod(notificationDTO.getDeliveryMethod())
                .deliveryStatus(notificationDTO.getDeliveryStatus())
                .deliveryError(notificationDTO.getDeliveryError())
                .retryCount(notificationDTO.getRetryCount())
                .maxRetries(notificationDTO.getMaxRetries())
                .nextRetryDate(notificationDTO.getNextRetryDate())
                .build();
    }

    @Override
    public List<NotificationDTO> toDtoList(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }

        return notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 