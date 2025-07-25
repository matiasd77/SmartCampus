import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useStudents } from '../hooks/useStudents';
import { StudentCard } from '../components/StudentCard';
import { StudentForm } from '../components/StudentForm';
import { Pagination } from '../components/Pagination';
import { STUDENT_STATUSES, STUDENT_YEARS } from '../types/dashboard';
import type { Student, StudentFilters, StudentRequest } from '../types/dashboard';
import { Navbar } from '../components/Navbar';
import { 
  RefreshCw, 
  Users, 
  Plus, 
  Search, 
  Filter,
  Calendar,
  GraduationCap
} from 'lucide-react';
import { testStudentsEndpoint, testAllStudentsEndpoint, testCreateStudents } from '../utils/tokenDebugger';

export default function Students() {
  const { user } = useAuth();
  const {
    students,
    isLoading,
    error,
    filters,
    majors,
    clearFilters,
    createStudent,
    updateStudent,
    deleteStudent,
    refresh,
  } = useStudents();

  const [searchValue, setSearchValue] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | undefined>();

  // Add debugging
  console.log('Students component render:', {
    students: students?.length || 0,
    isLoading,
    error,
    user: user?.role
  });

  // Auto-test endpoints when component mounts
  useEffect(() => {
    console.log('🔍 Students page mounted - testing endpoints...');
    testStudentsEndpoint();
    testAllStudentsEndpoint();
  }, []);

  const handleCreateTestStudents = async () => {
    try {
      await testCreateStudents();
      // Refresh the students list after creating test data
      setTimeout(() => {
        refresh();
      }, 1000);
    } catch (error) {
      console.error('Error creating test students:', error);
    }
  };

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
    // updateFilters({ // Removed as per edit hint
    //   ...filters,
    //   search: value || undefined,
    // });
  };

  const handleFiltersChange = (newFilters: Record<string, string | undefined>) => {
    // updateFilters({ // Removed as per edit hint
    //   ...newFilters,
    //   search: searchValue || undefined,
    // } as StudentFilters);
  };

  const handleClearFilters = () => {
    setSearchValue('');
    clearFilters();
  };

  const handleEditStudent = (student: Student) => {
    setEditingStudent(student);
    setIsFormOpen(true);
  };

  const handleDeleteStudent = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      try {
        await deleteStudent(id);
      } catch (error) {
        console.error('Error deleting student:', error);
        alert('Failed to delete student. Please try again.');
      }
    }
  };

  const handleCreateStudent = () => {
    setEditingStudent(undefined);
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (student: StudentRequest) => {
    try {
      if (editingStudent) {
        await updateStudent(editingStudent.id, student);
      } else {
        await createStudent(student);
      }
      setIsFormOpen(false);
      setEditingStudent(undefined);
      refresh();
    } catch (error) {
      console.error('Error saving student:', error);
      alert('Failed to save student. Please try again.');
    }
  };

  const handleFormCancel = () => {
    setIsFormOpen(false);
    setEditingStudent(undefined);
  };

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header Section */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-8 mb-8 overflow-hidden relative">
          {/* Background decoration */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-gradient-to-br from-blue-100/50 to-indigo-100/50 rounded-full -translate-y-32 translate-x-32"></div>
          <div className="absolute bottom-0 left-0 w-32 h-32 bg-gradient-to-tr from-purple-100/50 to-pink-100/50 rounded-full translate-y-16 -translate-x-16"></div>
          
          <div className="relative z-10">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
              <div className="flex-1">
                <div className="flex items-center mb-4">
                  <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg mr-4">
                    <Users className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h1 className="text-3xl lg:text-4xl font-bold text-gray-900 mb-2">
                      Students
                    </h1>
                    <p className="text-lg text-gray-600">
                      Manage student information and records
                    </p>
                  </div>
                </div>
                
                {/* Quick stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
                  <div className="text-center p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl border border-blue-200">
                    <div className="text-2xl font-bold text-blue-700">{students?.length || 0}</div>
                    <div className="text-sm text-blue-600 font-medium">Total</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border border-green-200">
                    <div className="text-2xl font-bold text-green-700">{(students ?? []).filter(s => s.status === 'ACTIVE').length}</div>
                    <div className="text-sm text-green-600 font-medium">Active</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-purple-50 to-violet-50 rounded-xl border border-purple-200">
                    <div className="text-2xl font-bold text-purple-700">{(students ?? []).filter(s => s.status === 'ENROLLED').length}</div>
                    <div className="text-sm text-purple-600 font-medium">Enrolled</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-yellow-50 to-amber-50 rounded-xl border border-yellow-200">
                    <div className="text-2xl font-bold text-yellow-700">1</div>
                    <div className="text-sm text-yellow-600 font-medium">Page</div>
                  </div>
                </div>
              </div>
              
              <div className="mt-6 lg:mt-0 lg:ml-8 flex flex-col sm:flex-row gap-3">
                {isAdmin && (
                  <button
                    onClick={handleCreateTestStudents}
                    disabled={isLoading}
                    className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                  >
                    <Users className="w-5 h-5 mr-2" />
                    Create Test Data
                  </button>
                )}
                <button
                  onClick={refresh}
                  disabled={isLoading}
                  className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                >
                  <RefreshCw className={`w-5 h-5 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                  {isLoading ? 'Refreshing...' : 'Refresh'}
                </button>
                
                {isAdmin && (
                  <button 
                    onClick={handleCreateStudent}
                    className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
                  >
                    <Plus className="w-5 h-5 mr-2" />
                    Create New
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Search and Filter Section */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6 mb-8">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-gray-400" />
              </div>
              <input
                type="text"
                id="student-search"
                name="studentSearch"
                value={searchValue}
                onChange={(e) => handleSearchChange(e.target.value)}
                placeholder="Search students by name, email, or student ID..."
                className="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              />
            </div>
            <div className="flex gap-3">
              <button className="inline-flex items-center px-4 py-3 bg-gradient-to-r from-purple-500 to-violet-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105">
                <Filter className="w-4 h-4 mr-2" />
                Filter
              </button>
              <button className="inline-flex items-center px-4 py-3 bg-gradient-to-r from-yellow-500 to-amber-600 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105">
                <Calendar className="w-4 h-4 mr-2" />
                Sort
              </button>
            </div>
          </div>
        </div>

        {/* Error Message with Retry Button */}
        {error && (
          <div className="mb-8 bg-red-50 border border-red-200 rounded-2xl p-6">
            <div className="flex items-start justify-between">
              <div className="flex items-start">
                <div className="flex-shrink-0">
                  <svg className="w-6 h-6 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                  </svg>
                </div>
                <div className="ml-3">
                  <h3 className="text-lg font-semibold text-red-800">Error loading students</h3>
                  <p className="text-red-600 mt-1">{error}</p>
                  <p className="text-sm text-red-500 mt-2">
                    Please check your internet connection and try again.
                  </p>
                </div>
              </div>
              <button
                onClick={refresh}
                disabled={isLoading}
                className="ml-4 inline-flex items-center px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                {isLoading ? 'Retrying...' : 'Retry'}
              </button>
            </div>
          </div>
        )}

        {/* Loading State */}
        {isLoading && (students ?? []).length === 0 && (
          <div className="space-y-6">
            {[...Array(3)].map((_, index) => (
              <div key={index} className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 animate-pulse overflow-hidden relative">
                <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
                <div className="flex items-start space-x-4">
                  <div className="w-10 h-10 bg-gray-200 rounded-xl flex-shrink-0"></div>
                  <div className="flex-1 space-y-3">
                    <div className="h-6 bg-gray-200 rounded w-3/4"></div>
                    <div className="flex space-x-2">
                      <div className="h-6 bg-gray-200 rounded w-16"></div>
                      <div className="h-6 bg-gray-200 rounded w-20"></div>
                      <div className="h-6 bg-gray-200 rounded w-16"></div>
                    </div>
                    <div className="h-4 bg-gray-200 rounded w-full"></div>
                    <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Empty State */}
        {!isLoading && (students ?? []).length === 0 && !error && (
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-12 text-center">
            <div className="w-20 h-20 bg-gradient-to-br from-gray-100 to-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <Users className="w-10 h-10 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">No students found</h3>
            <p className="text-gray-600 mb-8 max-w-md mx-auto">
              {Object.keys(filters).length > 0
                ? 'Try adjusting your search or filter criteria to find what you\'re looking for.'
                : 'There are no students in the system yet. Get started by adding a new student if you\'re an administrator.'}
            </p>
            {isAdmin && (
              <button
                onClick={handleCreateStudent}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
              >
                <Plus className="w-5 h-5 mr-2" />
                Add First Student
              </button>
            )}
          </div>
        )}

        {/* Students List */}
        {!isLoading && (students ?? []).length > 0 && (
          <>
            <div className="space-y-6 mb-8">
              {students.map((student) => (
                <div key={`student-${student.id}`} className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 overflow-hidden relative">
                  <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
                  <div className="flex items-start space-x-4">
                    <div className="w-10 h-10 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-xl flex items-center justify-center flex-shrink-0">
                      <GraduationCap className="w-5 h-5 text-blue-600" />
                    </div>
                    <div className="flex-1 space-y-3">
                      <div className="flex items-center justify-between">
                        <h3 className="text-xl font-semibold text-gray-900">{student.firstName} {student.lastName}</h3>
                        <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${
                          student.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                          student.status === 'INACTIVE' ? 'bg-red-100 text-red-800' :
                          student.status === 'ENROLLED' ? 'bg-blue-100 text-blue-800' :
                          'bg-gray-100 text-gray-800'
                        }`}>
                          {student.status}
                        </span>
                      </div>
                      <div className="flex space-x-4 text-sm text-gray-600">
                        <span><span className="font-semibold text-gray-700">ID:</span> {student.studentId}</span>
                        <span><span className="font-semibold text-gray-700">Year:</span> {student.year}</span>
                        <span><span className="font-semibold text-gray-700">Major:</span> {student.major}</span>
                      </div>
                      <div className="text-sm text-gray-600">
                        <span className="font-semibold text-gray-700">Email:</span> {student.email}
                      </div>
                      
                      {/* Action Buttons */}
                      {isAdmin && (
                        <div className="flex space-x-3 pt-2">
                          <button
                            onClick={() => handleEditStudent(student)}
                            className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDeleteStudent(student.id)}
                            disabled={isLoading}
                            className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-red-500 to-pink-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
                          >
                            Delete
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {/* Removed as per edit hint */}
          </>
        )}
      </div>

      {isFormOpen && (
        <StudentForm
          student={editingStudent}
          isOpen={isFormOpen}
          onClose={handleFormCancel}
          onSubmit={handleFormSubmit}
          isLoading={isLoading}
        />
      )}
    </div>
  );
}
