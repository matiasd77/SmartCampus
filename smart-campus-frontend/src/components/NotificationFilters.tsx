import React from 'react';
import type { NotificationFilters } from '../types/dashboard';
import { NOTIFICATION_PRIORITIES, NOTIFICATION_TYPES, NOTIFICATION_STATUSES } from '../types/dashboard';
import { 
  Filter, 
  X, 
  Search
} from 'lucide-react';

interface NotificationFiltersPanelProps {
  filters: NotificationFilters;
  onFiltersChange: (filters: NotificationFilters) => void;
  onClearFilters: () => void;
  unreadCount?: number;
  onMarkAllAsRead?: () => void;
}

export const NotificationFiltersPanel: React.FC<NotificationFiltersPanelProps> = ({
  filters,
  onFiltersChange,
  onClearFilters,
  unreadCount = 0,
  onMarkAllAsRead,
}) => {
  const handleFilterChange = (key: keyof NotificationFilters, value: string | boolean | undefined) => {
    onFiltersChange({
      ...filters,
      [key]: value,
    });
  };

  const hasActiveFilters = Object.values(filters).some(value => value !== undefined);

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6 mb-8 overflow-hidden relative">
      {/* Background decoration */}
      <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-purple-100/50 to-violet-100/50 rounded-full -translate-y-16 translate-x-16"></div>
      
      <div className="relative z-10">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between mb-6">
          <div className="flex items-center mb-4 lg:mb-0">
            <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-violet-600 rounded-xl flex items-center justify-center shadow-lg mr-3">
              <Filter className="w-5 h-5 text-white" />
            </div>
            <h3 className="text-xl font-bold text-gray-900">Filters & Actions</h3>
          </div>
          
          <div className="flex flex-col sm:flex-row gap-3">
            {unreadCount > 0 && onMarkAllAsRead && (
              <button
                onClick={onMarkAllAsRead}
                className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105"
              >
                <CheckCircle className="w-4 h-4 mr-2" />
                Mark All as Read ({unreadCount})
              </button>
            )}
            
            {hasActiveFilters && (
              <button
                onClick={onClearFilters}
                className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105"
              >
                <X className="w-4 h-4 mr-2" />
                Clear Filters
              </button>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Status Filter */}
          <div>
            <label htmlFor="status-filter" className="block text-sm font-semibold text-gray-700 mb-2">
              Status
            </label>
            <div className="relative">
              <select
                id="status-filter"
                value={filters.status || ''}
                onChange={(e) => handleFilterChange('status', e.target.value || undefined)}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-200 appearance-none bg-white"
              >
                <option value="">All Statuses</option>
                {NOTIFICATION_STATUSES.map((status) => (
                  <option key={status.value} value={status.value}>
                    {status.label}
                  </option>
                ))}
              </select>
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </div>
          </div>

          {/* Priority Filter */}
          <div>
            <label htmlFor="priority-filter" className="block text-sm font-semibold text-gray-700 mb-2">
              Priority
            </label>
            <div className="relative">
              <select
                id="priority-filter"
                value={filters.priority || ''}
                onChange={(e) => handleFilterChange('priority', e.target.value || undefined)}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-200 appearance-none bg-white"
              >
                <option value="">All Priorities</option>
                {NOTIFICATION_PRIORITIES.map((priority) => (
                  <option key={priority.value} value={priority.value}>
                    {priority.label}
                  </option>
                ))}
              </select>
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </div>
          </div>

          {/* Type Filter */}
          <div>
            <label htmlFor="type-filter" className="block text-sm font-semibold text-gray-700 mb-2">
              Type
            </label>
            <div className="relative">
              <select
                id="type-filter"
                value={filters.type || ''}
                onChange={(e) => handleFilterChange('type', e.target.value || undefined)}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-200 appearance-none bg-white"
              >
                <option value="">All Types</option>
                {NOTIFICATION_TYPES.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.icon} {type.label}
                  </option>
                ))}
              </select>
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </div>
          </div>

          {/* Read Status Filter */}
          <div>
            <label htmlFor="read-filter" className="block text-sm font-semibold text-gray-700 mb-2">
              Read Status
            </label>
            <div className="relative">
              <select
                id="read-filter"
                value={filters.read === undefined ? '' : filters.read.toString()}
                onChange={(e) => handleFilterChange('read', e.target.value === '' ? undefined : e.target.value === 'true')}
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-200 appearance-none bg-white"
              >
                <option value="">All</option>
                <option value="false">Unread</option>
                <option value="true">Read</option>
              </select>
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        {/* Active Filters Display */}
        {hasActiveFilters && (
          <div className="mt-6 pt-6 border-t border-gray-200">
            <div className="flex flex-wrap items-center gap-3">
              <span className="text-sm font-semibold text-gray-700 flex items-center">
                <Search className="w-4 h-4 mr-1" />
                Active filters:
              </span>
              {filters.status && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800 shadow-sm">
                  Status: {NOTIFICATION_STATUSES.find(s => s.value === filters.status)?.label}
                  <button
                    onClick={() => handleFilterChange('status', undefined)}
                    className="ml-2 text-blue-600 hover:text-blue-800 transition-colors duration-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}
              {filters.priority && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800 shadow-sm">
                  Priority: {NOTIFICATION_PRIORITIES.find(p => p.value === filters.priority)?.label}
                  <button
                    onClick={() => handleFilterChange('priority', undefined)}
                    className="ml-2 text-green-600 hover:text-green-800 transition-colors duration-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}
              {filters.type && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-purple-100 text-purple-800 shadow-sm">
                  Type: {NOTIFICATION_TYPES.find(t => t.value === filters.type)?.label}
                  <button
                    onClick={() => handleFilterChange('type', undefined)}
                    className="ml-2 text-purple-600 hover:text-purple-800 transition-colors duration-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}
              {filters.read !== undefined && (
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-yellow-100 text-yellow-800 shadow-sm">
                  Read: {filters.read ? 'Read' : 'Unread'}
                  <button
                    onClick={() => handleFilterChange('read', undefined)}
                    className="ml-2 text-yellow-600 hover:text-yellow-800 transition-colors duration-200"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}; 