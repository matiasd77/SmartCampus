import React from 'react';
import { Link } from 'react-router-dom';
import type { Student } from '../types/dashboard';
import { STUDENT_STATUSES, STUDENT_YEARS } from '../types/dashboard';

interface StudentCardProps {
  student: Student;
  onEdit?: (student: Student) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const StudentCard: React.FC<StudentCardProps> = ({
  student,
  onEdit,
  onDelete,
  showActions = false,
}) => {
  const getStatusInfo = (status: string) => {
    return STUDENT_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const getYearInfo = (year: number | undefined) => {
    if (year === undefined) {
      return { value: 'Unknown', label: 'Unknown', color: 'text-gray-600', bgColor: 'bg-gray-100' };
    }
    return STUDENT_YEARS.find(y => y.value === year) || 
           { value: year, label: `Year ${year}`, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const statusInfo = getStatusInfo(student.status);
  const yearInfo = getYearInfo(student.yearOfStudy);

  return (
    <div className="bg-white shadow rounded-lg hover:shadow-md transition-shadow border border-gray-200">
      <div className="p-6">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <div className="flex items-center space-x-3 mb-2">
              <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <span className="text-blue-600 font-semibold text-sm">
                  {student.firstName.charAt(0)}{student.lastName.charAt(0)}
                </span>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">
                  {student.firstName} {student.lastName}
                </h3>
                <p className="text-sm text-gray-500">ID: {student.studentId}</p>
              </div>
            </div>
            <div className="flex items-center space-x-4 text-sm">
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusInfo.color} ${statusInfo.bgColor}`}>
                {statusInfo.label}
              </span>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${yearInfo.color} ${yearInfo.bgColor}`}>
                {yearInfo.label}
              </span>
            </div>
          </div>
          
          {showActions && (
            <div className="flex items-center space-x-2 ml-4">
              <Link
                to={`/students/${student.id}`}
                className="text-blue-600 hover:text-blue-800 p-1"
                title="View details"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </Link>
              {onEdit && (
                <button
                  onClick={() => onEdit(student)}
                  className="text-green-600 hover:text-green-800 p-1"
                  title="Edit student"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
              )}
              {onDelete && (
                <button
                  onClick={() => onDelete(student.id)}
                  className="text-red-600 hover:text-red-800 p-1"
                  title="Delete student"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              )}
            </div>
          )}
        </div>

        {/* Details */}
        <div className="space-y-3">
          <div className="flex items-center text-sm">
            <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            <span className="text-gray-600">{student.email}</span>
          </div>
          
          <div className="flex items-center text-sm">
            <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <span className="text-gray-600">{student.major}</span>
          </div>
          
          {student.gpa && (
            <div className="flex items-center text-sm">
              <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span className="text-gray-600">GPA: {student.gpa.toFixed(2)}</span>
            </div>
          )}
          
          {student.phone && (
            <div className="flex items-center text-sm">
              <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              <span className="text-gray-600">{student.phone}</span>
            </div>
          )}
          
          <div className="flex items-center text-sm">
            <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span className="text-gray-600">
              Enrolled: {student.enrollmentDate ? formatDate(student.enrollmentDate) : 'Not specified'}
            </span>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="flex items-center justify-between">
            <Link
              to={`/students/${student.id}`}
              className="text-blue-600 hover:text-blue-800 font-medium text-sm"
            >
              View Details →
            </Link>
            {student.user && (
              <span className="text-xs text-gray-500">
                User Account: {student.user.username}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}; 