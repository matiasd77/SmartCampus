import React from 'react';
import { Link } from 'react-router-dom';
import type { Professor } from '../types/dashboard';
import { PROFESSOR_STATUSES, PROFESSOR_RANKS } from '../types/dashboard';

interface ProfessorCardProps {
  professor: Professor;
  onEdit?: (professor: Professor) => void;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

export const ProfessorCard: React.FC<ProfessorCardProps> = ({
  professor,
  onEdit,
  onDelete,
  showActions = false,
}) => {
  const getStatusInfo = (status: string) => {
    return PROFESSOR_STATUSES.find(s => s.value === status) || 
           { value: status, label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const getRankInfo = (rank: string) => {
    return PROFESSOR_RANKS.find(r => r.value === rank) || 
           { value: rank, label: rank, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const statusInfo = getStatusInfo(professor.status);
  const rankInfo = getRankInfo(professor.rank);

  return (
    <div className="bg-white shadow rounded-lg hover:shadow-md transition-shadow border border-gray-200">
      <div className="p-6">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <div className="flex items-center space-x-3 mb-2">
              <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                <span className="text-green-600 font-semibold text-sm">
                  {professor.firstName.charAt(0)}{professor.lastName.charAt(0)}
                </span>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">
                  {professor.firstName} {professor.lastName}
                </h3>
                <p className="text-sm text-gray-500">{professor.department}</p>
              </div>
            </div>
            <div className="flex items-center space-x-4 text-sm">
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusInfo.color} ${statusInfo.bgColor}`}>
                {statusInfo.label}
              </span>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${rankInfo.color} ${rankInfo.bgColor}`}>
                {rankInfo.label}
              </span>
            </div>
          </div>
          
          {showActions && (
            <div className="flex items-center space-x-2 ml-4">
              <Link
                to={`/professors/${professor.id}`}
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
                  onClick={() => onEdit(professor)}
                  className="text-green-600 hover:text-green-800 p-1"
                  title="Edit professor"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </button>
              )}
              {onDelete && (
                <button
                  onClick={() => onDelete(professor.id)}
                  className="text-red-600 hover:text-red-800 p-1"
                  title="Delete professor"
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
            <span className="text-gray-600">{professor.email}</span>
          </div>
          
          <div className="flex items-center text-sm">
            <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
            <span className="text-gray-600">{professor.department}</span>
          </div>
          
          {professor.specialization && (
            <div className="flex items-center text-sm">
              <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
              <span className="text-gray-600">{professor.specialization}</span>
            </div>
          )}
          
          {professor.phone && (
            <div className="flex items-center text-sm">
              <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              <span className="text-gray-600">{professor.phone}</span>
            </div>
          )}
          
          {professor.office && (
            <div className="flex items-center text-sm">
              <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span className="text-gray-600">Office: {professor.office}</span>
            </div>
          )}
          
          <div className="flex items-center text-sm">
            <svg className="w-4 h-4 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span className="text-gray-600">Hired: {formatDate(professor.hireDate)}</span>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="flex items-center justify-between">
            <Link
              to={`/professors/${professor.id}`}
              className="text-blue-600 hover:text-blue-800 font-medium text-sm"
            >
              View Details →
            </Link>
            {professor.user && (
              <span className="text-xs text-gray-500">
                User Account: {professor.user.username}
              </span>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}; 