import React, { useState, useEffect } from 'react';
import { X, Save, UserPlus } from 'lucide-react';
import type { Student, StudentRequest } from '../types/dashboard';
import { STUDENT_YEARS, STUDENT_STATUSES } from '../types/dashboard';
import { authAPI } from '../services/api';

interface StudentFormProps {
  student?: Student;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: StudentRequest) => Promise<void>;
  isLoading?: boolean;
}

export const StudentForm: React.FC<StudentFormProps> = ({
  student,
  isOpen,
  onClose,
  onSubmit,
  isLoading = false,
}) => {
  const [formData, setFormData] = useState<StudentRequest>({
    studentId: '',
    firstName: '',
    lastName: '',
    email: '',
    yearOfStudy: 1,
    major: '',
    status: 'ACTIVE',
    userId: 0, // Will be set after user creation
  });

  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (student) {
      setFormData({
        studentId: student.studentId || '',
        firstName: student.firstName || '',
        lastName: student.lastName || '',
        email: student.email || '',
        yearOfStudy: student.yearOfStudy || 1,
        major: student.major || '',
        status: student.status || 'ACTIVE',
        userId: student.user?.id || 0,
      });
      setPassword('');
    } else {
      setFormData({
        studentId: '',
        firstName: '',
        lastName: '',
        email: '',
        yearOfStudy: 1,
        major: '',
        status: 'ACTIVE',
        userId: 0,
      });
      setPassword('');
    }
    setErrors({});
  }, [student, isOpen]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.studentId.trim()) {
      newErrors.studentId = 'Student ID is required';
    } else if (!/^[A-Z0-9]{8,10}$/.test(formData.studentId)) {
      newErrors.studentId = 'Student ID must be 8-10 characters long and contain only uppercase letters and numbers';
    }

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }

    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email is invalid';
    }

    if (!formData.major.trim()) {
      newErrors.major = 'Major is required';
    }

    if (formData.yearOfStudy < 1 || formData.yearOfStudy > 10) {
      newErrors.yearOfStudy = 'Year must be between 1 and 10';
    }

    if (!formData.status) {
      newErrors.status = 'Status is required';
    }

    // For new students, password is required
    if (!student && !password.trim()) {
      newErrors.password = 'Password is required for new student accounts';
    } else if (!student && password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters long';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      let finalStudentData = { ...formData };

      // If creating a new student, create user account first
      if (!student) {
        // Create user account
        const userData = {
          name: `${formData.firstName} ${formData.lastName}`,
          email: formData.email,
          password: password,
          role: 'STUDENT' as const
        };

        const response = await authAPI.register(userData);
        // Extract user ID from the LoginResponse structure
        if (response.data) {
          finalStudentData.userId = response.data.id;
        } else {
          throw new Error('Failed to create user account - no user ID returned');
        }
      }

      await onSubmit(finalStudentData);
      onClose();
    } catch (error) {
      console.error('Error submitting student form:', error);
    }
  };

  const handleInputChange = (field: keyof StudentRequest, value: string | number) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
              <UserPlus className="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">
                {student ? 'Edit Student' : 'Add New Student'}
              </h2>
              <p className="text-sm text-gray-600">
                {student ? 'Update student information' : 'Create a new student account and profile'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Student ID */}
            <div>
              <label htmlFor="studentId" className="block text-sm font-medium text-gray-700 mb-2">
                Student ID *
              </label>
              <input
                type="text"
                id="studentId"
                value={formData.studentId}
                onChange={(e) => handleInputChange('studentId', e.target.value.toUpperCase())}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.studentId ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="e.g., STU123456"
                maxLength={10}
              />
              {errors.studentId && (
                <p className="mt-1 text-sm text-red-600">{errors.studentId}</p>
              )}
              <p className="mt-1 text-xs text-gray-500">8-10 characters, uppercase letters and numbers only</p>
            </div>

            {/* First Name */}
            <div>
              <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-2">
                First Name *
              </label>
              <input
                type="text"
                id="firstName"
                value={formData.firstName}
                onChange={(e) => handleInputChange('firstName', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.firstName ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter first name"
              />
              {errors.firstName && (
                <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>
              )}
            </div>

            {/* Last Name */}
            <div>
              <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-2">
                Last Name *
              </label>
              <input
                type="text"
                id="lastName"
                value={formData.lastName}
                onChange={(e) => handleInputChange('lastName', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.lastName ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter last name"
              />
              {errors.lastName && (
                <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>
              )}
            </div>

            {/* Email */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Email *
              </label>
              <input
                type="email"
                id="email"
                value={formData.email}
                onChange={(e) => handleInputChange('email', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.email ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter email address"
              />
              {errors.email && (
                <p className="mt-1 text-sm text-red-600">{errors.email}</p>
              )}
            </div>

            {/* Password (only for new students) */}
            {!student && (
              <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                  Password *
                </label>
                <input
                  type="password"
                  id="password"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (errors.password) {
                      setErrors(prev => ({ ...prev, password: '' }));
                    }
                  }}
                  className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                    errors.password ? 'border-red-300' : 'border-gray-300'
                  }`}
                  placeholder="Enter password (min 6 characters)"
                />
                {errors.password && (
                  <p className="mt-1 text-sm text-red-600">{errors.password}</p>
                )}
                <p className="mt-1 text-xs text-gray-500">Minimum 6 characters</p>
              </div>
            )}

            {/* Major */}
            <div>
              <label htmlFor="major" className="block text-sm font-medium text-gray-700 mb-2">
                Major *
              </label>
              <input
                type="text"
                id="major"
                value={formData.major}
                onChange={(e) => handleInputChange('major', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.major ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter major"
              />
              {errors.major && (
                <p className="mt-1 text-sm text-red-600">{errors.major}</p>
              )}
            </div>

            {/* Year of Study */}
            <div>
              <label htmlFor="yearOfStudy" className="block text-sm font-medium text-gray-700 mb-2">
                Year of Study *
              </label>
              <select
                id="yearOfStudy"
                value={formData.yearOfStudy}
                onChange={(e) => handleInputChange('yearOfStudy', parseInt(e.target.value))}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.yearOfStudy ? 'border-red-300' : 'border-gray-300'
                }`}
              >
                {STUDENT_YEARS.map((year) => (
                  <option key={year.value} value={year.value}>
                    {year.label}
                  </option>
                ))}
              </select>
              {errors.yearOfStudy && (
                <p className="mt-1 text-sm text-red-600">{errors.yearOfStudy}</p>
              )}
            </div>

            {/* Status */}
            <div>
              <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-2">
                Status *
              </label>
              <select
                id="status"
                value={formData.status}
                onChange={(e) => handleInputChange('status', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.status ? 'border-red-300' : 'border-gray-300'
                }`}
              >
                {STUDENT_STATUSES.map((status) => (
                  <option key={status.value} value={status.value}>
                    {status.label}
                  </option>
                ))}
              </select>
              {errors.status && (
                <p className="mt-1 text-sm text-red-600">{errors.status}</p>
              )}
            </div>
          </div>

          {/* Note about account creation */}
          {!student && (
            <div className="bg-green-50 border border-green-200 rounded-xl p-4">
              <p className="text-sm text-green-800">
                <strong>Account Creation:</strong> This will create both a user account and a student profile. 
                The student will be able to log in using their email and password.
              </p>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex items-center justify-end space-x-4 pt-6 border-t border-gray-200">
            <button
              type="button"
              onClick={onClose}
              className="px-6 py-3 text-gray-700 bg-gray-100 rounded-xl hover:bg-gray-200 transition-colors font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed font-medium flex items-center space-x-2"
            >
              {isLoading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Saving...</span>
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  <span>{student ? 'Update Student' : 'Create Student Account'}</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}; 