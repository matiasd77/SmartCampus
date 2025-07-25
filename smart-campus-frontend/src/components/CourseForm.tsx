import React, { useState, useEffect } from 'react';
import { useToast } from '../contexts/ToastContext';
import { professorsAPI } from '../services/api';
import { COURSE_STATUSES, DEPARTMENTS } from '../types/dashboard';
import type { Course, CourseRequest, Professor } from '../types/dashboard';

interface CourseFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (courseData: CourseRequest) => Promise<void>;
  course?: Course | null;
  isEditing?: boolean;
}

const CourseForm: React.FC<CourseFormProps> = ({
  isOpen,
  onClose,
  onSubmit,
  course,
  isEditing = false
}) => {
  const { showSuccess, showError } = useToast();
  const [isLoading, setIsLoading] = useState(false);
  const [professors, setProfessors] = useState<Professor[]>([]);
  const [formData, setFormData] = useState<CourseRequest>({
    name: '',
    code: '',
    description: '',
    credits: 3,
    department: '',
    professorId: 0,
    maxStudents: 30,
    status: 'ACTIVE'
  });

  // Load professors for the dropdown
  useEffect(() => {
    if (isOpen) {
      loadProfessors();
    }
  }, [isOpen]);

  // Initialize form data when editing
  useEffect(() => {
    if (course && isEditing) {
      setFormData({
        name: course.name,
        code: course.code,
        description: course.description || '',
        credits: course.credits,
        department: course.department,
        professorId: course.professorId,
        maxStudents: course.maxStudents,
        status: course.status
      });
    } else {
      // Reset form for new course
      setFormData({
        name: '',
        code: '',
        description: '',
        credits: 3,
        department: '',
        professorId: 0,
        maxStudents: 30,
        status: 'ACTIVE'
      });
    }
  }, [course, isEditing, isOpen]);

  const loadProfessors = async () => {
    try {
      const response = await professorsAPI.getProfessors(0, 100); // Get all professors
      if (response.success && response.data) {
        setProfessors(response.data.content || []);
      }
    } catch (error) {
      console.error('Error loading professors:', error);
      showError('Error', 'Failed to load professors');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'credits' || name === 'maxStudents' || name === 'professorId' 
        ? parseInt(value) || 0 
        : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validation
    if (!formData.name.trim()) {
      showError('Validation Error', 'Course name is required');
      return;
    }
    if (!formData.code.trim()) {
      showError('Validation Error', 'Course code is required');
      return;
    }
    if (!formData.department.trim()) {
      showError('Validation Error', 'Department is required');
      return;
    }
    if (formData.professorId === 0) {
      showError('Validation Error', 'Please select a professor');
      return;
    }
    if (formData.credits <= 0) {
      showError('Validation Error', 'Credits must be greater than 0');
      return;
    }
    if (formData.maxStudents <= 0) {
      showError('Validation Error', 'Maximum students must be greater than 0');
      return;
    }

    setIsLoading(true);
    try {
      await onSubmit(formData);
      showSuccess('Success', `Course ${isEditing ? 'updated' : 'created'} successfully`);
      onClose();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || `Failed to ${isEditing ? 'update' : 'create'} course`;
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
              {isEditing ? 'Edit Course' : 'Create New Course'}
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
            {/* Course Name */}
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                Course Name *
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., Introduction to Computer Science"
                required
              />
            </div>

            {/* Course Code */}
            <div>
              <label htmlFor="code" className="block text-sm font-medium text-gray-700">
                Course Code *
              </label>
              <input
                type="text"
                id="code"
                name="code"
                value={formData.code}
                onChange={handleInputChange}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="e.g., CS101"
                required
              />
            </div>

            {/* Description */}
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                Description
              </label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows={3}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                placeholder="Course description..."
              />
            </div>

            {/* Department and Credits */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="department" className="block text-sm font-medium text-gray-700">
                  Department *
                </label>
                <select
                  id="department"
                  name="department"
                  value={formData.department}
                  onChange={handleInputChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="">Select Department</option>
                  {DEPARTMENTS.map(dept => (
                    <option key={dept} value={dept}>{dept}</option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="credits" className="block text-sm font-medium text-gray-700">
                  Credits *
                </label>
                <input
                  type="number"
                  id="credits"
                  name="credits"
                  value={formData.credits}
                  onChange={handleInputChange}
                  min="1"
                  max="6"
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
            </div>

            {/* Professor and Max Students */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="professorId" className="block text-sm font-medium text-gray-700">
                  Professor *
                </label>
                <select
                  id="professorId"
                  name="professorId"
                  value={formData.professorId}
                  onChange={handleInputChange}
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                >
                  <option value="0">Select Professor</option>
                  {professors.map(prof => (
                    <option key={prof.id} value={prof.id}>
                      {prof.firstName} {prof.lastName} - {prof.department}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="maxStudents" className="block text-sm font-medium text-gray-700">
                  Maximum Students *
                </label>
                <input
                  type="number"
                  id="maxStudents"
                  name="maxStudents"
                  value={formData.maxStudents}
                  onChange={handleInputChange}
                  min="1"
                  max="200"
                  className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  required
                />
              </div>
            </div>

            {/* Status */}
            <div>
              <label htmlFor="status" className="block text-sm font-medium text-gray-700">
                Status *
              </label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              >
                {COURSE_STATUSES.map(status => (
                  <option key={status.value} value={status.value}>
                    {status.label}
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
                  isEditing ? 'Update Course' : 'Create Course'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CourseForm; 