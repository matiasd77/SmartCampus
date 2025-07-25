import React, { useState, useEffect } from 'react';
import { useToast } from '../contexts/ToastContext';
import { adminAPI } from '../services/api';
import { NOTIFICATION_TYPES, NOTIFICATION_PRIORITIES } from '../types/dashboard';
import type { Notification, NotificationRequest } from '../types/dashboard';
import type { User } from '../types/auth';

interface NotificationFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (notificationData: NotificationRequest) => Promise<void>;
  notification?: Notification | null;
  isEditing?: boolean;
}

const NotificationForm: React.FC<NotificationFormProps> = ({
  isOpen,
  onClose,
  onSubmit,
  notification,
  isEditing = false
}) => {
  const { showSuccess, showError } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [formData, setFormData] = useState<NotificationRequest>({
    title: '',
    message: '',
    userId: 0,
    type: 'GENERAL',
    priority: 'MEDIUM'
  });

  // Load users for the dropdown
  useEffect(() => {
    if (isOpen) {
      loadUsers();
    }
  }, [isOpen]);

  // Initialize form data when editing
  useEffect(() => {
    if (notification && isEditing) {
      setFormData({
        title: notification.title,
        message: notification.message,
        userId: notification.userId,
        type: notification.type,
        priority: notification.priority
      });
    } else {
      // Reset form for new notification
      setFormData({
        title: '',
        message: '',
        userId: 0,
        type: 'GENERAL',
        priority: 'MEDIUM'
      });
    }
  }, [notification, isEditing, isOpen]);

  const loadUsers = async () => {
    try {
      const response = await adminAPI.getAllUsers();
      if (response.success && response.data) {
        setUsers(response.data.content || []);
      }
    } catch (error) {
      console.error('Error loading users:', error);
      showError('Error', 'Failed to load users');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'userId' ? parseInt(value) || 0 : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.title.trim()) {
      showError('Validation Error', 'Title is required');
      return;
    }
    if (!formData.message.trim()) {
      showError('Validation Error', 'Message is required');
      return;
    }
    if (formData.userId === 0) {
      showError('Validation Error', 'Please select a user');
      return;
    }

    setIsLoading(true);
    try {
      await onSubmit(formData);
      showSuccess('Success', `Notification ${isEditing ? 'updated' : 'created'} successfully`);
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || `Failed to ${isEditing ? 'update' : 'create'} notification`;
      showError('Error', errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div className="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
        <div className="mt-3">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-medium text-gray-900">
              {isEditing ? 'Edit Notification' : 'Create New Notification'}
            </h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Title */}
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                Title *
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Enter notification title"
                required
              />
            </div>

            {/* Message */}
            <div>
              <label htmlFor="message" className="block text-sm font-medium text-gray-700">
                Message *
              </label>
              <textarea
                id="message"
                name="message"
                value={formData.message}
                onChange={handleInputChange}
                rows={4}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Enter notification message..."
                required
              />
            </div>

            {/* User and Type */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="userId" className="block text-sm font-medium text-gray-700">
                  User *
                </label>
                <select
                  id="userId"
                  name="userId"
                  value={formData.userId}
                  onChange={handleInputChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="0">Select User</option>
                  {users.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.firstName} {user.lastName} - {user.email}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="type" className="block text-sm font-medium text-gray-700">
                  Type *
                </label>
                <select
                  id="type"
                  name="type"
                  value={formData.type}
                  onChange={handleInputChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  {NOTIFICATION_TYPES.map(type => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Priority */}
            <div>
              <label htmlFor="priority" className="block text-sm font-medium text-gray-700">
                Priority *
              </label>
              <select
                id="priority"
                name="priority"
                value={formData.priority}
                onChange={handleInputChange}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              >
                {NOTIFICATION_PRIORITIES.map(priority => (
                  <option key={priority.value} value={priority.value}>
                    {priority.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Action Buttons */}
            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-500"
                disabled={isLoading}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isLoading}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isLoading ? (
                  <span className="flex items-center">
                    <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    {isEditing ? 'Updating...' : 'Creating...'}
                  </span>
                ) : (
                  isEditing ? 'Update Notification' : 'Create Notification'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default NotificationForm; 