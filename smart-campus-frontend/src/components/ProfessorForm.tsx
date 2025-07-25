import React, { useState, useEffect } from 'react';
import { X, Save, User, Mail, Building, Award, Phone, MapPin } from 'lucide-react';
import { PROFESSOR_STATUSES, PROFESSOR_RANKS } from '../types/dashboard';
import type { Professor, ProfessorRequest } from '../types/dashboard';
import { authAPI } from '../services/api';

interface ProfessorFormProps {
  professor?: Professor;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (professor: ProfessorRequest) => Promise<void>;
  isLoading?: boolean;
}

export const ProfessorForm: React.FC<ProfessorFormProps> = ({
  professor,
  isOpen,
  onClose,
  onSubmit,
  isLoading = false
}) => {
  console.log('🔍 ProfessorForm render:', { isOpen, professor, isLoading });

  const [formData, setFormData] = useState<ProfessorRequest>({
    firstName: '',
    lastName: '',
    email: '',
    department: '',
    academicRank: 'ASSISTANT_PROFESSOR',
    status: 'ACTIVE'
  });

  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    console.log('🔍 ProfessorForm useEffect:', { professor, isOpen });
    if (professor) {
      setFormData({
        firstName: professor.firstName || '',
        lastName: professor.lastName || '',
        email: professor.email || '',
        department: professor.department || '',
        academicRank: professor.rank || 'ASSISTANT_PROFESSOR',
        status: professor.status || 'ACTIVE'
      });
      setPassword('');
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        department: '',
        academicRank: 'ASSISTANT_PROFESSOR',
        status: 'ACTIVE'
      });
      setPassword('');
    }
    setErrors({});
  }, [professor, isOpen]);

  const handleInputChange = (field: keyof ProfessorRequest, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

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

    if (!formData.department.trim()) {
      newErrors.department = 'Department is required';
    }

    if (!professor && !password.trim()) {
      newErrors.password = 'Password is required for new professors';
    } else if (password.trim() && password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
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
      if (!professor) {
        // Create new professor - first register user, then create professor profile
        const userData = {
          name: `${formData.firstName} ${formData.lastName}`,
          email: formData.email,
          password: password,
          role: 'PROFESSOR' as const
        };

        console.log('Creating new professor user:', userData);
        const userResponse = await authAPI.register(userData);
        console.log('User created:', userResponse);

        if (userResponse.success && userResponse.data) {
          const finalProfessorData = {
            ...formData,
            userId: userResponse.data.id
          };

          console.log('🔍 Creating professor profile with data:', finalProfessorData);
          await onSubmit(finalProfessorData);
        } else {
          throw new Error('Failed to create user account');
        }
      } else {
        // Update existing professor
        await onSubmit(formData);
      }
    } catch (error: any) {
      console.error('Error saving professor:', error);
      console.error('Error response data:', error.response?.data);
      console.error('Error response status:', error.response?.status);
      console.error('Error response headers:', error.response?.headers);
      const errorMessage = error.response?.data?.message || 'Failed to save professor';
      setErrors({ general: errorMessage });
    }
  };

  if (!isOpen) return null;

  console.log('🔍 ProfessorForm rendering modal with isOpen:', isOpen);

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 rounded-t-2xl">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-900">
              {professor ? 'Edit Professor' : 'Create New Professor'}
            </h2>
            <button
              onClick={onClose}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="w-6 h-6 text-gray-500" />
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {!professor && (
            <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 mb-6">
              <p className="text-blue-800 text-sm">
                <strong>Note:</strong> This will create a new professor account with login credentials. 
                The professor will be able to log in using their email and password.
              </p>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* First Name */}
            <div>
              <label htmlFor="firstName" className="block text-sm font-semibold text-gray-700 mb-2">
                <User className="w-4 h-4 inline mr-2" />
                First Name *
              </label>
              <input
                type="text"
                id="firstName"
                name="firstName"
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
              <label htmlFor="lastName" className="block text-sm font-semibold text-gray-700 mb-2">
                <User className="w-4 h-4 inline mr-2" />
                Last Name *
              </label>
              <input
                type="text"
                id="lastName"
                name="lastName"
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
              <label htmlFor="email" className="block text-sm font-semibold text-gray-700 mb-2">
                <Mail className="w-4 h-4 inline mr-2" />
                Email *
              </label>
              <input
                type="email"
                id="email"
                name="email"
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

            {/* Department */}
            <div>
              <label htmlFor="department" className="block text-sm font-semibold text-gray-700 mb-2">
                <Building className="w-4 h-4 inline mr-2" />
                Department *
              </label>
              <input
                type="text"
                id="department"
                name="department"
                value={formData.department}
                onChange={(e) => handleInputChange('department', e.target.value)}
                className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                  errors.department ? 'border-red-300' : 'border-gray-300'
                }`}
                placeholder="Enter department"
              />
              {errors.department && (
                <p className="mt-1 text-sm text-red-600">{errors.department}</p>
              )}
            </div>

            {/* Rank */}
            <div>
              <label htmlFor="rank" className="block text-sm font-semibold text-gray-700 mb-2">
                <Award className="w-4 h-4 inline mr-2" />
                Academic Rank
              </label>
              <select
                id="rank"
                name="rank"
                value={formData.academicRank}
                onChange={(e) => handleInputChange('academicRank', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              >
                {PROFESSOR_RANKS.map((rank) => (
                  <option key={rank.value} value={rank.value}>
                    {rank.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Status */}
            <div>
              <label htmlFor="status" className="block text-sm font-semibold text-gray-700 mb-2">
                Status
              </label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={(e) => handleInputChange('status', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              >
                {PROFESSOR_STATUSES.map((status) => (
                  <option key={status.value} value={status.value}>
                    {status.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Password (only for new professors) */}
            {!professor && (
              <div className="md:col-span-2">
                <label htmlFor="password" className="block text-sm font-semibold text-gray-700 mb-2">
                  Password *
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className={`w-full px-4 py-3 border rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors ${
                    errors.password ? 'border-red-300' : 'border-gray-300'
                  }`}
                  placeholder="Enter password for new account"
                />
                {errors.password && (
                  <p className="mt-1 text-sm text-red-600">{errors.password}</p>
                )}
              </div>
            )}
          </div>

          {errors.general && (
            <div className="bg-red-50 border border-red-200 rounded-xl p-4">
              <p className="text-red-800 text-sm">{errors.general}</p>
            </div>
          )}

          <div className="flex justify-end space-x-4 pt-6 border-t border-gray-200">
            <button
              type="button"
              onClick={onClose}
              className="px-6 py-3 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100 flex items-center"
            >
              <Save className="w-4 h-4 mr-2" />
              {isLoading ? 'Saving...' : (professor ? 'Update Professor' : 'Create Professor')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}; 