import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useGrades } from '../hooks/useGrades';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import { GRADE_TYPES, GRADE_STATUSES } from '../types/dashboard';
import type { Grade, GradeFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';
import { 
  Award, 
  TrendingUp, 
  Plus, 
  Search, 
  Filter,
  Calendar,
  BookOpen,
  Users,
  Sparkles,
  BarChart3,
  Target,
  Star
} from 'lucide-react';

export default function Grades() {
  const { user } = useAuth();
  const {
    grades,
    isLoading,
    error,
    isCreating,
    isUpdating,
    isDeleting,
    filters,
    pagination,
    createGrade,
    updateGrade,
    deleteGrade,
    getGradeById,
    getStudentGrades,
    getCourseGrades,
    getEnrollmentGrades,
    updateFilters,
    refresh,
    nextPage,
    previousPage,
    goToPage,
  } = useGrades();

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
    } as GradeFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    updateFilters({});
  };

  const handleEditGrade = (grade: Grade) => {
    // TODO: Navigate to edit page or open edit modal
    console.log('Edit grade:', grade);
  };

  const handleDeleteGrade = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this grade?')) {
      try {
        await deleteGrade(id);
      } catch (error) {
        console.error('Error deleting grade:', error);
      }
    }
  };

  const handleCreateGrade = () => {
    // TODO: Navigate to create page or open create modal
    console.log('Create new grade');
  };

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';

  // Prepare filter options
  const filterOptions = [
    {
      key: 'type',
      label: 'Grade Type',
      options: GRADE_TYPES.map(type => ({ value: type.value, label: type.label })),
    },
    {
      key: 'status',
      label: 'Status',
      options: GRADE_STATUSES.map(status => ({ value: status.value, label: status.label })),
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-amber-50/30 to-yellow-50/50">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Enhanced Header Section */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-8 mb-8 overflow-hidden relative">
          {/* Background decoration */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-amber-100/50 to-yellow-100/50 rounded-full -translate-y-32 translate-x-32"></div>
          <div className="absolute bottom-0 left-0 w-32 h-32 bg-gradient-to-tr from-orange-100/50 to-amber-100/50 rounded-full translate-y-16 -translate-x-16"></div>
          
          <div className="relative z-10">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
              <div className="flex-1">
                <div className="flex items-center mb-6">
                  <div className="w-16 h-16 bg-gradient-to-br from-amber-500 to-orange-600 rounded-2xl flex items-center justify-center shadow-xl mr-6">
                    <Award className="w-8 h-8 text-white" />
                  </div>
                  <div>
                    <h1 className="text-4xl lg:text-5xl font-bold bg-gradient-to-r from-gray-900 via-amber-800 to-orange-800 bg-clip-text text-transparent mb-3">
                      Grades Management
                    </h1>
                    <p className="text-xl text-gray-600 leading-relaxed">
                      Track academic performance and manage student grades
                    </p>
                  </div>
                </div>
                
                {/* Quick stats */}
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mt-8">
                  <div className="bg-gradient-to-br from-amber-50 to-orange-50 rounded-2xl p-6 border border-amber-200/50 hover:shadow-xl transition-all duration-300 hover:scale-105">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-gradient-to-br from-amber-500 to-orange-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                        <BarChart3 className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold text-amber-700">
                          {grades?.length || 0}
                        </p>
                        <p className="text-sm font-semibold text-amber-600">Total Grades</p>
                      </div>
                    </div>
                  </div>
                  
                  <div className="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl p-6 border border-green-200/50 hover:shadow-xl transition-all duration-300 hover:scale-105">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                        <TrendingUp className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold text-green-700">
                          {grades?.filter(g => g.gradeValue >= 80).length || 0}
                        </p>
                        <p className="text-sm font-semibold text-green-600">High Scores</p>
                      </div>
                    </div>
                  </div>
                  
                  <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-200/50 hover:shadow-xl transition-all duration-300 hover:scale-105">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                        <BookOpen className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold text-blue-700">
                          {new Set(grades?.map(g => g.courseId)).size || 0}
                        </p>
                        <p className="text-sm font-semibold text-blue-600">Courses</p>
                      </div>
                    </div>
                  </div>
                  
                  <div className="bg-gradient-to-br from-purple-50 to-violet-50 rounded-2xl p-6 border border-purple-200/50 hover:shadow-xl transition-all duration-300 hover:scale-105">
                    <div className="flex items-center">
                      <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-violet-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                        <Users className="w-6 h-6 text-white" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold text-purple-700">
                          {new Set(grades?.map(g => g.studentId)).size || 0}
                        </p>
                        <p className="text-sm font-semibold text-purple-600">Students</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              {/* Action buttons */}
              <div className="flex flex-col sm:flex-row gap-4 mt-8 lg:mt-0 lg:ml-8">
                <button
                  onClick={refresh}
                  disabled={isLoading}
                  className="group relative overflow-hidden bg-gradient-to-r from-amber-500 to-orange-600 text-white font-bold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 px-6 py-3 flex items-center justify-center"
                >
                  <div className="absolute inset-0 bg-gradient-to-r from-amber-600 to-orange-700 opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
                  <div className="relative flex items-center">
                    <Sparkles className="w-5 h-5 mr-3" />
                    Refresh
                  </div>
                </button>
                
                {(isAdmin || isProfessor) && (
                  <button
                    onClick={handleCreateGrade}
                    className="group relative overflow-hidden bg-gradient-to-r from-green-500 to-emerald-600 text-white font-bold rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 hover:scale-105 px-6 py-3 flex items-center justify-center"
                  >
                    <div className="absolute inset-0 bg-gradient-to-r from-green-600 to-emerald-700 opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
                    <div className="relative flex items-center">
                      <Plus className="w-5 h-5 mr-3" />
                      Add Grade
                    </div>
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Enhanced Search and Filters */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 p-6 mb-8">
          <SearchAndFilters
            searchValue={searchValue}
            onSearchChange={handleSearchChange}
            filters={filters as Record<string, string | undefined>}
            onFiltersChange={handleFiltersChange}
            onClearFilters={handleClearFilters}
            filterOptions={filterOptions}
            placeholder="Search grades by student, course, or assignment..."
          />
        </div>

        {/* Enhanced Content */}
        <div className="bg-white/80 backdrop-blur-sm rounded-3xl shadow-2xl border border-white/20 overflow-hidden">
          {isLoading ? (
            <div className="p-12 text-center">
              <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-amber-600 mx-auto mb-4"></div>
              <p className="text-lg font-semibold text-gray-700">Loading grades...</p>
            </div>
          ) : error ? (
            <div className="p-12 text-center">
              <div className="w-24 h-24 bg-gradient-to-br from-red-100 to-pink-100 rounded-3xl flex items-center justify-center mx-auto mb-6">
                <svg className="w-12 h-12 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4">Error Loading Grades</h3>
              <p className="text-lg text-gray-600 mb-8">{error}</p>
              <button
                onClick={refresh}
                className="bg-gradient-to-r from-amber-500 to-orange-600 text-white font-bold rounded-2xl px-8 py-3 hover:shadow-xl transition-all duration-300 hover:scale-105"
              >
                Try Again
              </button>
            </div>
          ) : grades && grades.length > 0 ? (
            <>
              {/* Enhanced Grades Table */}
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gradient-to-r from-amber-50 to-orange-50">
                    <tr>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Student
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Course
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Assignment
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Grade
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Type
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-bold text-amber-800 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {grades.map((grade) => (
                      <tr key={grade.id} className="hover:bg-gradient-to-r hover:from-amber-50/50 hover:to-orange-50/50 transition-all duration-300">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                              <Users className="w-5 h-5 text-white" />
                            </div>
                            <div>
                              <div className="text-sm font-bold text-gray-900">
                                {grade.studentName || `Student ${grade.studentId}`}
                              </div>
                              <div className="text-sm text-gray-500">ID: {grade.studentId}</div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className="w-8 h-8 bg-gradient-to-br from-green-500 to-emerald-600 rounded-lg flex items-center justify-center shadow-md mr-3">
                              <BookOpen className="w-4 h-4 text-white" />
                            </div>
                            <div className="text-sm font-semibold text-gray-900">
                              {grade.courseName || `Course ${grade.courseId}`}
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm font-semibold text-gray-900">
                            {grade.assignmentName}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div className={`w-12 h-12 rounded-xl flex items-center justify-center shadow-lg mr-3 ${
                              grade.gradeValue >= 90 ? 'bg-gradient-to-br from-green-500 to-emerald-600' :
                              grade.gradeValue >= 80 ? 'bg-gradient-to-br from-blue-500 to-indigo-600' :
                              grade.gradeValue >= 70 ? 'bg-gradient-to-br from-yellow-500 to-amber-600' :
                              'bg-gradient-to-br from-red-500 to-pink-600'
                            }`}>
                              <Target className="w-5 h-5 text-white" />
                            </div>
                            <div>
                              <div className="text-lg font-bold text-gray-900">
                                {grade.gradeValue}/{grade.maxPoints}
                              </div>
                              <div className="text-sm text-gray-500">
                                {((grade.gradeValue / grade.maxPoints) * 100).toFixed(1)}%
                              </div>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold ${
                            grade.gradeType === 'EXAM' ? 'bg-purple-100 text-purple-800' :
                            grade.gradeType === 'QUIZ' ? 'bg-blue-100 text-blue-800' :
                            grade.gradeType === 'ASSIGNMENT' ? 'bg-green-100 text-green-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {grade.gradeType}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold ${
                            grade.status === 'PUBLISHED' ? 'bg-green-100 text-green-800' :
                            grade.status === 'DRAFT' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {grade.status}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex space-x-2">
                            <button
                              onClick={() => handleEditGrade(grade)}
                              className="text-indigo-600 hover:text-indigo-900 font-semibold hover:underline transition-colors duration-200"
                            >
                              Edit
                            </button>
                            {(isAdmin || isProfessor) && (
                              <button
                                onClick={() => handleDeleteGrade(grade.id)}
                                className="text-red-600 hover:text-red-900 font-semibold hover:underline transition-colors duration-200"
                              >
                                Delete
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              
              {/* Enhanced Pagination */}
              <div className="px-6 py-4 bg-gradient-to-r from-amber-50/50 to-orange-50/50">
                <Pagination
                  currentPage={pagination.page}
                  totalPages={pagination.totalPages}
                  totalElements={pagination.totalElements}
                  pageSize={pagination.size}
                  onPageChange={goToPage}
                />
              </div>
            </>
          ) : (
            <div className="p-12 text-center">
              <div className="w-24 h-24 bg-gradient-to-br from-amber-100 to-orange-100 rounded-3xl flex items-center justify-center mx-auto mb-6">
                <Star className="w-12 h-12 text-amber-500" />
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4">No Grades Found</h3>
              <p className="text-lg text-gray-600 mb-8">
                {searchValue || Object.keys(filters).length > 0
                  ? 'No grades match your current search criteria.'
                  : 'No grades have been added yet.'}
              </p>
              {(isAdmin || isProfessor) && (
                <button
                  onClick={handleCreateGrade}
                  className="bg-gradient-to-r from-amber-500 to-orange-600 text-white font-bold rounded-2xl px-8 py-3 hover:shadow-xl transition-all duration-300 hover:scale-105"
                >
                  Add First Grade
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
} 