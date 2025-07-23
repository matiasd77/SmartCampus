import React from 'react';
import type { Notification } from '../types/dashboard';
import { NOTIFICATION_PRIORITIES, NOTIFICATION_TYPES, NOTIFICATION_STATUSES } from '../types/dashboard';

interface NotificationDetailModalProps {
  notification: Notification | null;
  isOpen: boolean;
  onClose: () => void;
  onMarkAsRead?: (id: number) => void;
  onMarkAsArchived?: (id: number) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const NotificationDetailModal: React.FC<NotificationDetailModalProps> = ({
  notification,
  isOpen,
  onClose,
  onMarkAsRead,
  onMarkAsArchived,
  onDelete,
  showActions = false,
}) => {
  if (!isOpen || !notification) {
    return null;
  }

  const getPriorityInfo = (priority: string) => {
    return NOTIFICATION_PRIORITIES.find(p => p.value === priority) || 
           { value: priority, label: priority, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const getTypeInfo = (type: string) => {
    return NOTIFICATION_TYPES.find(t => t.value === type) || 
           { value: type, label: type, color: 'text-gray-600', bgColor: 'bg-gray-100', icon: '📝' };
  };

  const getStatusInfo = (status: string) => {
    return NOTIFICATION_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const priorityInfo = getPriorityInfo(notification.priority);
  const typeInfo = getTypeInfo(notification.type);
  const statusInfo = getStatusInfo(notification.status);

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        {/* Backdrop */}
        <div 
          className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          onClick={handleBackdropClick}
        ></div>

        {/* Modal */}
        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
          {/* Header */}
          <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-2">
                  <span className="text-2xl">{typeInfo.icon}</span>
                  <h3 className="text-lg leading-6 font-medium text-gray-900">
                    {notification.title}
                  </h3>
                  {!notification.read && (
                    <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      New
                    </span>
                  )}
                </div>
                <div className="flex items-center space-x-4 text-sm mb-4">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${priorityInfo.color} ${priorityInfo.bgColor}`}>
                    {priorityInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${typeInfo.color} ${typeInfo.bgColor}`}>
                    {typeInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusInfo.color} ${statusInfo.bgColor}`}>
                    {statusInfo.label}
                  </span>
                </div>
              </div>
              
              {showActions && (
                <div className="flex items-center space-x-2 ml-4">
                  {!notification.read && onMarkAsRead && (
                    <button
                      onClick={() => onMarkAsRead(notification.id)}
                      className="text-green-600 hover:text-green-800 p-1"
                      title="Mark as read"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                    </button>
                  )}
                  {notification.status !== 'ARCHIVED' && onMarkAsArchived && (
                    <button
                      onClick={() => onMarkAsArchived(notification.id)}
                      className="text-gray-600 hover:text-gray-800 p-1"
                      title="Archive"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
                      </svg>
                    </button>
                  )}
                  {onDelete && (
                    <button
                      onClick={() => onDelete(notification.id)}
                      className="text-red-600 hover:text-red-800 p-1"
                      title="Delete notification"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  )}
                  <button
                    onClick={onClose}
                    className="text-gray-400 hover:text-gray-600 p-1"
                    title="Close"
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              )}
            </div>

            {/* Message */}
            <div className="mb-6">
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {notification.message}
                </p>
              </div>
            </div>

            {/* Metadata */}
            <div className="border-t border-gray-200 pt-4">
              <dl className="grid grid-cols-1 gap-x-4 gap-y-2 text-sm">
                {notification.sender && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">From:</dt>
                    <dd className="text-gray-900">
                      {notification.sender.firstName} {notification.sender.lastName}
                    </dd>
                  </div>
                )}
                <div className="flex justify-between">
                  <dt className="text-gray-500">Created:</dt>
                  <dd className="text-gray-900">{formatDate(notification.createdAt)}</dd>
                </div>
                {notification.readAt && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Read:</dt>
                    <dd className="text-gray-900">{formatDate(notification.readAt)}</dd>
                  </div>
                )}
                {notification.archivedAt && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Archived:</dt>
                    <dd className="text-gray-900">{formatDate(notification.archivedAt)}</dd>
                  </div>
                )}
                {notification.relatedEntityId && notification.relatedEntityType && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Related:</dt>
                    <dd className="text-gray-900">
                      {notification.relatedEntityType} #{notification.relatedEntityId}
                    </dd>
                  </div>
                )}
                {notification.sender?.email && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Contact:</dt>
                    <dd className="text-gray-900">{notification.sender.email}</dd>
                  </div>
                )}
              </dl>
            </div>
          </div>

          {/* Footer */}
          <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button
              type="button"
              onClick={onClose}
              className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}; 