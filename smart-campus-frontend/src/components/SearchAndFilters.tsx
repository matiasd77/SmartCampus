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
    <div className="bg-white shadow-sm border border-gray-200 rounded-xl p-8">
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between mb-6">
        <h3 className="text-xl font-semibold text-gray-900 mb-4 lg:mb-0">Search & Filters</h3>
        
        <div className="flex items-center space-x-4">
          {showCreateButton && onCreateClick && (
            <button
              onClick={onCreateClick}
              className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm font-medium transition-colors"
              aria-label={createButtonText}
              title={createButtonText}
            >
              {createButtonText}
            </button>
          )}
          
          {hasActiveFilters && (
            <button
              onClick={onClearFilters}
              className="px-4 py-3 bg-gray-600 text-white rounded-lg hover:bg-gray-700 text-sm font-medium transition-colors"
              aria-label="Clear all filters"
              title="Clear all filters"
            >
              Clear All
            </button>
          )}
        </div>
      </div>

      {/* Search Bar */}
      <div className="mb-6">
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
            <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
            type="text"
            id="search-input"
            name="search"
            value={debouncedSearch}
            onChange={(e) => setDebouncedSearch(e.target.value)}
            placeholder={placeholder}
            className="block w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
          />
        </div>
      </div>

      {/* Filters */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {filterOptions.map((filter) => (
          <div key={filter.key}>
            <label htmlFor={`${filter.key}-filter`} className="block text-sm font-semibold text-gray-700 mb-2">
              {filter.label}
            </label>
            <select
              id={`${filter.key}-filter`}
              name={filter.key}
              value={filters[filter.key] || ''}
              onChange={(e) => handleFilterChange(filter.key, e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
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
        <div className="mt-6 pt-6 border-t border-gray-200">
          <div className="flex flex-wrap items-center gap-3">
            <span className="text-sm font-medium text-gray-700">Active filters:</span>
            
            {searchValue && (
              <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                Search: "{searchValue}"
                <button
                  onClick={() => {
                    setDebouncedSearch('');
                    onSearchChange('');
                  }}
                  className="ml-2 text-blue-600 hover:text-blue-800 transition-colors"
                  aria-label="Clear search"
                  title="Clear search"
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
                <span key={`filter-${key}-${value}`} className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  {filterOption?.label}: {optionLabel}
                  <button
                    onClick={() => handleFilterChange(key, '')}
                    className="ml-2 text-green-600 hover:text-green-800 transition-colors"
                    aria-label={`Clear ${filterOption?.label} filter`}
                    title={`Clear ${filterOption?.label} filter`}
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