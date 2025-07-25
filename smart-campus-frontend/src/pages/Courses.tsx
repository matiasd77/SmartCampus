import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useCourses } from '../hooks/useCourses';
import { useEnrollments } from '../hooks/useEnrollments';
import { SearchAndFilters } from '../components/SearchAndFilters';
import { Pagination } from '../components/Pagination';
import CourseForm from '../components/CourseForm';
import { CourseSkeleton } from '../components/CourseSkeleton';
import { COURSE_STATUSES, DEPARTMENTS } from '../types/dashboard';
import type { Course, CourseFilters } from '../types/dashboard';
import { Navbar } from '../components/Navbar';
import { FullScreenLoader } from '../components/FullScreenLoader';
import { 
  BookOpen, 
  RefreshCw, 
  Plus, 
  Search, 
  Filter,
  Calendar,
  Users,
  TrendingUp,
  UserPlus,
  UserMinus,
  UserCheck
} from 'lucide-react';
import React from 'react';

export default function Courses() {
  const { user, isLoading: authLoading } = useAuth();
  const {
    courses,
    isLoading: coursesLoading,
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

  // Debug: Show current user info
  console.log('=== COURSES PAGE DEBUG ===');
  console.log('Current user:', user);
  console.log('User ID:', user?.id);
  console.log('User role:', user?.role);
  console.log('User email:', user?.email);
  console.log('========================');

  // Debug function to check students
  const debugCheckStudents = async () => {
    try {
      console.log('=== DEBUG: Checking students in database ===');
      const response = await fetch('http://localhost:8080/api/students', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
          'Content-Type': 'application/json'
        }
      });
      const data = await response.json();
      console.log('Students in database:', data);
    } catch (error) {
      console.error('Error checking students:', error);
    }
  };

  // Function to create student profile for current user
  const createStudentProfile = async () => {
    if (!user?.id || !user?.email) return false;
    
    try {
      console.log('=== Creating student profile for user ===');
      console.log('User ID:', user.id);
      console.log('User email:', user.email);
      
      const studentData = {
        firstName: user.name?.split(' ')[0] || 'Student',
        lastName: user.name?.split(' ').slice(1).join(' ') || 'User',
        email: user.email,
        studentId: `STU${user.id.toString().padStart(4, '0')}`,
        major: 'Computer Science', // Default major
        yearOfStudy: 1, // Default year
        status: 'ACTIVE'
      };
      
      console.log('Student data to create:', studentData);
      
      const response = await fetch('http://localhost:8080/api/students', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(studentData)
      });
      
      const result = await response.json();
      console.log('Student creation result:', result);
      console.log('Response status:', response.status);
      console.log('Response status text:', response.statusText);
      
      if (response.ok) {
        console.log('✅ Student profile created successfully!');
        return true;
      } else {
        console.error('❌ Failed to create student profile:', result);
        console.error('Response status:', response.status);
        console.error('Response headers:', response.headers);
        return false;
      }
    } catch (error) {
      console.error('Error creating student profile:', error);
      return false;
    }
  };

  // Call debug function on component mount
  React.useEffect(() => {
    if (user?.role === 'ADMIN') {
      debugCheckStudents();
    }
  }, [user?.role]);

  // Debug function to show current student status
  const debugStudentStatus = async () => {
    try {
      console.log('=== DEBUG: Checking student status ===');
      console.log('Current user ID:', user?.id);
      console.log('Current user email:', user?.email);
      
      // Check if current user has a student record
      const response = await fetch(`http://localhost:8080/api/students/${user?.id}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const studentData = await response.json();
        console.log('✅ Student record found:', studentData);
        alert('✅ Student record found! You should be able to enroll now.');
      } else {
        console.log('❌ No student record found for user ID:', user?.id);
        alert('❌ No student record found for your user account.\n\nYou need a student profile to enroll in courses.');
      }
    } catch (error) {
      console.error('Error checking student status:', error);
      alert('Error checking student status. Please check console for details.');
    }
  };

  const {
    enrollments,
    createEnrollment,
    deleteEnrollment,
    getStudentEnrollments,
    isLoading: enrollmentsLoading,
  } = useEnrollments();

  const [searchValue, setSearchValue] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [studentEnrollments, setStudentEnrollments] = useState<number[]>([]);
  const [enrollingCourseId, setEnrollingCourseId] = useState<number | null>(null);
  const [droppingCourseId, setDroppingCourseId] = useState<number | null>(null);

  // Wait for auth to be initialized before showing any content
  if (authLoading) {
    return <FullScreenLoader message="Loading courses..." />;
  }

  const isAdmin = user?.role === 'ADMIN';
  const isProfessor = user?.role === 'PROFESSOR';
  const isStudent = user?.role === 'STUDENT';

  // Load student enrollments when component mounts
  React.useEffect(() => {
    if (isStudent && user?.id) {
      loadStudentEnrollments();
    }
  }, [isStudent, user?.id]);

  const loadStudentEnrollments = async () => {
    if (!user?.id) return;
    
    try {
      const response = await getStudentEnrollments(user.id);
      console.log('Student enrollments response:', response);
      
      // Handle different response structures
      let studentEnrollmentsData;
      if (response.data && Array.isArray(response.data)) {
        studentEnrollmentsData = response.data;
      } else if (Array.isArray(response)) {
        studentEnrollmentsData = response;
      } else if (response.content && Array.isArray(response.content)) {
        studentEnrollmentsData = response.content;
      } else {
        console.warn('Unexpected enrollment response structure:', response);
        studentEnrollmentsData = [];
      }
      
      const enrolledCourseIds = studentEnrollmentsData.map((enrollment: any) => enrollment.courseId);
      console.log('Enrolled course IDs:', enrolledCourseIds);
      setStudentEnrollments(enrolledCourseIds);
    } catch (error) {
      console.error('Error loading student enrollments:', error);
      // Don't throw error, just set empty enrollments
      setStudentEnrollments([]);
    }
  };

  const handleEnrollInCourse = async (courseId: number) => {
    if (!user?.id) return;
    
    // Debug: Check user information
    console.log('Current user:', user);
    console.log('User role:', user.role);
    console.log('User ID:', user.id);
    
    // Check if user is actually a student
    if (user.role !== 'STUDENT') {
      alert('Only students can enroll in courses. Current user role: ' + user.role + '\n\nPlease log in as a student to enroll in courses.');
      return;
    }
    
    setEnrollingCourseId(courseId);
    try {
      console.log('Enrolling in course:', courseId, 'for user:', user.id);
      const enrollmentData = {
        studentId: user.id,
        courseId: courseId
      };
      console.log('Enrollment data:', enrollmentData);
      
      await createEnrollment(enrollmentData);
      console.log('Enrollment successful');
      
      // Refresh student enrollments
      await loadStudentEnrollments();
    } catch (error: any) {
      console.error('Error enrolling in course:', error);
      
      // Check for specific error messages
      if (error.response?.data?.message?.includes('Student not found')) {
        console.log('Student record not found, attempting to create student profile...');
        
        // Try to create student profile
        const profileCreated = await createStudentProfile();
        
        if (profileCreated) {
          // Retry enrollment after creating profile
          try {
            console.log('Retrying enrollment after creating student profile...');
            const enrollmentData = {
              studentId: user.id,
              courseId: courseId
            };
            
            await createEnrollment(enrollmentData);
            console.log('Enrollment successful after creating profile!');
            
            // Refresh student enrollments
            await loadStudentEnrollments();
            
            alert('Student profile created and enrollment successful!');
            return;
          } catch (retryError: any) {
            console.error('Error on retry enrollment:', retryError);
            alert('Failed to enroll after creating student profile. Please try again.');
          }
        } else {
          alert('❌ Student Profile Required\n\nYour user account exists but you need a student profile to enroll in courses.\n\nTo fix this:\n\n1. Logout and login as an ADMIN user\n2. Go to the Students page\n3. Create a new student with your email: ' + user.email + '\n4. Logout and login back as a student\n5. Try enrolling again\n\nOr contact your system administrator to create your student profile.');
        }
      } else if (error.response?.data?.message?.includes('already enrolled')) {
        alert('You are already enrolled in this course.');
      } else if (error.response?.data?.message?.includes('Course is full')) {
        alert('This course is full. No available seats.');
      } else if (error.response?.data?.message?.includes('not active')) {
        alert('This course is not active for enrollment.');
      } else {
        alert('Failed to enroll in course. Please try again.');
      }
    } finally {
      setEnrollingCourseId(null);
    }
  };

  const handleDropCourse = async (courseId: number) => {
    if (!user?.id) return;
    
    setDroppingCourseId(courseId);
    try {
      console.log('Dropping course:', courseId, 'for user:', user.id);
      
      // Get the student's enrollments to find the enrollment ID
      const studentEnrollmentsData = await getStudentEnrollments(user.id);
      let enrollmentsArray;
      
      // Handle different response structures
      if (studentEnrollmentsData.data && Array.isArray(studentEnrollmentsData.data)) {
        enrollmentsArray = studentEnrollmentsData.data;
      } else if (Array.isArray(studentEnrollmentsData)) {
        enrollmentsArray = studentEnrollmentsData;
      } else if (studentEnrollmentsData.content && Array.isArray(studentEnrollmentsData.content)) {
        enrollmentsArray = studentEnrollmentsData.content;
      } else {
        enrollmentsArray = [];
      }
      
      // Find the enrollment for this course
      const enrollment = enrollmentsArray.find((e: any) => e.courseId === courseId);
      
      if (enrollment) {
        console.log('Found enrollment to drop:', enrollment.id);
        await deleteEnrollment(enrollment.id);
        console.log('Drop successful');
        
        // Refresh student enrollments
        await loadStudentEnrollments();
      } else {
        console.warn('No enrollment found for course:', courseId);
        alert('No enrollment found for this course.');
      }
    } catch (error) {
      console.error('Error dropping course:', error);
      alert('Failed to drop course. Please try again.');
    } finally {
      setDroppingCourseId(null);
    }
  };

  const isEnrolledInCourse = (courseId: number) => {
    return studentEnrollments.includes(courseId);
  };

  // Memoize the loading state to prevent unnecessary re-renders
  const shouldShowSkeleton = coursesLoading && (!courses || courses.length === 0);

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
    setEditingCourse(course);
    setIsFormOpen(true);
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
    setEditingCourse(null);
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (courseData: any) => {
    if (editingCourse) {
      await updateCourse(editingCourse.id, courseData);
    } else {
      await createCourse(courseData);
    }
    refresh();
  };

  const handleFormClose = () => {
    setIsFormOpen(false);
    setEditingCourse(null);
  };

  const filterOptions = [
    {
      key: 'status',
      label: 'Status',
      options: [
        { value: 'ACTIVE', label: 'Active' },
        { value: 'INACTIVE', label: 'Inactive' },
        { value: 'DRAFT', label: 'Draft' },
      ],
    },
    {
      key: 'department',
      label: 'Department',
      options: DEPARTMENTS.map(dept => ({ value: dept, label: dept })),
    },
    {
      key: 'availability',
      label: 'Availability',
      options: [
        { value: 'available', label: 'Available' },
        { value: 'full', label: 'Full' },
        { value: 'waitlist', label: 'Waitlist' },
      ],
    },
  ];

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
                    <BookOpen className="w-6 h-6 text-white" />
                  </div>
                  <div>
                    <h1 className="text-3xl lg:text-4xl font-bold text-gray-900 mb-2">
                      Courses
                    </h1>
                    <p className="text-lg text-gray-600">
                      Manage course information and enrollment
                    </p>
                  </div>
                </div>
                
                {/* Quick stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
                  <div className="text-center p-4 bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl border border-blue-200">
                    <div className="text-2xl font-bold text-blue-700">{pagination.totalElements}</div>
                    <div className="text-sm text-blue-600 font-medium">Total</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border border-green-200">
                    <div className="text-2xl font-bold text-green-700">{(courses ?? []).filter(c => c.status === 'ACTIVE').length}</div>
                    <div className="text-sm text-green-600 font-medium">Active</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-purple-50 to-violet-50 rounded-xl border border-purple-200">
                    <div className="text-2xl font-bold text-purple-700">{(courses ?? []).filter(c => c.currentEnrollment < c.maxStudents).length}</div>
                    <div className="text-sm text-purple-600 font-medium">Available</div>
                  </div>
                  <div className="text-center p-4 bg-gradient-to-br from-yellow-50 to-amber-50 rounded-xl border border-yellow-200">
                    <div className="text-2xl font-bold text-yellow-700">{pagination.page + 1}</div>
                    <div className="text-sm text-yellow-600 font-medium">Page</div>
                  </div>
                </div>
              </div>
              
              <div className="mt-6 lg:mt-0 lg:ml-8 flex flex-col sm:flex-row gap-3">
                <button
                  onClick={refresh}
                  disabled={coursesLoading}
                  className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:hover:scale-100"
                  aria-label="Refresh courses list"
                  title="Refresh courses list"
                >
                  <RefreshCw className={`w-5 h-5 mr-2 ${coursesLoading ? 'animate-spin' : ''}`} />
                  {coursesLoading ? 'Refreshing...' : 'Refresh'}
                </button>
                
                {isStudent && (
                  <button
                    onClick={debugStudentStatus}
                    className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
                    aria-label="Check student status"
                    title="Check if you have a student profile"
                  >
                    <UserCheck className="w-5 h-5 mr-2" />
                    Check Student Status
                  </button>
                )}
                
                {(isAdmin || isProfessor) && (
                  <button 
                    onClick={handleCreateCourse}
                    className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
                    aria-label="Add new course"
                    title="Add new course"
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
                id="course-search"
                name="courseSearch"
                value={searchValue}
                onChange={(e) => handleSearchChange(e.target.value)}
                placeholder="Search courses by name, code, or description..."
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
                  <h3 className="text-lg font-semibold text-red-800">Error loading courses</h3>
                  <p className="text-red-600 mt-1">{error}</p>
                  <p className="text-sm text-red-500 mt-2">
                    Please check your internet connection and try again.
                  </p>
                </div>
              </div>
              <button
                onClick={refresh}
                disabled={coursesLoading}
                className="ml-4 inline-flex items-center px-4 py-2 bg-red-600 text-white text-sm font-medium rounded-lg hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                aria-label="Retry loading courses"
                title="Retry loading courses"
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${coursesLoading ? 'animate-spin' : ''}`} />
                {coursesLoading ? 'Retrying...' : 'Retry'}
              </button>
            </div>
          </div>
        )}

        {/* Loading State */}
        {shouldShowSkeleton && <CourseSkeleton count={6} />}

        {/* Empty State */}
        {!shouldShowSkeleton && (courses ?? []).length === 0 && !error && (
          <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-12 text-center">
            <div className="w-20 h-20 bg-gradient-to-br from-gray-100 to-slate-100 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg">
              <BookOpen className="w-10 h-10 text-gray-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">No courses found</h3>
            <p className="text-gray-600 mb-8 max-w-md mx-auto">
              {Object.keys(filters).length > 0
                ? 'Try adjusting your search or filter criteria to find what you\'re looking for.'
                : 'Get started by adding your first course to the system.'}
            </p>
            {(isAdmin || isProfessor) && (
              <button 
                onClick={handleCreateCourse}
                className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-105"
                aria-label="Add new course"
                title="Add new course"
              >
                <Plus className="w-5 h-5 mr-2" />
                Create First Course
              </button>
            )}
          </div>
        )}

        {/* Courses List */}
        {!shouldShowSkeleton && (courses ?? []).length > 0 && (
          <>
            <div className="space-y-6 mb-8">
              {(courses ?? []).map((course) => (
                <div key={`course-${course.id}`} className="bg-white rounded-2xl shadow-lg border border-gray-200 p-6 overflow-hidden relative">
                  <div className="absolute top-0 left-0 w-1 h-full bg-gradient-to-b from-blue-500 to-indigo-600"></div>
                  <div className="flex items-start space-x-4">
                    <div className="w-10 h-10 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-xl flex items-center justify-center flex-shrink-0">
                      <BookOpen className="w-5 h-5 text-blue-600" />
                    </div>
                    <div className="flex-1 space-y-3">
                      <div className="flex items-center justify-between">
                        <h3 className="text-xl font-semibold text-gray-900">{course.name}</h3>
                        <div className="flex space-x-2">
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold ${
                            course.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                            course.status === 'INACTIVE' ? 'bg-red-100 text-red-800' :
                            course.status === 'DRAFT' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {course.status}
                          </span>
                          {isStudent && isEnrolledInCourse(course.id) && (
                            <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">
                              Enrolled
                            </span>
                          )}
                        </div>
                      </div>
                      <div className="flex space-x-4 text-sm text-gray-600">
                        <span><span className="font-semibold text-gray-700">Code:</span> {course.code}</span>
                        <span><span className="font-semibold text-gray-700">Credits:</span> {course.credits}</span>
                        <span><span className="font-semibold text-gray-700">Enrollment:</span> {course.currentEnrollment}/{course.maxStudents}</span>
                      </div>
                      {course.professorName && (
                        <div className="text-sm text-gray-600">
                          <span className="font-semibold text-gray-700">Professor:</span> {course.professorName}
                        </div>
                      )}
                      <p className="text-sm text-gray-700 line-clamp-2">
                        {course.description}
                      </p>
                      
                      {/* Action Buttons */}
                      {(isAdmin || isProfessor) && (
                        <div className="flex space-x-3 pt-2">
                          <button
                            onClick={() => handleEditCourse(course)}
                            className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105"
                            aria-label={`Edit course ${course.name}`}
                            title={`Edit course ${course.name}`}
                          >
                            Edit
                          </button>
                          {isAdmin && (
                            <button
                              onClick={() => handleDeleteCourse(course.id)}
                              disabled={isDeleting}
                              className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-red-500 to-pink-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
                              aria-label={`Delete course ${course.name}`}
                              title={`Delete course ${course.name}`}
                            >
                              Delete
                            </button>
                          )}
                        </div>
                      )}
                      
                      {/* Student Enrollment Buttons */}
                      {isStudent && course.status === 'ACTIVE' && (
                        <div className="flex space-x-3 pt-2">
                          {isEnrolledInCourse(course.id) ? (
                            <button
                              onClick={() => handleDropCourse(course.id)}
                              disabled={droppingCourseId === course.id || enrollmentsLoading}
                              className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-red-500 to-pink-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
                              aria-label={`Drop course ${course.name}`}
                              title={`Drop course ${course.name}`}
                            >
                              <UserMinus className="w-4 h-4 mr-2" />
                              {droppingCourseId === course.id ? 'Dropping...' : 'Drop Course'}
                            </button>
                          ) : (
                            <button
                              onClick={() => handleEnrollInCourse(course.id)}
                              disabled={enrollingCourseId === course.id || enrollmentsLoading || course.currentEnrollment >= course.maxStudents}
                              className="inline-flex items-center px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white text-sm font-medium rounded-lg hover:shadow-lg transition-all duration-200 hover:scale-105 disabled:opacity-50"
                              aria-label={`Enroll in course ${course.name}`}
                              title={`Enroll in course ${course.name}`}
                            >
                              <UserPlus className="w-4 h-4 mr-2" />
                              {enrollingCourseId === course.id ? 'Enrolling...' : 'Enroll in Course'}
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {pagination.totalPages > 1 && (
              <div className="bg-white rounded-2xl shadow-xl border border-gray-200 p-6">
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
              </div>
            )}
          </>
        )}
      </div>

      {/* Course Form Modal */}
      {isFormOpen && (
        <CourseForm
          isOpen={isFormOpen}
          course={editingCourse}
          onSubmit={handleFormSubmit}
          onClose={handleFormClose}
          isEditing={!!editingCourse}
        />
      )}
    </div>
  );
} 