import React from 'react';
import type { Announcement } from '../types/dashboard';
import { ANNOUNCEMENT_PRIORITIES, ANNOUNCEMENT_TYPES, ANNOUNCEMENT_STATUSES } from '../types/dashboard';

interface AnnouncementDetailModalProps {
  announcement: Announcement | null;
  isOpen: boolean;
  onClose: () => void;
  onEdit?: (announcement: Announcement) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const AnnouncementDetailModal: React.FC<AnnouncementDetailModalProps> = ({
  announcement,
  isOpen,
  onClose,
  onEdit,
  onDelete,
  showActions = false,
}) => {
  if (!isOpen || !announcement) {
    return null;
  }

  const getPriorityInfo = (priority: string) => {
    return ANNOUNCEMENT_PRIORITIES.find(p => p.value === priority) || 
           { value: priority, label: priority, color: 'text-gray-600' };
  };

  const getTypeInfo = (type: string) => {
    return ANNOUNCEMENT_TYPES.find(t => t.value === type) || 
           { value: type, label: type, color: 'text-gray-600' };
  };

  const getStatusInfo = (status: string) => {
    return ANNOUNCEMENT_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600' };
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

  const priorityInfo = getPriorityInfo(announcement.priority);
  const typeInfo = getTypeInfo(announcement.type);
  const statusInfo = getStatusInfo(announcement.status);

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
                <h3 className="text-lg leading-6 font-medium text-gray-900 mb-2">
                  {announcement.title}
                </h3>
                <div className="flex items-center space-x-4 text-sm mb-4">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${priorityInfo.color} bg-gray-100`}>
                    {priorityInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${typeInfo.color} bg-gray-100`}>
                    {typeInfo.label}
                  </span>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusInfo.color} bg-gray-100`}>
                    {statusInfo.label}
                  </span>
                </div>
              </div>
              
              {showActions && (
                <div className="flex items-center space-x-2 ml-4">
                  {onEdit && (
                    <button
                      onClick={() => onEdit(announcement)}
                      className="text-blue-600 hover:text-blue-800 p-1"
                      title="Edit announcement"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </button>
                  )}
                  {onDelete && (
                    <button
                      onClick={() => onDelete(announcement.id)}
                      className="text-red-600 hover:text-red-800 p-1"
                      title="Delete announcement"
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

            {/* Content */}
            <div className="mb-6">
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {announcement.content}
                </p>
              </div>
            </div>

            {/* Metadata */}
            <div className="border-t border-gray-200 pt-4">
              <dl className="grid grid-cols-1 gap-x-4 gap-y-2 text-sm">
                <div className="flex justify-between">
                  <dt className="text-gray-500">Created by:</dt>
                  <dd className="text-gray-900">
                    {announcement.createdByUser 
                      ? `${announcement.createdByUser.firstName} ${announcement.createdByUser.lastName}`
                      : announcement.createdBy
                    }
                  </dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-gray-500">Created:</dt>
                  <dd className="text-gray-900">{formatDate(announcement.createdAt)}</dd>
                </div>
                {announcement.updatedAt !== announcement.createdAt && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Last updated:</dt>
                    <dd className="text-gray-900">{formatDate(announcement.updatedAt)}</dd>
                  </div>
                )}
                {announcement.createdByUser?.email && (
                  <div className="flex justify-between">
                    <dt className="text-gray-500">Contact:</dt>
                    <dd className="text-gray-900">{announcement.createdByUser.email}</dd>
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