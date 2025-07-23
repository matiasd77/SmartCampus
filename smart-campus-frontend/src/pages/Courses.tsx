import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useCourses } from '../hooks/useCourses';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import { COURSE_STATUSES, DEPARTMENTS } from '../types/dashboard';
import type { Course, CourseFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';

export default function Courses() {
  const { user } = useAuth();
  const {
    courses,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    pagination,
    createCourse,
    updateCourse,
    deleteCourse,
    getCourseById,
    getAvailableCourses,
    getCoursesByDepartment,
    updateFilters,
    refresh,
    nextPage,
    previousPage,
    goToPage,
  } = useCourses();

  const [searchValue, setSearchValue] = useState('');

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
    updateFilters({
      ...filters,
      search: value || undefined,
    });
  };

  const handleFiltersChange = (newFilters: Record<string, string | undefined>) => {
    updateFilters({
      ...newFilters,
      search: searchValue || undefined,
    } as CourseFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    updateFilters({});
  };

  const handleEditCourse = (course: Course) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit course:', course);
  };

  const handleDeleteCourse = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this course?')) {
      try {
        await deleteCourse(id);
      } catch (error) {
        console.error('Error deleting course:', error);
      }
    }
  };

  const handleCreateCourse = () => {
    // TODO: Navigate to create page or open create modal
    console.log('Create new course');
  };

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';

  // Early return for loading state
  if (isLoading && (courses ?? []).length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
          <div className="px-4 py-6 sm:px-0">
            <div className="text-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
              <p className="text-gray-600">Loading courses...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Debug logging for courses data
  console.log('Courses component - courses data:', courses);
  console.log('Courses component - courses length:', courses?.length);

  // Prepare filter options
  const filterOptions = [
    {
      key: 'status',
      label: 'Status',
      options: COURSE_STATUSES.map(status => ({ value: status.value, label: status.label })),
    },
    {
      key: 'department',
      label: 'Department',
      options: DEPARTMENTS.map(department => ({ value: department, label: department })),
    },
    {
      key: 'available',
      label: 'Availability',
      options: [
        { value: 'true', label: 'Available' },
        { value: 'false', label: 'Full' },
      ],
    },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* Header */}
          <div className="mb-8">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Courses</h1>
                <p className="mt-1 text-sm text-gray-500">
                  Manage course information and enrollment.
                </p>
              </div>
              <button
                onClick={refresh}
                disabled={isLoading}
                className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 disabled:opacity-50 flex items-center space-x-2"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                <span>Refresh</span>
              </button>
            </div>
          </div>

          {/* Error Message with Retry Button */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
              <div className="flex items-start justify-between">
                <div className="flex items-start">
                  <div className="flex-shrink-0">
                    <svg className="w-5 h-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <div className="ml-3">
                    <h3 className="text-sm font-medium text-red-800">Error loading courses</h3>
                    <p className="text-sm text-red-600 mt-1">{error}</p>
                    <p className="text-xs text-red-500 mt-2">
                      Please check your internet connection and try again.
                    </p>
                  </div>
                </div>
                <button
                  onClick={refresh}
                  disabled={isLoading}
                  className="ml-4 inline-flex items-center px-3 py-2 bg-red-600 text-white text-sm font-medium rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  {isLoading ? 'Retrying...' : 'Retry'}
                </button>
              </div>
            </div>
          )}

          {/* Search and Filters */}
          <SearchAndFilters
            searchValue={searchValue}
            onSearchChange={handleSearchChange}
            filters={filters as Record<string, string | undefined>}
            onFiltersChange={handleFiltersChange}
            onClearFilters={handleClearFilters}
            filterOptions={filterOptions}
            placeholder="Search courses by name, code, or description..."
            showCreateButton={isAdmin || isProfessor}
            onCreateClick={handleCreateCourse}
            createButtonText="Add Course"
          />

          {/* Loading State */}
          {isLoading && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, index) => (
                <div key={index} className="bg-white shadow rounded-lg p-6 animate-pulse">
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2 mb-2"></div>
                  <div className="h-3 bg-gray-200 rounded w-2/3"></div>
                </div>
              ))}
            </div>
          )}

          {/* Courses List */}
          {!isLoading && (courses ?? []).length > 0 && (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {(courses ?? []).map((course) => (
                  <div key={course.id} className="bg-white shadow rounded-lg p-6">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-gray-900">{course.name}</h3>
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        course.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                        course.status === 'INACTIVE' ? 'bg-red-100 text-red-800' :
                        course.status === 'DRAFT' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {course.status}
                      </span>
                    </div>
                    
                    <div className="space-y-2 mb-4">
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Code:</span> {course.code}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Department:</span> {course.department}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Credits:</span> {course.credits}
                      </p>
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Enrollment:</span> {course.currentEnrollment}/{course.maxStudents}
                      </p>
                      {course.professorName && (
                        <p className="text-sm text-gray-600">
                          <span className="font-medium">Professor:</span> {course.professorName}
                        </p>
                      )}
                    </div>

                    <p className="text-sm text-gray-700 mb-4 line-clamp-2">
                      {course.description}
                    </p>

                    {/* Action Buttons */}
                    {(isAdmin || isProfessor) && (
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleEditCourse(course)}
                          className="flex-1 px-3 py-2 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          Edit
                        </button>
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteCourse(course.id)}
                            disabled={isDeleting}
                            className="flex-1 px-3 py-2 bg-red-600 text-white text-sm rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 disabled:opacity-50"
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                ))}
              </div>

              {/* Pagination */}
              <Pagination
                currentPage={pagination.page}
                totalPages={pagination.totalPages}
                totalElements={pagination.totalElements}
                pageSize={pagination.size}
                onPageChange={goToPage}
                onPageSizeChange={(size) => {
                  // TODO: Implement page size change
                  console.log('Change page size to:', size);
                }}
              />
            </>
          )}

          {/* Empty State */}
          {!isLoading && (courses ?? []).length === 0 && (
            <div className="text-center py-12">
              <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No courses found</h3>
              <p className="mt-1 text-sm text-gray-500">
                {Object.keys(filters).length > 0
                  ? 'Try adjusting your search or filter criteria.'
                  : 'Get started by adding a new course.'}
              </p>
              {(isAdmin || isProfessor) && (
                <div className="mt-6">
                  <button
                    onClick={handleCreateCourse}
                    className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                    Add Course
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 