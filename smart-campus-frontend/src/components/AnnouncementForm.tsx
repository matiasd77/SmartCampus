import React, { useState, useEffect } from 'react';
import { useToast } from '../contexts/ToastContext';
import { coursesAPI } from '../services/api';
import { ANNOUNCEMENT_PRIORITIES, ANNOUNCEMENT_TYPES, ANNOUNCEMENT_STATUSES } from '../types/dashboard';
import type { Announcement, AnnouncementRequest, Course } from '../types/dashboard';

interface AnnouncementFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (announcementData: AnnouncementRequest) => Promise<void>;
  announcement?: Announcement | null;
  isEditing?: boolean;
}

const AnnouncementForm: React.FC<AnnouncementFormProps> = ({
  isOpen,
  onClose,
  onSubmit,
  announcement,
  isEditing = false
}) => {
  const { showSuccess, showError } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const [courses, setCourses] = useState<Course[]>([]);
  const [formData, setFormData] = useState<AnnouncementRequest>({
    title: '',
    content: '',
    courseId: undefined,
    priority: 'MEDIUM',
    type: 'GENERAL',
    isPublic: true
  });

  // Load courses for the dropdown
  useEffect(() => {
    if (isOpen) {
      loadCourses();
    }
  }, [isOpen]);

  // Initialize form data when editing
  useEffect(() => {
    if (announcement && isEditing) {
      setFormData({
        title: announcement.title,
        content: announcement.content,
        courseId: announcement.courseId,
        priority: announcement.priority,
        type: announcement.type,
        isPublic: announcement.isPublic
      });
    } else {
      // Reset form for new announcement
      setFormData({
        title: '',
        content: '',
        courseId: undefined,
        priority: 'MEDIUM',
        type: 'GENERAL',
        isPublic: true
      });
    }
  }, [announcement, isEditing, isOpen]);

  const loadCourses = async () => {
    try {
      const response = await coursesAPI.getCourses(0, 100); // Get all courses
      if (response.success && response.data) {
        setCourses(response.data.content || []);
      }
    } catch (error) {
      console.error('Error loading courses:', error);
      showError('Error', 'Failed to load courses');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.title.trim()) {
      showError('Validation Error', 'Title is required');
      return;
    }
    if (!formData.content.trim()) {
      showError('Validation Error', 'Content is required');
      return;
    }

    setIsLoading(true);
    try {
      await onSubmit(formData);
      showSuccess('Success', `Announcement ${isEditing ? 'updated' : 'created'} successfully`);
      onClose();
    } catch (error: any) {
      console.error('AnnouncementForm - Error details:', error);
      
      // Enhanced error handling
      let errorMessage = `Failed to ${isEditing ? 'update' : 'create'} announcement`;
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.status === 400) {
        errorMessage = 'Invalid data provided. Please check your input.';
      } else if (error.response?.status === 403) {
        errorMessage = 'You do not have permission to create announcements.';
      } else if (error.response?.status === 401) {
        errorMessage = 'Authentication required. Please log in again.';
      } else if (error.message) {
        errorMessage = error.message;
      }
      
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
              {isEditing ? 'Edit Announcement' : 'Create New Announcement'}
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
                placeholder="Enter announcement title"
                required
              />
            </div>

            {/* Content */}
            <div>
              <label htmlFor="content" className="block text-sm font-medium text-gray-700">
                Content *
              </label>
              <textarea
                id="content"
                name="content"
                value={formData.content}
                onChange={handleInputChange}
                rows={6}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Enter announcement content..."
                required
              />
            </div>

            {/* Course and Type */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="courseId" className="block text-sm font-medium text-gray-700">
                  Course (Optional)
                </label>
                <select
                  id="courseId"
                  name="courseId"
                  value={formData.courseId || ''}
                  onChange={handleInputChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">General Announcement</option>
                  {courses.map(course => (
                    <option key={course.id} value={course.id}>
                      {course.code} - {course.name}
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
                  {ANNOUNCEMENT_TYPES.map(type => (
                    <option key={type.value} value={type.value}>
                      {type.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Priority and Public */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                  {ANNOUNCEMENT_PRIORITIES.map(priority => (
                    <option key={priority.value} value={priority.value}>
                      {priority.label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex items-center mt-6">
                <input
                  id="isPublic"
                  name="isPublic"
                  type="checkbox"
                  checked={formData.isPublic}
                  onChange={handleInputChange}
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label htmlFor="isPublic" className="ml-2 block text-sm text-gray-900">
                  Make this announcement public
                </label>
              </div>
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
                  isEditing ? 'Update Announcement' : 'Create Announcement'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AnnouncementForm; 