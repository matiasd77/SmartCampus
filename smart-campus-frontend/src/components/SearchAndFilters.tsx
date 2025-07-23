import React, { useState, useEffect } from 'react';

interface SearchAndFiltersProps {
  searchValue: string;
  onSearchChange: (value: string) => void;
  filters: Record<string, string | undefined>;
  onFiltersChange: (filters: Record<string, string | undefined>) => void;
  onClearFilters: () => void;
  filterOptions: {
    key: string;
    label: string;
    options: { value: string; label: string }[];
  }[];
  placeholder?: string;
  showCreateButton?: boolean;
  onCreateClick?: () => void;
  createButtonText?: string;
}

export const SearchAndFilters: React.FC<SearchAndFiltersProps> = ({
  searchValue,
  onSearchChange,
  filters,
  onFiltersChange,
  onClearFilters,
  filterOptions,
  placeholder = "Search...",
  showCreateButton = false,
  onCreateClick,
  createButtonText = "Create",
}) => {
  const [debouncedSearch, setDebouncedSearch] = useState(searchValue);

  // Debounce search input
  useEffect(() => {
    const timer = setTimeout(() => {
      onSearchChange(debouncedSearch);
    }, 300);

    return () => clearTimeout(timer);
  }, [debouncedSearch, onSearchChange]);

  const handleFilterChange = (key: string, value: string) => {
    onFiltersChange({
      ...filters,
      [key]: value || undefined,
    });
  };

  const hasActiveFilters = Object.values(filters).some(value => value !== undefined) || searchValue;

  return (
    <div className="bg-white shadow rounded-lg p-6 mb-6">
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between mb-4">
        <h3 className="text-lg font-medium text-gray-900 mb-4 lg:mb-0">Search & Filters</h3>
        
        <div className="flex items-center space-x-4">
          {showCreateButton && onCreateClick && (
            <button
              onClick={onCreateClick}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 text-sm"
            >
              {createButtonText}
            </button>
          )}
          
          {hasActiveFilters && (
            <button
              onClick={onClearFilters}
              className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 text-sm"
            >
              Clear All
            </button>
          )}
        </div>
      </div>

      {/* Search Bar */}
      <div className="mb-4">
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
            type="text"
            value={debouncedSearch}
            onChange={(e) => setDebouncedSearch(e.target.value)}
            placeholder={placeholder}
            className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
      </div>

      {/* Filters */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {filterOptions.map((filter) => (
          <div key={filter.key}>
            <label htmlFor={`${filter.key}-filter`} className="block text-sm font-medium text-gray-700 mb-2">
              {filter.label}
            </label>
            <select
              id={`${filter.key}-filter`}
              value={filters[filter.key] || ''}
              onChange={(e) => handleFilterChange(filter.key, e.target.value)}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All {filter.label}</option>
              {filter.options.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        ))}
      </div>

      {/* Active Filters Display */}
      {hasActiveFilters && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-sm text-gray-500">Active filters:</span>
            
            {searchValue && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                Search: "{searchValue}"
                <button
                  onClick={() => {
                    setDebouncedSearch('');
                    onSearchChange('');
                  }}
                  className="ml-1 text-blue-600 hover:text-blue-800"
                >
                  ×
                </button>
              </span>
            )}
            
            {Object.entries(filters).map(([key, value]) => {
              if (!value) return null;
              const filterOption = filterOptions.find(opt => opt.key === key);
              const optionLabel = filterOption?.options.find(opt => opt.value === value)?.label || value;
              
              return (
                <span key={key} className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  {filterOption?.label}: {optionLabel}
                  <button
                    onClick={() => handleFilterChange(key, '')}
                    className="ml-1 text-green-600 hover:text-green-800"
                  >
                    ×
                  </button>
                </span>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}; 