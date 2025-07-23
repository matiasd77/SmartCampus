import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getUserDisplayName } from '../utils/userUtils';
import { 
  GraduationCap, 
  Users, 
  BookOpen, 
  Bell, 
  Megaphone, 
  Award,
  ArrowRight,
  Sparkles
} from 'lucide-react';

export default function Home() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-indigo-50/50">
      {/* Header */}
      <div className="bg-white/80 backdrop-blur-sm shadow-lg border-b border-white/20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 sm:py-6">
          <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center space-y-4 sm:space-y-0">
            <div className="flex items-center justify-center sm:justify-start space-x-3">
              <div className="w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-br from-indigo-500 to-violet-600 rounded-2xl flex items-center justify-center shadow-lg">
                <GraduationCap className="w-5 h-5 sm:w-6 sm:h-6 text-white" />
              </div>
              <h1 className="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-gray-900 via-indigo-800 to-violet-800 bg-clip-text text-transparent">
                SmartCampus
              </h1>
            </div>
            <div className="flex flex-col sm:flex-row items-center space-y-3 sm:space-y-0 sm:space-x-4">
              {isAuthenticated ? (
                <>
                  <span className="text-sm sm:text-base text-gray-600 text-center sm:text-left">
                    Welcome, {getUserDisplayName(user)}!
                  </span>
                  <Link 
                    to="/dashboard" 
                    className="btn-primary px-4 sm:px-6 py-2 sm:py-3 flex items-center justify-center w-full sm:w-auto text-sm sm:text-base"
                  >
                    <ArrowRight className="w-4 h-4 mr-2" />
                    Go to Dashboard
                  </Link>
                </>
              ) : (
                <>
                  <Link 
                    to="/login" 
                    className="btn-primary px-4 sm:px-6 py-2 sm:py-3 w-full sm:w-auto text-center text-sm sm:text-base"
                  >
                    Sign In
                  </Link>
                  <Link 
                    to="/register" 
                    className="btn-secondary px-4 sm:px-6 py-2 sm:py-3 w-full sm:w-auto text-center text-sm sm:text-base"
                  >
                    Sign Up
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 lg:py-16">
        {/* Hero Section */}
        <div className="text-center mb-12 sm:mb-16 lg:mb-20">
          <h2 className="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold bg-gradient-to-r from-gray-900 via-indigo-800 to-violet-800 bg-clip-text text-transparent mb-4 sm:mb-6 leading-tight">
            Welcome to SmartCampus
          </h2>
          <p className="text-base sm:text-lg lg:text-xl text-gray-600 max-w-2xl sm:max-w-3xl mx-auto mb-6 sm:mb-8 px-4 sm:px-0 leading-relaxed">
            Your comprehensive university management platform for students, professors, and administrators.
          </p>
          {isAuthenticated && (
            <div className="flex flex-col sm:flex-row flex-wrap justify-center gap-3 sm:gap-4 mb-8 px-4 sm:px-0">
              <Link 
                to="/dashboard" 
                className="btn-primary px-6 sm:px-8 py-3 sm:py-4 flex items-center justify-center text-sm sm:text-base"
              >
                <Sparkles className="w-4 h-4 sm:w-5 sm:h-5 mr-2" />
                Dashboard
              </Link>
              <Link 
                to="/courses" 
                className="btn-secondary px-6 sm:px-8 py-3 sm:py-4 flex items-center justify-center text-sm sm:text-base"
              >
                <BookOpen className="w-4 h-4 sm:w-5 sm:h-5 mr-2" />
                Courses
              </Link>
              <Link 
                to="/notifications" 
                className="btn-accent px-6 sm:px-8 py-3 sm:py-4 flex items-center justify-center text-sm sm:text-base"
              >
                <Bell className="w-4 h-4 sm:w-5 sm:h-5 mr-2" />
                Notifications
              </Link>
            </div>
          )}
        </div>

        {/* Feature Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8 mb-12 sm:mb-16 lg:mb-20">
          <div className="card p-6 sm:p-8 text-center group hover:shadow-2xl transition-all duration-300">
            <div className="w-12 h-12 sm:w-16 sm:h-16 bg-gradient-to-br from-indigo-500 to-indigo-600 rounded-2xl flex items-center justify-center mx-auto mb-4 sm:mb-6 shadow-lg group-hover:scale-110 transition-transform duration-300">
              <Users className="w-6 h-6 sm:w-8 sm:h-8 text-white" />
            </div>
            <h3 className="text-lg sm:text-xl lg:text-2xl font-bold text-gray-900 mb-3 sm:mb-4">Student Management</h3>
            <p className="text-sm sm:text-base text-gray-600 leading-relaxed">Efficiently manage student records, enrollments, and academic progress.</p>
          </div>

          <div className="card p-6 sm:p-8 text-center group hover:shadow-2xl transition-all duration-300">
            <div className="w-12 h-12 sm:w-16 sm:h-16 bg-gradient-to-br from-emerald-500 to-emerald-600 rounded-2xl flex items-center justify-center mx-auto mb-4 sm:mb-6 shadow-lg group-hover:scale-110 transition-transform duration-300">
              <BookOpen className="w-6 h-6 sm:w-8 sm:h-8 text-white" />
            </div>
            <h3 className="text-lg sm:text-xl lg:text-2xl font-bold text-gray-900 mb-3 sm:mb-4">Course Management</h3>
            <p className="text-sm sm:text-base text-gray-600 leading-relaxed">Organize courses, schedules, and academic resources effectively.</p>
          </div>

          <div className="card p-6 sm:p-8 text-center group hover:shadow-2xl transition-all duration-300 sm:col-span-2 lg:col-span-1">
            <div className="w-12 h-12 sm:w-16 sm:h-16 bg-gradient-to-br from-violet-500 to-violet-600 rounded-2xl flex items-center justify-center mx-auto mb-4 sm:mb-6 shadow-lg group-hover:scale-110 transition-transform duration-300">
              <Bell className="w-6 h-6 sm:w-8 sm:h-8 text-white" />
            </div>
            <h3 className="text-lg sm:text-xl lg:text-2xl font-bold text-gray-900 mb-3 sm:mb-4">Real-time Notifications</h3>
            <p className="text-sm sm:text-base text-gray-600 leading-relaxed">Stay updated with important announcements and alerts.</p>
          </div>
        </div>

        {/* Quick Navigation Section */}
        {isAuthenticated && (
          <div className="card p-6 sm:p-8">
            <h3 className="text-xl sm:text-2xl font-bold text-gray-900 mb-4 sm:mb-6 text-center sm:text-left">🧭 Quick Navigation</h3>
            <p className="text-sm sm:text-base text-gray-600 mb-6 text-center sm:text-left">Access your main features:</p>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
              <Link to="/dashboard" className="btn-primary px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Dashboard
              </Link>
              <Link to="/courses" className="btn-secondary px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Courses
              </Link>
              <Link to="/notifications" className="btn-accent px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Notifications
              </Link>
              <Link to="/announcements" className="btn-outline px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Announcements
              </Link>
              <Link to="/profile" className="btn-primary px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Profile
              </Link>
              <Link to="/grades" className="btn-secondary px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                Grades
              </Link>
              {user?.role === 'ADMIN' && (
                <>
                  <Link to="/students" className="btn-accent px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                    Students
                  </Link>
                  <Link to="/professors" className="btn-outline px-3 sm:px-4 py-2 sm:py-3 text-center text-sm sm:text-base">
                    Professors
                  </Link>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
} 